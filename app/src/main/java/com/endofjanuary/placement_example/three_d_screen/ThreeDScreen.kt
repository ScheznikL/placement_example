package com.endofjanuary.placement_example.three_d_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.endofjanuary.placement_example.utils.BottomBar
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.ThreeDScreenTopBar
import com.google.android.filament.gltfio.ResourceLoader
import io.github.sceneview.Scene
import io.github.sceneview.math.Position
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreen(
    navController: NavController,
    modelId: Int?
) {
    val viewModel = getViewModel<ThreeDScreenViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    ThreeDMain(
        viewModel = viewModel,
        snackbarHostState = snackbarHostState,
        modelId = modelId!!,
        navController = navController
        // modelInstance = viewModel.loadedInstancesState.value.data!!
    )
}

@Composable
fun ThreeDMain(
    modifier: Modifier = Modifier,
//    modelInstance: ModelInstance,
    viewModel: ThreeDScreenViewModel,
    snackbarHostState: SnackbarHostState,
    modelId: Int,
    navController: NavController,
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val assetManager = LocalContext.current.assets
    var resourceLoader = ResourceLoader(engine, true)

    val instanceState by remember {
        viewModel.loadedInstancesState
    }
    val modelUrl by remember {
        viewModel.modelImgUrl
    }
    val modelDescription = mutableStateOf(viewModel.model.value.modelDescription )

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
                navController = navController
            )
        }
    ) { padding ->
        Box(modifier = modifier.fillMaxSize().padding(padding)) {
            when (instanceState) {
                is Resource.Success -> {
                    val environmentLoader = rememberEnvironmentLoader(engine)
                    val cameraNode = rememberCameraNode(engine).apply {
                        position = Position(z = 4.0f)
                    }
                    //  val centerNode = rememberNode(engine)
                    //     .addChildNode(cameraNode)


                    //TODO try to auto rotate
//        val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
//        val cameraRotation by cameraTransition.animateRotation(
//            initialValue = Rotation(y = 0.0f),
//            targetValue = Rotation(y = 360.0f),
//            animationSpec = infiniteRepeatable(
//                animation = tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
//            ),
//        )

//                    val transitionState = remember {
//                        mutableStateOf(false)
//                    }

                    // Define transition
                    //    val transition = updateTransition(targetState = transitionState.value)

                    // Define animations
//                    val cameraRotation by transition.animateRotation(
//                        transitionSpec = {
//                            tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
//                        },
////            animationSpec = repeatable(
////                iterations = 1,
////                repeatMode = RepeatMode.Reverse,
////                animation = tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
////            )
//                        label = "CameraTransition"
//                    ) { state ->
//                        if (state) Rotation(360f) else Rotation(0f)
//                    }

                    Scene(
                        modifier = Modifier.fillMaxSize(),
                        engine = engine,
                        modelLoader = modelLoader,
                        cameraNode = cameraNode,
                        childNodes = listOf(
                            //centerNode,
                            rememberNode {
                                ModelNode(
                                    modelInstance = instanceState.data!!,
//                        modelInstance = modelLoader.createModelInstance(
//                            assetFileLocation = "models/damaged_helmet.glb"
//                        ),
                                    scaleToUnits = 1.0f
                                )
                            }),
                        environment = environmentLoader.createHDREnvironment(
                            assetFileLocation = "environments/sky_2k.hdr" //todo user choice ?*
                        )!!,
//                        onFrame = {
//                         //   centerNode.rotation = cameraRotation
//                            cameraNode.lookAt(centerNode)
//                        },
                    )
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
                }

                is Resource.Error -> LaunchedEffect(snackbarHostState) {
                    snackbarHostState.showSnackbar(
                        message = "Error: {instanceState.message}",
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