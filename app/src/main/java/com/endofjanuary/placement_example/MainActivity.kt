package com.endofjanuary.placement_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.endofjanuary.placement_example.ui.theme.Placement_exampleTheme
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import com.google.ar.core.Config
import com.google.ar.core.Frame
import com.google.ar.core.Plane
import com.google.ar.core.TrackingFailureReason
import io.github.sceneview.ar.ARScene
import io.github.sceneview.ar.arcore.createAnchorOrNull
import io.github.sceneview.ar.arcore.getUpdatedPlanes
import io.github.sceneview.ar.arcore.isValid
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.ar.rememberARCameraNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import io.github.sceneview.rememberCollisionSystem
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberMaterialLoader
import io.github.sceneview.rememberModelLoader
import io.github.sceneview.rememberNodes
import io.github.sceneview.rememberOnGestureListener
import io.github.sceneview.rememberView

//private const val kModelFile = "models/damaged_helmet.glb"
private const val kModelFile = "models/model_v2_chair.glb"
private const val kMaxModelInstances = 10

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
/*
        val api = provideMeshyApi()
        val repo = MeshyRepo(api)*/

        setContent {
            Placement_exampleTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        var currentModel by remember {
                            mutableStateOf("model_v2_chair") //model_v2_chair"
                        }

                        ARScreenElse(currentModel)
                        Menu(modifier = Modifier.align(Alignment.BottomCenter)){
                            currentModel = it
                        }
                        //ARScreenElse("damaged_helmet")
                        // ARSceneWithComposeDialog()
                    }
                }
            }
        }
    }
}

@Composable
fun Menu(modifier: Modifier, onClick:(String)->Unit) {
    var currentIndex by remember {
        mutableStateOf(0)
    }
    val itemsList = listOf(
        ModelObject("model_v2_chair", R.drawable.preview_model),
        ModelObject("model_v2_refine", R.drawable.refine_model),
        ModelObject("damaged_helmet", R.drawable.refine_model),
    )

    fun updateIndex(offset: Int) {
        currentIndex = (currentIndex + offset + itemsList.size) % itemsList.size
        onClick(itemsList[currentIndex].name)
    }
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        IconButton(onClick = { updateIndex(-1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowLeft, contentDescription = "previous")
        }
        CircularImage(thumbnailId = itemsList[currentIndex].thumbnailId)
        IconButton(onClick = { updateIndex(1) }) {
            Icon(imageVector = Icons.Default.KeyboardArrowRight, contentDescription = "next")
        }
    }
}

@Composable
fun CircularImage(
    thumbnailId: Int,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(140.dp)
            .clip(CircleShape)
            .border(width = 2.dp, color = Color.Cyan),
    ) {
        Image(
            painter = painterResource(id = thumbnailId),
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
    }
}

/*
@Composable
fun ARScreen(dsiplModel: String) {
    /* val nodes = remember {
        mutableListOf<Node>() // ArNode
    }*/

    val engine = rememberEngine()
    val modelLoader = rememberModelLoader(engine)
    val model = modelLoader.createModel("models/${dsiplModel}.glb")
    var frame by remember { mutableStateOf<Frame?>(null) }
    val childNodes = rememberNodes()


    /*val modelNode = remember {
        mutableStateOf<ArModelNode?>(null)
    }*/

    val placeModelButton by remember {
        mutableStateOf(false)
    }
    Box(modifier = Modifier.fillMaxSize()) {
        ARScene(
            modifier = Modifier.fillMaxSize(),
            //nodes = nodes,
            planeRenderer = true,// ?
            sessionConfiguration = { session, config ->
                config.depthMode =
                    when (session.isDepthModeSupported(Config.DepthMode.AUTOMATIC)) {
                        true -> Config.DepthMode.AUTOMATIC
                        else -> Config.DepthMode.DISABLED
                    }
                config.instantPlacementMode = Config.InstantPlacementMode.LOCAL_Y_UP
                config.lightEstimationMode =
                    Config.LightEstimationMode.DISABLED
            },
            onViewCreated = {
                //arSceneView.lightEstimationMode = Config.LightEstimationMode.DISABLED
                this.planeRenderer.isShadowReceiver = false
                val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                val anchor = hitResults?.firstOrNull {
                    it.isValid(depthPoint = false, point = false)
                }?.createAnchorOrNull()
                modelNode.value = ModelNode(this.engine, PlacementMode.INSTANT).apply {
                    loadModelGlbAsync(
                        glbFileLocation = "models/${model}.glb",
                        scaleToUnits = 0.8f
                    ) {

                    }
                    onAnchorChanged = {
                        placeModelButton = !isAnchored
                    }
                    onHitResult = { node, hitResult ->
                        placeModelButton = node.isTracking
                    }

                }
                nodes.add(modelNode.value!!)
            },
            onSessionCreate = {
                planeRenderer.isVisible = false
            }
        )
        if (placeModelButton) {
            Button(onClick = {
                modelNode.value?.anchor()
            }, modifier = Modifier.align(Alignment.Center)) {
                Text(text = "Place It")
            }
        }
    }
}
*/

