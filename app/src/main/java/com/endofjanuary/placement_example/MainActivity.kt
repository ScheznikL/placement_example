package com.endofjanuary.placement_example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.endofjanuary.placement_example.ui.dialogs.ModelViewTypeDialog
import com.endofjanuary.placement_example.ui.screens.chat.ChatScreenNew
import com.endofjanuary.placement_example.ui.screens.home_screen.HomeScreen
import com.endofjanuary.placement_example.ui.screens.models_list_screen.ModelsListScreen
import com.endofjanuary.placement_example.ui.screens.register_screen.RegistrationScreen
import com.endofjanuary.placement_example.ui.screens.upload_image.UploadImageScreen
import com.endofjanuary.placement_example.ui.screens.user_cabinet.UserProfileScreen
import com.endofjanuary.placement_example.ui.screens.visualize_screens.ar_screen.ARScreen
import com.endofjanuary.placement_example.ui.screens.visualize_screens.three_d_screen.ThreeDScreen
import com.endofjanuary.placement_example.ui.theme.Placement_exampleTheme
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import org.koin.androidx.viewmodel.ext.android.viewModel


class MainActivity : ComponentActivity() {
    private val scope = MainScope()

    override fun onDestroy() {
        super.onDestroy()
        scope.cancel()//todo scope
        Log.d("cleared", "MainActivity destroyed & scope.cancel")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!hasRequiredPermissions()) {
            ActivityCompat.requestPermissions(
                this, CAMERAX_PERMISSIONS, 0
            )
        }
        setContent {
            Placement_exampleTheme {

                val lifecycleOwner = LocalLifecycleOwner.current
                LaunchedEffect(lifecycleOwner)
                {
                    Log.d(
                        "cleared stop",
                        "MAIN ${lifecycleOwner.lifecycle.currentState} - $lifecycleOwner"
                    )
                }

                //val mainViewModel = getViewModel<MainViewModel>()
                val mainViewModel: MainViewModel by viewModel()
                val mainOwnerScope = lifecycleOwner.lifecycleScope

                val navController = rememberNavController()

                val openAreYouSure = remember { mutableStateOf(false) }
                val confirmDialog = remember { mutableStateOf(false) }

                /*   LaunchedEffect(mainViewModel.isLoading, mainViewModel.model) {
                       Log.d(
                           "Main load",
                           "isLoading-${mainViewModel.isLoading.value} loadError-${mainViewModel.loadError} descr:${mainViewModel.model.value.modelDescription}"
                       )
                   }*/

                NavHost(
                    navController = navController,
                    startDestination = "reg_screen"
                ) {
                    composable("reg_screen") {
                        RegistrationScreen(navController = navController)
                    }
                    composable("chat_screen") {
                        ChatScreenNew(navController, /*mainViewModel,mainOwnerScope*/)
                    }
                    composable("home_screen") {
                        HomeScreen(navController)
                    }
                    composable("user_profile") {
                        UserProfileScreen(navController)
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
                            modelId = modelId,
                            meshyId = meshyId,
                        )
                    }
                    composable(
                        "models_list",
                    ) {
                        ModelsListScreen(navController = navController)
                    }
                    composable(
                        "upload_image/{type}",
                        arguments = listOf(
                            navArgument("type") {
                                type = NavType.BoolType
                            }
                        )
                    ) {
                        val type = remember {
                            it.arguments?.getBoolean("type")
                        }
                        UploadImageScreen(navController = navController, typeGallery = type ?: true)
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
                }

                /*BackHandler(enabled = (currentRoute(navController) == Screen.Home.route)) {
                    openDialog.value = true
                }*/
                BackHandler(false) { //todo BackHandler
                    openAreYouSure.value = true
                }

                val callback = object : OnBackPressedCallback(
                    true // default to enabled
                ) {
                    override fun handleOnBackPressed() { //out of APP
                        openAreYouSure.value = true
                        Log.d("BACK", "<-----")
                        /*   if(mainViewModel.isLoading.value) {

                           }*/
                    }

                }
                onBackPressedDispatcher.addCallback(this, callback)


                AreYouSureDialog(openDialog = openAreYouSure, confirm = confirmDialog){
                    finish()
                }
            }
        }
    }


    companion object {
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

@Composable
fun AreYouSureDialog(
    openDialog: MutableState<Boolean>,
    confirm: MutableState<Boolean>,
    onConfirm: (() -> Unit)? = null
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        },
            title = {
                Text(
                    text = "Cancel loading",
                    textAlign = TextAlign.Justify
                )
            },
            text = {
                Column {
                    Text(
                        "Are you sure want to go back and cancel loading ?",
                        textAlign = TextAlign.Justify
                    )
                    Text(
                        "The model probably won't be created...",
                        textAlign = TextAlign.Justify
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm?.invoke()
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.dismiss))
                }
            })
    }
}
    
    