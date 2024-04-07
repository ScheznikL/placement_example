package com.endofjanuary.placement_example.utils

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Share
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.utils.screens.DoRefineDialog
import com.endofjanuary.placement_example.utils.screens.SpecifyRefineOptions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreenTopBar(
    modelDescription: MutableState<String>,
    modelId: Int,
    meshyId: String,
    mainViewModel: MainViewModel,
    navController: NavController,
    overwrite: MutableState<Boolean>
) {
    var showMore by remember { mutableStateOf(false) }
    val text by remember { modelDescription }
    val openDialog = remember { mutableStateOf(false) }

    val confirm = remember{ mutableStateOf(false) }
    val openDetailedDialog = remember { mutableStateOf(false) }
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
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.Share,
                    contentDescription = "Localized description"
                )
            }
            IconButton(onClick = { /* do something */ }) {
                Icon(
                    imageVector = Icons.Filled.KeyboardArrowDown,
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
        mainViewModel, openDetailedDialog, meshyId,overwrite
        //TODO if time is up
    )
}