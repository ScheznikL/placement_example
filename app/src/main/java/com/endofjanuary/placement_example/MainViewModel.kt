package com.endofjanuary.placement_example

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.remote.meshy.request.Post
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.TextTo3DModel
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import io.github.sceneview.loaders.ModelLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

class MainViewModel(
    private val meshyRepository: MeshyRepo,
    private val modelRoom: ModelsRepo,
    private val context: Context
) : ViewModel() {
    //    init {
//        loadModelEntry()
//    }
    companion object {
        private const val CHANNEL_NEW_MODEL = "model_success"
    }

    private val _byteArrayState: MutableState<Resource<Boolean>> =
        mutableStateOf(Resource.None())
    val byteArrayState: State<Resource<Boolean>>
        get() = _byteArrayState

    var model = mutableStateOf(ModelEntry())
    var postId = mutableStateOf(PostId(""))

    var builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL)
        .setSmallIcon(R.drawable.ic_center_focus)
        .setContentTitle("My notification")
        .setContentText("Much longer text that cannot fit one line...")
        .setStyle(
            NotificationCompat.BigTextStyle()
                .bigText("Much longer text that cannot fit one line...")
        )
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NEW_MODEL
            val descriptionText = "new model successfully added"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_NEW_MODEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    suspend fun loadModelEntry(prompt: String): Resource<ModelEntry> {
        // viewModelScope.launch {
        //isLoading.value = true
        Log.d("loadModel", "Enter point")
        val result = meshyRepository.postTextTo3D(Post(prompt, "preview"))
        when (result) {
            is Resource.Success -> {
                /* loadError.value = ""
                 isLoading.value = false*/
                postId.value = result.data ?: PostId("")
                if (result.data != null) {
                    Log.d("loadModelEntry_result Success id:", result.data.result)
                    var eventualApiRes = getTextTo3D(result.data.result)
                    when (eventualApiRes) {
                        is Resource.Error -> {
                            Log.d("eventualApiRes Error", eventualApiRes.toString())
                            /* loadError.value = eventualApiRes.message!!
                             isLoading.value = false*/
                            return Resource.Error(eventualApiRes.message!!)
                        }

                        is Resource.Success -> {
                            Log.d(
                                "eventualApiRes Success id:",
                                eventualApiRes.data?.id ?: "none"
                            )
                            while (eventualApiRes.data!!.status == "PENDING" || eventualApiRes.data!!.status == "IN_PROGRESS") {
                                Log.d(
                                    "loadModel while",
                                    "while entered with status ${eventualApiRes.data!!.status}"
                                )
                                delay(30000)
                                eventualApiRes = getTextTo3D(result.data.result)
                                // return Resource.Loading()
                            }
                            if (eventualApiRes.data!!.status == "SUCCEEDED") {
                                /* loadError.value = ""
                                 isLoading.value = false*/

                                model.value =
                                    ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                return Resource.Success(
                                    ResponseToModelEntryConverter().toModelEntry(
                                        eventualApiRes.data
                                    )
                                )
                            }
                            if (eventualApiRes.data!!.status == "FAILED" || eventualApiRes.data!!.status == "EXPIRED") {
                                /* loadError.value = eventualApiRes.data!!.status
                                 isLoading.value = false*/
                                model.value =
                                    ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                return Resource.Error(eventualApiRes.data!!.status)
                            }
                        }

                        else -> {
                            return Resource.Loading()
                        }
                    }
                }
            }

            is Resource.Error -> {
                return Resource.Error(result.message!!)

            }

            is Resource.Loading -> {
                return Resource.Loading()
            }

            else -> {
                return Resource.Loading()
            }
        }
        return Resource.Loading()
    }

    suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        return meshyRepository.getTextTo3D(id)
    }

    suspend fun loadSaveGlbModel(
        modelLoader: ModelLoader
    ): Resource<Boolean> {
        return try {
            val result = modelLoader.loadInstancedModel(
                model.value.modelPath,
                MainActivity.kMaxModelInstances
            )
            viewModelScope.launch(context = Dispatchers.IO) {
                modelRoom.saveModel(
                    ModelEntity(
                        // modelInstance = result[0],
                        modelInstance = ByteArray(1), // TEMP
                        modelPath = model.value.modelPath,
                        modelDescription = model.value.modelDescription,
                        modelImageUrl = model.value.modelImageUrl
                    )
                )
            }
            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    //val context: Context = LocalContext.current
    suspend fun saveByteInstancedModel(
        context: Context,
        //fileLocation: String,
        count: Int,
        resourceResolver: (resourceFileName: String) -> String = {
            ModelLoader.getFolderPath(
                model.value.modelPath,
                it
            )
        }
    )/*: Resource<Boolean>*/ {
        // viewModelScope.launch {
        try {

            viewModelScope.launch(Dispatchers.IO) {
                URL(model.value.modelPath).openStream().use { inputStream: InputStream ->
                    val inStream = BufferedInputStream(inputStream)
                    ByteArrayOutputStream().use { output ->
                        inStream.copyTo(output)
                        val byteArr = output.toByteArray()
                        val byteBuffer = ByteBuffer.wrap(byteArr)
                        val rewound = byteBuffer.rewind()
                        modelRoom.saveModel(
                            ModelEntity(
                                // modelInstance = result[0], // TODO another ID
                                modelInstance = output.toByteArray(),
                                modelPath = model.value.modelPath,
                                modelDescription = model.value.modelDescription,
                                modelImageUrl = model.value.modelImageUrl
                            )
                        )
                        output.close()
                        inputStream.close()

//                val byteBuffer = ByteBuffer.wrap(byteArr)
//                val rewound = byteBuffer.rewind()
//                withContext(Dispatchers.Main) {
//                    modelViewer.destroyModel()
//                    modelViewer.loadModelGlb(rewound)
//                    modelViewer.transformToUnitCube()


//            val resultBuffer = context.loadFileBuffer(model.value.modelPath)
//            if (resultBuffer != null) {
//                val byteArray = ByteArray(resultBuffer.capacity())
//                resultBuffer.get(byteArray)
//                viewModelScope.launch(Dispatchers.IO){
//                    modelRoom.saveModel(
//                        ModelEntity(
//                            // modelInstance = result[0],
//                            modelInstance = byteArray,
//                            modelPath = model.value.modelPath,
//                            modelDescription = model.value.modelDescription,
//                            modelImageUrl = model.value.modelImageUrl
//                        )
//                    )
                    }
                }
            }
            _byteArrayState.value = Resource.Success(true)
            //return Resource.Success(true)
//                    } else
//                    {
//                        //return
//                        _byteArrayState.value = Resource.Error("Empty buffer")
//                    }
        } catch (e: Exception) {
            _byteArrayState.value = Resource.Error(e.message.toString())
        }
    }
}

/*
* to use
*
*  assetLoader.createInstancedAsset(buffer, this)!!.also { model ->
                    models += model
                    loadResourcesSuspended(model) { resourceFileName: String ->
                        context.loadFileBuffer(resourceResolver(resourceFileName))
                    }
                    // Release model since it will not be re-instantiated
//                model.releaseSourceData()
                }
* or
*     fun createInstancedModel(
        buffer: Buffer,
        count: Int,
        resourceResolver: (resourceFileName: String) -> Buffer? = { null }
    ): List<ModelInstance> =
        arrayOfNulls<ModelInstance>(count).apply {
            assetLoader.createInstancedAsset(buffer, this)!!.also { model ->
                models += model
                loadResources(model, resourceResolver)
                // Release model since it will not be re-instantiated
//                model.releaseSourceData()
            }
        }.filterNotNull()
*
* */