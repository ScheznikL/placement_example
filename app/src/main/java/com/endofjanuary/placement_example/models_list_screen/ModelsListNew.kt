package com.endofjanuary.placement_example.models_list_screen

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.endofjanuary.placement_example.data.models.ModelEntry

@Composable
fun ModelListNew(items: List<ModelEntry>) {
    LazyColumn {
        items(items) { item ->
            if ((items.indexOf(item) % 2) == 0) {
                val nextItemIndex = items.indexOf(item) + 1
                if (nextItemIndex < items.size) {
                    Row(modifier = Modifier.fillMaxWidth()) {
                     /*   ListItem(item = item)
                        ListItem(item = items[nextItemIndex])*/
                    }
                } else {
                    Row(modifier = Modifier.fillMaxWidth()) {
                       // ListItem(item = item)
                    }
                }
            }
        }
    }
}

/*@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ListItem(
    entry: ModelEntry,
    *//*    navController: NavController,

        viewModel: ModelsListViewModel,*//*
    inSelectionMode: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier,
) {
    val defaultDominantColor = MaterialTheme.colorScheme.surface
    var dominantColor by remember {
        mutableStateOf(defaultDominantColor)
    }

    val interactionSource = remember { MutableInteractionSource() }

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .shadow(5.dp, RoundedCornerShape(10.dp))
            .clip(RoundedCornerShape(10.dp))
            .aspectRatio(1f)
            .background(
                Brush.verticalGradient(
                    listOf(
                        dominantColor,
                        defaultDominantColor
                    )
                )
            )
            .combinedClickable(
                onClick = {
                    if (!inSelectionMode) {
                        viewModel.saveLastModel(entry.meshyId, entry.id, entry.modelImageUrl)
                        navController.navigate(
                            "transit_dialog/${entry.id}/${entry.meshyId}"
                        )
                    } else {
                        viewModel.selectedIds.value -= viewModel.selectedIds.value.last()
                    }
                },
                onLongClick = {
                    Log.d("onLongClick", "${entry.meshyId} - ${entry.modelDescription}")
                    if (!inSelectionMode) {
                        viewModel.selectedIds.value += entry.meshyId
                    } else {
                        viewModel.selectedIds.value -= viewModel.selectedIds.value.last()
                    }
                    Log.d("onLongClick", viewModel.selectedIds.value.toString())
//                    else
//                        Modifier.toggleable(
//                            value = selected,
//                            interactionSource = interactionSource,
//                            indication = null, // do not show a ripple
//                            onValueChange = {
//                                if (it) {
//                                    selectedIds.value += entry.meshyId
//                                } else {
//                                    selectedIds.value -= entry.meshyId
//                                }
//                            }
//                        )
                },
            )

    ) {
//        if (dialogRes.value != null && dialogRes.value!!) {
//            navController.navigate(
//                "ar_screen/${entry.id}"
//            )
//        } else if (dialogRes.value != null) {
//            navController.navigate(
//                "threed_screen/${entry.id}"
//            )
//        }
        Column {// SubcomposeAsyncImage
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(entry.modelImageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = entry.modelDescription,
                onSuccess = {
                    *//*    viewModel.calcDominantColor(it.result.drawable) { color ->
                            dominantColor = color
                        }*//*
                },
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(120.dp)
                    .align(Alignment.CenterHorizontally)
            )
            Text(
                text = entry.modelDescription,
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
    if (inSelectionMode) {
        if (selected) {
            val bgColor = MaterialTheme.colorScheme.surfaceColorAtElevation(3.dp)
            Icon(
                Icons.Filled.CheckCircle,
                tint = MaterialTheme.colorScheme.primary,
                contentDescription = null,
                modifier = Modifier
                    .padding(4.dp)
                    .border(2.dp, bgColor, CircleShape)
                    .clip(CircleShape)
                    .background(bgColor)
            )
        }
    }
}*/
