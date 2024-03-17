package com.endofjanuary.placement_example.repo

import android.util.Log
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.asByteStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import kotlin.time.Duration.Companion.hours

class AWStorageRepoImpl:AWStorageRepo {
    override suspend fun putObjectPresigned(s3: S3Client, bucketName: String, keyName: String, content: String) {
        // Create a PutObjectRequest.
        val unsignedRequest = PutObjectRequest {
            bucket = bucketName
            key = keyName
        }

        // Presign the request.
        val presignedRequest = s3.presignPutObject(unsignedRequest, 24.hours)

        // Use the URL and any headers from the presigned HttpRequest in a subsequent HTTP PUT request to retrieve the object.
        // Create a PUT request using the OKHttpClient API.
        val putRequest = Request
            .Builder()
            .url(presignedRequest.url.toString())
            .apply {
                presignedRequest.headers.forEach { key, values ->
                    header(key, values.joinToString(", "))
                }
            }
            .put(content.toRequestBody())
            .build()

        val response = OkHttpClient().newCall(putRequest).execute()
        assert(response.isSuccessful)
    }


    override suspend  fun putS3Object(bucketName: String, objectKey: String, objectPath: String) {
        val metadataVal = mutableMapOf<String, String>()
        metadataVal["myVal"] = "test"

        val request = PutObjectRequest {
            bucket = bucketName
            key = objectKey
            metadata = metadataVal
            body = File(objectPath).asByteStream()
        }

        S3Client { region = "us-east-1" }.use { s3 ->
            val response = s3.putObject(request)
            Log.d("S3 response","Tag information is ${response.eTag}")
        }
    }
}