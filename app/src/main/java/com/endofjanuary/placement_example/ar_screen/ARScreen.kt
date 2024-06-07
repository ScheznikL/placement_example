package com.endofjanuary.placement_example.ar_screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreenViewModel
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.components.BottomBar
import com.endofjanuary.placement_example.utils.screens.ErrorScreen
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView
import org.koin.androidx.compose.getViewModel


@Composable
fun ARScreen(
    modelId: Int?,
    navController: NavController,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        bottomBar = { BottomBar(navController = navController) }
    ) { contentPadding ->
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            ARSceneDisplay(modelId)
        }
    }

}


@Composable
fun ARSceneDisplay(
    modelId : Int?
) {

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val materialLoader = rememberMaterialLoader(engine)
    val cameraNode = rememberARCameraNode(engine)
    val childNodes = rememberNodes()
    val view = rememberView(engine)
    val collisionSystem = rememberCollisionSystem(view)
    var planeRenderer by remember { mutableStateOf(true) }

    val modelInstances = remember { mutableListOf<ModelInstance>() }
    var trackingFailureReason by remember {
        mutableStateOf<TrackingFailureReason?>(null)
    }
    var frame by remember { mutableStateOf<Frame?>(null) }

    val viewModel = getViewModel<ThreeDScreenViewModel>()

    LaunchedEffect(key1 = viewModel) {
        viewModel.loadModelRemote(modelLoader, modelId ?: 1)
    }

    val instanceState by remember {
        viewModel.loadedInstancesState
    }
    when (instanceState) {
        is Resource.Error -> {
            ErrorScreen(message = instanceState.message!!)
        }

        is Resource.Success -> {
            ARScene(
                modifier = Modifier.fillMaxSize(),
                childNodes = childNodes,
                engine = engine,
                view = view,
                modelLoader = modelLoader,
                collisionSystem = collisionSystem,
                sessionConfiguration = { session, config ->
                    config.depthMode =
                        when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                            true -> Config.DepthMode.AUTOMATIC
                            else -> Config.DepthMode.DISABLED
                        }
                    config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                    config.lightEstimationMode =
                        Config.LightEstimationMode.ENVIRONMENTAL_HDR
                },
                cameraNode = cameraNode,
                planeRenderer = planeRenderer,
                onTrackingFailureChanged = {
                    trackingFailureReason = it
                },
                onSessionUpdated = { session, updatedFrame ->
                    frame = updatedFrame

                    if (childNodes.isEmpty()) {
                        updatedFrame.getUpdatedPlanes()
                            .firstOrNull { it.type == Plane.Type.HORIZONTAL_UPWARD_FACING }
                            ?.let { it.createAnchorOrNull(it.centerPose) }?.let { anchor ->
                                childNodes += viewModel.createAnchorNode(
                                    engine = engine,
                                    materialLoader = materialLoader,
                                    modelInstances = instanceState.data!!,
                                    anchor = anchor,
                                )
                            }
                    }
                },
                onGestureListener = rememberOnGestureListener(
                    onDoubleTap = { motionEvent, node ->
                        val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                        hitResults?.firstOrNull {
                            it.isValid(
                                depthPoint = false,
                                point = false
                            )
                        }?.createAnchorOrNull()
                            ?.let { anchor ->
                                planeRenderer = false
                                childNodes += viewModel.createAnchorNode(
                                    engine = engine,
                                    materialLoader = materialLoader,
                                    modelInstances = instanceState.data!!,
                                    anchor = anchor,
                                )
                            }
                    },
                )
            )
        }

        else -> {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    style = MaterialTheme.typography.bodySmall,
                    text = stringResource(R.string.loading),
                    )
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
        }
    }
}
