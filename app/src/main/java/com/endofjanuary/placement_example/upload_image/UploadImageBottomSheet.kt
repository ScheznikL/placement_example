package com.endofjanuary.placement_example.upload_image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadImageBottomSheet(
    navController: NavController,
    showBottomSheet: MutableState<Boolean>,
    //sheetState: MutableState<SheetState>
) {
    val sheetState = rememberModalBottomSheetState()

    val scope = rememberCoroutineScope()
    if (showBottomSheet.value) {
        ModalBottomSheet(
            onDismissRequest = {
                showBottomSheet.value = false
            },
            sheetState = sheetState
        ) {
            // Sheet content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 25.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                                navController.navigate("upload_image/${false}")
                            }
                        }
                    }) {
                    Icon(
                        painterResource(R.drawable.ic_photo_camera),
                        "photo"
                    )
                }
                //Spacer(Modifier.width(20.dp))
                IconButton(
                    modifier = Modifier.size(48.dp),
                    onClick = {
                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                showBottomSheet.value = false
                                navController.navigate("upload_image/${true}")
                            }
                        }
                    }) {
                    Icon(
                        painterResource(R.drawable.ic_gallery),
                        "gallery"
                    )
                }
            }
        }
    }

}