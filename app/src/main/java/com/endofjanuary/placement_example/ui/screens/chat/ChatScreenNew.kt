package com.endofjanuary.placement_example.ui.screens.chat

import ERROR_KEY
import TAG_MODEL
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.converters.MessageType
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatScreenNew(
    navController: NavController,
    //mainViewModel: MainViewModel
//    mainScope: LifecycleCoroutineScope
) {

    val viewModel = getViewModel<ChatScreenViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val scrollState = rememberLazyListState()
    val isKeyboardOpen by keyboardAsState()
    val focusManager = LocalFocusManager.current


    val messagesListState by remember { viewModel.messagesListState }/*    val isLoading by remember { viewModel.isLoading }
        val isError by remember { viewModel.loadError }*/
    val loadError by remember { mainViewModel.loadError }


    val modelToRefine by remember { mainViewModel.model }
    val autoRefine by remember { mainViewModel.autoRefine }

    val modelIds by remember { mainViewModel.isSuccess }
    val textInput by remember { viewModel.inputValueState }
    var isTextFieldEnabled = remember { true }
    val openCancelRefineDialog = remember { mutableStateOf(false) }
    val cancelRefineDialogConfirm = remember { mutableStateOf(false) }
    val openErrorDialog = mutableStateOf(false)

    val context = LocalContext.current

    LaunchedEffect(messagesListState) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    LaunchedEffect(isKeyboardOpen) {
        if (messagesListState.isNotEmpty()) scrollState.animateScrollToItem(messagesListState.size - 1)
    }

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(topBar = {
        ChatTopBar(navController = navController, autoRefineEnabled = autoRefine)
    }, modifier = Modifier.fillMaxSize(), snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            if (messagesListState.isEmpty()) {
                ElevatedCard(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(horizontal = 16.dp)
                        .weight(weight = 1.0f, fill = true), elevation = CardDefaults.cardElevation(
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
                            fontSize = 18.sp, fontWeight = Bold
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
                        items(count = messagesListState.size, itemContent = { index ->
                            MessageBubble(modifier = Modifier.animateItemPlacement(),
                                message = messagesListState[index],
                                onEdit = {
                                    viewModel.send("NEXT")
                                },
                                onDone = {
                                    isTextFieldEnabled = false
                                    viewModel.loadingModel()/*mainViewModel.generateModelEntryFromText( // TODO BACK
                                        viewModel.description!!
                                    )*/
                                    mainViewModel.generateModelEntryFromTextOnBackGround(
                                        viewModel.description!!
                                    )
                                },
                                onGetRefineOptions = {
                                    viewModel.loadingModel()
                                    mainViewModel.autoRefine(it)
                                },
                                onRefineCancel = {
                                    openCancelRefineDialog.value = true
                                })
                        })
                    })
            }
            val modelInfo = mainViewModel.modelWorkInfo.collectAsState()

            LaunchedEffect(modelInfo.value) { // TODO
                if (modelInfo.value.isNotEmpty()) {
                    val data = modelInfo.value.first().outputData

                    Log.d("$TAG_MODEL LOG", "Entered Launche eff $data")

                    Log.d("$TAG_MODEL LOG", "${data.getString(ERROR_KEY)}")
                }
            }

            LaunchedEffect(openCancelRefineDialog.value) {
                if (cancelRefineDialogConfirm.value) {
                    mainViewModel.saveByteInstancedModel(isFromText = true, isRefine = false)
                }
            }

            LaunchedEffect(modelToRefine) {
                if (modelToRefine.meshyId != "1111" && autoRefine) { //todo 1111
                    viewModel.isAutoRefineEnabled.value = autoRefine
                    viewModel.addAutoRefineMessage(context = context)
                }
            }

            LaunchedEffect(modelIds) {//todo back to koin VM at ChatScreen
                if (modelIds != null) { // means model was uploaded to room
                    viewModel.cancelLoading()
                    navController.navigate("transit_dialog/${modelIds?.second}/${modelIds?.first}")
                }
            }
            LaunchedEffect(loadError) {
                openErrorDialog.value = (loadError != null)
            }
            ErrorDialog(
                openDialog = openErrorDialog,
                errorMessage = loadError.toString(),
                onDone = {
                    viewModel.cancelLoading()
                    navController.popBackStack()
                })
            if (messagesListState.lastOrNull()?.messageType == MessageType.User || messagesListState.lastOrNull()?.messageType == MessageType.Assistant || messagesListState.lastOrNull()?.messageType == null) {
                UserInput(navController = navController,
                    isTextFieldEnabled = isTextFieldEnabled,
                    focusManager = focusManager,
                    onValueChange = { viewModel.inputValueState.value = it },
                    onSend = {
                        if (textInput.isNotBlank() && isTextFieldEnabled) {
                            viewModel.send(textInput)
                            viewModel.inputValueState.value = ""
                        }
                    })
            } else {
                Spacer(modifier = Modifier.height(10.dp))
            }
        }
    }
    CancelDialog(
        openDialog = openCancelRefineDialog,
        title = stringResource(R.string.cancel_refine_dialog_header),
        confirm = cancelRefineDialogConfirm
    )
}
