package com.endofjanuary.placement_example.repo

import com.endofjanuary.placement_example.utils.Resource

interface FireStoreDBRepo {
    abstract suspend fun updateUserProfileData(
        userName: String,
        refine: Boolean?,
        save: Boolean?,
        userAuthID: String
    ): Resource<String>
}