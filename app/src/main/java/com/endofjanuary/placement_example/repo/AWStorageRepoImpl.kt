package com.endofjanuary.placement_example.repo


import android.util.Log
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.smithy.kotlin.runtime.content.asByteStream
import com.endofjanuary.placement_example.utils.Resource
import com.endofjanuary.placement_example.utils.convertToReadableFormat
import java.io.InputStream
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.hours

const val BUCKET_NAME = "3d-referense-image-storage"
const val DEFAULT_METADATA = "time"

class AWStorageRepoImpl(
    private val s3: S3Client,
) : AWStorageRepo {

    override suspend fun putObjectPresigned(
        content: String, stream: InputStream
    ): Resource<String> {

        val file = kotlin.io.path.createTempFile(suffix = ".png")

        stream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        try {
        val metadataVal = mutableMapOf<String, String>()
        metadataVal[DEFAULT_METADATA] = convertToReadableFormat(System.currentTimeMillis())

            val resp = s3.putObject(PutObjectRequest {
                bucket = BUCKET_NAME
                key = "${Path(content).name}.png"
                metadata = metadataVal
                body = file.asByteStream()
            })

            val unsignedRequest = GetObjectRequest {
                bucket = BUCKET_NAME
                key = "${Path(content).name}.png"
            }

            val presignedRequest = s3.presignGetObject(unsignedRequest, 1.hours)

            return Resource.Success(presignedRequest.url.toString())
        } catch (e: Exception) {
            Log.w(
                "loadModel AWS FAILED || //",
                e.message.toString()
            )
            return Resource.Error(e.message.toString())
        }
    }
}

