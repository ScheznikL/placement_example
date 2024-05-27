package com.endofjanuary.placement_example.home

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
import com.endofjanuary.placement_example.upload_image.UploadImageBottomSheet
import com.endofjanuary.placement_example.utils.BottomBar
import com.example.jetcaster.ui.home.HomeScreenModel
import com.example.jetcaster.ui.home.HomeViewModel
import com.example.jetcaster.ui.home.HomeViewState
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel = getViewModel<HomeViewModel>()
    //val VM_T = getViewModel<ModelsListViewModel>()
    //val snackbarHostState = remember { SnackbarHostState() }
    val viewState by viewModel.state.collectAsStateWithLifecycle()

/*    LaunchedEffect(viewModel.state.value.lastModels) {
        if (!viewState.lastModels.isNullOrEmpty()) {
            viewModel.loadLastModels()
        }
    }*/

    Surface(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomBar(navController)
            },
            // snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
           // Column{
             /*   IconButton(
                    onClick = { *//*VM_T.deleteModel() *//*},
                    enabled = !viewState.lastModels.isNullOrEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "delete all last viewed"
                    )
                }*/
                HomeContent(
                    modifier = Modifier.padding(innerPadding),
                    navController = navController,
                    viewModel = viewModel,
                    viewState = viewState
                )
            //}
        }
    }
}

@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navController: NavController,
    viewModel: HomeViewModel,
    viewState: HomeViewState
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val appBarColor = surfaceColor.copy(alpha = 0.87f)

    val showBottomSheet = remember { mutableStateOf(false) }

    val scrollState = rememberLazyListState()

    val context = LocalContext.current

    LaunchedEffect(viewState.lastModels) {
        if (!viewState.lastModels.isNullOrEmpty())
            scrollState.scrollToItem(viewState.lastModels.size - 1)
    }

    LaunchedEffect(viewState.errorMessage) {
        if (!viewState.errorMessage.isNullOrEmpty()) {
            Toast.makeText(
                context,
                "Error: $viewState.errorMessage",
                Toast.LENGTH_LONG,
            ).show()
        }
    }

    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            Spacer(
                Modifier
                    .background(appBarColor)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )
            HomeAppBar(
                backgroundColor = appBarColor, modifier = Modifier.fillMaxWidth()
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(//
                "Last Models:", style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
                )
            )
            IconButton(
                onClick = { viewModel.clearLastModelPreview(context) },
                enabled = !viewState.lastModels.isNullOrEmpty()
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "delete all last viewed"
                )
            }
        }
        if (!viewState.lastModels.isNullOrEmpty()) {
            LazyRow(
                reverseLayout = true,
                state = scrollState
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
                            viewModel = viewModel,
                            index = it
                        )
                    }
                }
            }
        } else {
            EmptyModelItem(
                message = viewModel.state.value.errorMessage ?: "You haven't viewed any model yet"
            )
        }
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 35.dp)
        ) {
            Button(
                onClick = {
                    navController.navigate("chat_screen")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Begin chat to create unique model")
            }
            Button(
                onClick = {
                    showBottomSheet.value = true
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Model from Image")
            }
        }

    }
    UploadImageBottomSheet(
        navController = navController,
        showBottomSheet = showBottomSheet,
    )
}


@Composable
fun ModelItem(
    modifier: Modifier = Modifier,
    modelItem: HomeScreenModel,
    navController: NavController,
    viewModel: HomeViewModel,
    index: Int
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
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
                if (modelItem.id != null)
                    navController.navigate(
                        "transit_dialog/${modelItem.id}/${modelItem.modelId}"
                    )
            }) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current).data(modelItem.imageUrl)
                    .crossfade(true).build(), contentDescription = "last model", onSuccess = {
                    viewModel.calcDominantColor(it.result.drawable) { color ->
                        dominantColor = color
                    }
                }, contentScale = ContentScale.Crop, modifier = Modifier.size(120.dp)
                //.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "$index ${modelItem.timeStep}", // todo get rid of indexes
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart)
            )

    }

}

@Composable
fun EmptyModelItem(
    message: String,
    modifier: Modifier = Modifier,
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            // .shadow(5.dp, RoundedCornerShape(10.dp))
            .aspectRatio(2f)
            .border(1.dp, Color.LightGray, RoundedCornerShape(10.dp))
        //.clip(RoundedCornerShape(10.dp))
        //.background(Color.Gray)
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
    backgroundColor: Color, modifier: Modifier = Modifier
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
