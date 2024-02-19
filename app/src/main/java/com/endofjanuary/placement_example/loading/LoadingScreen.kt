package com.endofjanuary.placement_example.loading

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.utils.Resource
import io.github.sceneview.rememberEngine
import io.github.sceneview.rememberModelLoader
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    prompt: String,
    navController: NavController
) {
    val viewModel = getViewModel<LoadingScreenViewModel>()
    val modelEntry = produceState<Resource<ModelEntry>>(initialValue = Resource.Loading()) {
        value = viewModel.loadModelEntry(prompt = prompt)
    }.value

    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { contentPadding ->
        Box(
            contentAlignment = Alignment.Center,
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
                }

                is Resource.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                is Resource.Success -> {

                    val engine = rememberEngine()
                    val modelLoader = rememberModelLoader(engine)
                    val context = LocalContext.current

                    LaunchedEffect(key1 = viewModel){
                        viewModel.saveByteInstancedModel(context = context, 1)
                    }

                    val byteArrayState by remember {
                        viewModel.byteArrayState
                    }
//                    val res = produceState<Resource<Boolean>>(initialValue = Resource.Loading()) {
//                        //value = viewModel.loadSaveGlbModel(modelLoader = modelLoader)
//                        value = viewModel.saveByteInstancedModel(context = LocalContext.current, count = 1)
//
//                    }.value

                    when (byteArrayState) {
                        is Resource.Error -> {
                            LaunchedEffect(snackbarHostState) {
                                snackbarHostState.showSnackbar(
                                    message = "Error: ${modelEntry.message}",
                                    actionLabel = "message"
                                )
                            }
                        }

                        is Resource.Loading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        is Resource.None -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                        is Resource.Success -> {
                            LaunchedEffect(snackbarHostState) {
                                snackbarHostState.showSnackbar(
                                    message = "Model successfully saved !",
                                    actionLabel = "success"
                                )
                            }
                            navController.navigate("home_screen")
                            //HomeScreen(navController = navController)
                        } // save to DataStore and go home
                    }

                }

                is Resource.None -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
        }
    }
}