/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui.home

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.datastore.core.DataStore
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.endofjanuary.placement_example.LastModelsParam
import com.endofjanuary.placement_example.ModelAccessParam
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date

private const val DEFAULT_TIMESTAMP = 0

class HomeViewModel(
    private val modelsRoomRepo: ModelsRepo,
    private val dataStore: DataStore<LastModelsParam>, // todo repo
) : ViewModel() {

    // Holds our view state which the UI collects via [state]
    private val _state = MutableStateFlow(HomeViewState())
    val state: StateFlow<HomeViewState>
        get() = _state.asStateFlow()

    private var appStartupParamsCollectorJob: Job? = null

    init {
        viewModelScope.launch(Dispatchers.IO) {
            dataStore.data.collectLatest { startupParams: LastModelsParam ->
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

    private fun List<ModelAccessParam>.toHomeScreenModelList(): List<HomeScreenModel> { // todo separate
        return this.map {
            HomeScreenModel(
                modelId = it.modelId,
                timeStep = convertToReadableFormat(it.unixTimestamp)
            )
        }
    }

    private fun convertToReadableFormat(unixTimestamp: Long): String {
        return if (unixTimestamp > DEFAULT_TIMESTAMP) {
            val timestampAsDate = Date(unixTimestamp)
            SimpleDateFormat.getDateTimeInstance().format(timestampAsDate)
        } else {
            "No app opening registered yet."
        }
    }

    fun loadLastModels() {
        if (_state.value.lastModels.isNullOrEmpty()) {
            viewModelScope.launch(Dispatchers.IO) {
                val result =
                    modelsRoomRepo.getModelsById(*_state.value.lastModels!!.mapNotNull { it.modelId }
                        .toTypedArray())
                when (result) {
                    is Resource.Error -> {
                        _state.update { currentState ->
                            currentState.copy(
                                errorMessage = result.message,
                            )
                        }
                    }

                    is Resource.Loading -> TODO()
                    is Resource.None -> TODO()
                    is Resource.Success -> {
                        _state.update { currentState ->
                            currentState.copy(
                                lastModels = currentState.lastModels!!.map { model ->
                                    val correspondingEntity =
                                        result.data!!.find { entity -> entity.meshyId == model.modelId }
                                    if (correspondingEntity != null) {
                                        model.copy(
                                            imageUrl = correspondingEntity.modelImageUrl,
                                            id = correspondingEntity.id
                                        )
                                    } else {
                                        model
                                    }
                                })
                        }
                    }
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

data class HomeViewState(
    val lastModels: List<HomeScreenModel>? = null,
    val errorMessage: String? = null,
)

data class HomeScreenModel(
    val imageUrl: String? = null,
    val modelId: String? = null,
    val id: Int? = null,
    val timeStep: String? = null
)

