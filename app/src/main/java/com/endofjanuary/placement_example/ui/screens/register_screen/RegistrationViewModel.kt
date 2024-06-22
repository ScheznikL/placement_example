package com.endofjanuary.placement_example.ui.screens.register_screen

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class RegistrationViewModel(
    private val authenticationRepo: AuthenticationRepo,
) : ViewModel() {

    var emailValueState = mutableStateOf("")
    var passwordValueState = mutableStateOf("")
    var confirmPasswordValueState = mutableStateOf("")

    val isEmailError = mutableStateOf(false)
    val isPasswordError = mutableStateOf(false)

    val isConfirmPasswordError = mutableStateOf(false)

    val isRegister = mutableStateOf(false)

    val currentUser = authenticationRepo.currentUser(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val signInError = authenticationRepo.signInError
    val signInState = authenticationRepo.signInState
    val wrongPasswordError = authenticationRepo.wrongPasswordError

init {
    viewModelScope.launch(){
        wrongPasswordError.collect {
           isPasswordError.value = it == true
        }
    }
}
    fun onSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authenticationRepo.signIn(emailValueState.value, passwordValueState.value)
        }
    }

    fun onSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepo.signUp(emailValueState.value, passwordValueState.value)
        }
    }

    private val passwordPattern =
        Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")

    fun onPasswordValueChanged() {
        isPasswordError.value =
            !passwordPattern.matches(passwordValueState.value) || passwordValueState.value.isBlank()
    }

    fun onEmailValueChanged() {
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(emailValueState.value)
            .matches() || emailValueState.value.isBlank()
    }

    fun onConfirmPasswordChanged() {
        isConfirmPasswordError.value =
            confirmPasswordValueState.value != passwordValueState.value
    }


}


data class UserUIState(
    val email: String = "",
    val password: String = "",
)