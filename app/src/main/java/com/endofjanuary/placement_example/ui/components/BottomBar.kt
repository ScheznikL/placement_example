package com.endofjanuary.placement_example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

@Composable
fun BottomBar(navController: NavController, modifier: Modifier = Modifier) {
    BottomAppBar(
        windowInsets = BottomAppBarDefaults.windowInsets,
        actions = {
            Row(
                modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { navController.navigate("home_screen") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Outlined.Home,
                        contentDescription = stringResource(R.string.home_sceeen_icon)
                    )
                }
                IconButton(onClick = { navController.navigate("chat_screen") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        painter = painterResource(id = R.drawable.ic_message),
                        contentDescription = stringResource(R.string.chat_icon)
                    )
                }
                IconButton(onClick = { navController.navigate("models_list") }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = stringResource(R.string.models_list_icon)
                    )
                }
                IconButton(onClick = { navController.navigate("user_profile")  }) {
                    Icon(
                        modifier = Modifier.size(35.dp),
                        imageVector = Icons.Default.Person,
                        contentDescription = stringResource(R.string.user_profile_icon)
                    )
                }
            }

        },
    )
}