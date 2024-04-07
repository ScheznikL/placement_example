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
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import com.endofjanuary.placement_example.data.remote.meshy.responses.ImageTo3DModel
import com.endofjanuary.placement_example.data.remote.meshy.responses.PostId
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
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

    val isLoading = mutableStateOf(false) //todo change to percentage
    val isSuccess = mutableStateOf<Pair<String, Long>?>(null)

    // val modelId = mutableStateOf(0)
    val loadError = mutableStateOf<String?>(null)
    var isSavedSuccess = mutableStateOf<Resource<Long>>(Resource.None())

    //var isUpdateSuccess = mutableStateOf<Resource<Boolean>>(Resource.None())
    var isUpdateSuccess = mutableStateOf<Resource<Int>>(Resource.None())


    val modelPath = mutableStateOf<String?>(null)
    val modelImageUrl = mutableStateOf<String?>(null)
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

    enum class TextureRichness {
        High,
        Medium,
        Low,
        None
    }

    fun loadRefineModel(id: String, textureRichness: TextureRichness, overwrite: Boolean) {
        Log.d("loadModel refine", "Enter point $id - $textureRichness - $overwrite")
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val result = meshyRepository.postRefine(
                PostRefine(
                    preview_task_id = id,
                    textureRichness.name.lowercase()
                )
            )

            when (result) {
                is Resource.Success -> {
                    postId.value = result.data ?: PostId("")
                    if (result.data != null) {
                        Log.d("loadModelRefine_result Success id:", result.data.result)

                        var refine = getRefine(result.data.result)

                        when (refine) {
                            is Resource.Error -> {
                                isLoading.value = false
                                loadError.value = refine.message!!
                                showNotification(NotificationType.ERROR, refine.message!!)
                            }

                            is Resource.Success -> {
                                Log.d(
                                    "eventualApiRes Success id:",
                                    refine.data?.id ?: "none"
                                )
                                while (refine.data!!.status == "PENDING" || refine.data!!.status == "IN_PROGRESS") {
                                    Log.d(
                                        "loadModel R: while",
                                        "entered with status ${refine.data!!.status} ${refine.data!!.progress}%"
                                    )
                                    delay(40000)
                                    refine = getRefine(result.data.result)

                                    if (refine is Resource.Error) {
                                        isLoading.value = false
                                        loadError.value = refine.message!!
                                        throw Exception(refine.message)
                                    }

                                }
                                if (refine.data!!.status == "SUCCEEDED") {

                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(
                                            refineModel = refine.data
                                        )
                                    if (!overwrite) {
                                        saveByteInstancedModel(
                                            context = context,
                                            1,
                                            isFromText = true,
                                            isRefine = true
                                        )
                                    } else {
                                        updateByteInstancedModel(
                                            context = context,
                                            1,
                                            isFromText = true,
                                            isRefine = true,
                                            oldMeshyId = id
                                        )
                                    }
                                }
                                if (refine.data!!.status == "FAILED" || refine.data!!.status == "EXPIRED") {
                                    loadError.value = "Model loading is ${refine.data!!.status}"
                                    isLoading.value = false
                                }
                            }

                            is Resource.Loading -> {}
                            is Resource.None -> {}
                        }
                    }
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                is Resource.Loading -> {
                    isLoading.value = true
                }

                is Resource.None -> {}
            }
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
                                        "while entered with status ${image3d.data!!.status} ${image3d.data!!.progress}%"
                                    )
                                    delay(60000)
                                    image3d = getImageTo3D(result.data.result)

                                    if (image3d is Resource.Error) {
                                        throw Exception(image3d.message)
                                    }

                                }
                                if (image3d.data!!.status == "SUCCEEDED") {
                                    isLoading.value = false

                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(modelfromimage = image3d.data)
                                    saveByteInstancedModel(
                                        context = context,
                                        1,
                                        isFromText = false,
                                        isRefine = false
                                    )
                                    //   modelId.value = model.value.id
                                    //isSuccess.value = model.value.meshyId

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

                            }
                        }
                    }
                }

                is Resource.Error -> {
                    loadError.value = result.message.toString()
                    isLoading.value = false
                }

                is Resource.Loading -> {
                    loadError.value = ""
                    isLoading.value = true
                }

                else -> {
                    isLoading.value = true
                    loadError.value = ""
                }
            }
        }
    }

    fun loadModelEntryFromText(prompt: String) {
        Log.d("loadModel text", "Enter point")

        viewModelScope.launch {
            val result = meshyRepository.postTextTo3D(PostFromText(prompt, "preview"))
            when (result) {
                is Resource.Success -> {
                    postId.value = result.data ?: PostId("")
                    if (result.data != null) {
                        Log.d("loadModelEntry_result Success id:", result.data.result)
                        var eventualApiRes = getTextTo3D(result.data.result)
                        when (eventualApiRes) {
                            is Resource.Error -> {
                                Log.d("eventualApiRes Error", eventualApiRes.toString())
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
                                        "while entered with status ${eventualApiRes.data!!.status} ${eventualApiRes.data!!.progress}%"
                                    )
                                    delay(30000)
                                    eventualApiRes = getTextTo3D(result.data.result)
                                    if (eventualApiRes is Resource.Error) {
                                        showNotification(NotificationType.ERROR, result.message!!)
                                        break
                                    }
                                    // return Resource.Loading()
                                }
                                if (eventualApiRes.data!!.status == "SUCCEEDED") {
                                    isLoading.value = false
                                    model.value =
                                        ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)

                                    // All main prop - MESHYid set in saveByteInstancedModel
                                    saveByteInstancedModel(
                                        context = context,
                                        1,
                                        isFromText = true,
                                        isRefine = false
                                    )

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

    private suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        return meshyRepository.getTextTo3D(id)
    }

    private suspend fun getRefine(id: String): Resource<Refine3dModel> {
        return meshyRepository.getRefine(id)
    }

    private suspend fun getImageTo3D(id: String): Resource<ImageTo3DModel> {
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
                        isRefine = false,
                        meshyId = model.value.meshyId
                    )
                )
            }

            Resource.Success(true)

        } catch (e: Exception) {
            Resource.Error(e.message.toString())
        }
    }

    //val context: Context = LocalContext.current
    private fun saveByteInstancedModel(
        context: Context,
        //fileLocation: String,
        count: Int,
        isFromText: Boolean,
        isRefine: Boolean,
        resourceResolver: (resourceFileName: String) -> String = {
            ModelLoader.getFolderPath(
                model.value.modelPath,
                it
            )
        }

    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                URL(model.value.modelPath).openStream().use { inputStream: InputStream ->
                    val inStream = BufferedInputStream(inputStream)
                    val output = ByteArrayOutputStream()
                    inStream.copyTo(output)

//                        .use { output ->
//                        inStream.copyTo(output)
//                        val byteArr = output.toByteArray()
//                        val byteBuffer = ByteBuffer.wrap(byteArr)
//                        val rewound = byteBuffer.rewind()
                    isSavedSuccess.value = modelRoom.saveModel(
                        ModelEntity(
                            modelInstance = output.toByteArray(),
                            modelPath = model.value.modelPath,
                            modelDescription = model.value.modelDescription,
                            modelImageUrl = model.value.modelImageUrl,
                            isFromText = isFromText,
                            isRefine = isRefine,
                            meshyId = model.value.meshyId
                        )
                    )

                    when (isSavedSuccess.value) {
                        is Resource.Success -> {
                            isLoading.value = false
                            modelImageUrl.value = model.value.modelImageUrl // TODO Pair?
                            modelPath.value = model.value.modelPath
                            isSuccess.value = Pair(model.value.meshyId, isSavedSuccess.value.data!!)
                            showNotification(NotificationType.SUCCESS, model.value.modelDescription)
                        }

                        is Resource.Loading -> {

                        }

                        is Resource.None -> {}
                        is Resource.Error -> {
                            isLoading.value = false
                            loadError.value = "Saving Model Error"
                            showNotification(
                                NotificationType.ERROR,
                                "Saving Model Error"
                            )
                        }
                    }

                    Log.d("loadModel in S", "isSavedSuccess id= ${isSavedSuccess.value.data} ")

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
                    //          }
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

    val MODEL_PATH =
        "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018eb80f-70d4-7ac0-b540-346a5f83c255/output/model.glb?Expires=4866048000&Signature=QU0jukq87VxKR03eucrlQzvLbULXlX9RbbjSO~4pSwBy2DRp5OLiwn~qvIo7qC-AgUuFh5doENZU15sb6lJxyHmWHadVwI8NxlQaHLKlCDFTFF4t-3exoFyRfIxUqHwHWg0h7~cXtsrFQljqyCLsuX-YzEj7BTbhxEIKXByJaisWHNxR0pThljxD~AtYKrZN5HVJB8Mid2xBHSak48rh7ew1Wox8yxx8PmJgj9mD5u4kBLzDLcBSJ7gvfn~nY-lFKrypXPmNs-k5x0GjBoQL~SKgzoEXczuqGkNCp990uT1n7kQf7hboRgdGhLl7snnHLe1JyREVG9nwc~1ioWA6rw__&Key-Pair-Id=KL5I0C8H7HX83"
    val IMG_PATH =
        "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018eb80f-70d4-7ac0-b540-346a5f83c255/output/preview.png?Expires=4866048000&Signature=fRovk8~XV3lryrT~zKhpjvMQj45n4oO6jJTgOSvN5IxA3jCe2D80w91dHn~yZgwpYnPelE7dt6peGNrIJL-KYUKCEsLsNuov86K6E0M-aNWAz~1kq0GDmW5trLZHdDkNk6UFueVZgAfXTjpZdYjkZJCJRPTXCasuur2tXuILVIldvkocFRqeU4NZgwnotYhyhDbgxNY7ptJyoe~is8R~FVnfLTGWj5JP5JZjsSoI4LJyPU-A4yUXJVPWpAudTKWIwQRIBJJOscMz~-JhwkZwJAe1y2Pe3n9nIGXbhnn0WvfB4-rHQ4aYtjogt0cJMiETxD9ht-~nRUSAkhv5~IRxnw__&Key-Pair-Id=KL5I0C8H7HX83"

    private fun updateByteInstancedModel(
        context: Context,
        count: Int,
        isFromText: Boolean,
        isRefine: Boolean,
        oldMeshyId: String,
        resourceResolver: (resourceFileName: String) -> String = {
            ModelLoader.getFolderPath(
                model.value.modelPath,
                it
            )
        }
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {

                isUpdateSuccess.value = modelRoom.update(
                    modelPath = model.value.modelPath,
                    modelImageUrl = model.value.modelImageUrl,
                    meshyId = oldMeshyId,
                    isRefine = isRefine
                )
                when (isUpdateSuccess.value) {
                    is Resource.Success -> {
                        isLoading.value = false

                        //    modelId.value = model.value.id
                        Log.d(
                            "loadModel Upd",
                            "for ${isUpdateSuccess.value} updated ROOM ${model.value.modelPath} \n\r ${model.value.modelImageUrl} "
                        )
                        modelPath.value = model.value.modelPath
                        modelImageUrl.value = model.value.modelImageUrl
                        isSuccess.value =
                            Pair(model.value.meshyId, isUpdateSuccess.value.data!!.toLong())
                        showNotification(
                            NotificationType.SUCCESS,
                            model.value.modelDescription
                        )

                    }

                    is Resource.Loading -> {}
                    is Resource.None -> {}
                    is Resource.Error -> {
                        isLoading.value = false
                        loadError.value = isSavedSuccess.value.message
                        showNotification(
                            NotificationType.ERROR,
                            "Saving Model Error"
                        )
                    }
                }
//                URL(model.value.modelPath).openStream().use { inputStream: InputStream ->
//                    val inStream = BufferedInputStream(inputStream)
//                    ByteArrayOutputStream().use { output ->
//                        inStream.copyTo(output)
//                        val byteArr = output.toByteArray()
//                        val byteBuffer = ByteBuffer.wrap(byteArr)
//                        val rewound = byteBuffer.rewind()
//                        /*isSavedSuccess.value = modelRoom.update(
//                            modelPath = model.value.modelPath,
//                            modelImageUrl = model.value.modelImageUrl,
//                            meshyId = model.value.meshyId,
//                            isRefine = isRefine
//                        )*/
//
//
//                        output.close()
//                        inputStream.close()
//                    }
//                }
            }
            _byteArrayState.value = Resource.Success(true)
            showNotification(NotificationType.SUCCESS, model.value.modelDescription)
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