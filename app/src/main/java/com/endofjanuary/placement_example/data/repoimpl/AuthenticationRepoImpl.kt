package com.endofjanuary.placement_example.data.repoimpl

import com.endofjanuary.placement_example.domain.models.User
import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo
import com.endofjanuary.placement_example.domain.repo.SignInState
import com.endofjanuary.placement_example.utils.Resource
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


const val DOCUMENT_PATH = "/users/"

class AuthenticationRepoImpl(
    private val firebaseAuth: FirebaseAuth, private val fireStoreDB: FirebaseFirestore
) : AuthenticationRepo {

    private lateinit var userDoc: DocumentReference
    private lateinit var user: FirebaseUser
    private var docStateListener: ListenerRegistration? = null


    override val signInError = MutableStateFlow<String?>(null)
    override val signInState = MutableStateFlow(SignInState.NOT_SIGNED_IN)
    override val wrongPasswordError = MutableStateFlow<Boolean?>(null)
    override fun currentUser(scope: CoroutineScope): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                userDoc = fireStoreDB.document(DOCUMENT_PATH + currentUser.uid)

                val fireStoreUserFlow = fireStoreUserData(userDoc)
                scope.launch {
                    fireStoreUserFlow.collect { fs ->
                        if (fs != null) {
                            signInState.value = SignInState.AUTHORIZED
                            trySend(
                                User(
                                    email = currentUser.email!!,
                                    displayName = fs.name,
                                    profilePictureUrl = currentUser.photoUrl?.toString(),
                                    id = currentUser.uid,
                                    isEmailVerified = currentUser.isEmailVerified,
                                    autoRefineModel = fs.auto_refine,
                                    autoSaveModel = fs.auto_save
                                )
                            )
                        } else {
                            trySend(
                                User(
                                    email = currentUser.email!!,
                                    displayName = null,
                                    profilePictureUrl = currentUser.photoUrl?.toString(),
                                    id = currentUser.uid,
                                    isEmailVerified = currentUser.isEmailVerified,
                                    autoRefineModel = false,
                                    autoSaveModel = false
                                )
                            )
                        }
                    }
                }
            } else {
                trySend(null)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    private fun fireStoreUserData(userDoc: DocumentReference): Flow<FireStoreUser?> = callbackFlow {
        val docStateListener = userDoc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                signInError.value = e.message
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val fireStoreUser = snapshot.toObject(FireStoreUser::class.java)
                trySend(fireStoreUser)
            } else {
                trySend(null)
            }
        }
        awaitClose {
            docStateListener.remove()
        }
    }

    override suspend fun signIn(email: String, password: String) {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) signInState.value = SignInState.AUTHORIZED
            else signInState.value = SignInState.CREDENTIAL_ERROR
        } catch (e: Exception) {
            signInError.value = e.message

            when (e) {

                is FirebaseAuthInvalidUserException -> signInState.value =
                    SignInState.USER_NOT_FOUND

                is FirebaseAuthInvalidCredentialsException -> {
                    signInState.value =
                        SignInState.CREDENTIAL_ERROR
                    wrongPasswordError.value = true
                }

                is FirebaseAuthUserCollisionException -> signInState.value =
                    SignInState.USER_COLLISION

                else -> {
                    signInState.value = SignInState.CREDENTIAL_ERROR
                }
            }
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            val result = suspendCoroutine<AuthResult> { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            continuation.resume(task.result)
                        } else {
                            continuation.resumeWithException(
                                task.exception ?: Exception()
                            )
                        }
                    }
            }
            verifyEmail()
            if (result.user != null) {
                updateUserProfileData(
                    userName = "", userAuthID = result.user!!.uid, refine = false, save = false
                )
            } else {
                signInState.value = SignInState.NOT_SIGNED_IN
            }
        } catch (e: Exception) {
            signInError.value = e.message
            when (e) {
                is FirebaseAuthInvalidUserException -> signInState.value =
                    SignInState.USER_NOT_FOUND

                is FirebaseAuthUserCollisionException -> signInState.value =
                    SignInState.USER_COLLISION

                is FirebaseAuthInvalidCredentialsException -> signInState.value =
                    SignInState.CREDENTIAL_ERROR

                else -> signInState.value = SignInState.CREDENTIAL_ERROR
            }
        }
    }


    override suspend fun verifyEmail() {
        try {
            val result = suspendCoroutine { continuation ->
                firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signInState.value = SignInState.VERIFYING_EMAIL
                        continuation.resume(task.result)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            signInState.value = SignInState.VERIFY_FAILED
            signInError.value = e.message
        }
    }

    override suspend fun signOut() {
        signInState.value = SignInState.NOT_SIGNED_IN
        docStateListener?.remove()
        firebaseAuth.signOut()
    }

    override suspend fun reloadUser() {
        try {
            firebaseAuth.currentUser?.reload()
            if (firebaseAuth.currentUser?.isEmailVerified == true && signInState.value == SignInState.VERIFYING_EMAIL) signInState.value =
                SignInState.AUTHORIZED
        } catch (e: Exception) {
            signInError.value = e.message
            if (e is FirebaseAuthInvalidUserException) {
                signInState.value = SignInState.USER_NOT_FOUND
            }
        }
    }


    override suspend fun updateUserProfileData(
        userName: String, refine: Boolean, save: Boolean, userAuthID: String
    ): Resource<String> {
        val def = CompletableDeferred<Resource<String>>()
        try {
            val user = FireStoreUser(
                auto_refine = refine,
                auto_save = save,
                name = userName,
            )
            userDoc.set(user).addOnSuccessListener {
                signInState.value = SignInState.AUTHORIZED
                def.complete(Resource.Success(""))
            }.addOnFailureListener { e ->
                signInState.value = SignInState.NOT_SIGNED_IN
                throw Exception(e.message)
            }
            return def.await()
        } catch (e: Exception) {
            return Resource.Error(e.message.toString())
        }
    }

    override suspend fun reAuthenticateUser(email: String, password: String) {
        val user = firebaseAuth.currentUser!!

        val credential = EmailAuthProvider.getCredential(email, password)

        try {
            suspendCoroutine { continuation ->
                user.reauthenticate(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signInState.value = SignInState.REAUTHORIZED
                        continuation.resume(Unit)

                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception()
                        )
                    }
                }
            }
            if (signInState.value == SignInState.REAUTHORIZED) sendPasswordResetEmail(email)
        } catch (e: Exception) {
            signInError.value = e.message

            when (e) {
                is FirebaseAuthInvalidUserException -> signInState.value =
                    SignInState.USER_NOT_FOUND

                is FirebaseAuthInvalidCredentialsException -> signInState.value =
                    SignInState.CREDENTIAL_ERROR

                else -> signInState.value = SignInState.CREDENTIALS_RESET_ERR
            }
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        try {
            suspendCoroutine<Unit> { continuation ->
                firebaseAuth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        signInState.value = SignInState.CREDENTIALS_RESET_REQ
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            signInError.value = e.message
            signInState.value = SignInState.CREDENTIALS_RESET_ERR
        }
    }

    override suspend fun forChangePassword(
        email: String, oldPassword: String
    ) {
        reAuthenticateUser(email, oldPassword)
    }
}

data class FireStoreUser(
    val auto_refine: Boolean = false, val auto_save: Boolean = false, val name: String = ""
)