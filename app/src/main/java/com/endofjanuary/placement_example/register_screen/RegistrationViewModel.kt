package com.endofjanuary.placement_example.register_screen

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.repo.AuthenticationRepo
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

    val currentUser = authenticationRepo.currentUser(viewModelScope)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val signInError = authenticationRepo.signInError
    val signInState = authenticationRepo.signInState

    //private val selectedCategory = MutableStateFlow(Category.FromText)
    fun onSignIn() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = authenticationRepo.signIn(emailValueState.value, passwordValueState.value)
            // Log.d("user: ${authenticationRepo.currentUser.toString()}")
        }
    }

    fun onSignUp() {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepo.signUp(emailValueState.value, passwordValueState.value)
            // Log.d("user: ${authenticationRepo.currentUser.toString()}")
        }
    }

    private val passwordPattern =
        Regex("^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$")

    fun onTextValueChanged(register: Boolean) { // TODO split password and email
        isPasswordError.value =
            !passwordPattern.matches(passwordValueState.value) || passwordValueState.value.isBlank()
        isEmailError.value = !Patterns.EMAIL_ADDRESS.matcher(emailValueState.value)
            .matches() || emailValueState.value.isBlank()

        if (register) {
            isConfirmPasswordError.value =
                confirmPasswordValueState.value != passwordValueState.value
        }
    }

//    private fun onVerifyEmail() {
//        viewModelScope.launch(Dispatchers.IO) {
//            authenticationRepo.verifyEmail()
//        }
//    }

    private fun validateSignIn(): Boolean { //todo
        return Patterns.EMAIL_ADDRESS.matcher(emailValueState.value)
            .matches() && passwordPattern.matches(
            passwordValueState.value
        )

    }
}


data class UserUIState(
    val email: String = "",
    val password: String = "",
//    val onEmailChanged: (String) -> Unit = {},
//    val onPasswordChanged: (String) -> Unit = {},
//    val onVerifyEmail: () -> Unit = {},
//    val onSignOut: () -> Unit = {}
)