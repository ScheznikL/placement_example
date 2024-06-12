package com.endofjanuary.placement_example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R

@Composable
fun AutoRefineProgressBubble(
    modifier: Modifier = Modifier,
    message: String = "",
    selectedRichness: MutableState<MainViewModel.TextureRichness> = remember {
        mutableStateOf(
            MainViewModel.TextureRichness.High
        )
    },
    onCancel: () -> Unit,
    onDone: (texture: MainViewModel.TextureRichness) -> Unit,
) {
    val configuration = LocalConfiguration.current
    val bubblePadding = configuration.screenWidthDp.dp / 5
    val richnessOptions = MainViewModel.TextureRichness.entries

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.size(11.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Icon(
                Icons.Default.Info,
                contentDescription = stringResource(R.string.info_message_icon),
                modifier = Modifier
                    .padding(start = 8.dp, end = 9.dp)
                    .size(33.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Box(
                modifier = Modifier.weight(1.0f)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .background(
                            MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(30.dp)
                        )
                        .padding(horizontal = 18.dp, vertical = 12.dp)
                ) {
                    Text(
                        modifier = Modifier.padding(top = 1.dp),
                        text = message.ifEmpty { stringResource(R.string.refine_mode_message) },
                        letterSpacing = MaterialTheme.typography.bodyLarge.letterSpacing,
                        textAlign = TextAlign.Justify
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2), modifier = Modifier.height(100.dp)
                        ) {
                            items(richnessOptions) {
                                val selected = selectedRichness.value == it
                                Card(
                                    colors = if (!selected) CardDefaults.cardColors() else CardDefaults.cardColors(
                                        containerColor = Color.Green.copy(0.4f)
                                    ),
                                    onClick = {
                                        selectedRichness.value = it
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .padding(vertical = 5.dp)
                                ) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxHeight()
                                            .align(Alignment.CenterHorizontally),
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(
                                            text = it.toString(),
                                            textAlign = TextAlign.Center,
                                        )
                                    }
                                }
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        IconButton(onClick = { onDone(selectedRichness.value) }) {
                            Icon(Icons.Default.Done, stringResource(R.string.confirm_icon))
                        }
                        IconButton(onClick = onCancel) {
                            Icon(Icons.Default.Close, stringResource(R.string.edit_icon))
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
fun AutoRefineProgressBubblePreview() {
    AutoRefineProgressBubble(
        onCancel = {},
        onDone = {},
    )
}

