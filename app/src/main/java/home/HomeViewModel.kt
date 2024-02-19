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

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow

class HomeViewModel(
) : ViewModel() {
    // Holds our currently selected home category
    private val selectedCategory = MutableStateFlow(HomeCategory.Chat)
    // Holds the currently available home categories
    //private val categories = MutableStateFlow(HomeCategory.values().asList())
    private val categories = HomeCategory.values().asList()

    // Holds our view state which the UI collects via [state]
    private val _state: MutableState<HomeViewState> = mutableStateOf(HomeViewState())
    val state: State<HomeViewState>
        get() = _state

    fun onHomeCategorySelected(category: HomeCategory) {
        selectedCategory.value = category
    }

}

enum class HomeCategory {
    Chat, ARScreen, ThreeDScreen
}

data class HomeViewState(
    //val featuredPodcasts: PersistentList<PodcastWithExtraInfo> = persistentListOf(),
  //  val refreshing: Boolean = false,
    val selectedHomeCategory: HomeCategory = HomeCategory.ARScreen,
    val homeCategories: List<HomeCategory> = emptyList(),
    val errorMessage: String? = null
){
    constructor():this(HomeCategory.Chat, HomeCategory.values().asList())
}
