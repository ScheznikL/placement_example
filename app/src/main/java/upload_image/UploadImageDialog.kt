package upload_image

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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

    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {

        Column(modifier = Modifier.padding(26.dp)) {
            Text(
                text = "Name the model *optional",
                textAlign = TextAlign.Justify
            )
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
                )
                Button(
                    onClick = {
                        pickImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                        /*                        navController.navigate(
                                                    "threed_screen/${modelId}"
                                                )*/
                    },
                ) {
                    Text("Pick an image")
                }

                if (uri != Uri.EMPTY) {
                    Card(modifier = Modifier
                       // .weight(1f)
                        .padding(top = 10.dp, bottom = 10.dp)) {
                        AsyncImage(
                            model = uri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Button(
                        onClick = {
                            viewModel.getPresignedUrl()
                        },
                    ) {
                        Text("Proceed")
                    }
                }
            }
        }
    }

}