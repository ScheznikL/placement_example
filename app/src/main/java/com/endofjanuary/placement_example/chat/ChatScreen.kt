package com.endofjanuary.placement_example.chat

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.endofjanuary.placement_example.data.remote.gpt.response.Message
import com.endofjanuary.placement_example.utils.BottomBar
import com.endofjanuary.placement_example.utils.TopBar
import org.koin.androidx.compose.getViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    navController: NavController
) {
    val viewModel = getViewModel<ChatScreenViewModel>()

    var textInput by remember {
        mutableStateOf("")
    }

    var list by remember {
        mutableStateOf(listOf<String>())
    }

    val messagesListState = remember {
        viewModel.messagesListState
    }

    val loadError by remember { viewModel.loadError }
    val isLoading by remember { viewModel.isLoading }
    val loadSuccess by remember { viewModel.isSuccess }

    Scaffold(
        topBar = {
            TopBar()
        },
        modifier = Modifier.fillMaxSize(),
        bottomBar = { BottomBar(navController = navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            //.weight(1f),
            // verticalArrangement = Arrangement.Bottom,
        ) {
            Row(
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    // .padding(15.dp)
                    .background(
                        //width = 2.dp,
                        color = MaterialTheme.colorScheme.inversePrimary,
                        shape = RoundedCornerShape(bottomStartPercent = 80, bottomEndPercent = 80)
                    )
                    .align(Alignment.TopCenter),
            ) {
                Text(
                    text = "Describe desirable model bellow",
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Companion.W200)
                )
            }
            MessagesList(
                list = messagesListState,
                isError = loadError,
                isLoading = isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    //.padding(20.dp)
                // .weight(9f, true)
            )
            if (isLoading) {
                LinearProgressIndicator()
                HorizontalDivider()
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .imePadding()
                    .align(Alignment.BottomStart)

                // .weight(1f)
                ,
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
                        // navController.navigate("loading_screen/${viewModel.description}")
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
                            viewModel.send(textInput)
//                            list += textInput
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
fun MessagesList(
    modifier: Modifier = Modifier,
    list: List<Message>,
    isLoading: Boolean = false,
    isError: String?
) {
    Row {
        LazyColumn {
            items(list) {
                if (it.role == "user") {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = modifier.border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.primary)
                        )
                    ) {
                        Text(
                            it.content,
                            modifier = modifier.padding(16.dp)
                        )
                    }
                    HorizontalDivider()
                }
                if (it.role == "assistant") {
                    Row(
                        horizontalArrangement = Arrangement.End,
                    ) {
                        Text(
                            it.content,
                            modifier = modifier.padding(16.dp)
                        )
                    }
                    HorizontalDivider()
                }

                if (!isError.isNullOrEmpty()) {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = modifier.background(color = MaterialTheme.colorScheme.errorContainer)
                    ) {
                        Text(
                            isError,
                            color = MaterialTheme.colorScheme.error,
                            modifier = modifier.padding(16.dp)
                        )
                    }
                    HorizontalDivider()
                }
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

