package com.endofjanuary.placement_example.upload_image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import org.koin.androidx.compose.getViewModel


@Composable
fun UploadImageDialog(
    navController: NavController
) {

    val viewModel = getViewModel<UploadImageViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val textInput by remember { viewModel.inputValueState }
    val uri by remember { viewModel.image }
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(), viewModel::onPhotoPickerSelect
    )
    val isUploading by remember { viewModel.isUploading }
    val isUploadingError by remember { viewModel.isUploadingError }
    var presignedUrl by remember { viewModel.presignedUrl }

    val isLoading by remember { mainViewModel.isLoading }
    var isSuccess by remember { mainViewModel.isSuccess }


    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(modifier = Modifier
        .padding(top = 50.dp, bottom = 50.dp)
        .wrapContentWidth()
        .wrapContentHeight(), snackbarHost = {
        SnackbarHost(hostState = snackbarHostState)
    }

    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(modifier = Modifier.padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                    ),
                    value = textInput,
                    onValueChange = { viewModel.inputValueState.value = it },
                    placeholder = { Text(text = stringResource(R.string.name_the_model_optional)) })
                Button(
                    onClick = {
                        pickImage.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                            )
                    },
                ) {
                    Text(stringResource(R.string.pick_an_image))
                }

                if (uri != Uri.EMPTY) {
                    if (isUploading) {
                        Box(contentAlignment = Alignment.Center) {
                            AsyncImage(
                                alpha = 0.5f,
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            CircularProgressIndicator()
                        }
                    } else if (isLoading) {
                        Box() {
                            AsyncImage(
                                alpha = 0.5f,
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        }
                    } else if (isUploadingError.isNotBlank()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(text = isUploadingError)
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 10.dp)
                        ) {
                            AsyncImage(
                                model = uri,
                                contentDescription = null,
                                modifier = Modifier.fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        }
                    }

                    val context = LocalContext.current
                    Button(
                        onClick = {
                            viewModel.getImagePresignedUrl(context)
                        },
                    ) {
                        Text(stringResource(id = R.string.proceed))
                    }

                    LaunchedEffect(presignedUrl) {
                        if (!presignedUrl.isNullOrBlank()) {
                            if (!isLoading || isSuccess == null) {
                                mainViewModel.loadModelEntryFromImage(
                                    url = presignedUrl!!, name = textInput
                                )
                                viewModel.presignedUrl.value = ""
                            }
                        }
                    }
                    LaunchedEffect(isSuccess) {
                        if (isSuccess != null) {
                            snackbarHostState.showSnackbar(
                                message = context.getString(R.string.loading_is_successful),
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                    Button(enabled = isSuccess != null,
                        onClick = { navController.popBackStack() }) {
                        Text(text = context.getString(R.string.done_button))
                    }
                }
            }
        }
    }
}
