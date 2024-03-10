package com.endofjanuary.placement_example.transit_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController


@Composable
fun ModelViewTypeDialog(navController: NavController, modelId: Int) {
        Surface(
            modifier = Modifier
                .wrapContentWidth()
                .wrapContentHeight(),
            shape = MaterialTheme.shapes.large,
            tonalElevation = AlertDialogDefaults.TonalElevation
        ) {
            Column(modifier = Modifier.padding(26.dp)) {
                Text(
                    text = "Would you like to place this model in your room via camera " +
                            "or just view in 3D viewer?",
                    textAlign = TextAlign.Justify
                )
                Spacer(modifier = Modifier.height(24.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    TextButton(
                        onClick = {
                            navController.navigate(
                                "threed_screen/${modelId}"
                            )
                        },
                        // modifier = Modifier.align(Start)
                    ) {
                        Text("Viewer")
                    }
                    TextButton(
                        onClick = {
                            navController.navigate(
                                "ar_screen/${modelId}"
                            )
                        },
                        // modifier = Modifier.align(Start)
                    ) {
                        Text("Camera")
                    }
                }
            }
        }

}