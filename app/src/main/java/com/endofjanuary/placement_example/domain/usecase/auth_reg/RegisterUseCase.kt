package com.endofjanuary.placement_example.domain.usecase.auth_reg

import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo

class RegisterUseCase(
    private val authenticationRepo: AuthenticationRepo,
) {
    suspend fun signIn(email: String, password: String) {
        val result = authenticationRepo.signIn(email, password)
    }

    suspend fun signUp(email: String, password: String) {
        authenticationRepo.signUp(email, password)
    }

}