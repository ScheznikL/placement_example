package com.endofjanuary.placement_example.models_list_screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.Log
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
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class ModelsListViewModel(
    private val modelsRoom: ModelsRepo,
    private val dataStore: DataStore<LastModelsParam>,
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

    private val _selectedIds: MutableState<List<String>> = mutableStateOf(emptyList())
    val selectedIds: MutableState<List<String>> get() = _selectedIds

    private val _selectionMode: MutableState<Boolean> = mutableStateOf(false)
    val selectionMode: MutableState<Boolean> get() = _selectionMode


    fun selectModel(model: ModelEntry) {
        val modelMeshyId = model.meshyId
        if (!selectedIds.value.contains(modelMeshyId)) _selectedIds.value += model.meshyId
        else _selectedIds.value = _selectedIds.value.minus(modelMeshyId)
        Log.d("CHECK selectedIds", selectedIds.value.toString())
    }

    fun activateSelectionMode() {
        _selectionMode.value = true
    }

    fun deactivateSelectionMode() {
        _selectionMode.value = false
        _selectedIds.value = emptyList()
    }

    init {
        viewModelScope.launch {
            // Combines the latest value from each of the flows, allowing us to generate a
            // view state instance which only contains the latest values.
            combine(
                categories,
                selectedCategory,
            ) {
                    categories,
                    selectedCategory,
                ->
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

    private val _rewriteIndex = MutableStateFlow(-1) // TODO get rid of

    private fun findDeviantIndex(list: List<Long>): Int/*?*/ { // todo transfer to repoImpl
        // if (list.size < 2) return null
        val deviantIndex = list.zipWithNext().indexOfFirst { (a, b) ->
            b < a
        }
        return /*if (deviantIndex != -1) */(deviantIndex + 1) /*else null*/
    }

    fun saveLastModel(modelId: String, id: Int, modelImageUrl: String) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                Log.d(
                    "modelList dataStore model _rewrite",
                    "start start ${dataStore.data.map { it.lastModelsList }}"
                )
                dataStore.updateData { currentSettings ->
                    Log.d("modelList calc _rewrite", "start ${_rewriteIndex.value}")
                    if (currentSettings.lastModelsCount >= 10) {
                        Log.d("modelList calc _rewrite", ">= 10: ${_rewriteIndex.value}")
                        currentSettings.toBuilder()
                            .removeLastModels(0).addLastModels(
                                //count,
                                ModelAccessParam.newBuilder()
                                    .setModelId(modelId)
                                    .setId(id)
                                    .setModelImage(modelImageUrl)
                                    .setUnixTimestamp(System.currentTimeMillis())
                            )
                            .build()
                    } else {
                        _rewriteIndex.value = 0
                        currentSettings.toBuilder()
                            .addLastModels(
                                currentSettings.lastModelsCount,
                                ModelAccessParam.newBuilder()
                                    .setModelId(modelId)
                                    .setId(id)
                                    .setModelImage(modelImageUrl)
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
            //todo ConvertModelEntityToModelEntry
            try{
                val result = modelsRoom.getAllModelsFlow().mapNotNull { models ->
                    models.map { model ->
                        ModelEntry(
                            id = model.id,
                            modelPath = model.modelPath,
                            modelImageUrl = model.modelImageUrl,
                            modelDescription = model.modelDescription,
                            meshyId = model.meshyId,
                            isFromText = model.isFromText
                        )
                    }
                }
                result.collect {
                    Log.d("modelsRoom.getAllModelsFlow()", " ->>>  ${it.size}")
                    _textModelsListState.value = it.filter { model -> model.isFromText }
                    _imageModelsListState.value = it.filter { model -> !(model.isFromText) }
                }
                isLoading.value = false
            }catch (e : Exception){
                isLoading.value = false
                loadError.value = e.message.toString()
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

    private var integer = 1

    @RequiresApi(Build.VERSION_CODES.Q)
    fun insetModel() { // todo TEMP

        viewModelScope.launch(context = Dispatchers.IO) {
            modelsRoom.saveModel(
                ModelEntity(
                    modelInstance = ByteArray(1), // TEMP
                    modelPath = "_textModelsListState.value.first().modelPath",
                    modelDescription = "model.value.modelDescription",
                    modelImageUrl = "_textModelsListState.value.first().modelImageUrl",
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
    val errorMessage: String? = null,
) {
    constructor() : this(Category.FromText, Category.values().asList())
}