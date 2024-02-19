package com.endofjanuary.placement_example.utils

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

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
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = "Chat"
                    )
                }
                IconButton(onClick = { navController.navigate("models_list") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        painter = painterResource(id = R.drawable.ic_token),
                        contentDescription = "3D display"
                    )
                }
                IconButton(onClick = { /* do something */ }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        painter = painterResource(R.drawable.ic_center_focus),
                        contentDescription = "AR display"
                    )
                }
            }

        },
    )
}