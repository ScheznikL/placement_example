package com.endofjanuary.placement_example.chat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController
) {
    var textInput by remember {
        mutableStateOf("")
    }

    var list by remember {
        mutableStateOf(listOf<String>())
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .padding(10.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Bottom,
        ) {
            GreetingList("Android", list = list)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .padding(end = 5.dp),
                    value = textInput,
                    onValueChange = { textInput = it },
                )
                Button(
                    modifier = Modifier
                        .weight(1f),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        navController.navigate("loading_screen/${textInput}")
                    }) {
                    Icon(
                        Icons.Default.Done,
                        contentDescription = "final",
                    )
                }
                Button(
                    modifier = Modifier
                        .weight(1f),
                    shape = RoundedCornerShape(100),
                    contentPadding = PaddingValues(0.dp),
                    onClick = {
                        if (textInput.isNotBlank()) {
                            list += textInput
                            textInput = ""
                        }
                    }) {
                    Icon(
                        Icons.Default.Send,
                        contentDescription = "send",
                        modifier = Modifier.size(ButtonDefaults.IconSize)
                    )
                }

            }

        }
    }
}

@Composable
fun GreetingList(name: String, modifier: Modifier = Modifier, list: List<String>) {
    Row {
        LazyColumn {
            items(list) {
                Text(
                    it,
                    modifier = Modifier.padding(16.dp)
                )
                Divider()
            }
        }
    }
}

@Preview
@Composable
fun SimpleComposablePreview() {
    val navController = rememberNavController()
    ChatScreen(navController)
}

