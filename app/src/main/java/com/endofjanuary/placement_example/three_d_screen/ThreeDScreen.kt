package com.endofjanuary.placement_example.three_d_screen

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.DownloaderImpl
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.chat.LottieDotsFlashing
import com.endofjanuary.placement_example.utils.BottomBar
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.ThreeDScreenTopBar
import com.google.android.filament.gltfio.ResourceLoader
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.model.model
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import org.koin.androidx.compose.getViewModel


const val MODEL_PATH =
    "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018eb80f-70d4-7ac0-b540-346a5f83c255/output/model.glb?Expires=4866048000&Signature=QU0jukq87VxKR03eucrlQzvLbULXlX9RbbjSO~4pSwBy2DRp5OLiwn~qvIo7qC-AgUuFh5doENZU15sb6lJxyHmWHadVwI8NxlQaHLKlCDFTFF4t-3exoFyRfIxUqHwHWg0h7~cXtsrFQljqyCLsuX-YzEj7BTbhxEIKXByJaisWHNxR0pThljxD~AtYKrZN5HVJB8Mid2xBHSak48rh7ew1Wox8yxx8PmJgj9mD5u4kBLzDLcBSJ7gvfn~nY-lFKrypXPmNs-k5x0GjBoQL~SKgzoEXczuqGkNCp990uT1n7kQf7hboRgdGhLl7snnHLe1JyREVG9nwc~1ioWA6rw__&Key-Pair-Id=KL5I0C8H7HX83"
const val IMG_PATH =
    "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018eb80f-70d4-7ac0-b540-346a5f83c255/output/preview.png?Expires=4866048000&Signature=fRovk8~XV3lryrT~zKhpjvMQj45n4oO6jJTgOSvN5IxA3jCe2D80w91dHn~yZgwpYnPelE7dt6peGNrIJL-KYUKCEsLsNuov86K6E0M-aNWAz~1kq0GDmW5trLZHdDkNk6UFueVZgAfXTjpZdYjkZJCJRPTXCasuur2tXuILVIldvkocFRqeU4NZgwnotYhyhDbgxNY7ptJyoe~is8R~FVnfLTGWj5JP5JZjsSoI4LJyPU-A4yUXJVPWpAudTKWIwQRIBJJOscMz~-JhwkZwJAe1y2Pe3n9nIGXbhnn0WvfB4-rHQ4aYtjogt0cJMiETxD9ht-~nRUSAkhv5~IRxnw__&Key-Pair-Id=KL5I0C8H7HX83"

@Composable
fun ThreeDScreen(
    navController: NavController,
    modelId: Int?,
    meshyId: String?,
) {
    val viewModel = getViewModel<ThreeDScreenViewModel>()
    val downloader = DownloaderImpl(LocalContext.current)

    ThreeDMain(
        viewModel = viewModel,
        modelId = modelId!!,
        navController = navController,
        meshyId = meshyId!!,
        downloader = downloader
    )
}

