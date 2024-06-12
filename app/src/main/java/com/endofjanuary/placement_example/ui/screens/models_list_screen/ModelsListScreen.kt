package com.endofjanuary.placement_example.ui.screens.models_list_screen

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomCenter
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.models.ModelEntry
import com.endofjanuary.placement_example.ui.components.BottomBar
import com.endofjanuary.placement_example.ui.components.ImageWithExpiredLabel
import com.endofjanuary.placement_example.ui.dialogs.DeleteDialog
import com.endofjanuary.placement_example.ui.dialogs.ModelExpiredDialog
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalLayoutApi::class)
@RequiresApi(Build.VERSION_CODES.Q)
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
            FlowColumn {
                FloatingActionButton(
                    onClick = {
                        viewModel.deactivateSelectionMode()
                    }, shape = CircleShape
                ) {
                    Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete))
                }
                Spacer(modifier = Modifier.height(10.dp))
                FloatingActionButton(
                    onClick = {
                        openDeleteFromSelection.value = true
                    }, shape = CircleShape
                ) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
                }
            }
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
fun ModelsListContent(
    navController: NavController,
    modelsListState: List<ModelEntry>,
    isFromImage: Boolean,
    loadError: String,
    isLoading: Boolean,
    viewModel: ModelsListViewModel,
    onSearch: (String, Boolean) -> Unit
) {
    SearchBar(
        hint = stringResource(R.string.search), modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        onSearch(it, isFromImage)
    }
    Spacer(modifier = Modifier.height(12.dp))
    ModelsFromList(
        navController = navController,
        modelListState = modelsListState,
        loadError = loadError,
        isLoading = isLoading,
        viewModel = viewModel
    )
}

@Composable
fun ModelsFromList(
    navController: NavController,
    modelListState: List<ModelEntry>,
    loadError: String,
    isLoading: Boolean,
    viewModel: ModelsListViewModel
) {
    if (modelListState.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            val itemCount = if (modelListState.size % 2 == 0) {
                modelListState.size / 2
            } else {
                modelListState.size / 2 + 1
            }
            items(itemCount) {
                ModelsInRow(
                    rowIndex = it,
                    entries = modelListState,
                    navController = navController,
                    viewModel = viewModel,
                    deleteExpired = viewModel::deleteModel
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

@Composable
fun ModelsInRow(
    rowIndex: Int,
    entries: List<ModelEntry>,
    navController: NavController,
    viewModel: ModelsListViewModel,
    deleteExpired: (ModelEntry) -> Unit
) {
    Column {
        Row {
            ModelInRowEntry(entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f),
                viewModel = viewModel,
                onDelete = { deleteExpired(entries[rowIndex * 2]) })
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowIndex * 2 + 2) {
                ModelInRowEntry(entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    onDelete = { deleteExpired(entries[rowIndex * 2 + 1]) })
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ModelInRowEntry(
    entry: ModelEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ModelsListViewModel,
    onDelete: () -> Unit,
) {
    val openExpiredDialog = remember { mutableStateOf(false) }
    val confirmDelete = remember { mutableStateOf(false) }


    val defaultDominantColor = MaterialTheme.colorScheme.surface
    val deletedDominantColor = Color.Gray.copy(alpha = 0.5f)
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    val selectedIds by remember { viewModel.selectedIds }
    val selectedMode by remember { viewModel.selectionMode }

    val border = if (selectedIds.contains(entry.meshyId) && selectedMode) {
        BorderStroke(
            3.dp, Color.Black
        )
    } else {
        BorderStroke(
            0.dp,
            Color.White,
        )
    }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                if (selectedIds.contains(entry.meshyId) && selectedMode) deletedDominantColor
                else defaultDominantColor
            )
            .clip(RoundedCornerShape(10.dp))
            .combinedClickable(
                onClick = {
                    if (!viewModel.selectionMode.value) {
                        if (!entry.isExpired) {
                            navController.navigate(
                                "transit_dialog/${entry.id}/${entry.meshyId}"
                            )
                        } else {
                            openExpiredDialog.value = true
                        }
                    } else {
                        viewModel.selectModel(entry)
                    }
                },
                onLongClick = {
                    if (!viewModel.selectionMode.value) viewModel.activateSelectionMode()
                    viewModel.selectModel(entry)
                },
            )
    ) {
        if (entry.isExpired == false) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(entry.modelImageUrl)
                    .crossfade(true).build(),
                contentDescription = entry.modelDescription,
                onSuccess = {
                    viewModel.calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                },
                contentScale = ContentScale.FillBounds,
                alpha = if (selectedIds.contains(entry.meshyId) && selectedMode) 0.5f else 1f,
                modifier = Modifier
                    .align(Center)
                    .fillMaxSize()
            )
            Text(
                text = entry.modelDescription,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent, MaterialTheme.colorScheme.surface
                            ),
                        )
                    )
            )
        } else {
            ImageWithExpiredLabel(picture = {
                Column {// SubcomposeAsyncImage
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(entry.modelImageUrl)
                            .crossfade(true).build(),
                        contentDescription = entry.modelDescription,
                        onSuccess = {
                            viewModel.calcDominantColor(it.result.drawable) { color ->
                                dominantColor = color
                            }
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(120.dp)
                            .align(CenterHorizontally)
                    )
                    Text(
                        text = entry.modelDescription,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            })
        }
    }

    ModelExpiredDialog(
        openDialog = openExpiredDialog, confirm = confirmDelete, onConfirm = onDelete
    )
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

@Composable
fun SearchBar(
    modifier: Modifier = Modifier, hint: String = "", onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                })
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}