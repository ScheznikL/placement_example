package com.endofjanuary.placement_example.repo

import android.util.Log
import com.endofjanuary.placement_example.data.models.User
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

class AuthenticationRepoImpl(
    private val firebaseAuth: FirebaseAuth, private val fireStoreDB: FirebaseFirestore
) : AuthenticationRepo {

    private lateinit var userDoc: DocumentReference
    private lateinit var user: FirebaseUser
    private var docStateListener: ListenerRegistration? = null


    override val signInError = MutableStateFlow<String?>(null)
    override val signInState = MutableStateFlow(SignInState.NOT_SIGNED_IN)


    /*    override val currentUser: User?
            get() = firebaseAuth.currentUser?.let {
                Log.d("user", "${it.isEmailVerified}")

                User(
                    email = it.email!!,
                    displayName = it.displayName,
                    profileUrl = it.photoUrl?.path,
                    phoneNumber = it.phoneNumber,
                    isEmailVerified = it.isEmailVerified
                )
            }*/

    override fun currentUser(scope: CoroutineScope): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            val currentUser = auth.currentUser
            if (currentUser != null) {
                userDoc = fireStoreDB.document("/users/${currentUser.uid}")

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
                Log.w("FIRESTORE get User", "Listen failed.", e)
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
            docStateListener.remove() // Ensure we remove the listener on close
        }
    }

    override suspend fun signIn(email: String, password: String) {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null) signInState.value = SignInState.AUTHORIZED
            else signInState.value = SignInState.CREDENTIAL_ERROR
        } catch (e: Exception) {
            signInError.value = e.message
            // Timber.d(e.toString())
            when (e) {
                is FirebaseAuthInvalidUserException -> signInState.value =
                    SignInState.USER_NOT_FOUND

                is FirebaseAuthInvalidCredentialsException -> signInState.value =
                    SignInState.CREDENTIAL_ERROR

                is FirebaseAuthUserCollisionException -> signInState.value =
                    SignInState.USER_COLLISION

                else -> signInState.value = SignInState.CREDENTIAL_ERROR
            }
        }
    }

    override suspend fun signUp(email: String, password: String) {
        try {
            val result = suspendCoroutine<AuthResult> { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d("signUp", "createUserWithEmail:success")
                            continuation.resume(task.result)
                        } else {
                            Log.w("signUp", "createUserWithEmail:failure", task.exception)
                            continuation.resumeWithException(
                                task.exception ?: Exception("Unknown error")
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
//            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//            user = authResult.user!!
//            signInState.value = SignInState.VERIFYING_EMAIL

        } catch (e: Exception) {
            signInError.value = e.message
            // Timber.d(e.toString())
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
                        Log.d("email verify", "Email sent.")
                        signInState.value = SignInState.VERIFYING_EMAIL
                        continuation.resume(task.result)
                    } else {
                        Log.w("email verify", "sendEmailVerification:failure", task.exception)
                        continuation.resumeWithException(
                            task.exception ?: Exception("Unknown error")
                        )
                    }
                }
            }
            //  firebaseAuth.currentUser?.sendEmailVerification()?.await()
        } catch (e: Exception) {
            Log.e("email verify", "error: ${e.message}")
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
            val user = hashMapOf(
                "auto_refine" to (refine),
                "auto_save" to (save),
                "name" to userName,
            )
            userDoc.set(user).addOnSuccessListener {
                Log.d("FIRESTORE upd", "Document has been updated!")
                signInState.value = SignInState.AUTHORIZED
                def.complete(Resource.Success("Document has been saved!"))
            }.addOnFailureListener { e ->
                signInState.value = SignInState.NOT_SIGNED_IN
                Log.w("FIRESTORE upd", "Error adding document", e)
                throw Exception(e.message)
            }
            return def.await()
        } catch (e: Exception) {
            return Resource.Error("Error adding document ${e.message}")
        }
    }

    override suspend fun reAuthenticateUser(email: String, password: String) {
        val user = firebaseAuth.currentUser!!

        val credential = EmailAuthProvider.getCredential(email, password)

        try {
            suspendCoroutine<Unit> { continuation ->
                user.reauthenticate(credential)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Log.d(
                                "FIREBASE reauth",
                                "User re-authenticated. Pas:$password"
                            )
                            signInState.value = SignInState.REAUTHORIZED
                            continuation.resume(Unit)

                        } else {
                            continuation.resumeWithException(
                                task.exception ?: Exception("Unknown error")
                            )
                        }
                    }
            }
            if (signInState.value == SignInState.REAUTHORIZED)
                sendPasswordResetEmail(email)
        } catch (e: Exception) {
            Log.d("FIRESTORE reauth ex", e.message.toString())
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
                firebaseAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            signInState.value = SignInState.CREDENTIALS_RESET_REQ
                            Log.d("FIREBASE changePassword", "Email sent.")
                        } else {
                            Log.w("FIREBASE", "changePassword:failure", task.exception)
                            continuation.resumeWithException(
                                task.exception ?: Exception("Unknown error")
                            )
                        }
                    }
            }
        } catch (e: Exception) {
            Log.d("FIREBASE ex changePassword", e.message.toString())
            signInError.value = e.message
            signInState.value = SignInState.CREDENTIALS_RESET_ERR
        }
    }

    override suspend fun forChangePassword(
        email: String,
        oldPassword: String
    ) {
        reAuthenticateUser(email, oldPassword)
     /*   if (signInState.value == SignInState.REAUTHORIZED) {
            sendPasswordResetEmail(email)
        } else {
            Log.w("FIREBASE forChangePassword", "else")
            return
        }*/
    }
}

object FireStoreUser {
    val auto_refine: Boolean = false
    val auto_save: Boolean = false
    val name: String = ""
}