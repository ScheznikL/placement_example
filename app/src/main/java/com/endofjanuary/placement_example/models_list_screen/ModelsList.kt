package com.endofjanuary.placement_example.models_list_screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.utils.BottomBar
import org.koin.androidx.compose.getViewModel

@RequiresApi(Build.VERSION_CODES.Q)
@Composable
fun ModelsListScreen(
    navController: NavController
) {
    val viewModel = getViewModel<ModelsListViewModel>()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadModels()
    }

    val itemToDelete = remember {
        viewModel.selectedIds
    }
    val deleted = remember {
        viewModel.deletedModel
    }
    val snackbarHostState = remember { SnackbarHostState() }

//    LaunchedEffect(deleted.value) {
//        when (deleted.value) {
//            is Resource.Error -> {
//                viewModel.selectedIds.value = emptySet()
//                snackbarHostState.showSnackbar(
//                    message = "Error when deleting model...",
//                )
//            }
//
//            is Resource.Success -> {
//                // viewModel.selectedIds.value -= viewModel.selectedIds.value.last()
//                snackbarHostState.showSnackbar(
//                    message = "Model deleted successfully",
//                )
//            }
//
//            else -> {}
//        }
//    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            if (itemToDelete.value.isEmpty()) {
                FloatingActionButton(onClick = {
                    viewModel.insetModel()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                /**
                 * Todo under save

                FloatingActionButton(onClick = {
                navController.navigate("new_model")
                //                    when (viewState.selectedCategory) {new_model
                //                        Category.FromText -> navController.navigate("chat_screen")
                //                        Category.FromImage -> navController.navigate("image_uploading")
                //                    }

                }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                }*/
            } else {
                FloatingActionButton(onClick = {
                    viewModel.deleteModels()
//                    when (viewState.selectedCategory) {
//                        Category.FromText -> viewModel.deleteModels()
//                        Category.FromImage -> viewModel.deleteModels()
//                    }
                }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    ) { padding ->
        Surface(
            color = MaterialTheme.colorScheme.background,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column {
                CategoryTabs(
                    categories = viewState.categories,
                    selectedCategory = viewState.selectedCategory,
                    onCategorySelected = viewModel::onCategorySelected,
                )
                when (viewState.selectedCategory) {
                    Category.FromText -> {
                        Spacer(modifier = Modifier.height(12.dp))
                        SearchBar(
                            hint = "Search...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            // viewModel.searchPokemonList(it)
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                        ModelsFromTextList(navController = navController, viewModel = viewModel)
                    }

                    Category.FromImage -> {
                        Spacer(modifier = Modifier.height(20.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_blur),
                            contentDescription = "Model",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(CenterHorizontally)
                        )
                        SearchBar(
                            hint = "Search in models from image...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                        ) {
                            // viewModel.searchPokemonList(it)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        ModelsFromImageList(navController = navController, viewModel = viewModel)
                    }
                }
            }
        }
    }
}

@Composable
fun ModelsFromTextList(
    navController: NavController,
    viewModel: ModelsListViewModel
) {
    val modelListState by viewModel.textModelsListState.collectAsState()
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    if (modelListState.isNotEmpty()) {
        LazyColumn(contentPadding = PaddingValues(16.dp)) {
            val itemCount = //modelListState.size - 1
                if (modelListState.size % 2 == 0) {
                    modelListState.size / 2
                } else {
                    modelListState.size / 2 + 1
                }
            items(itemCount) {
                ModelsInRow(
                    rowIndex = it,
                    entries = modelListState,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    } else {
        NoDataSection("It appears you have no model...") { navController.navigate("home_screen") }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
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
fun ModelsFromImageList(
    navController: NavController, viewModel: ModelsListViewModel
) {
    //val modelsList by remember { viewModel.modelsList }
    val modelListState by viewModel.imageModelsListState.collectAsState()
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val isSearching by remember { viewModel.isSearching }

    if (modelListState.isNotEmpty()) {
        LazyColumn(
            contentPadding = PaddingValues(16.dp)
        ) {
            val itemCount = //modelListState.size - 1
                if (modelListState.size % 2 == 0) {
                    modelListState.size / 2
                } else {
                    modelListState.size / 2 + 1
                }
            items(itemCount) {
                ModelsInRow(
                    rowIndex = it,
                    entries = modelListState,
                    navController = navController,
                    viewModel = viewModel
                )
            }
        }
    } else {
        NoDataSection("It appears you have no image model...") { navController.navigate("home_screen") }
    }
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
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
) {

    val selectedIds = remember { viewModel.selectedIds }
    val inSelectionMode by remember { derivedStateOf { selectedIds.value.isNotEmpty() } }
    Column {
        Row {
            val selected by remember { derivedStateOf { selectedIds.value.contains(entries[rowIndex * 2].meshyId) } }
            ModelInRowEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier
                    .weight(1f),
                viewModel = viewModel,
                inSelectionMode = inSelectionMode,
                selected = selected
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowIndex * 2 + 2) {
                val selected2 by remember { derivedStateOf { selectedIds.value.contains(entries[rowIndex * 2 + 1].meshyId) } }
                ModelInRowEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    viewModel = viewModel,
                    inSelectionMode = inSelectionMode,
                    selected = selected2
                )
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
    inSelectionMode: Boolean,
    selected: Boolean
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .combinedClickable(
                onClick = {
                    if (!inSelectionMode) {
                        viewModel.saveLastModel(entry.meshyId)
                        navController.navigate(
                            "transit_dialog/${entry.id}/${entry.meshyId}"
                        )
                    } else {
                        viewModel.selectedIds.value -= viewModel.selectedIds.value.last()
                    }
                },
                onLongClick = {
                    Log.d("onLongClick", "${entry.meshyId} - ${entry.modelDescription}")
                    if (!inSelectionMode) {
                        viewModel.selectedIds.value += entry.meshyId
                    } else {
                        viewModel.selectedIds.value -= viewModel.selectedIds.value.last()
                    }
                    Log.d("onLongClick", viewModel.selectedIds.value.toString())
//                    else
//                        Modifier.toggleable(
//                            value = selected,
//                            interactionSource = interactionSource,
//                            indication = null, // do not show a ripple
//                            onValueChange = {
//                                if (it) {
//                                    selectedIds.value += entry.meshyId
//                                } else {
//                                    selectedIds.value -= entry.meshyId
//                                }
//                            }
//                        )
                },
            )

    ) {
//        if (dialogRes.value != null && dialogRes.value!!) {
//            navController.navigate(
//                "ar_screen/${entry.id}"
//            )
//        } else if (dialogRes.value != null) {
//            navController.navigate(
//                "threed_screen/${entry.id}"
//            )
//        }
        Column {// SubcomposeAsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.modelImageUrl)
                    .crossfade(true)
                    .build(),
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
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (inSelectionMode) {
        if (selected) {
            val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            Icon(
                Icons.Filled.CheckCircle,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .border(2.dp, bgColor, CircleShape)
                    .clip(CircleShape)
                    .background(bgColor)
            )
        }
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column(verticalArrangement = Arrangement.Center, horizontalAlignment = CenterHorizontally) {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onRetry() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Retry")
        }
    }
}

@Composable
fun NoDataSection(
    error: String,
    onGoHome: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = CenterHorizontally
    ) {
        Text(error, color = Color.Red, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Button(
            onClick = { onGoHome() },
            modifier = Modifier.align(CenterHorizontally)
        ) {
            Text(text = "Request model")
        }
    }
}

@Composable
fun HomeCategoryTabIndicator(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.onSurface
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
    modifier: Modifier = Modifier
) {
    val selectedIndex = categories.indexOfFirst { it == selectedCategory }
    val indicator = @Composable { tabPositions: List<TabPosition> ->
        HomeCategoryTabIndicator(
            Modifier.tabIndicatorOffset(tabPositions[selectedIndex])
        )
    }
    TabRow(
        selectedTabIndex = selectedIndex,
        indicator = indicator,
        modifier = modifier
    ) {
        categories.forEachIndexed { index, category ->
            Tab(
                selected = index == selectedIndex,
                onClick = { onCategorySelected(category) },
                text = {
                    Text(
                        text = when (category) {
                            Category.FromText -> "Models from text"
                            Category.FromImage -> "Models from image"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
        }
    }
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    hint: String = "",
    onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }

    Box(modifier = modifier) {
        BasicTextField(
            value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    // isHintDisplayed = it !=   && text.isEmpty()
                }
        )
        if (isHintDisplayed) {
            Text(
                text = hint,
                color = Color.LightGray,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 12.dp)
            )
        }
    }
}


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun ModelViewTypeDialog(openDialog: MutableState<Boolean>, dialogRes: MutableState<Boolean?>) {
//    BasicAlertDialog(
//        onDismissRequest = {
//            // Dismiss the dialog when the user clicks outside the dialog or on the back
//            // button. If you want to disable that functionality, simply use an empty
//            // onDismissRequest.
//            openDialog.value = false
//        }
//    ) {
//        Surface(
//            modifier = Modifier
//                .wrapContentWidth()
//                .wrapContentHeight(),
//            shape = MaterialTheme.shapes.large,
//            tonalElevation = AlertDialogDefaults.TonalElevation
//        ) {
//            Column(modifier = Modifier.padding(26.dp)) {
//                Text(
//                    text = "Would you like to place this model in your room via camera " +
//                            "or just view in 3D viewer?",
//                    textAlign = TextAlign.Justify
//                )
//                Spacer(modifier = Modifier.height(24.dp))
//                Row(
//                    verticalAlignment = Alignment.CenterVertically,
//                    horizontalArrangement = Arrangement.SpaceBetween,
//                    modifier = Modifier.fillMaxWidth()
//                ) {
//                    TextButton(
//                        onClick = {
//                            openDialog.value = false
//                            dialogRes.value = false
//                        },
//                        // modifier = Modifier.align(Start)
//                    ) {
//                        Text("Viewer")
//                    }
//                    TextButton(
//                        onClick = {
//                            openDialog.value = false
//                            dialogRes.value = true
//                        },
//                        // modifier = Modifier.align(Start)
//                    ) {
//                        Text("Camera")
//                    }
//                }
//            }
//        }
//    }
//}
