package com.endofjanuary.placement_example

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.remote.request.Post
import com.endofjanuary.placement_example.data.remote.responses.RedusedAPIResp
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer
import javax.inject.Inject

@HiltViewModel
class UserModelViewModel @Inject constructor(
    private val repository: MeshyRepo
) : ViewModel() {

    private var curPage = 0

    var modelInfo = mutableStateOf<RedusedAPIResp?>(null)
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)

    init {
        loadUserModel()
    }

    fun loadUserModel() {
        postUserPrompt("lamp", "preview")
        /* todo download model save location*/
    }

    fun postUserPrompt(
        prompt: String,
        mode: String
    ): String? {

        var id: String = "";
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.postTextTo3D(Post(prompt = prompt, mode = mode))

            when (result) {
                is Resource.Success -> {
                    println(result.data)
                    id = result.data?.result ?: ""
                    loadError.value = ""
                    isLoading.value = false
                    getResult(id)
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                    id = result.data?.result ?: ""
                }

                is Resource.Loading -> {
                    id = result.data?.result ?: ""
                }
            }

        }
        return id
    }

    fun getResult(
        id: String,
    ) {
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getTextTo3D(id)

            when (result) {
                is Resource.Success -> {

                    println(result.data)

                    loadError.value = ""
                    isLoading.value = false
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                is Resource.Loading -> {

                }
            }
        }
    }

}


var url = "https://github.com/kelvinwatson/glb-files/raw/main/DamagedHelmet.glb"
/*runBlocking {
     async {
        /* customViewer!!.loadRemoteGlb(url)*/
     }
 }*/

suspend fun loadRemoteGlb(url: String) {
    GlobalScope.launch(Dispatchers.IO) {
        URL(url).openStream().use { inputStream: InputStream ->
            val inputStream = BufferedInputStream(inputStream)
            ByteArrayOutputStream().use { output ->
                inputStream.copyTo(output)
                val byteArr = output.toByteArray()
                val byteBuffer = ByteBuffer.wrap(byteArr)
                val rewound = byteBuffer.rewind()
                withContext(Dispatchers.Main) {
                    /*  modelViewer.destroyModel()
                      modelViewer.loadModelGlb(rewound)
                      modelViewer.transformToUnitCube()*/
                    output.close()
                    inputStream.close()
                }
            }
        }
    }
}
