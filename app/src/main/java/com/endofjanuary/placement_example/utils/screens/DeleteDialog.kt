package com.endofjanuary.placement_example.utils.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import com.endofjanuary.placement_example.R


@Composable
fun DeleteDialog(
    openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>
) {
    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        },
            icon = { Icon(painterResource(R.drawable.ic_clock), contentDescription = null) },
            title = {
                Text(text = "Refine the model")
            },
            text = {
                Text(
                    "Do you want to refine current model ?" + "\n\r processing may took some time"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text("Dismiss")
                }
            })
    }
}