package com.endofjanuary.placement_example.repo

import android.util.Log
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.asByteStream
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.net.URL
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
/*
* val dynamoDbClient = DynamoDbClient {
    region = "us-east-1"
    credentialsProvider = ProfileCredentialsProvider(profileName = "myprofile")
}
* */

    private val _bucketName = "3d-referense-image-storage"
    private var i = 0
    private val objectKey: String = "key${++i}"
    override suspend  fun putPresignedS3Object(objectPath: String) {
        val metadataVal = mutableMapOf<String, String>()
        //metadataVal["myVal"] = "test"

        val request = PutObjectRequest {
            bucket = _bucketName
            key = objectKey
         //   metadata = metadataVal
            body = File(objectPath).asByteStream()
        }

        val s3 = S3Client {
            region = "eu-north-1"
           // credentialsProvider = EnvironmentCredentialsProvider()
        }

        val presignedRequest = s3.presignPutObject(request, 5.hours)
        Log.d("resReq", presignedRequest.url.toString())
        val objectContents = URL(presignedRequest.url.toString()).readText()
       /* val putRequest = Request
            .Builder()
            .url(presignedRequest.url.toString())
            .apply {
                presignedRequest.headers.forEach { key, values ->
                    header(key, values.joinToString(", "))
                }
            }
            .put(content.toRequestBody())
            .build()

        val response = OkHttpClient().newCall(putRequest).execute()*/
      //  assert(response.isSuccessful)

    }

    suspend fun getObjectPresigned(s3: S3Client, bucketName: String, keyName: String): String {
        // Create a GetObjectRequest.
        val unsignedRequest = GetObjectRequest {
            bucket = bucketName
            key = keyName
        }

        // Presign the GetObject request.
        val presignedRequest = s3.presignGetObject(unsignedRequest, 24.hours)

        // Use the URL from the presigned HttpRequest in a subsequent HTTP GET request to retrieve the object.
        val objectContents = URL(presignedRequest.url.toString()).readText()

        return objectContents
    }

}