package com.endofjanuary.placement_example.modelsList

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ModelsListViewModel(
    private val modelsRoom: ModelsRepo
) : ViewModel() {

    private var curPage = 0

    var modelsList = mutableStateOf<List<ModelEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)

    var isSearching = mutableStateOf(false)

//    init {
//        loadModels()
//    }

    private val _modelsListState = MutableStateFlow<List<ModelEntry>>(emptyList())

    // The UI collects from this StateFlow to get its state updates
    val modelsListState: StateFlow<List<ModelEntry>> = _modelsListState


    fun loadModels() {
        viewModelScope.launch(Dispatchers.IO) {
            isLoading.value = true
            val result = modelsRoom.getAllModels()
            when (result) {
                is Resource.Success -> {

                    loadError.value = ""
                    isLoading.value = false
                    if (!result.data.isNullOrEmpty()) {
                        result.data!!.forEach { //TODO converter ?
//                            modelsList.value += ModelEntry(
//                                id = it.id,
//                                modelPath = it.modelPath,
//                                modelImageUrl = it.modelImageUrl,
//                                modelDescription = it.modelDescription
//                            )
                            _modelsListState.value += ModelEntry(
                                id = it.id,
                                modelPath = it.modelPath,
                                modelImageUrl = it.modelImageUrl,
                                modelDescription = it.modelDescription
                            )
                        }
                    }
                }

                is Resource.Error -> {
                    loadError.value = result.message!!
                    isLoading.value = false
                }

                is Resource.Loading -> {
                    loadError.value = ""
                    isLoading.value = true
                }

                is Resource.None -> {
                    loadError.value = ""
                    isLoading.value = false
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
