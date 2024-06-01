package com.endofjanuary.placement_example.user_cabinet

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.repo.AuthenticationRepo
import com.endofjanuary.placement_example.repo.DataStoreRepo
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class UserProfileViewModel(
    private val authenticationRepo: AuthenticationRepo,
    private val modelRoomRepo: ModelsRepo,
    private val dataStoreRepo: DataStoreRepo,
) : ViewModel() {

    val signInState = authenticationRepo.signInState
    val currentUser = authenticationRepo.currentUser(viewModelScope)

    private val _clearDataError = modelRoomRepo.clearModelsTableError
    val clearDataError: StateFlow<String>
        get() = _clearDataError

    private val _clearDataStoreError = dataStoreRepo.dataStoreState
    val clearDataStoreError: StateFlow<String>
        get() = _clearDataStoreError


    private val _state = MutableStateFlow(UserProfileViewState())
    val state: StateFlow<UserProfileViewState>
        get() = _state

    /*    val displayNameInput = MutableStateFlow<String?>(null)
        val autoSaveModel = MutableStateFlow<Boolean?>(false)
        val autoRefineModel = MutableStateFlow<Boolean?>(false)*/

    val error = mutableStateOf("")

    init {
        viewModelScope.launch(Dispatchers.IO) {
/*            currentUser.collect {
                _state.value = UserProfileViewState(
                    displayName = it?.displayName ?: "",
                    email = it?.email ?: "",
                    autoRefineModel = it?.autoRefineModel ?: false,
                    autoSaveModel = it?.autoSaveModel ?: false,
                    id = it?.id ?: "",
                    error = if (_clearDataError.value.isNotEmpty()) _clearDataError.value else if (_clearDataStoreError.value.isNotEmpty()) _clearDataStoreError.value else ""
                )
            }
            */
            combine(
                currentUser,
                _clearDataError,
                _clearDataStoreError
            ) { user, clearDataError, clearDataStoreError ->
                UserProfileViewState(
                    displayName = user?.displayName ?: "",
                    email = user?.email ?: "",
                    autoRefineModel = user?.autoRefineModel ?: false,
                    autoSaveModel = user?.autoSaveModel ?: false,
                    id = user?.id ?: "",
                    error = if (clearDataError.isNotEmpty()) clearDataError else if (clearDataStoreError.isNotEmpty()) clearDataStoreError else ""
                )
            }.collect { userProfileViewState ->
                _state.value = userProfileViewState
            }
        }
    }
/*


* */
    fun onSignOut() {
        viewModelScope.launch(Dispatchers.IO) {
            modelRoomRepo.deleteAll()
            /*  dataStore.data.collect {
                  if (it.lastModelsList.size > 0) {
                      dataStore.updateData { current ->
                          current.toBuilder().clear().build()
                      }
                  }
              }*/
            dataStoreRepo.clearDataStore()
            authenticationRepo.signOut()
        }
    }

    fun verifyEmail() {
        viewModelScope.launch { authenticationRepo.verifyEmail() }
    }


    private fun changePassword(email: String, newPassword: String, oldPassword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            authenticationRepo.askForChangePassword(
                email,
                oldPassword
            )
        }
    }

    fun updateUserData(refine: Boolean = false, save: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            //currentUser.collectLatest {
            val result = authenticationRepo.updateUserProfileData(
                //userName = displayNameInput.value ?: "",
                userName = _state.value.displayName.lowercase().replaceFirstChar(Char::titlecase),
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
    val id: String = "",
    val error: String = ""
)