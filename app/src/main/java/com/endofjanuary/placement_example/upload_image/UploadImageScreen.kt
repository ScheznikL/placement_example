package com.endofjanuary.placement_example.upload_image

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.utils.BottomBar
import com.endofjanuary.placement_example.utils.screens.DefaultTopAppBar
import org.koin.androidx.compose.getViewModel

@Composable
fun UploadImageScreen(
    navController: NavController, typeGallery: Boolean
) {
    val viewModel = getViewModel<UploadImageViewModel>()
    val mainViewModel = getViewModel<MainViewModel>()

    val image by remember { viewModel.image }
    val photo by remember { viewModel.photo }
    val textInput by remember { viewModel.inputValueState }
    val isUploading by remember { viewModel.isUploading }
    val isUploadingError by remember { viewModel.isUploadingError }
    val presignedUrl by remember { viewModel.presignedUrl }

    val isLoading by remember { mainViewModel.isLoading }
    val isSuccess by remember { mainViewModel.isSuccess }
    val progress by remember { mainViewModel.progress }


    val pickImage = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia(), viewModel::onPhotoPickerSelect
    )
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(presignedUrl) {
        if (!presignedUrl.isNullOrBlank()) {
            if (!isLoading || isSuccess == null) { // isSuccess == null - to avoid double loading
                mainViewModel.loadModelEntryFromImage(
                    url = presignedUrl!!, name = textInput
                )
                viewModel.presignedUrl.value = ""
            }
        }
    }
    LaunchedEffect(isSuccess) {
        if (isSuccess != null) {
            Log.d("loadModel UI", "isSuccess: ${isSuccess!!.first}:${isSuccess!!.second}")

            snackbarHostState.showSnackbar(
                message = "Loading is successful id is ${isSuccess!!.second} $!" // todo temp ID
            )
            navController.navigate("transit_dialog/${isSuccess!!.second}/${isSuccess!!.first}")
        }
    }
    if (typeGallery) {
        LaunchedEffect(Unit) {
            pickImage.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }
    } else {
        TakePhoto(onPhotoTaken = viewModel::onTakePhoto, onClose = { navController.popBackStack() })
    }
    if (image != null || photo != null) {
        Scaffold(topBar = { DefaultTopAppBar(title = "", navController = navController) },
            bottomBar = { BottomBar(navController = navController) },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }) { contentPadding ->

            Box(contentAlignment = Alignment.TopCenter) {
                UploadImageContent(
                    modifier = Modifier.padding(
                        top = 15.dp + contentPadding.calculateTopPadding(),
                        bottom = 20.dp + contentPadding.calculateBottomPadding(),
                        start = 10.dp,
                        //end = 10.dp,
                        /*contentPadding.plus(PaddingValues(top = 15.dp))*/
                    ),
                    isActionEnabled = !isUploading && !isLoading,
                    image = image,
                    photo = photo,
                    modelName = textInput,
                    onNameChange = { viewModel.inputValueState.value = it },
                    onProceedClick = {
                        viewModel.getPresignedUrl(context) /*viewModel.getPresignedUrl(context)*/
                    },
                    onRedo = { navController.popBackStack() },
                    isRedoEnabled = !isUploading && !isLoading,
                    scrollState = scrollState
                )
                OnDataLoading(
                    isLoading = isLoading,
                    isUploadingError = isUploadingError,
                    isUploading = isUploading,
                    progress = progress,
                    modifier = Modifier.align(Alignment.Center)
                )

            }
        }

    }
    /*   if (false) {
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
       }*/
}

@Composable
fun UploadImageContent(
    modifier: Modifier = Modifier,
    isActionEnabled: Boolean,
    image: Uri?,
    photo: Bitmap?,
    modelName: String,
    onNameChange: (String) -> Unit,
    onProceedClick: () -> Unit,
    onRedo: () -> Unit,
    isRedoEnabled: Boolean,
    scrollState: ScrollState,
) {

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {

        DismissPictureButton(
            picture = {
                if (image != null) {
                    AsyncImage(
                        //  alpha = 0.5f,
                        model = image,
                        contentDescription = null,
                        modifier = Modifier.padding(end = 15.dp).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Image(
                        //  alpha = 0.5f,
                        bitmap = photo!!.asImageBitmap(),
                        contentDescription = null,
                        modifier = Modifier.padding(end = 15.dp).clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
            },
            onClick = onRedo // TODO change
        )
        Spacer(modifier = Modifier.height(5.dp))
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(end = 15.dp),
            //  .align(Alignment.BottomCenter),
            value = modelName,
            onValueChange = onNameChange,
            textStyle = TextStyle(
                lineHeight = 1.5.em,
                fontSize = 16.sp
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default,
                capitalization = KeyboardCapitalization.Sentences
            ),
            placeholder = { Text(text = "Name the model *optional") },
            enabled = isActionEnabled,
            shape = RoundedCornerShape(9.dp),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(35.dp))
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier
                .fillMaxWidth(),
            // .padding(horizontal = 15.dp),
        ) {
            Button(
                enabled = isActionEnabled && (photo != null || image != null),
                onClick = onProceedClick,
                modifier = Modifier.size(width = 206.dp, height = 55.dp),
                contentPadding = PaddingValues(8.dp),
                shape = RoundedCornerShape(
                    topStart = 43.dp,
                    bottomStart = 43.dp,
                    topEnd = 0.dp,
                    bottomEnd = 0.dp
                )
            ) {
                Text("Proceed")
            }
            /*      Spacer(modifier = Modifier.height(10.dp))
                  Button(
                      enabled = isRedoEnabled,
                      onClick = onRedo,
                      modifier = Modifier.size(width = 206.dp, height = 45.dp),
                      contentPadding = PaddingValues(8.dp)
                  ) {
                      Text(text = "Redo")
                  }*/
        }

    }
}

@Composable
fun DismissPictureButton(
    modifier: Modifier = Modifier,
    picture: @Composable (() -> Unit),
    onClick: () -> Unit
) {
    Box(contentAlignment = Alignment.TopEnd, modifier = modifier) {
        picture()
        IconButton(
            onClick = onClick,
            modifier = Modifier.padding(end = 15.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "dismissImage"
            )
        }
    }
}
