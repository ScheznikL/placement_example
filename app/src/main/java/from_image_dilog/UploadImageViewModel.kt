package from_image_dilog

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.endofjanuary.placement_example.repo.AWStorageRepo

class UploadImageViewModel(
    private val awStorageRepo: AWStorageRepo,
    //private val modelRoom: ModelsRepo
) : ViewModel() {
    var inputValueState = mutableStateOf("")
    var selectedUri = mutableStateOf(Uri.EMPTY)


    fun onPhotoPickerSelect(uri: Uri?) {
        if(uri !=null) selectedUri.value = uri

        Log.d("selected", uri?.toString() ?: "null uri")
    }
}