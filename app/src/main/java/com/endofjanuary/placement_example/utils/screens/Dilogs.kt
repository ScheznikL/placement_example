package com.endofjanuary.placement_example.utils.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.DownloaderImpl
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R

@Composable
fun DoRefineDialog(
    openDialog: MutableState<Boolean>, confirm: MutableState<Boolean>
) {
    val proceed = remember {
        mutableStateOf(false)
    }
    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openDialog.value = false
        },
            icon = { Icon(painterResource(R.drawable.ic_clock), contentDescription = null) },
            title = {
                Text(text = "Refine the model")
            },
            text = {
                Text(
                    "Do you want to refine current model ?" + "\n\r processing may took some time"
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text("Dismiss")
                }
            })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecifyRefineOptions(
    mainViewModel: MainViewModel,
    openBasicDialog: MutableState<Boolean>,
    modelId: String,
    overwrite: MutableState<Boolean>
) {
    var expanded by remember { mutableStateOf(false) }

    val selectedRichness = remember { mutableStateOf(MainViewModel.TextureRichness.High) }
    val radioOptions = listOf("No", "Yes")

    val richnessOptions = MainViewModel.TextureRichness.entries

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    if (openBasicDialog.value) {
        BasicAlertDialog(onDismissRequest = {
            // Dismiss the dialog when the user clicks outside the dialog or on the back
            // button. If you want to disable that functionality, simply use an empty
            // onDismissRequest.
            openBasicDialog.value = false
        }) {
            Surface(
                modifier = Modifier
                    .wrapContentWidth()
                    .wrapContentHeight(),
                shape = MaterialTheme.shapes.large,
                tonalElevation = AlertDialogDefaults.TonalElevation
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Choose texture richness of the model"
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                        //.wrapContentSize(Alignment.TopStart)
                    ) {
//                        Row {
//                            Text(text = selectedRichness.value.toString())
//                            IconButton(onClick = { expanded = true }) {
//                                Icon(
//                                    Icons.Default.KeyboardArrowDown,
//                                    contentDescription = "Localized description"
//                                )
//                            }
//                        }
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2)
                        ) {
                            items(richnessOptions) {
                                val selected = selectedRichness.value == it
                                Card(
                                    colors = if (!selected) CardDefaults.cardColors() else CardDefaults.cardColors(
                                        //contentColor = Color.Green,
                                        containerColor = Color.Green.copy(0.4f)
                                    ),
                                    onClick = {
                                        selectedRichness.value = it
                                    },
                                    //border = if (!selected) CardDefaults.outlinedShape else CardDefaults.shape,
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(50.dp)
                                        .padding(vertical = 5.dp)
                                    //      .align(Center),

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

//                        richnessOptions.forEach {
//                            val selected = selectedRichness.value == it
//                            val interactionSource = remember { MutableInteractionSource() }
//                            //val isPressed by interactionSource.collectIsPressedAsState()
//
//                            Card(
//                                colors = if (!selected) CardDefaults.cardColors() else CardDefaults.cardColors(
//                                    //contentColor = Color.Green,
//                                    containerColor = Color.Green
//                                ),
//                                onClick = {
//                                    selectedRichness.value = it
//                                },
//                                //border = if (!selected) CardDefaults.outlinedShape else CardDefaults.shape,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .height(50.dp)
//                                    .padding(vertical = 5.dp),
//
//                                ) {
//                                Text(text = it.toString(), textAlign = TextAlign.Center)
//                            }
//                        }


//                        DropdownMenu(
//                            expanded = expanded,
//                            onDismissRequest = { expanded = false }
//                        ) {
//                            DropdownMenuItem(
//                                text = { Text("High") },
//                                onClick = {
//                                    selectedRichness.value = MainViewModel.TextureRichness.High
//                                },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Filled.Star,
//                                        contentDescription = null
//                                    )
//                                })
//                            DropdownMenuItem(
//                                text = { Text("Medium") },
//                                onClick = {
//                                    selectedRichness.value = MainViewModel.TextureRichness.Medium
//                                },
//                                /*                leadingIcon = {
//                                                    Icon(
//                                                        Icons.Outlined.Star,
//                                                        contentDescription = null
//                                                    )
//                                                }*/
//                            )
//                            DropdownMenuItem(
//                                text = { Text("Low") },
//                                onClick = {
//                                    selectedRichness.value = MainViewModel.TextureRichness.Low
//                                },
////                                leadingIcon = {
////                                    Icon(
////                                        Icons.Outlined.,
////                                        contentDescription = null
////                                    )
////                                },
//                                //  trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
//                            )
//                            DropdownMenuItem(
//                                text = { Text("None") },
//                                onClick = {
//                                    selectedRichness.value = MainViewModel.TextureRichness.None
//                                },
//                                //trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
//                            )
//                        }
//
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Row {
                        Column {
                            Text(text = "Would you like to erase")
                            Text(
                                text = " preview model ?", fontWeight = FontWeight.Bold
                            )
                        }
                        Column(Modifier.selectableGroup()) {
                            radioOptions.forEach { text ->
                                Row(
                                    Modifier
                                        //  .fillMaxWidth()
                                        //.height(56.dp)
                                        .selectable(
                                            selected = (text == selectedOption),
                                            onClick = { onOptionSelected(text) },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 16.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    RadioButton(
                                        selected = (text == selectedOption),
                                        onClick = null // null recommended for accessibility with screenreaders
                                    )
                                    Text(
                                        text = text,
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            overwrite.value = selectedOption == "Yes"
                            mainViewModel.loadRefineModel(
                                id = modelId,
                                textureRichness = selectedRichness.value,
                                overwrite = selectedOption == "Yes"
                            )
                            openBasicDialog.value = false
                        }, modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }

}


@Composable
fun DoDownload(
    openDialog: MutableState<Boolean>,
    confirm: MutableState<Boolean>,
    path: String?,
    downloader: DownloaderImpl,
    modelDescription: String
) {
    val modelName = modelDescription.split(' ')

    val helpText = remember { mutableStateOf("Save to downloads") }

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, title = {
            Text(text = "Download")
        }, text = {
            Text(
                helpText.value
            )
        }, confirmButton = {
            TextButton(onClick = {
                if (path != null) {
                    confirm.value = true
                    openDialog.value = false
                    downloader.downloadFile(path, modelName = "model")
                } //"${modelName[0]} ${modelName[1]}"
                else {
                    helpText.value = "There is no link or it is wrong :("
                }
            }) {
                Text("Confirm")
            }
        }, dismissButton = {
            TextButton(onClick = {
                confirm.value = false
                openDialog.value = false
            }) {
                Text("Dismiss")
            }
        })
    }
}