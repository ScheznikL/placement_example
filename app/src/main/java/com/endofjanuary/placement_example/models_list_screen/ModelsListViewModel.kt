package com.endofjanuary.placement_example.models_list_screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.endofjanuary.placement_example.LastModelsParam
import com.endofjanuary.placement_example.ModelAccessParam
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class ModelsListViewModel(
    private val modelsRoom: ModelsRepo,
    private val dataStore: DataStore<LastModelsParam>
) : ViewModel() {


    val deletedModel = mutableStateOf<Resource<Int>>(Resource.None())

    //var modelsList = mutableStateOf<List<ModelEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    var isSearching = mutableStateOf(false)

    private val selectedCategory = MutableStateFlow(Category.FromText)
    private val categories = MutableStateFlow(Category.entries.toList())

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(ModelListViewState())
    val state: StateFlow<ModelListViewState>
        get() = _state

    fun onCategorySelected(category: Category) {
        selectedCategory.value = category
    }

    private val _textModelsListState = MutableStateFlow<List<ModelEntry>>(emptyList())
    val textModelsListState: StateFlow<List<ModelEntry>> = _textModelsListState

    private val _imageModelsListState = MutableStateFlow<List<ModelEntry>>(emptyList())
    val imageModelsListState: StateFlow<List<ModelEntry>> = _imageModelsListState

    private val _selectedIds: MutableState<Set<String>> = mutableStateOf(emptySet())
    val selectedIds: MutableState<Set<String>> get() = _selectedIds

    private val _selectedModel: MutableState<ModelEntry> = mutableStateOf(ModelEntry())
    val selectedModel: MutableState<ModelEntry> get() = _selectedModel

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                categories,
                selectedCategory,
            ) { categories,
                selectedCategory ->
                ModelListViewState(
                    selectedCategory,
                    categories
                )
            }.catch { throwable ->
                // TODO: emit a UI error here.
                throw throwable
            }.collect {
                _state.value = it
            }
        }
    }

    private val _rewrite = MutableStateFlow(-1)

    private fun findDeviantIndex(list: List<Long>): Int/*?*/ { // todo transfer to repoImpl
        // if (list.size < 2) return null
        val deviantIndex = list.zipWithNext().indexOfFirst { (a, b) ->
            b < a
        }
        return /*if (deviantIndex != -1) */(deviantIndex + 1) /*else null*/
    }

    private suspend fun checkExistingSaves() {
        //    viewModelScope.launch(Dispatchers.IO) {
        // check where to write - 10 is max
        dataStore.data.collectLatest { listOfLastModels ->
            if (listOfLastModels.lastModelsCount >= 10) {

                val increase = listOfLastModels.lastModelsList.zipWithNext { a, b ->
                    b.unixTimestamp > a.unixTimestamp
                }.all { it }

                if (increase) {// first or if all in increasing matter                    {
                    _rewrite.value = 0
                } else {
                    _rewrite.value =
                        findDeviantIndex(listOfLastModels.lastModelsList.map { it.unixTimestamp })
                    //    ?: 0
                }
            } else if (listOfLastModels.lastModelsCount == 0) {
                _rewrite.value = -1
            }
        }
    }

    fun saveLastModel(modelId: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                checkExistingSaves()

                // write

                dataStore.updateData { currentSettings ->
                    if (_rewrite.value != -1) {
                        currentSettings.toBuilder()
                            .addLastModels(
                                _rewrite.value,
                                ModelAccessParam.newBuilder()
                                    .setModelId(modelId)
                                    .setUnixTimestamp(System.currentTimeMillis())
                            )
                            .build()
                    } else {
                        currentSettings.toBuilder()
                            .addLastModels(
                                currentSettings.lastModelsCount + 1,
                                ModelAccessParam.newBuilder()
                                    .setModelId(modelId)
                                    .setUnixTimestamp(System.currentTimeMillis())
                            )
                            .build()
                    }
                }
            }
        } catch (e: Exception) {
            throw e
        }
    }


    fun loadModels() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            val result = modelsRoom.getAllModelsFlow()
            result.collect {
                it.forEach { modelEntity ->
                    if (modelEntity.isFromText) { //todo ConvertModelEntityToModelEntry
                        _textModelsListState.value += ModelEntry(
                            id = modelEntity.id,
                            modelPath = modelEntity.modelPath,
                            modelImageUrl = modelEntity.modelImageUrl,
                            modelDescription = modelEntity.modelDescription,
                            meshyId = modelEntity.meshyId
                        )
                    } else {
                        _imageModelsListState.value += ModelEntry(
                            id = modelEntity.id,
                            modelPath = modelEntity.modelPath,
                            modelImageUrl = modelEntity.modelImageUrl,
                            modelDescription = modelEntity.modelDescription,
                            meshyId = modelEntity.meshyId
                        )
                    }
                }
            }
