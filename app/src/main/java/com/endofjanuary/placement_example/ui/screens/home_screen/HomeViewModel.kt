package com.endofjanuary.placement_example.ui.screens.home_screen

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.endofjanuary.placement_example.LastModelsParam
import com.endofjanuary.placement_example.ModelAccessParam
import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.convertToReadableFormat
import com.endofjanuary.placement_example.utils.hasThreeDaysPassed
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch


class HomeViewModel(
    private val modelsRoomRepo: ModelsRepo,
    private val dataStoreRepo: DataStoreRepo,
) : ViewModel() {

    val _dataStoreData: Flow<LastModelsParam> = dataStoreRepo.dataStoreData

    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state.asStateFlow()

    private var appStartupParamsCollectorJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            _dataStoreData.collectLatest { startupParams: LastModelsParam ->
                _state.update { currentState ->
                    currentState.copy(
                        lastModels = startupParams.lastModelsList.toHomeScreenModelList(),
                    )
                }
            }
        }

    }

    override fun onCleared() {
        appStartupParamsCollectorJob?.cancel()
        super.onCleared()
    }

    private fun List<ModelAccessParam>.toHomeScreenModelList(): List<HomeScreenModel> {
        return this.map {
            HomeScreenModel(
                modelId = it.modelId,
                id = it.id,
                timeStep = convertToReadableFormat(it.unixTimestamp),
                imageUrl = it.modelImage,
                isExpired = hasThreeDaysPassed(it.unixTimestamp)
            )
        }
    }

    fun clearLastModelPreview(context: Context) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                dataStoreRepo.clearModelsInDataStore()
            }
        } catch (e: Exception) {
            Toast.makeText(
                context, e.message, LENGTH_LONG
            ).show()
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

    fun deleteModel(meshyId: String?) {
        if (meshyId != null) {
            viewModelScope.launch(Dispatchers.IO) {
                val result = modelsRoomRepo.deleteModelById(meshyId)

                when (result) {
                    is Resource.Error -> {
                        _state.value = _state.value.copy(errorMessage = result.message.toString())
                    }

                    else -> {
                        val list = _state.value.lastModels?.filter { it.modelId != meshyId }
                        _state.value = _state.value.copy(
                            lastModels = list
                        )
                        dataStoreRepo.removeModelById(meshyId)
                    }
                }
            }
        }
    }
}

data class HomeViewState(
    val lastModels: List<HomeScreenModel>? = null,
    val errorMessage: String? = null,
)

data class HomeScreenModel(
    val imageUrl: String? = null,
    val modelId: String? = null,
    val id: Int? = null,
    val timeStep: String? = null,
    val isExpired: Boolean? = null
)

