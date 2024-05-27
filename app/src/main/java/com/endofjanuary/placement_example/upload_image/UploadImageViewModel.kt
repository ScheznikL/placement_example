package com.endofjanuary.placement_example.upload_image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.repo.AWStorageRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException


class UploadImageViewModel(
    private val awStorageRepo: AWStorageRepo,
    //private val modelRoom: ModelsRepo
) : ViewModel() {
    var inputValueState = mutableStateOf("")
    var selectedUri: MutableState<Uri?> = mutableStateOf(null)
    var photo: MutableState<Bitmap?> = mutableStateOf(null)
    var presignedUrl = mutableStateOf("")

    var isUploading = mutableStateOf(false)
    var isUploadingError = mutableStateOf("")


    fun onPhotoPickerSelect(uri: Uri?) {
        if (uri != null) selectedUri.value = uri

        Log.d("selected", uri?.toString() ?: "null uri")
    }

    fun getPresignedUrl(context: Context) {
        /*
                val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(selectedUri.value, flag)
        */
        if (selectedUri.value != null) {
            isUploading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                val result = awStorageRepo.putObjectPresigned(
                    selectedUri.value!!.path!!,
                    context.contentResolver.openInputStream(selectedUri.value!!)!!
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
                }
            }
        }
    }

    fun getBitmapPresignedUrl(context: Context) {
        if (photo.value != null) {
            isUploading.value = true

            viewModelScope.launch(Dispatchers.IO) {
                val result = awStorageRepo.putObjectPresigned(
                    "photo",
                    ByteArrayInputStream(convertBitmapToByteArray(photo.value!!))
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
                }
            }
        }
    }

    fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        return try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            baos.toByteArray()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    Log.e(
                        "bitmap log",
                        "ByteArrayOutputStream was not closed"
                    )
                }
            }
        }
    }

    fun onTakePhoto(bitmap: Bitmap) {
        photo.value = bitmap
    }
}