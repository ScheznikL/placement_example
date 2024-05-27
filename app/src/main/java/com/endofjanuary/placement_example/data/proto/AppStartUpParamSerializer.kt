package com.endofjanuary.placement_example.data.proto

import android.content.Context
import androidx.datastore.core.CorruptionException
import androidx.datastore.core.DataStore
import androidx.datastore.core.Serializer
import androidx.datastore.dataStore
import com.endofjanuary.placement_example.LastModelsParam
import com.google.protobuf.InvalidProtocolBufferException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream

object LastModelsParamSerializer : Serializer<LastModelsParam> {

    override val defaultValue: LastModelsParam = LastModelsParam.getDefaultInstance()

    override suspend fun readFrom(input: InputStream): LastModelsParam = withContext(Dispatchers.IO) {
        try {
            return@withContext LastModelsParam.parseFrom(input)
        } catch (exception: InvalidProtocolBufferException) {
            throw CorruptionException("Cannot read proto.", exception)
        }
    }

    override suspend fun writeTo(t: LastModelsParam, output: OutputStream) = withContext(Dispatchers.IO) { t.writeTo(output) }
}

val Context.appStartupParamsDataStore: DataStore<LastModelsParam> by dataStore(
    fileName = "app_startup_params.pb",
    serializer = LastModelsParamSerializer
)