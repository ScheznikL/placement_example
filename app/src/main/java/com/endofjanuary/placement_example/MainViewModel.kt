package com.endofjanuary.placement_example

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.remote.meshy.request.PostRefine
import com.endofjanuary.placement_example.data.remote.meshy.responses.ProgressStatus
import com.endofjanuary.placement_example.data.remote.meshy.responses.Refine3dModel
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.domain.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.repo.AuthenticationRepo
import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.MeshyRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.domain.usecase.models_act.GenerateModelFromImageUseCase
import com.endofjanuary.placement_example.domain.usecase.models_act.GenerateModelFromTextUseCase
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL

class MainViewModel(
    private val meshyRepository: MeshyRepo,
    private val modelRoom: ModelsRepo,
    private val authenticationRepo: AuthenticationRepo,
    private val dataStoreRepo: DataStoreRepo,
    private val context: Context,
    private val generateModelFromText: GenerateModelFromTextUseCase,
    private val generateModelFromImageUseCase: GenerateModelFromImageUseCase
) : ViewModel() {

    private val _currentUser = authenticationRepo.currentUser(viewModelScope)
    private val _dataStoreState = dataStoreRepo.dataStoreState

    val modelWorkInfo = generateModelFromText.workerInfo.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(),
        emptyList()
    )

    companion object {
        const val CHANNEL_NEW_MODEL = "model_download"
        const val NOTIFICATION_ID = 100
        const val NOTIFICATION_PROGRESS_MAX = 100
    }

    val autoRefine = mutableStateOf(false)
    val autoSave = mutableStateOf(false)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _currentUser.collectLatest {
                autoRefine.value = it?.autoRefineModel ?: false
                autoSave.value = it?.autoSaveModel ?: false
            }

        }
    }


    private val _byteArrayState: MutableState<Resource<Boolean>> = mutableStateOf(Resource.None())


    var model = mutableStateOf(ModelEntry())
    // private var postId: MutableState<PostId?> = mutableStateOf(PostId(""))

    val isLoading = mutableStateOf(false)
    val isSuccess = mutableStateOf<Pair<String, Long>?>(null)
    //val progress: MutableState<Int?> = mutableStateOf(null)

    val progress = generateModelFromImageUseCase.progress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), null)

    val loadError = mutableStateOf<String?>(null)
    private var isSavedSuccess = mutableStateOf<Resource<Long>>(Resource.None())

    private var isUpdateSuccess = mutableStateOf<Resource<Int>>(Resource.None())


    val modelPath = mutableStateOf<String?>(null)
    val modelImageUrl = mutableStateOf<String?>(null)


    enum class TextureRichness {
        High, Medium, Low, None
    }

    fun autoRefine(textureRichness: TextureRichness) {
        if (autoRefine.value) {
            loadRefineModel(
                meshyId = model.value.meshyId,
                textureRichness = textureRichness,
                overwrite = false,
                id = 0
            )
        }
    }

    fun loadRefineModel(
        meshyId: String,
        id: Int,
        textureRichness: TextureRichness,
        overwrite: Boolean
    ) {
        isLoading.value = true

        viewModelScope.launch(Dispatchers.IO) {
            val result = meshyRepository.postRefine(
                PostRefine(
                    preview_task_id = meshyId, textureRichness.name.lowercase()
                )
            )
            when (result) {
                is Resource.Success -> {

                    if (result.data != null) {

                        var refine = getRefine(result.data.result)

                        when (refine) {
                            is Resource.Error -> {
                                isLoading.value = false
                                loadError.value = refine.message!!
                                showNotification(NotificationType.ERROR, refine.message!!)
                            }

                            is Resource.Success -> {

                                while (refine.data!!.status == ProgressStatus.PENDING.toString() || refine.data!!.status == ProgressStatus.IN_PROGRESS.toString()) {

                                    delay(40000)
                                    refine = getRefine(result.data.result)

                                    if (refine is Resource.Error) {
                                        isLoading.value = false
                                        loadError.value = refine.message!!
                                    }

                                }
                                if (refine.data!!.status == ProgressStatus.SUCCEEDED.toString()) {

                                    model.value = ResponseToModelEntryConverter().toModelEntry(
                                        refineModel = refine.data
                                    )
                                    if (!overwrite) {
                                        saveByteInstancedModel(
                                            isFromText = true,
                                            isRefine = true,
                                        )
                                    } else {
                                        updateByteInstancedModel(
                                            oldMeshyId = meshyId,
                                            id = id
                                        )
                                    }
                                }
                                if (refine.data!!.status == ProgressStatus.FAILED.toString() || refine.data!!.status == ProgressStatus.EXPIRED.toString()) {
                                    loadError.value = context.getString(
                                        R.string.model_loading_error,
                                        refine.data!!.status,
                                        refine.data!!.task_error
                                    )
                                    isLoading.value = false
                                    showNotification(
                                        NotificationType.ERROR, refine.data!!.task_error.toString()
                                    )
                                }
                            }

                            else -> {}
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

    fun generateModelEntryFromImage(url: String, name: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            val result = generateModelFromImageUseCase.loadModelEntryFromImage(
                url = url,
                name = name
            )
            when (result) {
                is Resource.Success -> {
                    isLoading.value = false
                    if (result.data != null) {
                        model.value = result.data
                        saveByteInstancedModel(
                            isFromText = false,
                            isRefine = false,
                            imageDescription = name
                        )
                    }
                }

                is Resource.Error -> {
                    loadError.value = result.message
                    isLoading.value = false
                    showNotification(NotificationType.ERROR, result.message!!)
                }

                is Resource.Loading -> {
                    isLoading.value = true
                    showNotification(NotificationType.LOADING, "${result.data!!.progress}%")
                }

                else -> {
                    isLoading.value = true
                    showNotification(NotificationType.LOADING)
                }
            }
        }
    }

    fun generateModelEntryFromTextOnBackGround(prompt: String) {
        generateModelFromText.loadModelEntryFromTextOnBackGround(prompt)
    }

    fun generateModelEntryFromText(prompt: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = generateModelFromText.loadModelEntryFromText(
                prompt = prompt,
            )
            when (result) {
                is Resource.Success -> {
                    isLoading.value = false
                    if (result.data != null) {
                        model.value = result.data
                        if (!autoRefine.value) {
                            saveByteInstancedModel(
                                isFromText = true,
                                isRefine = false,
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    isLoading.value = false
                    loadError.value = result.message!!
                    showNotification(NotificationType.ERROR, result.message)
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
    }

    private suspend fun getRefine(id: String): Resource<Refine3dModel> {
        return meshyRepository.getRefine(id)
    }

    fun saveByteInstancedModel(
        isFromText: Boolean, isRefine: Boolean, imageDescription: String? = null,
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                //  scope.launch(Dispatchers.IO) {
                URL(model.value.modelImageUrl).openStream()
                    .use { inputStream: InputStream ->
                        val inStream = BufferedInputStream(inputStream)
                        val output = ByteArrayOutputStream()
                        inStream.copyTo(output)
                        isSavedSuccess.value = modelRoom.saveModel(
                            ModelEntity(
                                modelInstance = output.toByteArray(), //todo get rid of Instance in table
                                modelPath = model.value.modelPath,
                                modelDescription = imageDescription ?: model.value.modelDescription,
                                modelImageUrl = model.value.modelImageUrl,
                                isFromText = isFromText,
                                isRefine = isRefine,
                                meshyId = model.value.meshyId,
                                creationTime = System.currentTimeMillis()
                            )
                        )

                        when (isSavedSuccess.value) {
                            is Resource.Success -> {
                                saveLastModel(
                                    modelId = model.value.meshyId,
                                    modelImageUrl = model.value.modelImageUrl,
                                    id = isSavedSuccess.value.data?.toInt() ?: 0
                                )
                                if (_dataStoreState.value.isEmpty()) {
                                    modelImageUrl.value = model.value.modelImageUrl
                                    modelPath.value = model.value.modelPath

                                    isSuccess.value =
                                        Pair(model.value.meshyId, isSavedSuccess.value.data!!)
                                    showNotification(
                                        NotificationType.SUCCESS, model.value.modelDescription
                                    )
                                } else {
                                    loadError.value = _dataStoreState.value
                                }
                                isLoading.value = false
                            }

                            is Resource.Loading -> {
                                isLoading.value = true
                            }

                            is Resource.Error -> {
                                isLoading.value = false
                                loadError.value = isSavedSuccess.value.message
                                showNotification(
                                    NotificationType.ERROR, isSavedSuccess.value.message.toString()
                                )
                            }

                            else -> {}
                        }

                        output.close()
                        inputStream.close()
                    }
            }
            _byteArrayState.value = Resource.Success(true)
            showNotification(NotificationType.SUCCESS, model.value.modelDescription)
        } catch (e: Exception) {
            _byteArrayState.value = Resource.Error(e.message.toString())
            loadError.value = e.message
        }
    }

    private fun updateByteInstancedModel(
        oldMeshyId: String, id: Int
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                isUpdateSuccess.value = modelRoom.update(
                    modelPath = model.value.modelPath,
                    modelImageUrl = model.value.modelImageUrl,
                    meshyId = oldMeshyId,
                    isRefine = true
                )
                when (isUpdateSuccess.value) {
                    is Resource.Success -> {
                        saveLastModel(
                            modelId = oldMeshyId,
                            modelImageUrl = model.value.modelImageUrl,
                            id = id
                        )
                        if (_dataStoreState.value.isEmpty()) {
                            modelPath.value = model.value.modelPath
                            modelImageUrl.value = model.value.modelImageUrl
                            isSuccess.value =
                                Pair(model.value.meshyId, isUpdateSuccess.value.data!!.toLong())
                            showNotification(
                                NotificationType.SUCCESS, model.value.modelDescription
                            )
                        } else {
                            loadError.value = _dataStoreState.value
                        }
                        isLoading.value = false
                    }

                    is Resource.Error -> {
                        isLoading.value = false
                        loadError.value = isUpdateSuccess.value.message
                        showNotification(
                            NotificationType.ERROR, isUpdateSuccess.value.message.toString()
                        )
                    }

                    else -> {}
                }
            }
            _byteArrayState.value = Resource.Success(true)
            showNotification(NotificationType.SUCCESS, model.value.modelDescription)
        } catch (e: Exception) {
            _byteArrayState.value = Resource.Error(e.message.toString())
        }
    }

    private fun showNotification(type: NotificationType, description: String = "") { // todo clean
        with(NotificationManagerCompat.from(context)) {
            if (ActivityCompat.checkSelfPermission(
                    context, Manifest.permission.POST_NOTIFICATIONS
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
                        setContentTitle(context.getString(R.string.app_name))
                        setContentText(context.getString(R.string.notification_context) + description)
                        setStyle(
                            NotificationCompat.BigTextStyle()
                                .bigText(context.getString(R.string.big_style_notif_loading))
                        )
                        setSmallIcon(R.drawable.ic_blur)
                        priority = NotificationCompat.PRIORITY_MAX
                    }

                    val progressCurrent = 0
                    NotificationManagerCompat.from(context).apply {
                        // Issue the initial notification with zero progress.
                        builder.setProgress(NOTIFICATION_PROGRESS_MAX, progressCurrent, false)
                        notify(NOTIFICATION_ID, builder.build())

                        // Do the job that tracks the progress here.
                        // Usually, this is in a worker thread.
                        // To show progress, update PROGRESS_CURRENT and update the notification with:
                        // builder.setProgress(PROGRESS_MAX, PROGRESS_CURRENT, false);
                        // notificationManager.notify(notificationId, builder.build());

                        // When done, update the notification once more to remove the progress bar.
                        builder.setContentText(context.getString(R.string.notif_download_complete))
                            .setProgress(0, 0, false)
                        notify(NOTIFICATION_ID, builder.build())
                    }
                }

                NotificationType.ERROR -> {

                    val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL)
                        .setSmallIcon(R.drawable.ic_token)
                        .setContentTitle(context.getString(R.string.error))
                        .setContentText(description).setPriority(NotificationCompat.PRIORITY_HIGH)
                    // notificationId is a unique int for each notification that you must define.
                    notify(NOTIFICATION_ID, builder.build())
                }

                NotificationType.SUCCESS -> {

                    val builder = NotificationCompat.Builder(context, CHANNEL_NEW_MODEL)
                        .setSmallIcon(R.drawable.ic_center_focus).setContentTitle(
                            context.getString(
                                R.string.notif_success_title
                            )
                        ).setContentText(context.getString(R.string.success_notif_context))
                        .setStyle(NotificationCompat.BigTextStyle())
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                    // notificationId is a unique int for each notification that you must define.
                    notify(NOTIFICATION_ID, builder.build())
                }
            }

        }
    }

    private fun saveLastModel(modelId: String, id: Int, modelImageUrl: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //scope.launch(Dispatchers.IO) {
            dataStoreRepo.updateData(
                modelId = modelId, modelImageUrl = modelImageUrl, id = id
            )
        }
    }

}

enum class NotificationType {
    LOADING, ERROR, SUCCESS
}