package com.endofjanuary.placement_example.ui.screens.models_list_screen

import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.R

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DeleteItemControls(
    onDeactivateSelection: () -> Unit,
    onDelete: () -> Unit,
) {
    FlowColumn {
        FloatingActionButton(
            onClick = onDeactivateSelection,
            shape = CircleShape
        ) {
            Icon(Icons.Default.Close, contentDescription = stringResource(R.string.delete))
        }
        Spacer(modifier = Modifier.height(10.dp))
        FloatingActionButton(
            onClick = onDelete, shape = CircleShape
        ) {
            Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete))
        }
    }
}