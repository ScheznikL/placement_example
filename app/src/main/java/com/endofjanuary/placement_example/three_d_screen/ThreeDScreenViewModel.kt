package com.endofjanuary.placement_example.three_d_screen

import android.content.res.AssetManager
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.repo.ModelsRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.android.filament.Engine
import com.google.android.filament.gltfio.AssetLoader
import com.google.android.filament.gltfio.ResourceLoader
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.Model
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.model.model
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.nio.Buffer
import java.nio.ByteBuffer


class ThreeDScreenViewModel(
    //private val meshyRepository: MeshyRepo,
    private val modelRoom: ModelsRepo,
) : ViewModel() {

    private val _loadedInstancesState: MutableState<Resource<ModelInstance>> =
        mutableStateOf(Resource.None())
    val loadedInstancesState: State<Resource<ModelInstance>>
        get() = _loadedInstancesState

    var modelImgUrl = mutableStateOf("")
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
        localId: Int
    ) {
        try {
            viewModelScope.launch(Dispatchers.IO) {
                val model = modelRoom.getModelById(localId)
                when (model) {
                    is Resource.Success -> {
                        modelImgUrl.value = model.data!!.modelImageUrl
                        val result1 = modelLoader.loadModel(
                            model.data.modelPath
                        )
                        val result = modelLoader.loadModelInstance(
                            model.data.modelPath
                        )
                        //val temp = result1?.resourceUris
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

    private val models = mutableListOf<Model>()

    fun loadModelLocal(
        modelLoader: ModelLoader,
        //engine: Engine,
        assetLoader: AssetLoader,
        assetManager: AssetManager,
        resourceLoader: ResourceLoader,
//        resourceResolver: (resourceFileName: String) -> String = {
//            ModelLoader.getFolderPath(
//                model.value.modelPath,
//                it
//            )
//        }
        resourceResolver: (resourceFileName: String) -> Buffer? = { null }
    ) {
        //  val materialProvider = UbershaderProvider(engine)

        //val assetLoader = AssetLoader(engine, materialProvider, EntityManager.get())

        try {
            viewModelScope.launch(Dispatchers.IO) {
                val result = modelRoom.getAllModels()
                if (result.data != null) {

                    val asset = result.data.last().modelInstance.let { buffer ->
                        assetLoader.createAsset(ByteBuffer.wrap(buffer)).also { model ->
                            models += model!!
                            loadResources(
                                model = model as Model,
                                resourceResolver = { ByteBuffer.wrap(buffer) },
                                resourceLoader = resourceLoader,
                                buf = ByteBuffer.wrap(buffer)
                            )
                        }
                    }

                    //  val mod = assetLoader.createInstance(asset!!)
                    val mod = asset!!.instance

//                    val resBuf = ByteBuffer.wrap(result.data[1].modelInstance)
//                    val rewound = resBuf.rewind()
//                    val asset = assetLoader.createAsset(resBuf)?.instance //TODO
                    // val instance = assetLoader.createInstance(asset!!)

//                    val instance = asset.also { model ->
//                        models += model
//                        loadResources(model, resourceResolver)
//                    }?.instance
                    val temp = mod!!.model.engine.nativeObject
                    try {
                        //  val inst = modelLoader.createModel(buffer = rewound).instance
                        _loadedInstancesState.value = Resource.Success(
                            //  instance!!
                            //inst
                            mod!!
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

    fun loadInstanceNone(){
        _loadedInstancesState.value = Resource.None()
    }
    private fun loadResources(
        model: Model,
        resourceLoader: ResourceLoader,
        resourceResolver: (String) -> Buffer?,
        buf: Buffer?
    ) {
        for (uri in model.resourceUris) {
            buf?.let {
                viewModelScope.launch(Dispatchers.Main) {
                    resourceLoader.addResourceData(uri, it)
                }
            }
        }
//        for (uri in model.resourceUris) {
//            resourceResolver(uri)?.let { resourceLoader.addResourceData(uri, it) }
//        }
        //resourceLoader.loadResources(model)
        viewModelScope.launch(Dispatchers.Main) { resourceLoader.loadResources(model) }
//        resourceLoader.asyncBeginLoad(model)
//        resourceLoader.evictResourceData()
    }

    //    private fun loadResource(uri: String, assetManager: AssetManager): Buffer {
//        //TODO("Load your asset here (e.g. using Android's AssetManager API)")
//        val buffer: InputStream?
//        return try {
//            buffer = assetManager.open(uri)
//            val bytes = ByteArray(buffer.available())
//            buffer.read(bytes)
//            val byteB = ByteBuffer.wrap(bytes)
//            byteB
//        } catch (e: IOException) {
//            e.printStackTrace()
//            throw e
//        }
//    }
    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        modelInstances: ModelInstance,
    ): AnchorNode {

//    Log.d("createAnchorNode", modelPath) // todo

        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode = ModelNode(
            modelInstance = modelInstances,
            // Scale to fit in a 0.5 meters cube
            scaleToUnits = 0.5f
        ).apply {
            // Model Node needs to be editable for independent rotation from the anchor rotation
            isEditable = true
            //todo true init
        }
        /*
                modelInstance = modelInstances.apply {
                    //if (isEmpty()) {
                    if (isNotEmpty()) {
                        this.removeAt(0)
                    }
                    //this += loadedInstances!!

                    this += _loadedInstancesState.value.data!!
                    Log.d("createAnchorNode _loadedInstancesState added", _loadedInstancesState.value.data.toString())
                    /* this += modelLoader.createInstancedModel(
                         "models/model_v2_chair.glb",
                         MainActivity.kMaxModelInstances
                     )*/
                    // var i = loadedInstances!!

                    Log.d("createAnchorNode createInstancedModel", this[0].toString())
                    // MainActivity.Companion.kMaxModelInstances
        //                this += modelLoader.createInstancedModel(kModelFile, kMaxModelInstances)
                    //  }
                }.removeLast(),
                //modelInstance = modelLoader.createInstancedModel(modelFile, 1)[0],
                // Scale to fit in a 0.5 meters cube
                scaleToUnits = 0.5f
                ).apply {
                    // Model Node needs to be editable for independent rotation from the anchor rotation
                    isEditable = false
                    //todo true init
                }
        */

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
}

