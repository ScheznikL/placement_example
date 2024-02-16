package com.endofjanuary.placement_example.ar_screen

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import com.endofjanuary.placement_example.MainActivity
import com.endofjanuary.placement_example.data.converters.ResponseToModelEntryConverter
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.data.remote.request.Post
import com.endofjanuary.placement_example.data.remote.responses.PostId
import com.endofjanuary.placement_example.data.remote.responses.TextTo3DModel
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource
import com.google.android.filament.Engine
import com.google.ar.core.Anchor
import io.github.sceneview.ar.node.AnchorNode
import io.github.sceneview.loaders.MaterialLoader
import io.github.sceneview.loaders.ModelLoader
import io.github.sceneview.model.ModelInstance
import io.github.sceneview.node.CubeNode
import io.github.sceneview.node.ModelNode
import kotlinx.coroutines.delay

class ARScreenViewModel(
    // private val prompt: String,
    private val meshyRepository: MeshyRepo,
    //private val localStoreRepository: LocalStorageRepo,
) : ViewModel()
{


    var model = mutableStateOf(ModelEntry())
    var postId = mutableStateOf(PostId(""))

    //var loadedInstances : MutableList<ModelInstance>? = mutableListOf<ModelInstance>()
//    var loadedInstances: MutableList<ModelInstance>? = null

    private val _loadedInstancesState: MutableState<Resource<List<ModelInstance>>> =
        mutableStateOf(Resource.None())
    val loadedInstancesState: State<Resource<List<ModelInstance>>>
        get() = _loadedInstancesState

    /* var loadError = mutableStateOf("")
     var isLoading = mutableStateOf(false)
     val isSuccess = mutableStateOf(false)*/

    /*  init {
          loadModelEntry()
      }
  */
    suspend fun loadModelEntry(prompt: String): Resource<ModelEntry> {
        // viewModelScope.launch {
        //isLoading.value = true
        Log.d("loadModel", "Enter point")
        val result = meshyRepository.postTextTo3D(Post(prompt, "preview"))
        when (result) {
            is Resource.Success -> {
                /* loadError.value = ""
                 isLoading.value = false*/
                postId.value = result.data ?: PostId("")
                if (result.data != null) {
                    Log.d("loadModelEntry_result Success id:", result.data.result)
                    var eventualApiRes = getTextTo3D(result.data.result)
                    when (eventualApiRes) {
                        is Resource.Error -> {
                            Log.d("eventualApiRes Error", eventualApiRes.toString())
                            /* loadError.value = eventualApiRes.message!!
                             isLoading.value = false*/
                            return Resource.Error(eventualApiRes.message!!)
                        }

                        is Resource.Success -> {
                            Log.d(
                                "eventualApiRes Success id:",
                                eventualApiRes.data?.id ?: "none"
                            )
                            while (eventualApiRes.data!!.status == "PENDING" || eventualApiRes.data!!.status == "IN_PROGRESS") {
                                Log.d(
                                    "loadModel while",
                                    "while entered with status ${eventualApiRes.data!!.status}"
                                )
                                delay(30000)
                                eventualApiRes = getTextTo3D(result.data.result)
                                // return Resource.Loading()
                            }
                            if (eventualApiRes.data!!.status == "SUCCEEDED") {
                                /* loadError.value = ""
                                 isLoading.value = false*/
                                model.value =
                                    ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                return Resource.Success(
                                    ResponseToModelEntryConverter().toModelEntry(
                                        eventualApiRes.data
                                    )
                                )
                            }
                            if (eventualApiRes.data!!.status == "FAILED" || eventualApiRes.data!!.status == "EXPIRED") {
                                /* loadError.value = eventualApiRes.data!!.status
                                 isLoading.value = false*/
                                model.value =
                                    ResponseToModelEntryConverter().toModelEntry(eventualApiRes.data)
                                return Resource.Error(eventualApiRes.data!!.status)
                            }
                        }

                        else -> {
                            return Resource.Loading()
                        }
                    }
                }
            }

            is Resource.Error -> {
                /* loadError.value = result.message!!
                 isLoading.value = false*/
                return Resource.Error(result.message!!)

            }

            is Resource.Loading -> {
                return Resource.Loading()
            }

            else -> {}
        }        //}
        return Resource.Loading()
    }

    suspend fun getTextTo3D(id: String): Resource<TextTo3DModel> {
        return meshyRepository.getTextTo3D(id)
    }

    suspend fun loadGlbModel(
        modelLoader: ModelLoader,
        modelPath: String
    ) {
        try {
            val result = modelLoader.loadInstancedModel(
                modelPath,
                MainActivity.kMaxModelInstances
            )
            _loadedInstancesState.value = Resource.Success(result)

        } catch (e: Exception) {
            _loadedInstancesState.value =
            Resource.Error(e.message.toString())
        }
    }

    fun createAnchorNode(
        engine: Engine,
        modelLoader: ModelLoader,
        materialLoader: MaterialLoader,
        anchor: Anchor,
        modelPath: String,
       // modelInstances: MutableList<ModelInstance>,
    ): AnchorNode {

        Log.d("createAnchorNode", modelPath) // todo

        val anchorNode = AnchorNode(engine = engine, anchor = anchor)
        val modelNode = ModelNode(
            modelInstance = _loadedInstancesState.value.data!![0],
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