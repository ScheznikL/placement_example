package com.endofjanuary.placement_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.endofjanuary.placement_example.ar_screen.ARScreen
import com.endofjanuary.placement_example.chat.ChatScreen
import com.endofjanuary.placement_example.ui.theme.Placement_exampleTheme
import com.endofjanuary.placement_example.user_models.ThreeDScreen
import com.example.jetcaster.ui.ARExampleAppState
import com.example.jetcaster.ui.rememberARExampleAppState
import home.HomeScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        val currentModel = mutableStateOf("model_v2_chair")
//        val currentModelModule =
//            mutableStateOf("model_v2_chair") //model_v2_chair"

        setContent {
            Placement_exampleTheme {
                var appState: ARExampleAppState = rememberARExampleAppState()
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "home_screen"
                ) {
                    composable("chat_screen") {
                        ChatScreen(navController)
                    }
                    composable("home_screen") {
                        HomeScreen(navController = navController)
                    }
                    composable(
                        "ar_screen",
                        //"ar_screen/{prompt}",
//                        arguments = listOf(
//                            navArgument("prompt") {
//                                type = NavType.StringType
//                            }
//                        )
                    ) {
//                        val modelName = remember {
//                            it.arguments?.getString("prompt")
//                        }
                        ARScreen(prompt = "chair", navController = navController)
                    }
                    composable(
                        "threed_screen/{prompt}",
                        arguments = listOf(
                            navArgument("prompt") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val modelName = remember {
                            it.arguments?.getString("prompt")
                        }
                        ThreeDScreen(prompt = modelName ?: "none", navController = navController)
                    }
                }
            }
        }
    }

    companion object {
        // private const val kModelFile = "models/model_v2_chair.glb"
        const val kMaxModelInstances = 5
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
    
    