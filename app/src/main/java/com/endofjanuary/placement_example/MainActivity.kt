package com.endofjanuary.placement_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.runtime.remember
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.endofjanuary.placement_example.ar_screen.ARScreen
import com.endofjanuary.placement_example.chat.ChatScreenNew
import com.endofjanuary.placement_example.home.HomeScreen
import com.endofjanuary.placement_example.loading.LoadingScreen
import com.endofjanuary.placement_example.models_list_screen.ModelsListScreen
import com.endofjanuary.placement_example.register_screen.RegistrationScreen
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreen
import com.endofjanuary.placement_example.transit_dialog.ModelViewTypeDialog
import com.endofjanuary.placement_example.transit_dialog.NewModelType
import com.endofjanuary.placement_example.ui.theme.Placement_exampleTheme
import com.endofjanuary.placement_example.upload_image.UploadImage
import com.endofjanuary.placement_example.user_cabinet.UserProfile


class MainActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //   val downloader = DownloaderImpl(this)
        if (!hasRequiredPermissions()) { //todo move to proper place
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }

        val appContext = applicationContext
        setContent {
            Placement_exampleTheme {
                //  var appState: ARExampleAppState = rememberARExampleAppState()
                val navController = rememberNavController()
                NavHost(
                    navController = navController,
                    startDestination = "reg_screen"
                ) {
                    composable("reg_screen") {
                        RegistrationScreen(navController = navController)
                    }
                    composable("chat_screen") {
                        ChatScreenNew(navController)
                    }
                    composable("home_screen") {
                        HomeScreen(navController)
                    }
                    composable("user_profile") {
                        UserProfile(navController)
                    }
                    composable(
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
                        "threed_screen/{id}/{meshyId}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                            navArgument("meshyId") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val modelId = remember {
                            it.arguments?.getInt("id")
                        }
                        val meshyId = remember {
                            it.arguments?.getString("meshyId")
                        }
                        ThreeDScreen(
                            navController = navController,
                            // downloader = downloader,
                            modelId = modelId,
                            meshyId = meshyId,
                        )
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
                    dialog(
                        "com/endofjanuary/placement_example/upload_image/{type}",
                        arguments = listOf(
                            navArgument("type") {
                                type = NavType.BoolType
                            }
                        )
                    ) {
                        val type = remember {
                            it.arguments?.getBoolean("type")
                        }
                        UploadImage(navController = navController, typeGallery = type ?: true)
                    }

                    dialog(
                        "transit_dialog/{id}/{meshyId}",
                        arguments = listOf(
                            navArgument("id") {
                                type = NavType.IntType
                            },
                            navArgument("meshyId") {
                                type = NavType.StringType
                            }
                        )
                    ) {
                        val model = remember {
                            it.arguments?.getInt("id")
                        }
                        val meshyId = remember {
                            it.arguments?.getString("meshyId")
                        }
                        ModelViewTypeDialog(navController, modelId = model!!, meshyId!!)
                    }
                    dialog(
                        "new_model",
                    ) {
                        NewModelType(navController)
                    }
                }
            }
        }
    }

    companion object {
        // private const val kModelFile = "models/model_v2_chair.glb"
        const val kMaxModelInstances = 5
        private val CAMERAX_PERMISSIONS = arrayOf(
            Manifest.permission.CAMERA,
        )
    }

    private fun hasRequiredPermissions(): Boolean {
        return CAMERAX_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(
                applicationContext,
                it
            ) == PackageManager.PERMISSION_GRANTED
        }
    }
}
    
    