@Composable
fun ThreeDMain(
    modifier: Modifier = Modifier,
    viewModel: ThreeDScreenViewModel,
    modelId: Int,
    meshyId: String,
    navController: NavController,
    downloader: DownloaderImpl
) {
    val mainViewModel = getViewModel<MainViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    var resourceLoader = ResourceLoader(engine, true)

    var blurOnRefine = remember { 0.dp }
    val refineIsError = remember { mainViewModel.loadError }
    val refineIsLoading = remember { mainViewModel.isLoading }
    val refineSuccess = remember { mainViewModel.isSuccess }
    val overwriteRefine = remember { mutableStateOf(false) }
    var reloadModel = remember { mutableStateOf(SnackbarResult.Dismissed) }

    val instanceState by remember {
        viewModel.loadedInstancesState
    }
    val modelUrl by remember {
        viewModel.modelImgUrl
    }
    val modelPath = mutableStateOf(viewModel.modelFromRoom.value.data?.modelPath)

    val modelDescription: String? =
        remember { viewModel.modelFromRoom.value.data?.modelDescription }

    val deleteSuccess = remember { viewModel.modelDeleted }

    LaunchedEffect(true) {
        //  viewModel.loadInstanceNone()
        //  viewModel.loadModelLocal(modelLoader,/*engine,*/ modelLoader.assetLoader, assetManager, resourceLoader)
        viewModel.loadModelRemote(modelLoader, modelId)
    }

    Scaffold(
        bottomBar = { BottomBar(navController = navController) },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        topBar = {
            ThreeDScreenTopBar(
                modelId = modelId,
                modelDescription = modelDescription,
                navController = navController,
                mainViewModel = mainViewModel,
                meshyId = meshyId,
                modelPath = modelPath,
                overwrite = overwriteRefine,
                viewModel = viewModel,
                downloader = downloader
            )
        }
    ) { padding ->
        Box(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LaunchedEffect(refineSuccess.value) {
                if (refineSuccess.value != null) {
                    //    if (!overwriteRefine.value) {
                    if (snackbarHostState.showSnackbar(
                            message = "Refine model is Done\n\r Would you like to reload page ?",
                            actionLabel = "OK"
                        ) == SnackbarResult.ActionPerformed
                    ) {
                        Log.d("loadModel R", " loadModelFromPath cause success")

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
                            message = "Error when deleting model...",
                        )
                    }

                    is Resource.Success -> {
                        if (snackbarHostState.showSnackbar(
                                message = "Model deleted successfully",
                                actionLabel = "OK"
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
                    //TODO try to auto rotate
                    /**
                     * Ask to reload page if overwrite not requested
                     */

                    Log.d("loadModel R", "data - ${instanceState.data!!.model.instance}")


                    val newNode by remember {
                        viewModel.newNode
                    }
                    val node = rememberNode {
                        ModelNode(
                            modelInstance = instanceState.data!!,
                            scaleToUnits = 1.0f
                        )
                    }
                    val currentNodes = remember {
                        viewModel.currentNodes
                    }

                    Scene(
                        modifier = Modifier
                            .fillMaxSize()
                            .blur(200.dp),
                        engine = engine,
                        modelLoader = modelLoader,
                        cameraNode = cameraNode,
                        childNodes = currentNodes.toList(),
                        environment = environmentLoader.createHDREnvironment(
                            assetFileLocation = "environments/sky_2k.hdr" //todo user choice ?*
                        )!!,
//                        onFrame = {
//                         //   centerNode.rotation = cameraRotation
//                            cameraNode.lookAt(centerNode)
//                        },
                    )

                    /**
                     *     Button(
                     *                         modifier = Modifier.align(Alignment.TopStart),
                     *                         onClick = {
                     *                             viewModel.loadModelFromPath(
                     *                                 modelLoader,
                     *                                 modelPath = MODEL_PATH,
                     *                                 modelImageUrl = IMG_PATH,
                     *                                 true
                     *                             )
                     *                         }) {
                     *                         Text(text = "Temp Button")
                     *                     }
                     */


                    /**
                     *if doesn't request overwrite
                     **/
                    if (refineIsLoading.value /*&& !overwriteRefine.value*/) {
                        Box {
                            Text(text = "Refining is in process ...")
                            LottieDotsFlashing(
                                modifier = Modifier
                                    .size(100.dp)
                                    .align(Alignment.BottomStart)
                            )
                        }
                    }
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(modelUrl)
                            .crossfade(true)
                            .build(),
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
                                ),
                                shape = MaterialTheme.shapes.small
                            )
                            .padding(8.dp)
                    )


                    if (refineIsError.value != null) {
                        LaunchedEffect(snackbarHostState) {
                            snackbarHostState.showSnackbar(
                                message = "Error: ${refineIsError.value}",
                                actionLabel = "message"
                            )
                        }
                    }
                }

                is Resource.Error -> LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Error: ${instanceState.message}",
                        actionLabel = "message"
                    )
                }

                is Resource.Loading -> {
                    Column(
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            style = MaterialTheme.typography.bodySmall,
                            text = "loading model...",
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
                            text = "loading...",
                        )
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WarningDialog(
    modelId: Int
) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Dialog(onDismissRequest = { /* Handle dismiss if needed */ }) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.error)
                    .padding(16.dp)
            ) {
                Text(
                    "There is no model with $modelId",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )
                Button(onClick = { /* Handle button click */ }) {
                    Text("OK")
                }
            }
        }
    }
}