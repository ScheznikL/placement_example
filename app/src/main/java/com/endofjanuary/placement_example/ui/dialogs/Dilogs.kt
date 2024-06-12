package com.endofjanuary.placement_example.ui.dialogs

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.Edit
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
import androidx.compose.material3.TextField
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
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
            openDialog.value = false
        },
            icon = { Icon(painterResource(R.drawable.ic_clock), contentDescription = null) },
            title = {
                Text(text = stringResource(R.string.refine_dialog_header))
            },
            text = {
                Text(
                    stringResource(R.string.refine_model_question)
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirm.value = true
                    openDialog.value = false
                }) {
                    Text(stringResource(id = R.string.confirm))
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    confirm.value = false
                    openDialog.value = false
                }) {
                    Text(stringResource(id = R.string.dismiss))
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
    val radioOptions = listOf(R.string.no, R.string.yes)

    val richnessOptions = MainViewModel.TextureRichness.entries

    val (selectedOption, onOptionSelected) = remember { mutableStateOf(radioOptions[0]) }

    if (openBasicDialog.value) {
        BasicAlertDialog(onDismissRequest = {
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
                        text = stringResource(R.string.choose_texture_richness)
                    )
                    Spacer(modifier = Modifier.height(22.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                    ) {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2)
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
                    Spacer(modifier = Modifier.height(22.dp))
                    Row {
                        Column {
                            Text(text = stringResource(R.string.erase_question))
                            Text(
                                text = stringResource(R.string.model_type_bold),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(Modifier.selectableGroup()) {
                            radioOptions.forEach { text ->
                                Row(
                                    Modifier
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
                                        onClick = null
                                    )
                                    Text(
                                        text = stringResource(text),
                                        style = MaterialTheme.typography.bodyLarge,
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        }
                    }
                    TextButton(
                        onClick = {
                            overwrite.value = selectedOption == R.string.yes
                            mainViewModel.loadRefineModel(
                                id = modelId,
                                textureRichness = selectedRichness.value,
                                overwrite = selectedOption == R.string.yes
                            )
                            openBasicDialog.value = false
                        }, modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(stringResource(id = R.string.confirm))
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
    onDownload: (String, String?) -> Unit,
    modelFileName: String?,
    refinedUrl: String?
) {
    val name = mutableStateOf(modelFileName.toString())
    val text = stringResource(R.string.initial_download_help)
    val helpText = remember { mutableStateOf(text) }

    val enableEdit = mutableStateOf(false)

    if (openDialog.value) {
        AlertDialog(onDismissRequest = {
            openDialog.value = false
        }, title = {
            Text(text = stringResource(R.string.download))
        }, text = {
            Column {
                Text(
                    helpText.value
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (!enableEdit.value) {
                        Text(
                            name.value
                        )
                    } else {
                        TextField(
                            value = name.value,
                            onValueChange = { name.value = it },
                        )
                    }
                    Icon(imageVector = if (enableEdit.value) Icons.Default.Edit else Icons.Outlined.Edit,
                        contentDescription = stringResource(R.string.edit_name),
                        modifier = Modifier.clickable {
                            enableEdit.value = !enableEdit.value
                        })
                }
            }
        }, confirmButton = {
            TextButton(onClick = {
                onDownload(name.value, refinedUrl)
                confirm.value = true
                openDialog.value = false
            }) {
                Text(stringResource(id = R.string.confirm))
            }
        }, dismissButton = {
            TextButton(onClick = {
                confirm.value = false
                openDialog.value = false
            }) {
                Text(stringResource(id = R.string.dismiss))
            }
        })
    }
}
