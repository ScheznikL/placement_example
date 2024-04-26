package upload_image

import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.endofjanuary.placement_example.MainViewModel
import org.koin.androidx.compose.getViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImage(
    navController: NavController,
    typeGallery: Boolean
) {
    val viewModel = getViewModel<UploadImageViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val selectedUri by remember { viewModel.selectedUri }
    val photo by remember { viewModel.photo }
    val textInput by remember { viewModel.inputValueState }
    val isUploading by remember { viewModel.isUploading }
    val isUploadingError by remember { viewModel.isUploadingError }
    var presignedUrl by remember { viewModel.presignedUrl }

    val isLoading by remember { mainViewModel.isLoading }
    var isSuccess by remember { mainViewModel.isSuccess }


    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(),
        viewModel::onPhotoPickerSelect
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                message = "Loading is successful!"
            )
        }
    }

    if (typeGallery) {
        LaunchedEffect(Unit) {
            pickImage
                .launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts
                            .PickVisualMedia
                            .ImageOnly
                    )
                )
        }
    } else {
        TakePhoto(viewModel)
    }
    if (selectedUri != null || photo != null) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { contentPadding ->
            Column(modifier = Modifier.padding(contentPadding)) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        if (selectedUri != null) {
                            AsyncImage(
                                alpha = 0.5f,
                                model = selectedUri,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.Fit
                            )
                        } else {
                            Image(
                                alpha = 0.5f,
                                bitmap = photo!!.asImageBitmap(),
                                contentDescription = null,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp)),
                                contentScale = ContentScale.Fit
                            )
                        }
                        OutlinedTextField(
                            modifier = Modifier
                                .padding(5.dp)
                                .align(Alignment.BottomCenter),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Text,
                                imeAction = ImeAction.Default,
                            ),
                            value = textInput,
                            onValueChange = { viewModel.inputValueState.value = it },
                            placeholder = { Text(text = "Name the model *optional") },
                            enabled = !isUploading && !isLoading
                        )
                    }
                    Button(
                        enabled = !isUploading && !isLoading && photo != null,
                        onClick = {
                            //viewModel.getPresignedUrl(context)
                            viewModel.getBitmapPresignedUrl(context)
                        },
                    ) {
                        Text("Proceed")
                    }
                    Button(
                        enabled = isSuccess != null,
                        onClick = { navController.popBackStack() }) {
                        Text(text = "Done")
                    }
                }
                if (isUploading) {
                    CircularProgressIndicator()
                } else if (isLoading) {

                    Log.d(
                        "loadModel UI",
                        "is Loading"
                    )
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())

                } else if (isUploadingError.isNotBlank()) {
                    Card(
                        colors = CardDefaults
                            .cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(text = isUploadingError)
                    }
                }
            }
        }
    } else {
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp, end = 9.dp)
                    .size(35.dp)
                //    .clip(CircleShape)
                ,
                imageVector = Icons.Outlined.Warning,
                contentDescription = "warning",
                tint = Color(255, 119, 0, 226)
            )
            Box(modifier = Modifier.weight(1.0f)) {
                Column(horizontalAlignment = Alignment.Start) {
                    Column(
                        modifier = Modifier
                            .background(
                                Color.Red.copy(alpha = 0.4f, green = 0.4f),
                                RoundedCornerShape(37.dp)
                            )
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(
                            modifier = Modifier.padding(top = 1.dp),
                            text = "You haven't choose any of the image"
                        )
                    }
                }
            }
        }
    }
}