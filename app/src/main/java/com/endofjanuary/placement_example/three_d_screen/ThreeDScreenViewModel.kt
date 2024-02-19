package com.endofjanuary.placement_example.three_d_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.android.filament.Engine
import com.google.android.filament.EntityManager
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.UbershaderProvider
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.ByteBuffer


class ThreeDScreenViewModel(
    //private val meshyRepository: MeshyRepo,
    private val modelRoom: ModelsRepo,

    ) : ViewModel() {

    private val _loadedInstancesState: MutableState<Resource<ModelInstance>> =
        mutableStateOf(Resource.None())
    val loadedInstancesState: State<Resource<ModelInstance>>
        get() = _loadedInstancesState

    var model = mutableStateOf(ModelEntry())
//       private val _loadedInstancesBufferState: MutableState<Resource<ModelInstance>> =
//        mutableStateOf(Resource.None())
//    val loadedInstancesBufferState: State<Resource<ModelInstance>>
//        get() = _loadedInstancesBufferState


    fun loadModelLocalOld() {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val result = modelRoom.getAllModels()
                if (result.data != null) {
//                    _loadedInstancesState.value =
//                        Resource.Success(result.data.first().modelInstance)
                } else {
                    _loadedInstancesState.value = Resource.Error("Model data is empty")
                }
            }
        } catch (e: Exception) {
            _loadedInstancesState.value =
                Resource.Error(e.message.toString())
        }
    }

    suspend fun loadModelRemote(
        modelLoader: ModelLoader,
        //modelPath: String,
        localId :Int
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO){
                val model = modelRoom.getModelById(localId)
                when (model) {
                    is Resource.Success -> {
                        val result = modelLoader.loadModelInstance(
                            model.data!!.modelPath
                        )
                        if (result != null) {
                            _loadedInstancesState.value = Resource.Success(result)
                        } else {
                            throw IllegalArgumentException("Empty")
                        }
                    }

                    is Resource.Error -> throw IllegalArgumentException(model.message)
                    else -> {} //TODO something
                }
            }
        } catch (e: Exception) {
            _loadedInstancesState.value =
                Resource.Error(e.message.toString())
        }
    }

    fun loadModelLocal(
        modelLoader: ModelLoader,
        engine: Engine,
        resourceResolver: (resourceFileName: String) -> String = {
            ModelLoader.getFolderPath(
                model.value.modelPath,
                it
            )
        }
    ) {
        val materialProvider = UbershaderProvider(engine)
        val assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())

        try {
            viewModelScope.launch(Dispatchers.IO) {
                val result = modelRoom.getAllModels()
                if (result.data != null) {
                    val resBuf = ByteBuffer.wrap(result.data[1].modelInstance)
                    val rewound = resBuf.rewind()
                    val asset = assetLoader.createAsset(resBuf)?.instance //TODO
                    // val instance = assetLoader.createInstance(asset!!)

//                    val instance = asset.also { model ->
//                        models += model
//                        loadResources(model, resourceResolver)
//                    }?.instance

                    try {
                        //  val inst = modelLoader.createModel(buffer = rewound).instance
                        _loadedInstancesState.value = Resource.Success(
                            //  instance!!
                            //inst
                            asset!!
                        )
                    } catch (e: Exception) {
                        _loadedInstancesState.value = Resource.Error(
//                            "Empty instance"
                            e.message.toString()
                        )
                    }
//                    resBuf.let { buffer ->
//                            arrayOfNulls<ModelInstance>(1).apply {
//                                assetLoader.createInstancedAsset(buffer, this)!!.also { model ->
//                                    models += model
//                                    loadResourcesSuspended(model) { resourceFileName: String ->
//                                        context.loadFileBuffer(resourceResolver(resourceFileName))
//                                    }
//                                    // Release model since it will not be re-instantiated
////                model.releaseSourceData()
//                                }
//                            }.filterNotNull()
//                        }
//                    _loadedInstancesState.value = Resource.Success(
//                        modelLoader.createInstancedModel(
//                            buffer = resBuf,
//                            count = 1
//                        ).first()
//                    )
//
//                    _loadedInstancesState.value =
//                        Resource.Success(result.data.first().modelInstance)
                } else {
                    _loadedInstancesState.value = Resource.Error("Model data is empty")
                }
            }
        } catch (e: Exception) {
            _loadedInstancesState.value =
                Resource.Error(e.message.toString())
        }
    }
}