//            when (result) {
//                is Resource.Success -> {
//
//                    loadError.value = ""
//                    isLoading.value = false
//
//                    if (!result.data.isNullOrEmpty()) {
//                        result.data!!.forEach { //TODO converter ?
////                            modelsList.value += ModelEntry(
////                                id = it.id,
////                                modelPath = it.modelPath,
////                                modelImageUrl = it.modelImageUrl,
////                                modelDescription = it.modelDescription
////                            )
//                            if (it.isFromText) {
//                                _textModelsListState.value += ModelEntry(
//                                    id = it.id,
//                                    modelPath = it.modelPath,
//                                    modelImageUrl = it.modelImageUrl,
//                                    modelDescription = it.modelDescription,
//                                    meshyId = it.meshyId
//                                )
//                            } else {
//                                _imageModelsListState.value += ModelEntry(
//                                    id = it.id,
//                                    modelPath = it.modelPath,
//                                    modelImageUrl = it.modelImageUrl,
//                                    modelDescription = it.modelDescription,
//                                    meshyId = it.meshyId
//                                )
//                            }
//                        }
//                    }
//                }
//
//                is Resource.Error -> {
//                    loadError.value = result.message!!
//                    isLoading.value = false
//                }
//
//                is Resource.Loading -> {
//                    loadError.value = ""
//                    isLoading.value = true
//                }
//
//                is Resource.None -> {
//                    loadError.value = ""
//                    isLoading.value = false
//                }
//            }
        }
    }

    private var integer = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun insetModel() { // todo TEMP

        viewModelScope.launch(context = Dispatchers.IO) {
            modelsRoom.saveModel(
                ModelEntity(
                    modelInstance = ByteArray(1), // TEMP
                    modelPath = _textModelsListState.value.first().modelPath,
                    modelDescription = "model.value.modelDescription",
                    modelImageUrl = _textModelsListState.value.first().modelImageUrl,
                    isFromText = true,
                    isRefine = false,
                    meshyId = "new${integer++}",
                )
            )
        }

    }

    fun deleteModels() {
        // val id = selectedIds.value.first()
        try {
//            if (isFromText) {
//                val indexToDelete = _textModelsListState.value.indexOfFirst { it.meshyId == id }
//                _textModelsListState.value -= _textModelsListState.value.elementAt(indexToDelete)
//            } else {
//                val indexToDelete = _imageModelsListState.value.indexOfFirst { it.meshyId == id }
//                _imageModelsListState.value -= _imageModelsListState.value.elementAt(indexToDelete)
//            }


            viewModelScope.launch(Dispatchers.IO) {
                val result: MutableList<Resource<Int>> = mutableListOf()

                selectedIds.value.forEach { result += modelsRoom.deleteModelById(it) }

                //TODO Loading - try - ...

            }

        } catch (e: Exception) { // TODO are two try bad
            deletedModel.value = Resource.Error(message = e.message.toString())
        }
    }

    fun deleteModel(model: ModelEntry) {
        try {
            if (model.isFromText) {
                _textModelsListState.value -= _textModelsListState.value.first { it.meshyId == model.meshyId }
            } else {
                _imageModelsListState.value -= _imageModelsListState.value.first { it.meshyId == model.meshyId }
            }

            viewModelScope.launch(Dispatchers.IO) {
                val result = modelsRoom.deleteModelById(model.meshyId)
                when (result) {
                    is Resource.Success -> {
                        deletedModel.value = result
                    }

                    is Resource.Error -> {
                        deletedModel.value = result
                    }

                    else -> {}
                }
            }
        } catch (e: Exception) { // TODO are two try bad
            deletedModel.value = Resource.Error(message = e.message.toString())
        }
    }

    fun calcDominantColor(drawable: Drawable, onFinish: (Color) -> Unit) {
        val bmp = (drawable as BitmapDrawable).bitmap.copy(Bitmap.Config.ARGB_8888, true)

        Palette.from(bmp).generate { palette ->
            palette?.dominantSwatch?.rgb?.let { colorValue ->
                onFinish(Color(colorValue))
            }
        }
    }
}

enum class Category {
    FromText, FromImage
}

data class ModelListViewState(
    //val featuredPodcasts: PersistentList<PodcastWithExtraInfo> = persistentListOf(),
    //  val refreshing: Boolean = false,
    val selectedCategory: Category = Category.FromText,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null
) {
    constructor() : this(Category.FromText, Category.values().asList())
}