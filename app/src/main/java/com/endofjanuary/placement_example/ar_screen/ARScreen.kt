package com.endofjanuary.placement_example.ar_screen

import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.utils.Resource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARScreen(
    //modelEntry: ModelEntry = ModelEntry("", "", ""),
    //modelName: String,
    prompt: String = "chair",
    navController: NavController,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    //val viewModel = getViewModel<ARScreenViewModel>(parameters = { parametersOf(prompt) })
    val viewModel = getViewModel<ARScreenViewModel>()


    val modelEntry = produceState<Resource<ModelEntry>>(initialValue = Resource.Loading()) {
        value = viewModel.loadModelEntry(prompt = prompt)
    }.value


    // val viewModel: ARScreenViewModel by koinViewModel(parametersOf(prompt))
    //val modelEntry = remember { viewModel.model }
    /*  val loadError by remember { viewModel.loadError }
      val isLoading by remember { viewModel.isLoading }
      val loadSuccess by remember {viewModel.isSuccess }*/

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Box(
            contentAlignment = Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(contentPadding)
        ) {
            when (modelEntry) {
                is Resource.Error -> {
                    LaunchedEffect(snackbarHostState) {
                        snackbarHostState.showSnackbar(
                            message = "Error: ${modelEntry.message}",
                            actionLabel = "message"
                        )
                    }
                    //ErrorScreen(modelEntry.message ?: "error")
                }

                is Resource.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                is Resource.Success -> ARMain(modelEntry = modelEntry.data!!, viewModel = viewModel)
                is Resource.None -> TODO()
            }
        }
    }

}

@Composable
fun ARMain(
    //modelEntry: MutableState<ModelEntry>,
    modelEntry: ModelEntry,
    viewModel: ARScreenViewModel,
    modifier: Modifier = Modifier
) {
    Box(modifier = Modifier.fillMaxSize()) {
        ARSceneDisplay(modelEntry = modelEntry, viewModel = viewModel)////
        /* Row(
             modifier = Modifier.fillMaxWidth(),
             verticalAlignment = Alignment.CenterVertically,
             horizontalArrangement = Arrangement.SpaceAround,
         ) {
             CoilImage(
                 request = ImageRequest.Builder(LocalContext.current)
                     .data(modelEntry.modelImageUrl)
                     .build(),
                 contentDescription = modelEntry.modelDescription.split(' ').get(0),
                 fadeIn = true,
                 modifier = Modifier
                     .size(120.dp)
                     .align(CenterVertically)
                     .clip(CircleShape)
                     .border(width = 2.dp, color = Color.Cyan)
                     .clickable {
                         //TODO go just 3d (nav)
                     }
             ) {
                 CircularProgressIndicator(
                     color = MaterialTheme.colorScheme.primary,
                     modifier = Modifier.scale(0.5f)
                 )
             }

         }
     */
    }
}

//from local storage
@Composable
fun DecoratedImage(
    thumbnailPath: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .border(width = 2.dp, color = Color.Cyan),
    ) {
        //todo add progress
        val path: String = Environment.getExternalStorageDirectory().path + "/myImage.jpg"
        val bitmap = BitmapFactory.decodeFile(path).asImageBitmap()
        Image(bitmap = bitmap, contentDescription = null)
        /*Image(
            painter = painterResource(id = thumbnailPath),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )*/
    }
}


@Composable
fun ARSceneDisplay(
    viewModel: ARScreenViewModel,
    modelEntry: ModelEntry,
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

    //var loadedInstances = remember { viewModel.loadedInstances }

    val instances =
       /* produceState<Resource<MutableList<ModelInstance>?>>(initialValue = Resource.Loading()) {
            value =
                viewModel.loadGlbModel(modelLoader = modelLoader, modelPath = modelEntry.modelPath)
//                viewModel.loadGlbModel(
//                    modelLoader = modelLoader,
//                    //modelPath = "https://github.com/kelvinwatson/glb-files/raw/main/DamagedHelmet.glb",
//                    modelPath = "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018d8e69-28c2-7a80-8c36-fde62fe0d134/output/model.glb?Expires=1707609600&Signature=FZj0vZ~i9zNHCRqv0TJm~zVDzqQUSL9SYt8J3k0aiPK5RUDhsRSGgrqLRQYxwnMGdkqRihR2PqloDWLEWGmvPHoutqWDpD4hd7Og26YXO2XFNubkRwVLem0xV3EfPt61wYI~1E5fSvZHc9D3jljtQbsT4xmNg0iMAUx7zbo5aeayShvVqCjf7gskZqtnLmzJZ8mr1YDk2y9fqIUa44GSsxk9dyFeq7jzFVTCk8lmTPx0t8Qxclqm5lyXh3LfsrCF-jMJPH9g23FTUDLI3GI5Ha4~ip8sOB8H~O1ImXaZiwGvQ8BY3MUK9DS4ue6sc~drHTfI73GbumtkbRT3D2RpOg__&Key-Pair-Id=KL5I0C8H7HX83",
//                )

        }.value*/

    LaunchedEffect(key1 = viewModel){
        viewModel.loadGlbModel(modelLoader = modelLoader, modelPath = modelEntry.modelPath)
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
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstances = modelInstances,
                                    anchor = anchor,
                                    modelPath = modelEntry.modelPath
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
                                Log.d("onDoubleTap", modelEntry.modelPath)
                                planeRenderer = false
                                childNodes += viewModel.createAnchorNode(
                                    engine = engine,
                                    modelLoader = modelLoader,
                                    materialLoader = materialLoader,
                                    modelInstances = modelInstances,
                                    anchor = anchor,
                                    modelPath = modelEntry.modelPath
                                )
                            }
                    },
                    /* onSingleTapConfirmed = { motionEvent, node ->
                         if (node == null) {
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
                                         modelLoader = modelLoader,
                                         materialLoader = materialLoader,
                                         modelInstances = modelInstances,
                                         anchor = anchor,
                                         modelPath =  modelEntry.value.modelPath
                                     )
                                 }
                         }
                     }*/

                )
            )
        }

        else -> {

//        LaunchedEffect(true) {
//            viewModel.loadGlbModel(modelLoader = modelLoader, modelPath = modelEntry.modelPath)
//        }

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
    }


}
