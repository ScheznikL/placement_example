package com.endofjanuary.placement_example.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.three_d_screen.ThreeDScreenViewModel
import com.endofjanuary.placement_example.utils.screens.DoDownload
import com.endofjanuary.placement_example.utils.screens.DoRefineDialog
import com.endofjanuary.placement_example.utils.screens.SpecifyRefineOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreenTopBar(
    modelDescriptionShorten: String,
    modelId: Int,
    meshyId: String,
    mainViewModel: MainViewModel,
    viewModel: ThreeDScreenViewModel,
    navController: NavController,
    overwrite: MutableState<Boolean>,
    isFromText: Boolean,
    isRefined: Boolean,
) {


    val openRefineDialog = remember { mutableStateOf(false) }
    val openDownloadDialog = remember { mutableStateOf(false) }
    val confirmDownload = remember { mutableStateOf(false) }

    val confirmRefine = remember { mutableStateOf(false) }
    val openDetailedDialog = remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row { }
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = { navController.navigate("ar_screen/${modelId}") }) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_token),
                    contentDescription = stringResource(R.string.to_ar_icon)
                )
            }
        },
        actions = {
            if (isFromText && !isRefined) {
                IconButton(onClick = { openRefineDialog.value = true }) {
                    Icon(
                        painterResource(id = R.drawable.ic_awesome_filled),
                        contentDescription = stringResource(R.string.refine),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            IconButton(onClick = { openDownloadDialog.value = true }) {
                Icon(
                    painterResource(id = R.drawable.ic_download),
                    contentDescription = stringResource(R.string.download),
                )
            }
            IconButton(onClick = { viewModel.deleteModel(meshyId) }) {
                Icon(
                    imageVector = Icons.Filled.Delete,
                    contentDescription = stringResource(R.string.delete),
                )
            }
        },
    )
    if (confirmRefine.value) {
        openDetailedDialog.value = true
    }

    DoRefineDialog(openRefineDialog, confirmRefine)
    SpecifyRefineOptions(
        mainViewModel, openDetailedDialog, meshyId, overwrite
    )
    DoDownload(
        openDialog = openDownloadDialog,
        confirm = confirmDownload,
        onDownload = viewModel::onDownload,
        modelFileName = modelDescriptionShorten,
        refinedUrl = mainViewModel.model.value.modelPath
    )
}