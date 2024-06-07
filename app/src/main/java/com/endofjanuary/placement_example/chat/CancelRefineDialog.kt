package com.endofjanuary.placement_example.chat

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R

@Composable
fun CancelDialog(
    title: String, openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>, modifier: Modifier = Modifier,
) {

    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            icon = { Icon(Icons.Default.Close, contentDescription = stringResource(R.string.error_icon)) },
            title = {
                Text(text = title)
            },
            text = {
                Text(
                    stringResource(R.string.cancel_refining)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.yes))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.no))
                }
            }
        )
    }
}