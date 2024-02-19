package com.endofjanuary.placement_example.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Place
import androidx.compose.material.icons.sharp.PlayArrow
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomBar(navController: NavController) {
    BottomAppBar(
        windowInsets = BottomAppBarDefaults.windowInsets,
        actions = {
            Row(
                Modifier.fillMaxWidth(),
                // verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ){
                IconButton(onClick = { navController.navigate("home_screen") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Outlined.Home,
                        contentDescription = "Home"
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = "Chat"
                    )
                }
                IconButton(onClick = { navController.navigate("threed_screen") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Outlined.Place,
                        contentDescription = "3D display"
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Sharp.PlayArrow,
                        contentDescription = "AR display"
                    )
                }
            }

        },
    )
}