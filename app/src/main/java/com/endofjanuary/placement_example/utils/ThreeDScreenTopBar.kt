package com.endofjanuary.placement_example.utils

import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThreeDScreenTopBar(
    modelDescription: MutableState<String>,
    modelId: Int,
    navController: NavController
) {
    var showMore by remember { mutableStateOf(false) }
    val text by remember { modelDescription }
    TopAppBar(
        title = {
            Row {
                if (showMore) {
                    Text(text = text)
                } else {
                    Text(text = text, maxLines = 3, overflow = TextOverflow.Ellipsis)
                }
                IconButton(onClick = { showMore = !showMore }) {
                    if (!showMore)
                        Icon(Icons.Default.KeyboardArrowDown, contentDescription = "more")
                    else
                        Icon(Icons.Default.KeyboardArrowUp, contentDescription = "less")
                }
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
    )
}