package com.endofjanuary.placement_example.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun FinalMessageBubble(
    modifier: Modifier = Modifier,
    message: String,
    onEdit: () -> Unit,
    onDone: () -> Unit
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
            Image(
                modifier = Modifier
                    .padding(start = 8.dp, end = 9.dp)
                    .size(33.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop,
                painter = painterResource(com.endofjanuary.placement_example.R.drawable.chatgptlogo),
                contentDescription = "chat image"
            )
            Box(
                modifier = Modifier
                    .weight(1.0f)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,

                    ) {
                    Column(
                        modifier = Modifier
                            .background(
                                Brush.horizontalGradient(
                                    listOf(
                                        Color(0xA412A37E),
                                        Color(0x94A6FF18)
                                    )
                                ), RoundedCornerShape(30.dp)
                            )
                            //.shadow(100.dp, spotColor = MaterialTheme.colorScheme.secondary, ambientColor = MaterialTheme.colorScheme.primary, shape = RoundedCornerShape(30.dp))
                            .padding(horizontal = 18.dp, vertical = 12.dp)
                    ) {
                        Text(modifier = Modifier.padding(top = 1.dp), text = message)
                        Spacer(modifier = Modifier.size(11.dp))
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            IconButton(onClick = onDone) {
                                Icon(Icons.Default.Done, "done")
                            }
                            IconButton(onClick = onEdit) {
                                Icon(Icons.Default.Edit, "edit")
                            }
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.size(bubblePadding))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FinalMessageBubblePreview() {
    // FinalMessageBubble(message = "final object description is <............. .... .............. word \n\r.......... .......................>")
}

