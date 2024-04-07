package com.endofjanuary.placement_example.utils.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R

@Composable
fun DoRefineDialog(
    openDialog: MutableState<Boolean>,
    confirm: MutableState<Boolean>
) {
    val proceed = remember {
        mutableStateOf(false)
    }
    if (openDialog.value) {
        AlertDialog(
            onDismissRequest = {
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
                    "Do you want to refine current model ?" +
                            "\n\r processing may took some time"
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        confirm.value = true
                        openDialog.value = false
                    }
                ) {
                    Text("Confirm")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        confirm.value = false
                        openDialog.value = false
                    }
                ) {
                    Text("Dismiss")
                }
            }
        )
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
    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    if (openBasicDialog.value) {
        BasicAlertDialog(
            onDismissRequest = {
                // Dismiss the dialog when the user clicks outside the dialog or on the back
                // button. If you want to disable that functionality, simply use an empty
                // onDismissRequest.
                openBasicDialog.value = false
            }
        ) {
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
                    Spacer(modifier = Modifier.height(24.dp))
                    Box(
                        modifier = Modifier
                            //  .fillMaxSize()
                            .wrapContentSize(Alignment.TopStart)
                    ) {
                        Row {
                            Text(text = selectedRichness.value.toString())
                            IconButton(onClick = { expanded = true }) {
                                Icon(
                                    Icons.Default.KeyboardArrowDown,
                                    contentDescription = "Localized description"
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("High") },
                                onClick = {
                                    selectedRichness.value = MainViewModel.TextureRichness.High
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Filled.Star,
                                        contentDescription = null
                                    )
                                })
                            DropdownMenuItem(
                                text = { Text("Medium") },
                                onClick = {
                                    selectedRichness.value = MainViewModel.TextureRichness.Medium
                                },
                                /*                leadingIcon = {
                                                    Icon(
                                                        Icons.Outlined.Star,
                                                        contentDescription = null
                                                    )
                                                }*/
                            )
                            DropdownMenuItem(
                                text = { Text("Low") },
                                onClick = {
                                    selectedRichness.value = MainViewModel.TextureRichness.Low
                                },
//                                leadingIcon = {
//                                    Icon(
//                                        Icons.Outlined.,
//                                        contentDescription = null
//                                    )
//                                },
                                //  trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
                            )
                            DropdownMenuItem(
                                text = { Text("None") },
                                onClick = {
                                    selectedRichness.value = MainViewModel.TextureRichness.None
                                },
                                //trailingIcon = { Text("F11", textAlign = TextAlign.Center) }
                            )
                        }
                    }

                    //  Spacer(modifier = Modifier.height(10.dp))

                    Row {
                        Column {
                            Text(text = "Would you like to erase")
                            Text(
                                text = " preview model ?",
                                fontWeight = FontWeight.Bold
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
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }

}