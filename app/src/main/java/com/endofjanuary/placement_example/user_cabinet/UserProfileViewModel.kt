package com.endofjanuary.placement_example.user_cabinet

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.repo.AuthenticationRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authenticationRepo: AuthenticationRepo,
) : ViewModel() {

    val signInState = authenticationRepo.signInState
    val currentUser = authenticationRepo.currentUser(viewModelScope)

    private val _state = MutableStateFlow(UserProfileViewState())
    val state: StateFlow<UserProfileViewState>
        get() = _state

/*    val displayNameInput = MutableStateFlow<String?>(null)
    val autoSaveModel = MutableStateFlow<Boolean?>(false)
    val autoRefineModel = MutableStateFlow<Boolean?>(false)*/

    val error = mutableStateOf("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
            currentUser.collect {
   /*             autoRefineModel.value = it!!.autoSaveModel
                autoSaveModel.value = it.autoRefineModel
                displayNameInput.value = it.displayName ?: ""*/

                _state.value = UserProfileViewState(
                    displayName = it!!.displayName ?: "",
                    email = it.email,
                    autoRefineModel = it.autoRefineModel,
                    autoSaveModel = it.autoSaveModel,
                    id = it.id
                )
            }

        }

    }

    fun onSinghOut() {
        viewModelScope.launch { authenticationRepo.signOut() }
    }

    fun verifyEmail() {
        viewModelScope.launch { authenticationRepo.verifyEmail() }
    }

    fun updateUserData(refine: Boolean = false, save: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            //currentUser.collectLatest {
                val result = authenticationRepo.updateUserProfileData(
                    //userName = displayNameInput.value ?: "",
                    userName = _state.value.displayName,
                    save = _state.value.autoSaveModel,
                    refine = _state.value.autoRefineModel,
                    /*save = autoSaveModel.value!!,
                    refine = autoRefineModel.value!!,*/
                    /*     save = save,
                         refine = refine,*/
                    userAuthID = _state.value.id,
                )
                when (result) {
                    is Resource.Error -> {
                        error.value = result.message.toString()
                    }

                    else -> {}
                }
           // }

        }
    }

    fun onSaveSwitch(checked: Boolean) {
        //autoSaveModel.value = checked //
        _state.value = _state.value.copy(
            autoSaveModel = checked
        )
        updateUserData()
    }

    fun onRefineSwitch(checked: Boolean) {
        //autoRefineModel.value = checked //
        _state.value = _state.value.copy(
            autoRefineModel = checked
        )
        updateUserData()
    }

    fun onNameChange(name: String) {
        Log.d("FIRESTORE", "name changed $name")
        _state.value = _state.value.copy(
            displayName = name
        )
      //  updateUserData()
    }
}

data class UserProfileViewState(
    val displayName: String = "",
    val email: String = "",
    val autoSaveModel: Boolean = false,
    val autoRefineModel: Boolean = false,
    val id: String = ""
)