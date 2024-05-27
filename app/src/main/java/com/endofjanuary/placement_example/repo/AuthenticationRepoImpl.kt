package com.endofjanuary.placement_example.repo

import android.util.Log
import com.endofjanuary.placement_example.data.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class AuthenticationRepoImpl(
    private val firebaseAuth: FirebaseAuth
) : AuthenticationRepo {

    // private val firebaseAuth = Firebase.auth // todo move to di ?
    private lateinit var user: FirebaseUser
    private lateinit var verifyId: String

    override val signInError = MutableStateFlow<String?>(null)
    override val signInState = MutableStateFlow(SignInState.NOT_SIGNED_IN)


//    override val currentUser: User?
//        get() = firebaseAuth.currentUser?.let {
//            Log.d("user", "${it.isEmailVerified}")
//
//            User(
//                email = it.email!!,
//                displayName = it.displayName,
//                profileUrl = it.photoUrl?.path,
//                phoneNumber = it.phoneNumber,
//                isEmailVerified = it.isEmailVerified
//            )
//        }

    override fun currentUser(scope: CoroutineScope): Flow<User?> = callbackFlow {
        val authStateListener = FirebaseAuth.AuthStateListener { auth ->
            // Timber.d("auth listener called...")
            trySend(auth.currentUser?.let {
                signInState.value = SignInState.AUTHORIZED
                User(
                    email = it.email!!,
                    displayName = it.displayName,
                    profilePictureUrl = it.photoUrl?.path,
//                    phoneNumber = it.phoneNumber,
                    isEmailVerified = it.isEmailVerified
                )
            })
        }
        firebaseAuth.addAuthStateListener(authStateListener)
        awaitClose {
            firebaseAuth.removeAuthStateListener(authStateListener)
        }
    }

    override suspend fun signIn(email: String, password: String) {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            if (authResult.user != null)
                signInState.value = SignInState.AUTHORIZED
            else
                signInState.value = SignInState.CREDENTIAL_ERROR
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

    override suspend fun verifyEmail() {
        try {
//            val actionCodeSettings = actionCodeSettings {
//                url = "https://github.com/santansarah"
//                handleCodeInApp
//            }
            firebaseAuth.currentUser!!.sendEmailVerification()
                .addOnCompleteListener { task ->
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

    override suspend fun signOut() {
        signInState.value = SignInState.NOT_SIGNED_IN
        firebaseAuth.signOut()
    }

    override suspend fun reloadUser() {
        try {
            firebaseAuth.currentUser?.reload()
            if (firebaseAuth.currentUser?.isEmailVerified == true && signInState.value == SignInState.VERIFYING_EMAIL)
                signInState.value = SignInState.AUTHORIZED
        } catch (e: Exception) {
            signInError.value = e.message
            if (e is FirebaseAuthInvalidUserException) {
                signInState.value = SignInState.USER_NOT_FOUND
            }
        }
    }

}