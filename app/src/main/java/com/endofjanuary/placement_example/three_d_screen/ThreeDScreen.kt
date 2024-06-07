package com.endofjanuary.placement_example.three_d_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.chat.LottieDotsFlashing
import com.endofjanuary.placement_example.repo.DownloaderRepoImpl
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.ThreeDScreenTopBar
import com.endofjanuary.placement_example.utils.components.BottomBar
import com.endofjanuary.placement_example.utils.screens.DoDownload
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import org.koin.androidx.compose.getViewModel

@Composable
fun ThreeDScreen(
    navController: NavController,
    modelId: Int?,
    meshyId: String?,
) {
    val viewModel = getViewModel<ThreeDScreenViewModel>()
    val downloader = DownloaderRepoImpl(LocalContext.current)

    ThreeDMain(
        viewModel = viewModel,
        modelId = modelId!!,
        navController = navController,
        meshyId = meshyId!!,
    )
}

@Composable
fun ThreeDMain(
    modifier: Modifier = Modifier,
    viewModel: ThreeDScreenViewModel,
    modelId: Int,
    meshyId: String,
    navController: NavController,
) {

    val mainViewModel = getViewModel<MainViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)

    val refineIsError = remember { mainViewModel.loadError }
    val refineIsLoading = remember { mainViewModel.isLoading }
    val refineSuccess = remember { mainViewModel.isSuccess }
    val overwriteRefine = remember { mutableStateOf(false) }

    val instanceState by remember {
        viewModel.loadedInstancesState
    }
    val modelUrl by remember {
        viewModel.modelImgUrl
    }
    val modelPath = mutableStateOf(viewModel.modelFromRoom.value.data?.modelPath)
    val isFromText = mutableStateOf(viewModel.modelFromRoom.value.data?.isFromText)
    val isRefined = mutableStateOf(viewModel.modelFromRoom.value.data?.isRefine)

    val isAutoSaveEnabled by remember { mainViewModel.autoSave }
    val openDownloadDialog = remember { mutableStateOf(false) }
    val confirmDownload = remember { mutableStateOf(false) }

    val modelShortDescription: String? by remember { viewModel.modelDescriptionShorten }

    val deleteSuccess = remember { viewModel.modelDeleted }
    val downloadError by remember {
        viewModel.downloadError
    }

    val context = LocalContext.current

    LaunchedEffect(downloadError) {
        if (downloadError != null) {
            snackbarHostState.showSnackbar(
                message = downloadError.toString(), actionLabel = context.getString(R.string.error_OK)
            )
        }
    }

    LaunchedEffect(true) {
        viewModel.loadModelRemote(modelLoader, modelId)
    }

    LaunchedEffect(instanceState) {
        if (instanceState is Resource.Success && isAutoSaveEnabled) {
            openDownloadDialog.value = isAutoSaveEnabled
        }
    }
    Scaffold(bottomBar = { BottomBar(navController = navController) }, snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }, topBar = {
        ThreeDScreenTopBar(
            modelId = modelId,
            modelDescriptionShorten = modelShortDescription.toString(),
            navController = navController,
            mainViewModel = mainViewModel,
            meshyId = meshyId,
            overwrite = overwriteRefine,
            viewModel = viewModel,
            isFromText = isFromText.value ?: false,
            isRefined = isRefined.value ?: false
        )
    }) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LaunchedEffect(refineSuccess.value) {
                if (refineSuccess.value != null) {
                    if (snackbarHostState.showSnackbar(
                            message = context.getString(R.string.reload_page_question),
                            actionLabel = context.getString(R.string.yes)
                        ) == SnackbarResult.ActionPerformed
                    ) {
                        viewModel.loadModelFromPath(
                            modelLoader = modelLoader,
                            modelPath = mainViewModel.modelPath.value!!,
                            modelImageUrl = mainViewModel.modelImageUrl.value!!,
                            overwrite = overwriteRefine.value
                        )
                    }
                }
            }

            LaunchedEffect(deleteSuccess.value) {
                when (deleteSuccess.value) {
                    is Resource.Error -> {
                        snackbarHostState.showSnackbar(
                            message = context.getString(R.string.error_header) + deleteSuccess.value.message,
                        )
                    }

                    is Resource.Success -> {
                        if (snackbarHostState.showSnackbar(
                                message = context.getString(R.string.model_deleted_successfully), actionLabel = "OK"
                            ) == SnackbarResult.ActionPerformed
                        ) {
                            navController.popBackStack()
                        }
                    }

                    else -> {}
                }
            }

            when (instanceState) {
                is Resource.Success -> {
                    val environmentLoader = rememberEnvironmentLoader(engine)
                    val cameraNode = rememberCameraNode(engine).apply {
                        position = Position(z = 4.0f)
                    }

                    val currentNodes = remember {
                        viewModel.currentNodes
                    }

                    Scene(modifier = Modifier
                        .fillMaxSize()
                        .blur(200.dp),
                        engine = engine,
                        modelLoader = modelLoader,
                        cameraNode = cameraNode,
                        childNodes = currentNodes.toList(),
                        environment = environmentLoader.createHDREnvironment(
                            assetFileLocation = "environments/neutral.hdr"
                        )!!,
                        onViewUpdated = {
                            if (currentNodes.toList().size >= 2) {
                                cameraNode.setShift(xShift = 2.0, 0.0)
                            }
                        })
                    if (refineIsLoading.value) {
                        Box {
                            Text(text = stringResource(R.string.refining_in_process))
                            LottieDotsFlashing(
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.BottomStart)
                            )
                        }
                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current).data(modelUrl)
                            .crossfade(true).build(),
                        contentDescription = "modelDescription",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .width(192.dp)
                            .height(192.dp)
                            .align(Alignment.BottomEnd)
                            .navigationBarsPadding()
                            .padding(16.dp)
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer.copy(
                                    alpha = 0.5f
                                ), shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )


                    if (refineIsError.value != null) {
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.error)+refineIsError.value,
                            )
                        }
                    }
                }

                is Resource.Error -> {

                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodyLarge,
                            text = stringResource(id = R.string.error_header),
                            color = MaterialTheme.colorScheme.error
                        )
                        Text(
                            style = MaterialTheme.typography.bodyMedium,
                            text = instanceState.message.toString(),
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }

                is Resource.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodySmall,
                            text = stringResource(id = R.string.loading),
                        )
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }

                is Resource.None -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodySmall,
                            text = stringResource(id = R.string.loading),
                        )
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

        }
    }

    DoDownload(
        openDialog = openDownloadDialog,
        confirm = confirmDownload,
        modelFileName = modelShortDescription,
        onDownload = viewModel::onDownload,
        refinedUrl = mainViewModel.modelPath.value
    )
}

