package com.endofjanuary.placement_example.ui.dialogs

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.endofjanuary.placement_example.R


@Composable
fun DeleteDialog(
    title: String,
    text: String,
    icon: @Composable (() -> Unit)? = {
        Icon(
            Icons.Default.Warning,
            contentDescription = null
        )
    },
    openDialog: MutableState<Boolean>,
    confirm: MutableState<Boolean>,
    onConfirm: () -> Unit
) {
    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        },
            icon = icon,
            title = {
                Text(
                    text = title,
                    textAlign = TextAlign.Justify
                )
            },
            text = {
                Text(
                    text,
                    textAlign = TextAlign.Justify
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    onConfirm()
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text(stringResource(R.string.dismiss))
                }
            })
    }
}