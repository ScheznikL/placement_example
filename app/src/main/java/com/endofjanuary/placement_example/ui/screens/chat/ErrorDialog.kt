package com.endofjanuary.placement_example.ui.screens.chat

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R

@Composable
fun ErrorDialog(
    errorMessage: String, openDialog: MutableState<Boolean>, modifier: Modifier = Modifier
) {
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
                openDialog.value = false
            },
            title = {
                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = stringResource(id = R.string.error_header))
                    Icon(
                        Icons.Default.Close,
                        contentDescription = stringResource(id = R.string.error_icon),
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
                    Text(stringResource(R.string.error_OK))
                }
            },
            containerColor = MaterialTheme.colorScheme.errorContainer
        )
    }
}