package com.endofjanuary.placement_example.ui.dialogs

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
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.endofjanuary.placement_example.MainViewModel
import com.endofjanuary.placement_example.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SpecifyRefineOptions(
    mainViewModel: MainViewModel,
    openBasicDialog: MutableState<Boolean>,
    modelId: String,
    id:Int,
    overwrite: MutableState<Boolean>
) {
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
                                meshyId = modelId,
                                id = id,
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
