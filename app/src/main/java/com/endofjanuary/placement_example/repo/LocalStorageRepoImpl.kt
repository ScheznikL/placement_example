package com.endofjanuary.placement_example.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.net.URL
import java.nio.ByteBuffer

class LocalStorageRepoImpl : LocalStorageRepo {
    override suspend fun saveModel(url: String): String { //todo return path
        try {
            GlobalScope.launch(Dispatchers.IO) {
                URL(url).openStream().use { inputStream: InputStream ->
                    val inputStream = BufferedInputStream(inputStream)
                    ByteArrayOutputStream().use { output ->
                        inputStream.copyTo(output)
                        val byteArr = output.toByteArray()
                        val byteBuffer = ByteBuffer.wrap(byteArr)
                        val rewound = byteBuffer.rewind()
                        withContext(Dispatchers.Main) {
                            /*  modelViewer.destroyModel()
                              modelViewer.loadModelGlb(rewound)
                              modelViewer.transformToUnitCube()*/
                            output.close()
                            inputStream.close()

                        }
                    }
                }
            }
            return "true"
        } catch (e: Exception) {
            return "false"
        }

    }

  /*  override suspend fun loadModel(): Resource<ModelEntry> {
        TODO("Not yet implemented")
    }*/
}