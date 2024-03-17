package com.endofjanuary.placement_example.chat

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.ui.theme.PurpleGrey40
import com.endofjanuary.placement_example.utils.ChatTopBar
import kotlinx.coroutines.launch
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreenNew(
    navController: NavController,
) {

    val viewModel = getViewModel<ChatScreenViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val scrollState = rememberLazyListState()
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current

    val messagesListState by remember { viewModel.messagesListState }
    val isLoading by remember { viewModel.isLoading }
    val textInput by remember { viewModel.inputValueState }

    LaunchedEffect(messagesListState) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    LaunchedEffect(isKeyboardOpen) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    /*    LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = "write ",
                actionLabel = "message"
            )
        }*/

    Scaffold(
        topBar = {
            ChatTopBar(navController = navController)
        },
        modifier = Modifier.fillMaxSize(),
//        bottomBar = {
//            BottomBar(navController = navController)
//        }
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (messagesListState.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                        .weight(weight = 1.0f, fill = true),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    )
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 32.dp)
                            .align(Alignment.CenterHorizontally),
                        text = "Describe desirable model bellow or choose photo to generate 3D model",
                        textAlign = TextAlign.Center,
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = Bold
                        ),
                    )
                }
            } else {
                LazyColumn(modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp)
                    .weight(weight = 1.0f, fill = true),
                    contentPadding = WindowInsets.statusBars.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .asPaddingValues(),
                    verticalArrangement = Arrangement.Bottom,
                    state = scrollState,
                    content = {
                        items(
                            count = messagesListState.size,
                            key = { messagesListState[it].content },
                            itemContent = { index ->
                                MessageBubble(
                                    modifier = Modifier.animateItemPlacement(),
                                    message = messagesListState[index],
                                )
                            })
                    })
            }

            if (isLoading) LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green
            )

            Row(
                modifier = Modifier.background(PurpleGrey40),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    value = textInput,
                    onValueChange = { viewModel.inputValueState.value = it },
                )
                Icon(
                    Icons.Default.Done,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            if (viewModel.description == null && textInput.isBlank()) {
                                viewModel.viewModelScope.launch {
                                    snackbarHostState.showSnackbar(
                                        message = "Write description of desirable model and press send.",
                                        actionLabel = "Info"
                                    )
                                }
                            } else {
                                mainViewModel.loadModelEntryFromText(
                                    viewModel.description!!
                                )
                            }
                            //if (textInput.isNotBlank()) navController.navigate("loading_screen/${viewModel.description}")
                            Log.d("description", viewModel.description ?: "null")
                        }
                        .alpha(if (textInput.isNotBlank()) 1.0f else 0.5f),
                    contentDescription = "final",
                )
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            if (textInput.isNotBlank()) {
                                viewModel.send(textInput)
                                viewModel.inputValueState.value = ""
                            }
                        }
                        .alpha(if (textInput.isNotBlank()) 1.0f else 0.5f),
                    contentDescription = "final",
                )
            }
        }
    }
}
