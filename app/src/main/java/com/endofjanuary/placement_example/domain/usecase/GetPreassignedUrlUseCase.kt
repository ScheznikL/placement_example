package com.endofjanuary.placement_example.domain.usecase

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.endofjanuary.placement_example.domain.repo.AWStorageRepo
import com.endofjanuary.placement_example.utils.Resource
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException

class GetPreassignedUrlUseCase(
    private val awStorageRepo: AWStorageRepo,
) {
    suspend fun getPresignedUrl(context: Context, image: Uri?, photo: Bitmap?): Resource<String>? {
        if (image != null) {
            return getImagePresignedUrl(context = context, image)
        } else if (photo != null) {
            return getBitmapPresignedUrl(photo)
        }
        return null
    }

    suspend fun getImagePresignedUrl(context: Context, image: Uri): Resource<String> {
        return awStorageRepo.putObjectPresigned(
            image.path!!,
            context.contentResolver.openInputStream(image)!!
        )
    }

    suspend fun getBitmapPresignedUrl(photo: Bitmap): Resource<String> {
        return try {
            awStorageRepo.putObjectPresigned(
                "photo",
                ByteArrayInputStream(convertBitmapToByteArray(photo))
            )
        } catch (e: IOException) {
            Resource.Error(e.message.toString())
        }
    }

    private fun convertBitmapToByteArray(bitmap: Bitmap): ByteArray? {
        var baos: ByteArrayOutputStream? = null
        return try {
            baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
            baos.toByteArray()
        } finally {
            if (baos != null) {
                try {
                    baos.close()
                } catch (e: IOException) {
                    throw e
                }
            }
        }
    }
}