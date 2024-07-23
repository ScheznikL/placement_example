package com.endofjanuary.placement_example.domain.usecase.auth_reg

import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo
import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo

class SignOutUseCase(
    private val authenticationRepo: AuthenticationRepo,
    private val modelRoomRepo: ModelsRepo,
    private val dataStoreRepo: DataStoreRepo,
) {
    suspend fun signOut(temp: Boolean = false) {
        if (!temp) {
            modelRoomRepo.deleteAll()
            dataStoreRepo.clearDataStore()
            authenticationRepo.signOut()
        } else {
            tempSignOut()
        }
    }
    private suspend fun tempSignOut() {
        authenticationRepo.signOut()
    }
}