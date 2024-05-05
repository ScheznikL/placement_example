package com.endofjanuary.placement_example.transit_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.endofjanuary.placement_example.R


@Composable
fun NewModelType(navController: NavController) {
    Surface(
        modifier = Modifier
            .wrapContentWidth()
            .wrapContentHeight(),
        shape = MaterialTheme.shapes.large,
        tonalElevation = AlertDialogDefaults.TonalElevation
    ) {
        Column(modifier = Modifier.padding(26.dp)) {
            Text(
                text = "New model",
                textAlign = TextAlign.Justify
            )
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = {
                        navController.navigate(
                            "chat_screen"
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 9.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painterResource(id = R.drawable.chatgptlogo),
                        "to chat"
                    )
                  //  Text("Chat")
                }
                IconButton(
                    onClick = {
                        navController.navigate(
                            "upload_image/${false}"
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 9.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_photo_camera),
                        "to camera"
                    )
                   // Text("Camera")
                }
                IconButton(
                    onClick = {
                        navController.navigate(
                            "upload_image/${true}"
                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 9.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_gallery),
                        "to gallery"
                    )
                 //   Text("Gallery")
                }
                IconButton(
                    onClick = {
//                        navController.navigate(
//                            "ar_screen/${modelId}"
//                        )
                    },
                    modifier = Modifier
                        .padding(start = 8.dp, end = 9.dp)
                        .size(35.dp)
                        .clip(CircleShape)
                ) {
                    Icon(
                        painterResource(id = R.drawable.ic_open_file),
                        "open"
                    )
                 //   Text("Open")
                }
            }
        }
    }

}