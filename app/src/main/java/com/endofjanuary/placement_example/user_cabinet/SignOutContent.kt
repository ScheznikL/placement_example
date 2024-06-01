package com.endofjanuary.placement_example.user_cabinet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SignOutContent(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    Column(
        modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,

        ) {
        Text(text = "You logged out successfully")
        Button(onClick = { navController.navigate("reg_screen") }) {
            Text(text = "To Welcome Screen")
        }
    }
}