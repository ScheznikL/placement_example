package com.endofjanuary.placement_example.upload_image

import android.util.Log
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
            Text(text = "Your data is uploading ...")
            CircularProgressIndicator()
        } else if (isLoading) {
            Log.d("loadModel UI", "is Loading")
            Text(text = "The model is in progress...\r\n${progress}%")
            CircularProgressIndicator()
        } else if (isUploadingError.isNotBlank()) {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "There is an error: $isUploadingError")
            }
        }
    }
}