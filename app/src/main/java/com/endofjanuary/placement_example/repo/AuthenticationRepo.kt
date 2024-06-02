package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.data.models.User
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

interface AuthenticationRepo {
    //val currentUser: User?
    val signInError: MutableStateFlow<String?>
    val signInState: MutableStateFlow<SignInState>
    fun currentUser(scope: CoroutineScope): Flow<User?>
    suspend fun signIn(email: String, password: String)
    suspend fun verifyEmail()
    suspend fun signUp(email: String, password: String)
    suspend fun signOut()
    suspend fun reloadUser()
    suspend fun updateUserProfileData(
        userName: String,
        refine: Boolean,
        save: Boolean,
        userAuthID: String,
    ): Resource<String>
    suspend fun reAuthenticateUser(email: String, password: String)
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun forChangePassword(email: String, oldPassword: String)
}

enum class SignInState {
    NOT_SIGNED_IN,
    AUTHORIZED,
    CREDENTIAL_ERROR,
    USER_NOT_FOUND,
    USER_COLLISION,
    CREDENTIALS_RESET_REQ,
    CREDENTIALS_RESET_ERR,
    CREDENTIALS_RESET,
    REAUTHORIZED,
    VERIFY_FAILED,
    VERIFYING_EMAIL,
}
