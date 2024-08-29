package com.endofjanuary.placement_example.ui.screens.models_list_screen

import ModelInRowEntry
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.models.ModelEntry

@Composable
fun ModelsList(
    navController: NavController,
    modelListState: List<ModelEntry>,
    loadError: String,
    isLoading: Boolean,
    viewModel: ModelsListViewModel
) {
    if (modelListState.isNotEmpty()) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            userScrollEnabled = true
        ) {
            items(modelListState) {
                ModelInRowEntry(
                    entry = it,
                    navController = navController,
                    onDelete = { viewModel.deleteModel(it) },
                    selectedIds = viewModel.selectedIds.value,
                    selectionMode = viewModel.selectionMode.value,
                    activateSelectionMode = viewModel::activateSelectionMode,
                    selectModel = viewModel::selectModel,
                    calcDominantColor = viewModel::calcDominantColor,
                    modifier = Modifier.padding(5.dp)
                )
            }
        }
    } else {
        NoDataSection(
            error = stringResource(R.string.no_models_data),
            modifier = Modifier.fillMaxSize(),
        ) { navController.navigate("home_screen") }
    }
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        if (isLoading) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }
        if (loadError.isNotEmpty()) {
            RetrySection(error = loadError) {
                viewModel.loadModels()
            }
        }
    }
}