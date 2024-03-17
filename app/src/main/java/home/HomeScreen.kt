package home

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.utils.BottomBar
import com.example.jetcaster.ui.home.HomeViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun HomeScreen(
    navController: NavController
) {
    val viewModel = getViewModel<HomeViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }
    Surface(Modifier.fillMaxSize()) {
        Scaffold(
            bottomBar = {
                BottomBar(navController)
            },
            snackbarHost = { SnackbarHost(snackbarHostState) }
        ) { innerPadding ->
            HomeContent(
//                homeCategories = viewModel.state.value.categories,
//                selectedHomeCategory = viewModel.state.value.selectedCategory,
//                onCategorySelected = viewModel::onHomeCategorySelected,
                modifier = Modifier
                    //     .fillMaxSize()
                    .padding(innerPadding),
                  navController = navController
            )

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Row {
//                Image(
//                    painter = painterResource(),
//                    contentDescription = null
//                )
                Icon(
                    painter = painterResource(id = R.drawable.ic_blur),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        // backgroundColor = backgroundColor,
        actions = {
            IconButton(
                onClick = { /* TODO: Open search */ }
            ) {
                Icon(
                    imageVector = Icons.Filled.Notifications,
                    contentDescription = "notification"
                )
            }
//                IconButton(
//                    onClick = { /* TODO: Open account? */ }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.AccountCircle,
//                        contentDescription = "acc"
//                    )
//                }

        },
        modifier = modifier
    )
}

@Composable
fun HomeContent(
//    selectedHomeCategory: HomeCategory,
//    homeCategories: List<HomeCategory>,
    modifier: Modifier = Modifier,
//    onCategorySelected: (HomeCategory) -> Unit,
    navController: NavController,
) {
    Column(
        modifier = modifier.windowInsetsPadding(
            WindowInsets.systemBars.only(WindowInsetsSides.Horizontal)
        )
    ) {

        val surfaceColor = MaterialTheme.colorScheme.surface
        val appBarColor = surfaceColor.copy(alpha = 0.87f)

        Column(
            modifier = Modifier
                .fillMaxWidth()
//                .verticalGradientScrim(
//                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
//                    startYPercentage = 1f,
//                    endYPercentage = 0f
//                )
        ) {
            // Draw a scrim over the status bar which matches the app bar
            Spacer(
                Modifier
                    .background(appBarColor)
                    .fillMaxWidth()
                    .windowInsetsTopHeight(WindowInsets.statusBars)
            )

            HomeAppBar(
                backgroundColor = appBarColor,
                modifier = Modifier.fillMaxWidth()
            )

        }

//        LazyHorizontalGrid(rows = GridCells.Adaptive(minSize = 128.dp)) {
//            items(20) { i ->
//                PhotoItem()
//            }
//        }
        Text(
            "Last Models",
            style = MaterialTheme.typography.headlineMedium.copy(
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)
            )
        )
        LazyRow {
            items(10) {
                Box(
                    modifier = modifier
                        .size(300.dp, 350.dp)
                        .padding(horizontal = 5.dp, vertical = 10.dp)
                ) {
                    PhotoItem()
                }
            }
        }
        Column{
            Button(
                onClick = {
                    navController.navigate("chat_screen")
                },
            ) {
                Text(text = "Begin chat to create unique model")
            }
            Button(
                onClick = {
                    navController.navigate("image_uploading")
                },
            ) {
                Text(text = "Model from Image")
            }
        }

    }
}

@Composable
fun PhotoItem(modifier: Modifier = Modifier) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }
    Box(
        contentAlignment = Alignment.Center,
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
//                .clickable {
//                    navController.navigate(
//                        "threed_screen/${entry.id}"
//                    )
//                }
    ) {
        Column {// SubcomposeAsyncImage
            //                AsyncImage(
            //                    model = ImageRequest.Builder(LocalContext.current)
            //                        .data(entry.modelImageUrl)
            //                        .crossfade(true)
            //                        .build(),
            //                    contentDescription = entry.modelDescription,
            //                    onSuccess = {
            //                        viewModel.calcDominantColor(it.result.drawable) { color ->
            //                            dominantColor = color
            //                        }
            //                    },
            //                    contentScale = ContentScale.Crop,
            //                    modifier = Modifier
            //                        .size(120.dp)
            //                        .align(Alignment.CenterHorizontally)
            //                )
            Text(
                text = "temp description",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }

}
