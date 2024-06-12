package com.endofjanuary.placement_example.ui.screens.chat

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ModelLoadingBubble(
    modifier: Modifier = Modifier,
) {
    val configuration = LocalConfiguration.current
    val bubblePadding = configuration.screenWidthDp.dp / 5
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Spacer(modifier = Modifier.size(11.dp))
        LottieDotsFlashing(modifier = Modifier.size(60.dp))
    }
}

@Preview(showBackground = true)
@Composable
fun OutgoingMessageBubblePreview() {
    ModelLoadingBubble()
}
