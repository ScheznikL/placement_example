package com.endofjanuary.placement_example.repo

import android.util.Log
import com.endofjanuary.placement_example.data.models.User
import com.endofjanuary.placement_example.utils.Resource
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

class AuthenticationRepoImpl(
    private val firebaseAuth: FirebaseAuth, private val fireStoreDB: FirebaseFirestore
) : AuthenticationRepo {

    private lateinit var userDoc: DocumentReference
    private lateinit var user: FirebaseUser
    private lateinit var docStateListener: ListenerRegistration


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
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d("signUp", "createUserWithEmail:success")
                        user = firebaseAuth.currentUser!!
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("signUp", "createUserWithEmail:failure", task.exception)
                        throw Exception(task.exception)
                    }
                }.await()

            verifyEmail()
//            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
//            user = authResult.user!!
//            signInState.value = SignInState.VERIFYING_EMAIL

        } catch (e: Exception) {
            signInError.value = e.message
            // Timber.d(e.toString())
            when (e) {
                is FirebaseAuthInvalidUserException -> signInState.value =
                    SignInState.USER_NOT_FOUND

                is FirebaseAuthInvalidCredentialsException -> signInState.value =
                    SignInState.CREDENTIAL_ERROR

                else -> signInState.value = SignInState.CREDENTIAL_ERROR
            }
        }
    }


    override suspend fun verifyEmail() {
        try {
//            val actionCodeSettings = actionCodeSettings {
//                url = "https://github.com/santansarah"
//                handleCodeInApp
//            }
            firebaseAuth.currentUser!!.sendEmailVerification().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("email verify", "Email sent.")
                    signInState.value = SignInState.VERIFYING_EMAIL
                    // signInState.value = SignInState.AUTHORIZED
                } else {
                    throw Exception(task.exception)
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
        docStateListener.remove()
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

    private fun getUserProfileData() {
        userDoc.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w("FIRESTORE get User", "Listen failed.", e)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                Log.d("FIRESTORE get User", "Current data: ${snapshot.data to FireStoreUser}")
            } else {
                Log.d("FIRESTORE get User", "Current data: null")
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
                def.complete(Resource.Success("Document has been saved!"))
            }.addOnFailureListener { e ->
                Log.w("FIRESTORE upd", "Error adding document", e)
                throw Exception(e.message)
            }
            return def.await()
        } catch (e: Exception) {
            return Resource.Error("Error adding document ${e.message}")
        }
    }

}

object FireStoreUser {
    val auto_refine: Boolean = false
    val auto_save: Boolean = false
    val name: String = ""
}