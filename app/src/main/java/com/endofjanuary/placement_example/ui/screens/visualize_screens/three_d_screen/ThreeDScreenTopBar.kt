package com.endofjanuary.placement_example.ui.screens.visualize_screens.three_d_screen

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.ui.dialogs.DoDownload
import com.endofjanuary.placement_example.ui.dialogs.DoRefineDialog
import com.endofjanuary.placement_example.ui.dialogs.SpecifyRefineOptions
import com.endofjanuary.placement_example.ui.screens.visualize_screens.VisualizeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreenTopBar(
    modelDescriptionShorten: String,
    modelId: Int,
    meshyId: String,
    mainViewModel: MainViewModel,
    viewModel: VisualizeViewModel,
    navController: NavController,
    overwrite: MutableState<Boolean>,
    isFromText: Boolean,
    isRefined: Boolean,
    openRefineDialog: MutableState<Boolean>,
    confirmRefine: MutableState<Boolean>
) {


    val openDownloadDialog = remember { mutableStateOf(false) }
    val confirmDownload = remember { mutableStateOf(false) }

    val position = mutableStateOf(4.0f)

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
    LaunchedEffect(confirmRefine.value) {
        if (confirmRefine.value) {
            openDetailedDialog.value = true
        }
    }

    DoRefineDialog(openRefineDialog, confirmRefine)
    SpecifyRefineOptions(
        mainViewModel = mainViewModel,
        openBasicDialog = openDetailedDialog,
        modelId = meshyId,
        overwrite = overwrite,
        id = modelId
    )
    DoDownload(
        openDialog = openDownloadDialog,
        confirm = confirmDownload,
        onDownload = viewModel::onDownload,
        modelFileName = modelDescriptionShorten,
        refinedUrl = mainViewModel.model.value.modelPath
    )
}