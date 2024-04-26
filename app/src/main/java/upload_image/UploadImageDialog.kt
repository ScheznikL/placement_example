package upload_image

import android.net.Uri
import android.util.Log
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
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.endofjanuary.placement_example.MainViewModel
import org.koin.androidx.compose.getViewModel


@Composable
fun UploadImageDialog(
    navController: NavController
) {

    val viewModel = getViewModel<UploadImageViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val textInput by remember { viewModel.inputValueState }
    val uri by remember { viewModel.selectedUri }
    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        viewModel::onPhotoPickerSelect
    )
    val isUploading by remember { viewModel.isUploading }
    val isUploadingError by remember { viewModel.isUploadingError }
    var presignedUrl by remember { viewModel.presignedUrl }

    val isLoading by remember { mainViewModel.isLoading }
    var isSuccess by remember { mainViewModel.isSuccess }


    val snackbarHostState = remember { SnackbarHostState() }

    /*    Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {*/
    Scaffold(
        modifier = Modifier
            .padding(top = 50.dp, bottom = 50.dp)
            .wrapContentWidth()
            .wrapContentHeight(),
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }

    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            /*  Text(
                  text = "Name the model *optional",
                  textAlign = TextAlign.Justify
              )*/
            Spacer(modifier = Modifier.height(24.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                /* Column(
                     //verticalArrangement = Arrangement.Center,
                     horizontalAlignment = Alignment.CenterHorizontally,
                     modifier = Modifier
                     .weight(3f)
                     //.border(1.dp, Color.DarkGray)
                 ) {*/
                OutlinedTextField(
                    modifier = Modifier
                        .padding(5.dp),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Text,
                        imeAction = ImeAction.Default,
                    ),
                    value = textInput,
                    onValueChange = { viewModel.inputValueState.value = it },
                    placeholder = { Text(text = "Name the model *optional") }
                )
                Button(
                    onClick = {
                        pickImage
                            .launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly
                                )
                            )
                    },
                ) {
                    Text("Pick an image")
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
                            Log.d(
                                "loadModel UI",
                                "is Loading"
                            )
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
                            colors = CardDefaults
                                .cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                        ) {
                            Text(text = isUploadingError)
                        }
                    } else {
                        Card(
                            modifier = Modifier
                                // .weight(1f)
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
                            viewModel.getPresignedUrl(context)
                        },
                    ) {
                        Text("Proceed")
                    }

                    LaunchedEffect(presignedUrl) {
                        if (presignedUrl.isNotBlank()) {
                            if (!isLoading || isSuccess == null) { // isSuccess == null - to avoid double loading
                                mainViewModel.loadModelEntryFromImage(
                                    url = presignedUrl,
                                    name = textInput
                                )
                                viewModel.presignedUrl.value = ""
                            }
                        }
                    }
                    LaunchedEffect(isSuccess) {
                        if (isSuccess != null) {
                            Log.d("loadModel UI", "enter UI")
                            snackbarHostState.showSnackbar(
                                message = "Loading is successful!",
                                actionLabel = "Success",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                    Button(
                        enabled = isSuccess != null,
                        onClick = { navController.popBackStack() }) {
                        Text(text = "Done")
                    }
                }
            }
        }
    }
    //}

}
