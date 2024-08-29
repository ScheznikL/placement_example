package com.endofjanuary.placement_example.ui.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R

@Composable
fun UserInput(
    navController: NavController,
    isTextFieldEnabled: Boolean,
    focusManager: FocusManager,
    //textValue: MutableState<String>,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
) {
    var textInput by remember { mutableStateOf("") }
    Row(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.secondaryContainer)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_photo_camera),
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate("upload_image/${false}")
                },
            contentDescription = stringResource(R.string.camera),
        )
        BasicTextField(
            enabled = isTextFieldEnabled,
            modifier = Modifier
                .padding(end = 3.dp, start = 15.dp)
                .weight(1f),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Default,
                capitalization = KeyboardCapitalization.Sentences
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
            }),
            textStyle = TextStyle(
                lineHeight = 1.5.em, fontSize = 16.sp
            ),
            value = textInput,
            onValueChange = {
                textInput = it
                onValueChange(textInput)
            },
            maxLines = 7
        )

        Icon(
            painter = painterResource(R.drawable.ic_attachment),
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    navController.navigate("upload_image/${true}")
                },
            contentDescription = stringResource(R.string.gallery),
        )
        Icon(
            Icons.AutoMirrored.Filled.Send,
            modifier = Modifier
                .padding(16.dp)
                .clickable {
                    onSend()
                }
                .alpha(if (textInput.isNotBlank()) 1.0f else 0.5f),
            contentDescription = stringResource(R.string.send),
        )
    }
}