fun createAnchorNode(
    engine: Engine,
    modelLoader: ModelLoader,
    materialLoader: MaterialLoader,
    modelInstances: MutableList<ModelInstance>,
    anchor: Anchor,
    modelFile : String
): AnchorNode {
    val anchorNode = AnchorNode(engine = engine, anchor = anchor)
    val modelNode = ModelNode(
        modelInstance = modelInstances.apply {
            if (isEmpty()) {
                this += modelLoader.createInstancedModel(modelFile, kMaxModelInstances)
//                this += modelLoader.createInstancedModel(kModelFile, kMaxModelInstances)
            }
        }.removeLast(),
        // Scale to fit in a 0.5 meters cube
        scaleToUnits = 0.5f
    ).apply {
        // Model Node needs to be editable for independent rotation from the anchor rotation
        isEditable = false
    //todo true init
    }
    val boundingBoxNode = CubeNode(
        engine,
        size = modelNode.extents,
        center = modelNode.center,
        materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
    ).apply {
        isVisible = false
    }
    modelNode.addChildNode(boundingBoxNode)
    anchorNode.addChildNode(modelNode)

    listOf(modelNode, anchorNode).forEach {
        it.onEditingChanged = { editingTransforms ->
            boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
        }
    }
    return anchorNode
}

@Composable
fun ARScreenElse(modelName: String) {
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
                        childNodes += createAnchorNode(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            modelInstances = modelInstances,
                            anchor = anchor,
                            modelFile = "models/$modelName.glb"
                        )
                    }
            }
        },
        onViewUpdated = {
            var j = 0;
        },
        onViewCreated = {
                        var i = 0;
        },
        onGestureListener = rememberOnGestureListener(
            onDoubleTap = {  motionEvent, node ->
                val hitResults = frame?.hitTest(motionEvent.x, motionEvent.y)
                hitResults?.firstOrNull {
                    it.isValid(
                        depthPoint = false,
                        point = false
                    )
                }?.createAnchorOrNull()
                    ?.let { anchor ->
                        planeRenderer = false
                        childNodes += createAnchorNode(
                            engine = engine,
                            modelLoader = modelLoader,
                            materialLoader = materialLoader,
                            modelInstances = modelInstances,
                            anchor = anchor,
                            modelFile = "models/{$modelName}.glb"
                        )
                    }
            },
            onSingleTapConfirmed = { motionEvent, node ->
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
                            childNodes += createAnchorNode(
                                engine = engine,
                                modelLoader = modelLoader,
                                materialLoader = materialLoader,
                                modelInstances = modelInstances,
                                anchor = anchor,
                                modelFile = "models/{$modelName}.glb"
                            )
                        }
                }
            })
    )

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ARSceneWithComposeDialog() {
    // AR Scene setup (ARCore or any other AR framework)

    // Compose UI overlay on top of AR scene
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // AR Scene content

        // Compose UI Dialog
        Dialog(onDismissRequest = { /* Handle dismiss if needed */ }) {
            Column(
                modifier = Modifier
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                TextField(
                    value = TextFieldValue(""),
                    onValueChange = { /* Handle text change */ },
                    label = { Text("Enter text") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                )

                // Add other UI elements or buttons in the dialog
                Button(onClick = { /* Handle button click */ }) {
                    Text("Submit")
                }
            }
        }
    }
}


data class ModelObject(var name: String, var thumbnailId: Int)
    
    