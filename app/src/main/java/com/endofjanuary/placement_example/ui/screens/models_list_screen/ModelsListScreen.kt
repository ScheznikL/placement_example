package com.endofjanuary.placement_example.ui.screens.models_list_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.ui.components.BottomBar
import com.endofjanuary.placement_example.ui.dialogs.DeleteDialog
import org.koin.androidx.compose.getViewModel

@Composable
fun ModelsListScreen(
    navController: NavController,
) {
    val viewModel = getViewModel<ModelsListViewModel>()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadModels()
    }

    val openDeleteFromSelection = remember { mutableStateOf(false) }
    val confirmDeleteFromSelection = remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val textModelsListState by viewModel.textModelsListState.collectAsState()
    val imageModelsListState by viewModel.imageModelsListState.collectAsState()

    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    Scaffold(snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, bottomBar = { BottomBar(navController = navController) }, floatingActionButton = {
        if (!viewModel.selectionMode.value) {
            FloatingActionButton(onClick = {
                viewModel.insetModel()
            }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add))
            }
        } else {
            DeleteItemControls(
                onDelete = { openDeleteFromSelection.value = true },
                onDeactivateSelection = viewModel::deactivateSelectionMode
            )
        }
    }) { padding ->
        Surface(color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .clickable {
                    viewModel.deactivateSelectionMode()
                }) {
            Column {
                CategoryTabs(
                    categories = viewState.categories,
                    selectedCategory = viewState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected,
                )
                Spacer(modifier = Modifier.height(20.dp))
                when (viewState.selectedCategory) {
                    Category.FromText -> {
                        ModelsListContent(
                            navController = navController,
                            modelsListState = textModelsListState,
                            isFromImage = false,
                            loadError = loadError,
                            isLoading = isLoading,
                            viewModel = viewModel,
                            onSearch = viewModel::onSearch
                        )
                    }

                    Category.FromImage -> {
                        ModelsListContent(
                            navController = navController,
                            modelsListState = imageModelsListState,
                            isFromImage = true,
                            loadError = loadError,
                            isLoading = isLoading,
                            viewModel = viewModel,
                            onSearch = viewModel::onSearch
                        )
                    }
                }
            }
        }
    }

    DeleteDialog(
        title = stringResource(R.string.delete), text = stringResource(
            id = R.string.delete_selected,
            viewModel.selectedIds.value.size,
            if (viewModel.selectedIds.value.size > 1) "s" else ""
        ), openDialog = openDeleteFromSelection, confirm = confirmDeleteFromSelection
    ) {
        viewModel.deleteModels()
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit,
) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() }, modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Spacer(
        modifier
            .padding(horizontal = 24.dp)
            .height(4.dp)
            .background(color, RoundedCornerShape(topStartPercent = 100, topEndPercent = 100))
    )
}

@Composable
private fun CategoryTabs(
    categories: List<Category>,
    selectedCategory: Category,
    onCategorySelected: (Category) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }
    TabRow(
        selectedTabIndex = selectedIndex, indicator = indicator, modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            Category.FromText -> stringResource(R.string.tab_from_text)
                            Category.FromImage -> stringResource(R.string.tab_from_image)
                        }, style = MaterialTheme.typography.bodySmall
                    )
                })
        }
    }
}

