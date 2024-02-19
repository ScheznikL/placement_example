package home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Face
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.chat.ChatScreen
import com.endofjanuary.placement_example.utils.BottomBar
import com.example.jetcaster.ui.home.HomeCategory
import com.example.jetcaster.ui.home.HomeViewModel
import com.example.jetcaster.util.verticalGradientScrim
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
                homeCategories = viewModel.state.value.homeCategories,
                selectedHomeCategory = viewModel.state.value.selectedHomeCategory,
                onCategorySelected = viewModel::onHomeCategorySelected,
                modifier = Modifier
                    .fillMaxSize()
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
                Icon(imageVector = Icons.Default.Face, contentDescription = null)
                Icon(
                    imageVector = Icons.Default.Face,
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        // backgroundColor = backgroundColor,
//        actions = {
//            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
//                IconButton(
//                    onClick = { /* TODO: Open search */ }
//                ) {
//                    Icon(
//                        imageVector = Icons.Filled.Search,
//                        contentDescription = stringResource(R.string.cd_search)
//                    )
//                }
//                IconButton(
//                    onClick = { /* TODO: Open account? */ }
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.AccountCircle,
//                        contentDescription = stringResource(R.string.cd_account)
//                    )
//                }
//            }
//        },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeContent(
    selectedHomeCategory: HomeCategory,
    homeCategories: List<HomeCategory>,
    modifier: Modifier = Modifier,
    onCategorySelected: (HomeCategory) -> Unit,
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
                .verticalGradientScrim(
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.38f),
                    startYPercentage = 1f,
                    endYPercentage = 0f
                )
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


        if (homeCategories.isNotEmpty()) {
            HomeCategoryTabs(
                categories = homeCategories,
                selectedCategory = selectedHomeCategory,
                onCategorySelected = onCategorySelected
            )
        }

        when (selectedHomeCategory) {
            HomeCategory.Chat -> /*navController.navigate("chat_screen")*/ChatScreen(navController)
            HomeCategory.ARScreen -> navController.navigate("chat_screen")
            HomeCategory.ThreeDScreen -> navController.navigate("chat_screen")
        }


    }
}


@Composable
private fun HomeCategoryTabs(
    categories: List<HomeCategory>,
    selectedCategory: HomeCategory,
    onCategorySelected: (HomeCategory) -> Unit,
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
                            HomeCategory.Chat -> "Open Chat with AI"
                            HomeCategory.ARScreen -> "Display model on camera"
                            HomeCategory.ThreeDScreen -> "Show model"
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            )
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


//@Composable
//@Preview
//fun PreviewHomeContent() {
//    val viewModel = getViewModel<HomeViewModel>()
//    Placement_exampleTheme {
//        HomeContent(
//            homeCategories = HomeCategory.values().asList(),
//            selectedHomeCategory = HomeCategory.Chat,
//            onCategorySelected = viewModel::onHomeCategorySelected,
//            navController = rememberNavController()
//        )
//    }
//}

//@Composable
//@Preview
//fun PreviewPodcastCard() {
//    Placement_exampleTheme {}
//}