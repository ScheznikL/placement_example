package com.endofjanuary.placement_example.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier

@Composable
fun CancelDialog(
    title: String, openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>, modifier: Modifier = Modifier,
) {
    val proceed = remember {
        mutableStateOf(false)
    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            icon = { Icon(Icons.Default.Close, contentDescription = "Error cross") },
            title = {
                Text(text = title)
            },
            text = {
                Text(
                    "Cancel refining and save preview model ?"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text("Yes")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text("No")
                }
            }
        )
    }
}