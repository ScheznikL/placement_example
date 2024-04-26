package com.endofjanuary.placement_example.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ErrorMessageBubble(
    message: String,
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val bubblePadding = configuration.screenWidthDp.dp / 5
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(11.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                modifier = Modifier
                    .padding(start = 8.dp, end = 9.dp)
                    .size(35.dp)
                //    .clip(CircleShape)
                ,
                imageVector = Icons.Outlined.Warning,
                contentDescription = "warning",
                tint = Color(255, 119, 0, 226)
            )
            Box(modifier = Modifier.weight(1.0f)) {
                Column(horizontalAlignment = Alignment.Start) {
                    Column(
                        modifier = Modifier
                            .background(Color.Red.copy(alpha = 0.4f), RoundedCornerShape(37.dp))
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(modifier = Modifier.padding(top = 1.dp), text = message)
                    }
                }
            }
            Spacer(modifier = Modifier.size(bubblePadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorMessageBubblePreview() {
    ErrorMessageBubble(message = "some message from chat gpt")
}

