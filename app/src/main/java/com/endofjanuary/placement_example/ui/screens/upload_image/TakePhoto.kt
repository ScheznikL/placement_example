package com.endofjanuary.placement_example.ui.screens.upload_image

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture.OnImageCapturedCallback
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.endofjanuary.placement_example.R

@Composable
fun TakePhoto(
    onPhotoTaken: (Bitmap) -> Unit, modifier: Modifier = Modifier, onClose: () -> Unit
) {
    val context = LocalContext.current

    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(
                CameraController.IMAGE_CAPTURE
            )
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        CameraPreview(
            controller = controller, modifier = Modifier.fillMaxSize()
        )

        Row(
            modifier = Modifier
                .offset(10.dp, 16.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            IconButton(
                onClick = {
                    controller.cameraSelector =
                        if (controller.cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) {
                            CameraSelector.DEFAULT_FRONT_CAMERA
                        } else CameraSelector.DEFAULT_BACK_CAMERA
                },
            ) {
                Icon(
                    painterResource(R.drawable.ic_switch_camera),
                    contentDescription = stringResource(R.string.switch_camera)
                )
            }
            IconButton(onClick = onClose) {
                Icon(
                    Icons.Default.Close, contentDescription = stringResource(R.string.back)
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            IconButton(
                onClick = {
                    takePhoto(
                        controller = controller,
                        onPhotoTaken = onPhotoTaken,
                        context = context
                    )
                },
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.background,
                        shape = CircleShape
                    )
                    .size(80.dp),
            ) {
                Icon(
                    painterResource(id = R.drawable.ic_photo_camera),
                    contentDescription = stringResource(R.string.take_photo),
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }

}

private fun takePhoto(
    controller: LifecycleCameraController, onPhotoTaken: (Bitmap) -> Unit, context: Context
) {
    controller.takePicture(ContextCompat.getMainExecutor(context),
        object : OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                super.onCaptureSuccess(image)

                val matrix = Matrix().apply {
                    postRotate(image.imageInfo.rotationDegrees.toFloat())
                }
                val rotatedBitmap = Bitmap.createBitmap(
                    image.toBitmap(), 0, 0, image.width, image.height, matrix, true
                )

                onPhotoTaken(rotatedBitmap)
            }

            override fun onError(exception: ImageCaptureException) {
                super.onError(exception)
            }
        })
}