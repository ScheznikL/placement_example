package com.endofjanuary.placement_example.ui.screens.chat

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
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
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
/*    val isLoading by remember { viewModel.isLoading }
    val isError by remember { viewModel.loadError }*/
    val loadError by remember { mainViewModel.loadError }


    val modelToRefine by remember { mainViewModel.model }
    val autoRefine by remember { mainViewModel.autoRefine }

    val modelIds by remember { mainViewModel.isSuccess }
    val textInput by remember { viewModel.inputValueState }
    var isTextFieldEnabled = remember { true }
    val openCancelRefineDialog = remember { mutableStateOf(false) }
    val cancelRefineDialogConfirm = remember { mutableStateOf(false) }

    val context = LocalContext.current

    LaunchedEffect(messagesListState) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    LaunchedEffect(isKeyboardOpen) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        topBar = {
            ChatTopBar(navController = navController, autoRefineEnabled = autoRefine)
        },
        modifier = Modifier.fillMaxSize(),
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
                        text = stringResource(R.string.chat_card_text),
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
                            itemContent = { index ->
                                MessageBubble(
                                    modifier = Modifier.animateItemPlacement(),
                                    message = messagesListState[index],
                                    onEdit = {
                                        viewModel.send("NEXT")
                                    },
                                    onDone = {
                                        isTextFieldEnabled = false
                                        viewModel.loadingModel()
                                        mainViewModel.loadModelEntryFromText(
                                            viewModel.description!!
                                        )
                                    },
                                    onGetRefineOptions = {
                                        viewModel.loadingModel()
                                        mainViewModel.autoRefine(it)
                                    },
                                    onRefineCancel = {
                                        openCancelRefineDialog.value = true
                                    }
                                )
                            })
                    })
            }
            LaunchedEffect(openCancelRefineDialog.value) {
                if (cancelRefineDialogConfirm.value) {
                    mainViewModel.saveByteInstancedModel(isFromText = true, isRefine = false)
                }
            }

            LaunchedEffect(modelToRefine) {
                if (modelToRefine.meshyId != "1111" && autoRefine) {
                    viewModel.isAutoRefineEnabled.value = autoRefine
                    viewModel.addAutoRefineMessage(context = context)
                }
            }

            LaunchedEffect(modelIds) {

                if (modelIds != null) { // means model was uploaded to room
                    navController.navigate("transit_dialog/${modelIds?.second}/${modelIds?.first}")
                }
            }
/*            if (isLoading) LinearProgressIndicator(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Green
            )*/
            val openErrorDialog = mutableStateOf(/*(isError != null) ||*/ (loadError != null))

            LaunchedEffect(/*isError,*/ loadError) {
                openErrorDialog.value =/* (isError != null) ||*/ (loadError != null)
            }
            ErrorDialog(
                openDialog = openErrorDialog,
                errorMessage = /*isError ?:*/ loadError.toString()
            )
            Row(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.secondaryContainer)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_photo_camera),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("upload_image/${false}")
                        },
                    contentDescription = stringResource(R.string.camera),
                )
                BasicTextField(
                    enabled = isTextFieldEnabled,
                    modifier = Modifier
                        .padding(end = 3.dp, start = 15.dp)
                        .weight(1f),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                        capitalization = KeyboardCapitalization.Sentences
                    ),
                    keyboardActions = KeyboardActions(onDone = {
                        focusManager.clearFocus()
                    }),
                    textStyle = TextStyle(
                        lineHeight = 1.5.em,
                        fontSize = 16.sp
                    ),
                    value = textInput,
                    onValueChange = { viewModel.inputValueState.value = it },
                    maxLines = 7
                )

                Icon(
                    painter = painterResource(R.drawable.ic_attachment),
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            navController.navigate("upload_image/${true}")
                        },
                    contentDescription = stringResource(R.string.gallery),
                )
                Icon(
                    Icons.AutoMirrored.Filled.Send,
                    modifier = Modifier
                        .padding(16.dp)
                        .clickable {
                            if (textInput.isNotBlank() && isTextFieldEnabled) {
                                viewModel.send(textInput)
                                viewModel.inputValueState.value = ""
                            }
                        }
                        .alpha(if (textInput.isNotBlank()) 1.0f else 0.5f),
                    contentDescription = stringResource(R.string.send),
                )
            }
        }
    }
    CancelDialog(
        openDialog = openCancelRefineDialog,
        title = stringResource(R.string.cancel_refine_dialog_header),
        confirm = cancelRefineDialogConfirm
    )
}
