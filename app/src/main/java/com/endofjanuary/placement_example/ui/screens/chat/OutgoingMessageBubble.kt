package com.example.gffcompose.composables.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun OutgoingMessageBubble(
    modifier: Modifier = Modifier,
    message: String,
) {
    val configuration = LocalConfiguration.current
    val bubblePadding = configuration.screenWidthDp.dp / 5
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp), horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(11.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Spacer(modifier = Modifier.size(bubblePadding))
            Box(modifier = Modifier.weight(1.0f), contentAlignment = Alignment.CenterEnd) {
                Column(horizontalAlignment = Alignment.End) {
                    Column(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.primary.copy(0.5f), RoundedCornerShape(37.dp))
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(modifier = Modifier, text = message)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OutgoingMessageBubblePreview() {
    OutgoingMessageBubble(message = "some message we send to gpt chat")
}
