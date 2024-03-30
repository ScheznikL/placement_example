package com.endofjanuary.placement_example.repo

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import aws.sdk.kotlin.runtime.auth.credentials.StaticCredentialsProvider
import aws.sdk.kotlin.services.s3.S3Client
import aws.sdk.kotlin.services.s3.model.GetObjectRequest
import aws.sdk.kotlin.services.s3.model.PutObjectRequest
import aws.sdk.kotlin.services.s3.presigners.presignGetObject
import aws.sdk.kotlin.services.s3.presigners.presignPutObject
import aws.smithy.kotlin.runtime.content.asByteStream
import com.endofjanuary.placement_example.AwsAccessKeyId
import com.endofjanuary.placement_example.AwsSecretAccessKey
import com.endofjanuary.placement_example.AwsSessionToken
import com.endofjanuary.placement_example.utils.Resource
import java.io.File
import java.io.InputStream
import java.net.URL
import java.nio.file.Path
import kotlin.io.path.Path
import kotlin.io.path.name
import kotlin.io.path.outputStream
import kotlin.time.Duration.Companion.hours


class AWStorageRepoImpl : AWStorageRepo {

    private val ingestionDirPath = "/tmp/media-in"
    // private val _bucketArn = "arn:aws:s3:::3d-referense-image-storage"

    private val _bucketName = "3d-referense-image-storage"
    private var i = 0
    //private val objectKey: String = "key${++i}"


    override suspend fun putObject(
        content: String,
        stream: InputStream
    ) {
        val s3 = getClient()

        val file = kotlin.io.path.createTempFile(suffix = ".jpg")

        stream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val metadataVal = mutableMapOf<String, String>()
        metadataVal[Path(content).name] = "test"
        try {
            val resp = s3.putObject(PutObjectRequest {
                bucket = _bucketName
                key = "${Path(content).name}.jpg"
                metadata = metadataVal
                body = file.asByteStream()
            })
            Log.d("resReq", resp.toString())
        } catch (e: Exception) {
            Log.d("resReq Err put", e.message.toString())
        }

        /*        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Upload files
                    if (moveFilesToIngestionDirPath(file)) {
                        val uploadResult = ingestionDir
                            .walk()
                            .asFlow()
                            .mapNotNull(::mediaMetadataExtractor)
                            .map { mediaMetadata ->
                                s3.putObject(PutObjectRequest {
                                    bucket = _bucketName
                                    key = Path(content).name
                                    metadata = metadataVal
                                    body = file.asByteStream()
                                })
                            }
                            .toList()
                    }
                }*/

        // Create a PutObjectRequest.
//        val unsignedRequest = PutObjectRequest {
//            bucket = _bucketName
//            key = Path(content).name
//            body = ByteStream.fromFile(File(content))
//        }

        /***
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

        val response = OkHttpClient().newCall(putRequest).execute()   //  assert(response.isSuccessful)
         ****/


        //  val presignedRequest = s3.putObject(unsignedRequest)


        // Use the URL and any headers from the presigned HttpRequest in a subsequent HTTP PUT request to retrieve the object.
        // Create a PUT request using the OKHttpClient API.

    }

    override suspend fun putPresignedS3Object(
        objectPath: String,
        stream: InputStream
    ) {
        val s3 = getClient()

        val metadataVal = mutableMapOf<String, String>()
        metadataVal[Path(objectPath).name] = "test"

        val file = kotlin.io.path.createTempFile(suffix = ".jpg")

        stream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }

        val request = PutObjectRequest {
            bucket = _bucketName
            key = Path(objectPath).name
            metadata = metadataVal
            body = file.asByteStream()
        }
        val name = Path(objectPath).name
        val presignedRequest = s3.presignPutObject(request, 5.hours)
        Log.d("resReq", presignedRequest.url.toString())
        val objectContents = URL(presignedRequest.url.toString()).readText()

    }

    override suspend fun putObjectPresigned(
        content: String, stream: InputStream
    ): Resource<String> {
        val s3 = getClient()

        val file = kotlin.io.path.createTempFile(suffix = ".jpg")

        stream.use { input ->
            file.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val metadataVal = mutableMapOf<String, String>()
        metadataVal[Path(content).name] = "test"
        try {
            val resp = s3.putObject(PutObjectRequest {
                bucket = _bucketName
                key = "${Path(content).name}.jpg"
                metadata = metadataVal
                body = file.asByteStream()
            })
            Log.d("resReq", resp.toString())

            val unsignedRequest = GetObjectRequest {
                bucket = _bucketName
                key = "${Path(content).name}.jpg"
            }

            // Presign the GetObject request.
            val presignedRequest = s3.presignGetObject(unsignedRequest, 3.hours)

            Log.d("resReq Presighn", presignedRequest.url.toString())
            return Resource.Success(presignedRequest.url.toString())
        } catch (e: Exception) {
            Log.d("resReq Err put", e.message.toString())
            return Resource.Error(e.message.toString())
        }
    }

    private fun getClient(): S3Client {

        val staticCredentials = StaticCredentialsProvider {
            this.accessKeyId = AwsAccessKeyId
            this.secretAccessKey = AwsSecretAccessKey
            this.sessionToken = AwsSessionToken
        }

        return S3Client {
            region = "eu-north-1"
            credentialsProvider = staticCredentials
        }
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

    /** Validate file path and optionally create directory */
    fun validateDirectory(dirPath: String): File {
        val dir = File(dirPath)

        require(dir.isDirectory || !dir.exists()) { "Unable to use $dir" }

        if (!dir.exists()) {
            try {
                //      dir.mkdirs()
            } catch (e: Exception) {
                Log.d("Error", e.message.toString())
            }
        }

        return dir
    }

    data class MediaMetadata(val title: String, val year: Int, val file: File)

    fun mediaMetadataExtractor(file: File): MediaMetadata? {
        // media metadata is extracted from filename: <title>_<year>.avi
        val filenameMetadataRegex = "([\\w\\s]+)_([\\d]+).jpg".toRegex()
        if (!file.isFile || file.length() == 0L) return null

        val matchResult = filenameMetadataRegex.find(file.name) ?: return null

        val (title, year) = matchResult.destructured
        return MediaMetadata(title, year.toInt(), file)
    }

    /** Move files to directories based on upload results */
    @RequiresApi(Build.VERSION_CODES.O)
    fun moveFilesToIngestionDirPath(file: Path): Boolean {
        try {
            val targetFilePath = ingestionDirPath
            val targetPath = File(targetFilePath)
            /// Files.move(file, File(targetPath, file.fileName.toString()).toPath())
            // Files.createFile(file.toPath())
            return true
        } catch (e: Exception) {
            Log.d("transfer error", e.message.toString())
            return false
        }
    }
}

