package com.endofjanuary.placement_example.ui.screens.models_list_screen

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.hasThreeDaysPassed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.launch

class ModelsListViewModel(
    private val modelsRoomRepo: ModelsRepo,
    private val dataStoreRepo: DataStoreRepo,
) : ViewModel() {

    val deletedModel = mutableStateOf<Resource<Int>>(Resource.None())

    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    private val selectedCategory = MutableStateFlow(Category.FromText)
    private val categories = MutableStateFlow(Category.entries.toList())

    private val _state = MutableStateFlow(ModelListViewState())
    val state: StateFlow<ModelListViewState>
        get() = _state

    fun onCategorySelected(category: Category) {
        selectedCategory.value = category
    }

    private var cachedModelsList = listOf<ModelEntry>()

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
            combine(
                categories,
                selectedCategory,
            ) {
                    categories,
                    selectedCategory,
                ->
                ModelListViewState(
                    selectedCategory, categories
                )
            }.catch { throwable ->
                loadError.value = throwable.message.toString()
            }.collect {
                _state.value = it
            }
        }
    }

    fun onSearch(query: String, isFromImage: Boolean) {
        val listToSearch = if (isSearchStarting) {
            if (!isFromImage) _textModelsListState.value else _imageModelsListState.value //
        } else {
            cachedModelsList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()) {

                if (!isFromImage) _textModelsListState.value =
                    cachedModelsList else _imageModelsListState.value = cachedModelsList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.modelDescription.contains(query.trim(), ignoreCase = true)
            }
            if (isSearchStarting) {
                cachedModelsList =
                    if (!isFromImage) _textModelsListState.value else _imageModelsListState.value
                isSearchStarting = false
            }
            if (!isFromImage) _textModelsListState.value =
                results else _imageModelsListState.value = results
            isSearching.value = true
        }
    }

    fun loadModels() {
        isLoading.value = true
        viewModelScope.launch(Dispatchers.IO) {

            try {
                val result = modelsRoomRepo.getAllModelsFlow().mapNotNull { models ->
                    models.map { model ->
                        ModelEntry(
                            id = model.id,
                            modelPath = model.modelPath,
                            modelImageUrl = model.modelImageUrl,
                            modelDescription = model.modelDescription,
                            meshyId = model.meshyId,
                            isFromText = model.isFromText,
                            isExpired = hasThreeDaysPassed(model.creationTime)
                        )
                    }
                }
                isLoading.value = false
                result.collect {
                    _textModelsListState.value = it.filter { model -> model.isFromText }
                    _imageModelsListState.value = it.filter { model -> !(model.isFromText) }
                }

            } catch (e: Exception) {
                isLoading.value = false
                loadError.value = e.message.toString()
            }
        }
    }

    private var integer = 1

    fun insetModel() { // todo TEMP

        viewModelScope.launch(context = Dispatchers.IO) {
            modelsRoomRepo.saveModel(
                ModelEntity(
                    modelInstance = ByteArray(1),
                    modelPath = "_textModelsListState.value.first().modelPath",
                    modelDescription = "model.value.modelDescription",
                    modelImageUrl = "_textModelsListState.value.first().modelImageUrl",
                    isFromText = true,
                    isRefine = false,
                    meshyId = "new${integer++}",
                    creationTime = System.currentTimeMillis()
                )
            )
        }
    }

    fun deleteModels() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val result: MutableList<Resource<Int>> = mutableListOf()

                selectedIds.value.forEach {
                    result += modelsRoomRepo.deleteModelById(it)
                    dataStoreRepo.removeModelById(it)
                }
                selectedIds.value = emptyList()
            }
        } catch (e: Exception) {
            _state.value = _state.value.copy(errorMessage = e.message.toString())
        }
    }

    fun deleteModel(model: ModelEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            val result = modelsRoomRepo.deleteModelById(model.meshyId)

            when (result) {
                is Resource.Error -> {
                    _state.value = _state.value.copy(errorMessage = result.message.toString())
                }

                else -> {
                    dataStoreRepo.removeModelById(model.meshyId)
                }
            }
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
    val selectedCategory: Category = Category.FromText,
    val categories: List<Category> = emptyList(),
    val errorMessage: String? = null,
) {
    constructor() : this(Category.FromText, Category.values().asList())
}