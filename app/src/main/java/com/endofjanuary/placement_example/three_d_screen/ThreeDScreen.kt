package com.endofjanuary.placement_example.three_d_screen

import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.utils.Resource
import io.github.sceneview.Scene
import io.github.sceneview.animation.Transition.animateRotation
import io.github.sceneview.math.Position
import io.github.sceneview.math.Rotation
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCameraNode
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberEnvironmentLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNode
import org.koin.androidx.compose.getViewModel
import kotlin.time.Duration.Companion.seconds
import kotlin.time.DurationUnit

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreen(
    navController: NavController,
) {
    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val viewModel = getViewModel<ThreeDScreenViewModel>()
    val snackbarHostState = remember { SnackbarHostState() }

    val temp: Resource<Boolean> = Resource.Success(true)

    when (temp) {
        is Resource.Success -> Scaffold(
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            },
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = R.string.app_name)
                        )
                    },
                    colors = TopAppBarDefaults.mediumTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                        titleContentColor = MaterialTheme.colorScheme.onPrimary

                    ),
                )
            }
        ) { padding ->
            ThreeDMain(
                modifier = Modifier.padding(padding),
                viewModel = viewModel,
                snackbarHostState = snackbarHostState
                // modelInstance = viewModel.loadedInstancesState.value.data!!
            )
        }

        is Resource.Error -> LaunchedEffect(snackbarHostState) {
            snackbarHostState.showSnackbar(
                message = "Error: {instanceState.message}",
                actionLabel = "message"
            )
        }

        is Resource.Loading -> Column(
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

        is Resource.None -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
    }
}

@Composable
fun ThreeDMain(
    modifier: Modifier = Modifier,
//    modelInstance: ModelInstance,
    viewModel: ThreeDScreenViewModel,
    snackbarHostState: SnackbarHostState
) {
    Box(modifier = modifier.fillMaxSize()) {
        val engine = rememberEngine()
        val modelLoader = rememberModelLoader(engine)

        LaunchedEffect(key1 = viewModel) {
            //viewModel.loadModel(modelLoader, engine)
            viewModel.loadModelRemote(modelLoader, 2)
        }
        val instanceState by remember {
            viewModel.loadedInstancesState
        }

        when (instanceState) {
            is Resource.Success -> {
                val environmentLoader = rememberEnvironmentLoader(engine)
                val cameraNode = rememberCameraNode(engine).apply {
                    position = Position(z = 4.0f)
                }
                val centerNode = rememberNode(engine)
                    .addChildNode(cameraNode)


                //TODO try to auto rotate
//        val cameraTransition = rememberInfiniteTransition(label = "CameraTransition")
//        val cameraRotation by cameraTransition.animateRotation(
//            initialValue = Rotation(y = 0.0f),
//            targetValue = Rotation(y = 360.0f),
//            animationSpec = infiniteRepeatable(
//                animation = tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
//            ),
//        )

                val transitionState = remember {
                    mutableStateOf(false)
                }

                // Define transition
                val transition = updateTransition(targetState = transitionState.value)

                // Define animations
                val cameraRotation by transition.animateRotation(
                    transitionSpec = {
                        tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
                    },
//            animationSpec = repeatable(
//                iterations = 1,
//                repeatMode = RepeatMode.Reverse,
//                animation = tween(durationMillis = 17.seconds.toInt(DurationUnit.MILLISECONDS))
//            )
                    label = "CameraTransition"
                ) { state ->
                    if (state) Rotation(360f) else Rotation(0f)
                }

                Scene(
                    modifier = Modifier.fillMaxSize(),
                    engine = engine,
                    modelLoader = modelLoader,
                    cameraNode = cameraNode,
                    childNodes = listOf(centerNode,
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
                    onFrame = {
                        centerNode.rotation = cameraRotation
                        cameraNode.lookAt(centerNode)
                    }
                )
                Image(
                    modifier = Modifier
                        .width(192.dp)
                        .align(Alignment.BottomEnd)
                        .navigationBarsPadding()
                        .padding(16.dp)
                        .background(
                            color = MaterialTheme.colorScheme.primaryContainer.copy(
                                alpha = 0.5f
                            ),
                            shape = MaterialTheme.shapes.small
                        )
                        .padding(8.dp),
                    painter = painterResource(id = R.drawable.ic_launcher_background), //todo load image
                    contentDescription = null
                )
            }

            is Resource.Error -> LaunchedEffect(snackbarHostState) {
                snackbarHostState.showSnackbar(
                    message = "Error: {instanceState.message}",
                    actionLabel = "message"
                )
            }

            is Resource.Loading -> Column(
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

            is Resource.None -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
        }

    }
}