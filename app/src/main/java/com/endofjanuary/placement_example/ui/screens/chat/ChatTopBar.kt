package com.endofjanuary.placement_example.ui.screens.chat

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
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatTopBar(navController: NavController, autoRefineEnabled: Boolean) {
    TopAppBar(
        title = { Text(text = stringResource(R.string.chat_top_bar_header)) },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = stringResource(R.string.arrow_back)
                )
            }
        },
        actions = {
            if (autoRefineEnabled) {
                IconButton(onClick = {
                    navController.navigate("user_profile")
                }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_awesome_filled),
                        contentDescription = stringResource(R.string.refine_enabled),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            } else {
                IconButton(onClick = { navController.navigate("user_profile") }) {
                    Icon(
                        painter = painterResource(R.drawable.ic_awesome_outlined),
                        contentDescription = stringResource(R.string.refine_disabled),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    )
}