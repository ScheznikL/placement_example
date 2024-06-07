package com.endofjanuary.placement_example.upload_image

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.endofjanuary.placement_example.R


@Composable
fun OnDataLoading(
    isLoading: Boolean,
    isUploadingError: String,
    isUploading: Boolean,
    progress: Int?,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier.fillMaxWidth()
    ) {
        if (isUploading) {
            Text(text = stringResource(R.string.your_data_is_uploading))
            CircularProgressIndicator()
        } else if (isLoading) {
            Text(text = stringResource(R.string.model_progress, progress.toString()))
            CircularProgressIndicator()
        } else if (isUploadingError.isNotEmpty()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.error_header, progress.toString()) + isUploadingError)
            }
        }
    }
}