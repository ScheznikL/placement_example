package com.endofjanuary.placement_example.ui.dialogs

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R

@Composable
fun DoRefineDialog(
    openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>
) {
    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        },
            icon = { Icon(painterResource(R.drawable.ic_clock), contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.refine_dialog_header))
            },
            text = {
                Text(
                    stringResource(R.string.refine_model_question)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text(stringResource(id = R.string.dismiss))
                }
            })
    }
}