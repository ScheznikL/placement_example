package com.endofjanuary.placement_example.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(navController: NavController, autoRefineEnabled: Boolean) {
    TopAppBar(
        title = { Text(text = "Chat") },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Arrow Back"
                )
            }
        },
        actions = {
            if (autoRefineEnabled) {//Text(" Auto Refine mode enabled")
                IconButton(onClick = {
                    navController.navigate("user_profile")
                    // todo add explanation dilog
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_awesome_filled),
                        contentDescription = "refine",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(onClick = { navController.navigate("user_profile") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_awesome_outlined),
                        contentDescription = "refine",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}