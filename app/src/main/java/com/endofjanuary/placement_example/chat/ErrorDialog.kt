package com.endofjanuary.placement_example.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun ErrorDialog(
    errorMessage: String, openDialog: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    val proceed = remember {
        mutableStateOf(false)
    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Error")
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Error cross",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            },
            text = {
                Text(
                    errorMessage
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    openDialog.value = false
                }) {
                    Text("understood")
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}