package com.endofjanuary.placement_example.utils.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em

@Composable
fun ImageWithExpiredLabel(
    modifier: Modifier = Modifier,
    picture: @Composable (() -> Unit),
    label: String = "EXPIRED",
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier.size(190.dp)) {
        picture()
        Box(
            Modifier
                .fillMaxWidth()
                .background(
                    MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.5f),
                    //RoundedCornerShape(10.dp)
                ),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text = label, style = TextStyle(
                    fontWeight = FontWeight.W500, letterSpacing = 1.em
                ), color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp)
            )
        }
    }
}

@Preview
@Composable
fun Tempor() {
    ImageWithExpiredLabel(
        picture = {
            Box(
                Modifier
                    .size(100.dp)
                    .background(Color.White)
            )
        }, label = "EXPIRED"
    )
}