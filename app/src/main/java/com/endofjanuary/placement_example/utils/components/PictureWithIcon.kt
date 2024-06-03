package com.endofjanuary.placement_example.utils.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun PictureWithIcon(
    modifier: Modifier = Modifier,
    picture: @Composable (() -> Unit),
    icon: @Composable () -> Unit,
    painter: Painter? = null,
    imageVector: ImageVector? = null
) {
    Box(contentAlignment = Alignment.TopEnd, modifier = modifier) {
        picture()
        if (painter != null || imageVector != null) {
            if (painter == null) {
                Icon(imageVector!!, null)
            } else {
                Icon(painter, null)
            }
        }
    }
}