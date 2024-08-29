package com.endofjanuary.placement_example.ui.dialogs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R
@Composable
fun DoDownload(
    openDialog: MutableState<Boolean>,
    confirm: MutableState<Boolean>,
    onDownload: (String, String?) -> Unit,
    modelFileName: String?,
    refinedUrl: String?
) {
    val name = mutableStateOf(modelFileName.toString())
    val text = stringResource(R.string.initial_download_help)
    val helpText = remember { mutableStateOf(text) }

    val enableEdit = mutableStateOf(false)

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, title = {
            Text(text = stringResource(R.string.download))
        }, text = {
            Column {
                Text(
                    helpText.value
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!enableEdit.value) {
                        Text(
                            name.value
                        )
                    } else {
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                        )
                    }
                    Icon(imageVector = if (enableEdit.value) Icons.Default.Edit else Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_name),
                        modifier = Modifier.clickable {
                            enableEdit.value = !enableEdit.value
                        })
                }
            }
        }, confirmButton = {
            TextButton(onClick = {
                onDownload(name.value, refinedUrl)
                confirm.value = true
                openDialog.value = false
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        }, dismissButton = {
            TextButton(onClick = {
                confirm.value = false
                openDialog.value = false
            }) {
                Text(stringResource(id = R.string.dismiss))
            }
        })
    }
}

