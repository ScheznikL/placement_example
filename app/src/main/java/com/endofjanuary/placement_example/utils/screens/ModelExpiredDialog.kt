package com.endofjanuary.placement_example.utils.screens

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import com.endofjanuary.placement_example.R


@Composable
fun ModelExpiredDialog(
    openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>, onConfirm: () -> Unit
) {

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, icon = {
            Icon(
                painterResource(R.drawable.ic_clock),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onError
            )
        }, title = {
            Text(text = "The model has expired")
        }, text = {
            Text(
                "Do you want to delete this record ?"
            )
        }, confirmButton = {
            TextButton(onClick = {
                confirm.value = true
                onConfirm()
                openDialog.value = false
            }) {
                Text("Delete")
            }
        }, dismissButton = {
            TextButton(onClick = {
                confirm.value = false
                openDialog.value = false
            }) {
                Text("Cancel")
            }
        })
    }
}