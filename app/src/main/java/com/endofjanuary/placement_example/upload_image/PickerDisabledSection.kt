package com.endofjanuary.placement_example.upload_image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R
import com.endofjanuary.placement_example.utils.components.EdgeButton
import com.endofjanuary.placement_example.utils.screens.DefaultTopAppBar

@Composable
fun PickerDisabledSection(
    navController: NavController,
    modifier: Modifier = Modifier,
    onChooseAgain: () -> Unit,
) {
    Scaffold(
        topBar = { DefaultTopAppBar(navController = navController) },
        modifier = modifier
    ) { padding ->
        Column(Modifier.padding(padding), Arrangement.Center, Alignment.CenterHorizontally) {
            ElevatedCard(
                modifier = Modifier
                    .wrapContentSize()
                    .padding(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                )
            ) {
                Text(
                    text = stringResource(R.string.choose_your_image_or_go_back),
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 32.dp)
                )
            }
            Box(modifier = Modifier.fillMaxWidth()) {
                EdgeButton(
                    onProceedClick = { onChooseAgain() },
                    title = stringResource(R.string.try_choosing_once_more),
                    inverseShape = true,
                    modifier = Modifier.align(Alignment.TopStart)
                )
            }
        }
    }
}