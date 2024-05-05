package com.endofjanuary.placement_example.utils

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.DownloaderImpl
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreenViewModel
import com.endofjanuary.placement_example.utils.screens.DoDownload
import com.endofjanuary.placement_example.utils.screens.DoRefineDialog
import com.endofjanuary.placement_example.utils.screens.SpecifyRefineOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreenTopBar(
    modelDescription: String?,
    modelId: Int,
    meshyId: String,
    mainViewModel: MainViewModel,
    viewModel: ThreeDScreenViewModel,
    navController: NavController,
    overwrite: MutableState<Boolean>,
    downloader: DownloaderImpl,
    modelPath: MutableState<String?>,
    isFromText: MutableState<Boolean?>
) {


    val openDialog = remember { mutableStateOf(false) }
    val openDownloadDialog = remember { mutableStateOf(false) }
    val confirmDownload = remember { mutableStateOf(false) }

    val confirm = remember { mutableStateOf(false) }
    val openDetailedDialog = remember { mutableStateOf(false) }


    Log.d("modelPath & desc", "${modelPath.value} $modelDescription")
    TopAppBar(
        title = {
            Row {
                /* if (showMore) {
                     Text(text = text)
                 } else {
                     Text(text = text, maxLines = 3, overflow = TextOverflow.Ellipsis)
                 }
                 IconButton(onClick = { showMore = !showMore }) {
                     if (!showMore)
                         Icon(Icons.Default.KeyboardArrowDown, contentDescription = "more")
                     else
                         Icon(Icons.Default.KeyboardArrowUp, contentDescription = "less")
                 }*/
            }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigate("ar_screen/${modelId}") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_token),
                    contentDescription = "to AR"
                )
            }
        },
        actions = {
            if (isFromText.value ?: true) {
                Column {
                    IconButton(onClick = {
                        openDialog.value = true
                    }) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "Refine"
                        )
                    }
                    Text("Refine")
                }
            }
            IconButton(onClick = { openDownloadDialog.value = true }) {
                Icon(
                    painterResource(id = R.drawable.ic_download),
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = "Localized description"
                )
            }
        },
    )
    if (confirm.value) {
        openDetailedDialog.value = true
    }

    DoRefineDialog(openDialog, confirm)
    SpecifyRefineOptions(
        mainViewModel, openDetailedDialog, meshyId, overwrite
        //TODO if time is up
    )
    DoDownload(
        openDialog = openDownloadDialog,
        confirm = confirmDownload,
        path = modelPath.value,
        downloader = downloader,
        modelDescription = modelDescription ?: "none description"
    )
}