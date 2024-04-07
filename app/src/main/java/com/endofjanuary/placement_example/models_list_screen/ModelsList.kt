package com.endofjanuary.placement_example.models_list_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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

@Composable
fun ModelsListScreen(
    navController: NavController
) {
    val viewModel = getViewModel<ModelsListViewModel>()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true) {
        viewModel.loadModels()
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                when (viewState.selectedCategory) {
                    Category.FromText -> navController.navigate("chat_screen")
                    Category.FromImage -> navController.navigate("image_uploading")
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add")
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
                        Spacer(modifier = Modifier.height(20.dp))
                        Image(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = "Model",
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(CenterHorizontally)
                        )
                        SearchBar(
                            hint = "Search...",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                        ) {
                            // viewModel.searchPokemonList(it)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
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
                                .padding(16.dp)
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
//            if(it >= itemCount - 1 && !endReached && !isLoading && !isSearching) {
//                LaunchedEffect(key1 = true) {
//                    viewModel.loadPokemonPaginated()
//                }
//            }
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

@Composable
fun ModelInRowEntry(
    entry: ModelEntry,
    navController: NavController,
    modifier: Modifier = Modifier,
    viewModel: ModelsListViewModel
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
//    val dialogRes: MutableState<Boolean?> = remember { mutableStateOf(null) }
//
//    val showDialog = remember { mutableStateOf(false) }
//    if (showDialog.value) {
//        ModelViewTypeDialog(dialogRes = dialogRes, openDialog = showDialog)
//    }

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
            .clickable {
//                showDialog.value = true
//                navController.navigate(
//                    "ar_screen/${entry.id}"
//                )
                navController.navigate(
                    "transit_dialog/${entry.id}/${entry.meshyId}"
                )
            }

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
}

@Composable
fun ModelsInRow(
    rowIndex: Int,
    entries: List<ModelEntry>,
    navController: NavController,
    viewModel: ModelsListViewModel
) {
    Column {
        Row {
            ModelInRowEntry(
                entry = entries[rowIndex * 2],
                navController = navController,
                modifier = Modifier.weight(1f),
                viewModel = viewModel
            )
            Spacer(modifier = Modifier.width(16.dp))
            if (entries.size >= rowIndex * 2 + 2) {
                ModelInRowEntry(
                    entry = entries[rowIndex * 2 + 1],
                    navController = navController,
                    modifier = Modifier.weight(1f),
                    viewModel
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun RetrySection(
    error: String,
    onRetry: () -> Unit
) {
    Column {
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
