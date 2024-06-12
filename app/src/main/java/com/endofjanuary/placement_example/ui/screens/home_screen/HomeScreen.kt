package com.endofjanuary.placement_example.ui.screens.home_screen

import android.content.Context
import android.graphics.drawable.Drawable
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.ui.components.BottomBar
import com.endofjanuary.placement_example.ui.components.ImageWithExpiredLabel
import com.endofjanuary.placement_example.ui.dialogs.ModelExpiredDialog
import com.endofjanuary.placement_example.ui.screens.upload_image.UploadImageBottomSheet
import com.endofjanuary.placement_example.ui.theme.MeshyBlack
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel = getViewModel<HomeViewModel>()
    val viewState by viewModel.state.collectAsStateWithLifecycle()

    Surface(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomBar(navController)
            },
        ) { innerPadding ->
            HomeContent(
                modifier = Modifier.padding(innerPadding),
                navController = navController,
                viewState = viewState,
                onClearLastModels = viewModel::clearLastModelPreview,
                calcDominantColor = viewModel::calcDominantColor,
                onDeleteExpired = viewModel::deleteModel
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewState: HomeViewState,
    onClearLastModels: (Context) -> Unit,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit,
    onDeleteExpired: (String?) -> Unit
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val appBarColor = surfaceColor.copy(alpha = 0.87f)

    val showBottomSheet = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    val scrollState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(viewState.lastModels) {
        if (!viewState.lastModels.isNullOrEmpty()) scrollState.scrollToItem(viewState.lastModels.size - 1)
    }

    LaunchedEffect(viewState.errorMessage) {
        if (!viewState.errorMessage.isNullOrEmpty()) {
            Toast.makeText(
                context,
                viewState.errorMessage,
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    Column(
        modifier = modifier
            .windowInsetsPadding(
                WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
            )
            .padding(bottom = 35.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {
            Spacer(
                Modifier
                    .background(appBarColor)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            HomeAppBar(
                modifier = Modifier.fillMaxWidth()
            )
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                Text(
                    stringResource(id = R.string.home_header),
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                    )
                )
                IconButton(
                    onClick = {
                        onClearLastModels(context)
                    }, enabled = !viewState.lastModels.isNullOrEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = stringResource(R.string.delete_all_last_viewed)
                    )
                }
            }
            if (!viewState.lastModels.isNullOrEmpty()) {
                LazyRow(
                    reverseLayout = true, state = scrollState
                ) {
                    items(viewState.lastModels.size) {
                        Box(
                            modifier = modifier
                                .size(300.dp, 350.dp)
                                .padding(horizontal = 5.dp, vertical = 10.dp)
                        ) {
                            ModelItem(
                                modelItem = viewState.lastModels[it],
                                navController = navController,
                                calcDominantColor = calcDominantColor,
                                onDelete = {
                                    onDeleteExpired(viewState.lastModels[it].modelId)
                                }
                            )
                        }
                    }
                }
            } else {
                EmptyModelItem(
                    message = viewState.errorMessage
                        ?: stringResource(id = R.string.home_empty_text),
                    modifier = Modifier
                        .padding(10.dp)
                        .height(190.dp)
                        .fillMaxWidth()
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("chat_screen")
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.home_button_chat))
            }
            Button(
                onClick = {
                    showBottomSheet.value = true
                }, modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(id = R.string.home_button_image))
            }
        }
    }
    UploadImageBottomSheet(
        navController = navController,
        showBottomSheet = showBottomSheet,
        sheetState = sheetState,
        scope = scope
    )
}


@Composable
fun ModelItem(
    modifier: Modifier = Modifier,
    modelItem: HomeScreenModel,
    navController: NavController,
    calcDominantColor: (drawable: Drawable, onFinish: (Color) -> Unit) -> Unit,
    onDelete: () -> Unit
) {
    val openExpiredDialog = remember { mutableStateOf(false) }
    val confirmDelete = remember { mutableStateOf(false) }


    val surfaceColor = MaterialTheme.colorScheme.surface
    var defaultDominantColor by remember {
        mutableStateOf(surfaceColor)
    }
    var dominantColor by remember {
        mutableStateOf(surfaceColor)
    }
    var textColor by remember {
        mutableStateOf(Color.Black)
    }

    Box(contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor, defaultDominantColor
                    )
                )
            )
            .clickable {
                if (modelItem.id != null && modelItem.isExpired != true) {
                    navController.navigate(
                        "transit_dialog/${modelItem.id}/${modelItem.modelId}"
                    )
                } else {
                    openExpiredDialog.value = true
                }
            }) {
        Text(
            text = "${modelItem.timeStep}",
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart),
            color = textColor
        )

        if (modelItem.isExpired == false) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(modelItem.imageUrl)
                    .crossfade(true).build(),
                contentDescription = stringResource(R.string.last_model_image),
                onSuccess = {
                    calcDominantColor(it.result.drawable) { color ->
                        if (color != Color(0.09411765f, 0.09411765f, 0.09411765f, 1.0f)) {
                            dominantColor = color
                        } else {
                            dominantColor = color
                            defaultDominantColor =
                                Color(0.09411765f, 0.09411765f, 0.09411765f, 1.0f)
                            textColor = Color.LightGray
                        }
                    }
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(190.dp)
            )
        } else {
            ImageWithExpiredLabel(
                picture = {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(modelItem.imageUrl)
                            .crossfade(true).build(),
                        contentDescription = stringResource(R.string.last_model_image),
                        onSuccess = {
                            calcDominantColor(it.result.drawable) { color ->
                                if (color != MeshyBlack) {
                                    dominantColor = color
                                } else {
                                    dominantColor = color
                                    defaultDominantColor = MeshyBlack
                                    textColor = Color.LightGray
                                }
                            }
                        },
                        contentScale = ContentScale.Crop,
                    )
                })
        }
    }
    ModelExpiredDialog(
        openDialog = openExpiredDialog,
        confirm = confirmDelete,
        onConfirm = onDelete
    )
}

@Composable
fun EmptyModelItem(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center, modifier = modifier
            .aspectRatio(2f)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
    ) {
        Text(
            text = message,
            fontSize = 20.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_blur),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        }, modifier = modifier
    )
}
