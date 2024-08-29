package com.endofjanuary.placement_example.ui.screens.models_list_screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.domain.models.ModelEntry

@Composable
fun ModelsListContent(
    navController: NavController,
    modelsListState: List<ModelEntry>,
    isFromImage: Boolean,
    loadError: String,
    isLoading: Boolean,
    viewModel: ModelsListViewModel,
    onSearch: (String, Boolean) -> Unit
) {
    SearchBar(
        hint = stringResource(R.string.search), modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        onSearch(it, isFromImage)
    }
    Spacer(modifier = Modifier.height(12.dp))
    ModelsList(
        navController = navController,
        modelListState = modelsListState,
        loadError = loadError,
        isLoading = isLoading,
        viewModel = viewModel
    )
}

@Composable
fun SearchBar(
    modifier: Modifier = Modifier, hint: String = "", onSearch: (String) -> Unit = {}
) {
    var text by remember {
        mutableStateOf("")
    }
    var isHintDisplayed by remember {
        mutableStateOf(hint != "")
    }
    Box(modifier = modifier) {
        BasicTextField(value = text,
            onValueChange = {
                text = it
                onSearch(it)
            },
            maxLines = 1,
            singleLine = true,
            textStyle = TextStyle(color = Color.Black),
            modifier = Modifier
                .fillMaxWidth()
                .shadow(5.dp, CircleShape)
                .background(Color.White, CircleShape)
                .padding(horizontal = 20.dp, vertical = 12.dp)
                .onFocusChanged {
                    isHintDisplayed = !it.isFocused && text.isEmpty()
                }) {
            if (isHintDisplayed) {
                Text(
                    text = hint,
                    color = Color.LightGray,
                    modifier = Modifier
                    // .padding(horizontal = 20.dp, vertical = 12.dp)
                )
            } else {
                Text(
                    text = text,
                    modifier = Modifier
                    // .padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }
    }
}
