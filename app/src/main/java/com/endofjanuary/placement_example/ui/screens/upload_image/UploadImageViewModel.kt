package com.endofjanuary.placement_example.ui.screens.upload_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.domain.usecase.GetPreassignedUrlUseCase
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class UploadImageViewModel(
    private val getPreassignedUrlUseCase: GetPreassignedUrlUseCase
) : ViewModel() {
    var inputValueState = mutableStateOf("")
    var image: MutableState<Uri?> = mutableStateOf(null)
    var photo: MutableState<Bitmap?> = mutableStateOf(null)
    var presignedUrl: MutableState<String?> = mutableStateOf(null)

    var isUploading = mutableStateOf(false)
    var isUploadingError = mutableStateOf("")

    fun onPhotoPickerSelect(uri: Uri?) {
        if (uri != null) image.value = uri
    }
    fun getPresignedUrl(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = getPreassignedUrlUseCase.getPresignedUrl(
                context = context,
                image = image.value,
                photo = photo.value
            )
            when (result) {
                is Resource.Error -> {
                    isUploadingError.value = result.message.toString()
                    isUploading.value = false
                }

                is Resource.Loading -> isUploading.value = true
                is Resource.None -> {}
                is Resource.Success -> {
                    isUploading.value = false
                    presignedUrl.value = result.data!!
                }

                else -> {
                    isUploadingError.value = result.toString()
                    isUploading.value = false
                }
            }
        }
    }
    fun pickImage(pickImage: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>) {
        pickImage.launch(
            PickVisualMediaRequest(
                ActivityResultContracts.PickVisualMedia.ImageOnly
            )
        )
    }
    fun onTakePhoto(bitmap: Bitmap) {
        photo.value = bitmap
    }
}