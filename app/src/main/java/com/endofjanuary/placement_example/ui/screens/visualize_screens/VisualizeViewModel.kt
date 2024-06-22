package com.endofjanuary.placement_example.ui.screens.visualize_screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.room.ModelEntity
import com.endofjanuary.placement_example.domain.repo.DownloaderRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.math.Position
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class VisualizeViewModel(
    private val modelRoomRepo: ModelsRepo,
    private val downloader: DownloaderRepo,
) : ViewModel() {


    private val _loadedInstancesState: MutableState<Resource<ModelInstance>> =
        mutableStateOf(Resource.None())
    val loadedInstancesState: State<Resource<ModelInstance>>
        get() = _loadedInstancesState

    var modelImgUrl = mutableStateOf("")

    val downloadError: MutableState<String?> = mutableStateOf(null)
    val modelDescriptionShorten: MutableState<String?> = mutableStateOf(null)

    var modelFromRoom = mutableStateOf<Resource<ModelEntity>>(Resource.None())
    val modelDeleted = mutableStateOf<Resource<Int>>(Resource.None())

    fun loadModelRemote(
        modelLoader: ModelLoader, localId: Int
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                modelFromRoom.value = modelRoomRepo.getModelById(localId)
                when (modelFromRoom.value) {
                    is Resource.Success -> {
                        val names = modelFromRoom.value.data!!.modelDescription.split(' ')
                        modelDescriptionShorten.value = if (names.size >= 2) {
                            names.subList(0, 2).joinToString(" ") { it }
                        } else {
                            names.joinToString(" ") { it }
                        }

                        modelImgUrl.value = modelFromRoom.value.data!!.modelImageUrl
                        val result = modelLoader.loadModelInstance(
                            modelFromRoom.value.data!!.modelPath
                        )
                        if (result != null) {
                            currentNodes = mutableStateListOf(
                                ModelNode(
                                    modelInstance = result, scaleToUnits = 1.0f
                                ),
                            )

                            _loadedInstancesState.value = Resource.Success(result)
                        } else {
                            throw IllegalArgumentException("Empty")
                        }
                    }

                    is Resource.Error -> throw IllegalArgumentException(modelFromRoom.value.message)
                    else -> {}
                }
            } catch (e: Exception) {
                _loadedInstancesState.value = Resource.Error(e.message.toString())
            }
        }

    }

    fun deleteModel(
        meshyId: String
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val result = modelRoomRepo.deleteModelById(meshyId)
                when (result) {
                    is Resource.Success -> {
                        modelDeleted.value = result
                    }

                    is Resource.Error -> {
                        modelDeleted.value = result
                    }

                    else -> {}
                }
            }
        } catch (e: Exception) {
            modelDeleted.value = Resource.Error(message = e.message.toString())
        }
    }

    var currentNodes = mutableStateListOf<ModelNode>()

    fun loadModelFromPath(
        modelLoader: ModelLoader, modelPath: String, modelImageUrl: String, overwrite: Boolean
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {

                val result = modelLoader.loadModelInstance(
                    modelPath
                )
                if (result != null) {
                    if (overwrite) {
                        currentNodes.removeLast() // TODO is it fine
                        currentNodes += mutableStateListOf(
                            ModelNode(
                                modelInstance = result, scaleToUnits = 1.0f
                            )
                        )
                    } else {

                        val newNodePosition = Position(1f, 0f, 0f)
                        val newNode = ModelNode(
                            modelInstance = result,
                            centerOrigin = newNodePosition,
                            scaleToUnits = 1.0f
                        )
                        val newNodeCenter = newNode.center

                        val existingNode = currentNodes.first()
                        val existingNodeCenter = existingNode.center

                        val positionOffset = newNodeCenter - existingNodeCenter

                        currentNodes += ModelNode(
                            modelInstance = result, centerOrigin = Position(
                                newNodePosition.x,
                                newNodePosition.y + positionOffset.y,
                                newNodePosition.z + positionOffset.z
                            ), scaleToUnits = 1.0f
                        )
                    }

                    modelImgUrl.value = modelImageUrl
                } else {
                    throw IllegalArgumentException("Empty")
                }
            }

        } catch (e: Exception) {
            _loadedInstancesState.value = Resource.Error(e.message.toString())
        }
    }

    /*
        fun loadModelFromPath(
            modelLoader: ModelLoader, modelPath: String, modelImageUrl: String, overwrite: Boolean
        ) {

            viewModelScope.launch(Dispatchers.IO) {
                try {
                    val result = modelLoader.loadModelInstance(
                        modelPath
                    )
                    if (result != null) {
                        if (overwrite) {
                            currentNodes.removeLast()
                            currentNodes += mutableStateListOf(
                                ModelNode(
                                    modelInstance = result, scaleToUnits = 1.0f
                                )
                            )
                        } else {
                            currentNodes += ModelNode(
                                modelInstance = result,

                                centerOrigin = Position(1f, 0f, 0f), scaleToUnits = 1.0f
                            )
                        Log.d("AFTER LOAD", "F")

                       *//*        currentNodes += ModelNode(
                                   modelInstance = result,
                                   centerOrigin = Position(1f, 1f, 0f),
                                   scaleToUnits = 1.0f
                               )*//*

                        *//*              currentNodes.also { it.add( ModelNode(
                                              modelInstance = result,
                                              centerOrigin = Position(1f, 0f, 0f),
                                              scaleToUnits = 1.0f
                                          )) }*//*

                        //   currentNodes.removeLast() currentNodes.
                      *//*  currentNodes +=
                                mutableStateListOf(
                                    ModelNode(
                                        modelInstance = result, scaleToUnits = 1.0f
                                    )
                                )*//*


                        *//*  currentNodes.apply { add( ModelNode(
                                  modelInstance = result,
                                  centerOrigin = Position(2f, 0f, 0f),
                                  scaleToUnits = 2.0f
                              )) }*//*

                    }

                 //   modelImgUrl.value = modelImageUrl

                } else {
                    throw IllegalArgumentException("Empty")
                }
            } catch (e: Exception) {
                _loadedInstancesState.value = Resource.Error(e.message.toString())
            }
        }
    }
*/

    fun createAnchorNode(
        engine: Engine,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        modelInstances: ModelInstance,
    ): AnchorNode {
        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode = ModelNode(
            modelInstance = modelInstances, scaleToUnits = 0.5f
        ).apply {
            isEditable = true
        }

        val boundingBoxNode = CubeNode(
            engine,
            size = modelNode.extents,
            center = modelNode.center,
            materialInstance = materialLoader.createColorInstance(Color.White.copy(alpha = 0.5f))
        ).apply {
            isVisible = false
        }
        modelNode.addChildNode(boundingBoxNode)
        anchorNode.addChildNode(modelNode)

        listOf(modelNode, anchorNode).forEach {
            it.onEditingChanged = { editingTransforms ->
                boundingBoxNode.isVisible = editingTransforms.isNotEmpty()
            }
        }
        return anchorNode
    }

    fun onDownload(modelName: String?, modelPath: String?) {

        val result = if (modelPath == null) {
            downloader.downloadFile(
                modelFromRoom.value.data!!.modelPath,
                modelName = modelName ?: modelDescriptionShorten.value.toString()
            )
        } else {
            downloader.downloadFile(
                modelPath, modelName = modelName ?: modelDescriptionShorten.value.toString()
            )
        }

        if (result is Resource.Error) {
            downloadError.value = result.message
        }
    }

}