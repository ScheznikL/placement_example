/*
package com.endofjanuary.placement_example.chat

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.utils.BottomBar
import com.endofjanuary.placement_example.utils.ChatTopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController
) {
    val viewModel = getViewModel<ChatScreenViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

//    var list by remember {
//        mutableStateOf(listOf<String>())
//    }


    val messagesList = remember {
        viewModel.messagesListState
    }
    val scrollState: LazyListState = rememberLazyListState()

//    LaunchedEffect(viewModel.messagesListState) {
//
//        if (messagesListState.isNotEmpty()) {
//            Log.d("scrollLog", "${messagesListState.size - 1}")
//            //state.scrollToItem(messagesListState.size - 1)
//
//        }
//    }
    val context = LocalContext.current
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else mutableStateOf(true)
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
        }
    )


    Scaffold(
        topBar = {
            ChatTopBar(navController = navController)
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController = navController) }
    ) { padding ->
//        Column(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding),
//            //.weight(1f),
//            // verticalArrangement = Arrangement.Bottom,
//        )

        */
/*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && !hasNotificationPermission) { // todo fro download purposes
            launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }*//*

       */
/* Box(modifier = Modifier.padding(padding)) {
            MessagesList(
                scrollState = scrollState,
                list = messagesList,
                modifier = Modifier
                    //   .padding(padding)
                    .fillMaxSize(),
                navController = navController,
                viewModel = viewModel,
                mainViewModel = mainViewModel
            )
        }*//*



    }
}

private val JumpToBottomThreshold = 56.dp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MessagesList(
    modifier: Modifier = Modifier,
    list: List<Message>,
    scrollState: LazyListState,
    viewModel: ChatScreenViewModel,
    navController: NavController,
    mainViewModel: MainViewModel
) {
    Log.d("scrollLog", "${list.size - 1}")

    val scope = rememberCoroutineScope()
    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val loadSuccess by remember { viewModel.isSuccess }

    Column(modifier = modifier) {
        LazyColumn(
            state = scrollState,
            modifier = modifier.weight(1f)
        ) {
            stickyHeader {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        //.padding(-15.dp)
                        .background(
                            //width = 2.dp,
                            color = MaterialTheme.colorScheme.inversePrimary,
                            shape = RoundedCornerShape(
                                bottomStartPercent = 40,
                                bottomEndPercent = 40
                            )
                        )
                ) {
                    Text(
                        textAlign = TextAlign.Justify,
                        text = "Describe desirable model bellow",
                        modifier = Modifier.padding(horizontal = 12.dp),
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Companion.W200)
                    )
                    Row(
                        verticalAlignment = Alignment.Bottom,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.clickable {

                        }) {
                        Text(
                            textAlign = TextAlign.Justify,
                            text = "Or transform image to 3D",
                            modifier = Modifier.padding(horizontal = 12.dp),
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.W200)
                        )
                        Icon(
                            painter = painterResource(id = R.drawable.ic_gallery),
                            contentDescription = "gallery",
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
            items(list) {
                if (it.role == "user") {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = modifier
                            .border(
                                BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(100)
                            )
                        //  .padding(8.dp)
                        //   .align(Alignment.CenterEnd)
                        //.weight(1f)
                    ) {
                        Text(
                            it.content,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    //     HorizontalDivider()
                }
                if (it.role == "assistant") {
                    Row(
                        //horizontalArrangement = Arrangement.Start,
                        modifier = modifier
                        //  .padding(8.dp)
                        //  .align(Alignment.CenterStart)
                    ) {
                        Text(
                            it.content,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    //   HorizontalDivider()
                }


            }

        }
        if (!loadError.isNullOrEmpty()) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.background(color = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    loadError,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(16.dp)
                )
            }
            //      HorizontalDivider()
        }
        if (isLoading) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .border(
                        BorderStroke(1.dp, MaterialTheme.colorScheme.secondary)
                    )
            ) {
                LinearProgressIndicator()
                //         HorizontalDivider()
            }
        }
        UserInputRow(
            viewModel = viewModel,
            navController = navController,
            modifier = Modifier
                .fillMaxWidth(),
            mainViewModel = mainViewModel
            // .align(Alignment.BottomCenter)
            //.imePadding(),
        ) {
//            Log.d("scrollLog", "${viewModel.messagesListState.size - 1}")
//            Log.d("scrollLog", "${viewModel.messagesListState}")
            viewModel.viewModelScope.launch() {
                Log.d("scrollLogFromLaunch", "${viewModel.messagesListState} and ${list.size}")
                if (list.isNotEmpty()) scrollState.scrollToItem(
                    list.size - 1
                )
            }
        }
        // Jump to bottom button shows up when user scrolls past a threshold.
        // Convert to pixels:
//        val jumpThreshold = with(LocalDensity.current) {
//            JumpToBottomThreshold.toPx()
//        }
//
//        // Show the button if the first visible item is not the first one or if the offset is
//        // greater than the threshold.
//        val jumpToBottomButtonEnabled by remember {
//            derivedStateOf {
//                scrollState.firstVisibleItemIndex != 0 ||
//                        scrollState.firstVisibleItemScrollOffset > jumpThreshold
//            }
//        }
//
//        JumpToBottom(
//            // Only show if the scroller is not at the bottom
//            enabled = jumpToBottomButtonEnabled,
//            onClicked = {
//                scope.launch {
//                    if (list.isNotEmpty()) {
//                        scrollState.animateScrollToItem(list.size - 1)
//                    }
//                }
//            },
//            modifier = Modifier.align(Alignment.BottomCenter)
//        )
    }
}

@Composable
fun UserInputRow(
    viewModel: ChatScreenViewModel,
    navController: NavController,
    modifier: Modifier = Modifier,
    mainViewModel: MainViewModel,
    onFocusChange: () -> Unit,
) {
    Row(
        modifier = modifier
            .navigationBarsPadding()
            .imePadding(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,

        ) {
        var textInput by remember {
            mutableStateOf("")
        }
        val scope = rememberCoroutineScope()
        var lastFocusState by remember { mutableStateOf(false) }
        OutlinedTextField(
            modifier = Modifier
//                .onFocusChanged { state ->
//                    if (lastFocusState != state.isFocused) {
//                        if (state.isFocused) {
//                            onFocusChange()
//                        }
//                        //textFieldFocusState = focused
//                    }
//                    // lastFocusState = state.isFocused
//                }
                .padding(end = 5.dp),
            value = textInput,
            onValueChange = { textInput = it },

            )
        Button(
            modifier = Modifier
                .weight(1f),
            contentPadding = PaddingValues(0.dp),
            onClick = {
                Log.d("description", viewModel.description ?: "null")
                mainViewModel.loadModelEntryFromText(viewModel.description ?: textInput)
               // navController.navigate("home_screen")
            }) {
            Icon(
                Icons.Default.Done,
                contentDescription = "final",
            )
        }
        Button(
            modifier = Modifier
                .weight(1f),
            shape = RoundedCornerShape(100),
            contentPadding = PaddingValues(0.dp),
            onClick = {
                if (textInput.isNotBlank()) {
                    viewModel.send(textInput)
//                            list += textInput
                    textInput = ""
                    onFocusChange()
                }
            }) {
            Icon(
                Icons.AutoMirrored.Filled.Send,
                contentDescription = "send",
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
        }

    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    val navController = rememberNavController()
    ChatScreen(navController)
}
*/
