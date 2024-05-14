package com.endofjanuary.placement_example.user_cabinet

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.repo.AuthenticationRepo
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authenticationRepo: AuthenticationRepo,
) : ViewModel() {

    val signInState = authenticationRepo.signInState
    val currentUser = authenticationRepo.currentUser(viewModelScope)

    fun onSinghOut() {
        viewModelScope.launch { authenticationRepo.signOut() }
    }

    fun verifyEmail(){
        viewModelScope.launch { authenticationRepo.verifyEmail() }
    }
}