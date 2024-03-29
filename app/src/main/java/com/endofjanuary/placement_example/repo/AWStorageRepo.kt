package com.endofjanuary.placement_example.repo

import aws.sdk.kotlin.services.s3.S3Client

interface AWStorageRepo {
    suspend fun putPresignedS3Object(objectPath: String)
    suspend fun putObjectPresigned(s3: S3Client, bucketName: String, keyName: String, content: String)

}