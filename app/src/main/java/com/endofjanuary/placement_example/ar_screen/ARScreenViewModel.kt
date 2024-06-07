package com.endofjanuary.placement_example.ar_screen

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.endofjanuary.placement_example.data.models.ModelEntry
import com.endofjanuary.placement_example.repo.MeshyRepo
import com.endofjanuary.placement_example.utils.Resource
import io.github.sceneview.model.ModelInstance

class ARScreenViewModel(
    private val meshyRepository: MeshyRepo,
) : ViewModel()
{

    var model = mutableStateOf(ModelEntry())

    private val _loadedInstancesState: MutableState<Resource<List<ModelInstance>>> =
        mutableStateOf(Resource.None())
}