package com.endofjanuary.placement_example

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromImage
import com.endofjanuary.placement_example.data.remote.meshy.request.PostFromText
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
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
        const val CHANNEL_NEW_MODEL = "model_success"
        const val NOTIFICATION_ID = 100
    }

    init {
        createNotificationChannel()
    }


    private val _byteArrayState: MutableState<Resource<Boolean>> =
        mutableStateOf(Resource.None())
    val byteArrayState: State<Resource<Boolean>>
        get() = _byteArrayState

    var model = mutableStateOf(ModelEntry())
    var postId = mutableStateOf(PostId(""))

    val isLoading = mutableStateOf(false)
    var isSuccess = mutableStateOf(false)
    val loadError = mutableStateOf("")
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_NEW_MODEL
            val descriptionText = "Information of new model"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_NEW_MODEL, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system.
            val notificationManager: NotificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun loadModelEntryFromImage(url: String, name: String = "") {
        Log.d("loadModel img", "Enter point")
        viewModelScope.launch(Dispatchers.IO) {
            val result = meshyRepository.postImageTo3D(PostFromImage(url))
            isLoading.value = true

            when (result) {
                is Resource.Success -> {
                    postId.value = result.data ?: PostId("")
                    if (result.data != null) {
                        Log.d("loadModelEntry_result Success id:", result.data.result)

                        var image3d = getImageTo3D(result.data.result)

                        when (image3d) {
                            is Resource.Error -> {
                                isSuccess.value = false
                                Log.d("eventualApiRes Error", image3d.toString())
                                showNotification(NotificationType.ERROR, image3d.message!!)
                            }

                            is Resource.Success -> {
                                Log.d(
                                    "eventualApiRes Success id:",
                                    image3d.data?.id ?: "none"
                                )
                                while (image3d.data!!.status == "PENDING" || image3d.data!!.status == "IN_PROGRESS") {
                                    Log.d(
                                        "loadModel while",
                                        "while entered with status ${image3d.data!!.status}"
                                    )
                                    delay(60000)
                                    image3d = getImageTo3D(result.data.result)

                                    if (image3d is Resource.Error) {
                                        throw Exception(image3d.message)
                                    }

                                }
                                if (image3d.data!!.status == "SUCCEEDED") {
                                    loadError.value = ""
                                    isLoading.value = false

                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(modelfromimage = image3d.data)
                                    saveByteInstancedModel(context = context, 1)

                                    isSuccess.value = true
                                }
                                if (image3d.data!!.status == "FAILED" || image3d.data!!.status == "EXPIRED") {
                                    loadError.value = image3d.data!!.status
                                    isLoading.value = false
                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(image3d.data)
                                }
                            }

                            else -> {
                                isLoading.value = true
                                isSuccess.value = false
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    loadError.value = result.message.toString()
                    isLoading.value = false
                    isSuccess.value = false
                }

                is Resource.Loading -> {
                    loadError.value = ""
                    isLoading.value = true
                    isSuccess.value = false
                }

                else -> {
                    isLoading.value = true
                    loadError.value = ""
                    isSuccess.value = false
                }
            }
        }
    }

    fun loadModelEntryFromText(prompt: String) {
        // viewModelScope.launch {
        //isLoading.value = true
        Log.d("loadModel", "Enter point")
        viewModelScope.launch {
            val result = meshyRepository.postTextTo3D(PostFromText(prompt, "preview"))
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
                                showNotification(NotificationType.ERROR, eventualApiRes.message!!)
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
                                    loadError.value = ""
                                    isLoading.value = false
                                    isSuccess.value = true

                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                    saveByteInstancedModel(context = context, 1)
                                    showNotification(NotificationType.SUCCESS, prompt)
                                }
                                if (eventualApiRes.data!!.status == "FAILED" || eventualApiRes.data!!.status == "EXPIRED") {
                                    loadError.value = eventualApiRes.data!!.status
                                    isLoading.value = false
                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                    showNotification(
                                        NotificationType.ERROR,
                                        eventualApiRes.data!!.status
                                    )
                                }
                            }

                            else -> {
                                showNotification(NotificationType.LOADING)
                            }
                        }
                    }
                }

                is Resource.Error -> {
                    showNotification(NotificationType.ERROR, result.message!!)

                }

                is Resource.Loading -> {
                    isLoading.value = true
                    showNotification(NotificationType.LOADING)
                }

                else -> {
                    isLoading.value = true
                    showNotification(NotificationType.LOADING)
                }
            }
        }
        showNotification(NotificationType.LOADING)
    }

    suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        return meshyRepository.getTextTo3D(id)
    }

    suspend fun getImageTo3D(id: String): Resource<ImageTo3DModel> {
        return meshyRepository.getImageTo3D(id)
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
                        modelImageUrl = model.value.modelImageUrl,
                        isFromText = true,
                        isRefine = false
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
                                modelImageUrl = model.value.modelImageUrl,
                                isFromText = true,
                                isRefine = false
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
            showNotification(NotificationType.SUCCESS, model.value.modelDescription)
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

    private fun showNotification(type: NotificationType, description: String = "") {
        Log.d("loadModel", "showNotification Enter point")
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                // ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                // public fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>,
                //                                        grantResults: IntArray)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return@with
            }
            when (type) {
                NotificationType.LOADING -> {
                    val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL).apply {
                        setContentTitle("Model is loading")
                        setContentText("Download in progress")
                        setStyle(NotificationCompat.BigTextStyle().bigText("loading"))
                        setSmallIcon(R.drawable.ic_blur)
                        setPriority(NotificationCompat.PRIORITY_MAX)
                    }
                    val PROGRESS_MAX = 100
                    val PROGRESS_CURRENT = 0
                    NotificationManagerCompat.from(context).apply {
                        // Issue the initial notification with zero progress.
                        builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false)
                        notify(NOTIFICATION_ID, builder.build())

                        // Do the job that tracks the progress here.
                        // Usually, this is in a worker thread.
                        // To show progress, update PROGRESS_CURRENT and update the notification with:
                        // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        // notificationManager.notify(notificationId, builder.build());

                        // When done, update the notification once more to remove the progress bar.
                        builder.setContentText("Download complete")
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID, builder.build())
                    }
                }

                NotificationType.ERROR -> {

                    val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL)
                        .setSmallIcon(R.drawable.ic_token)
                        .setContentTitle("Error!")
                        .setContentText(description)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                    // notificationId is a unique int for each notification that you must define.
                    notify(NOTIFICATION_ID, builder.build())
                }

                NotificationType.SUCCESS -> {

                    val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL)
                        .setSmallIcon(R.drawable.ic_center_focus)
                        .setContentTitle("New model!")
                        .setContentText("New model was successfully created")
                        .setStyle(NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                    // notificationId is a unique int for each notification that you must define.
                    notify(NOTIFICATION_ID, builder.build())
                }
            }

        }
    }
}

enum class NotificationType {
    LOADING,
    ERROR,
    SUCCESS
}