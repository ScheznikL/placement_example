package com.endofjanuary.placement_example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.endofjanuary.placement_example.ar_screen.ARScreen
import com.endofjanuary.placement_example.chat.ChatScreen
import com.endofjanuary.placement_example.loading.LoadingScreen
import com.endofjanuary.placement_example.modelsList.ModelsListScreen
import com.endofjanuary.placement_example.register_screen.RegistrationScreen
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreen
import com.endofjanuary.placement_example.ui.theme.Placement_exampleTheme
import home.HomeScreen


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Placement_exampleTheme {
                //  var appState: ARExampleAppState = rememberARExampleAppState()
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "reg_screen"
                ) {
                    composable("reg_screen"){
                        RegistrationScreen(navController = navController)
                    }
                    composable("chat_screen") {
                        ChatScreen(navController)
                    }
                    composable("home_screen") {
                        HomeScreen(navController)
                    }
                    composable(
                        //"ar_screen",
                        "ar_screen/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            }
                        )
                    ) {
                        val modelId = remember {
                            it.arguments?.getInt("id")
                        }
                        ARScreen(navController = navController, modelId = modelId ?: 0)
                    }
                    composable(
                        "threed_screen/{id}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            }
                        )
                    ) {
                        val modelId = remember {
                            it.arguments?.getInt("id")
                        }
                        ThreeDScreen(navController = navController, modelId = modelId)
                    }
                    composable(
                        "loading_screen/{prompt}",
                        arguments = listOf(
                            navArgument("prompt") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val model = remember {
                            it.arguments?.getString("prompt")
                        }
                        LoadingScreen(prompt = model ?: "none", navController = navController)
                    }
                    composable(
                        "models_list",
                    ) {
                        ModelsListScreen(navController = navController)
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
    
    