package com.endofjanuary.placement_example.utils.components

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun EdgeButton(
    enabled: Boolean = true,
    onProceedClick: () -> Unit,
    modifier: Modifier = Modifier,
    title: String,
    inverseShape: Boolean = false
) {
    val shape = if (!inverseShape) {
        RoundedCornerShape(
            topStart = 43.dp,
            bottomStart = 43.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp
        )
    } else {
        RoundedCornerShape(
            topStart = 0.dp,
            bottomStart = 0.dp,
            topEnd =43.dp,
            bottomEnd = 43.dp
        )
    }
    Button(
        enabled = enabled,
        onClick = onProceedClick,
        modifier = modifier.size(width = 206.dp, height = 55.dp),
        contentPadding = PaddingValues(8.dp),
        shape = shape,
        colors = ButtonColors(
            containerColor = MaterialTheme.colorScheme.onPrimary,
            contentColor = MaterialTheme.colorScheme.primary,
            disabledContainerColor = MaterialTheme.colorScheme.inverseOnSurface,
            disabledContentColor = MaterialTheme.colorScheme.inversePrimary
        )
    ) {
        Text(title)
    }
}