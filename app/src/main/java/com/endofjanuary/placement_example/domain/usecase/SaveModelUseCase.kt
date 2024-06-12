package com.endofjanuary.placement_example.domain.usecase

import com.endofjanuary.placement_example.domain.repo.DataStoreRepo
import com.endofjanuary.placement_example.domain.repo.ModelsRepo

class SaveModelUseCase(
    private val modelRoom: ModelsRepo,
    private val dataStoreRepo: DataStoreRepo,
) {
}