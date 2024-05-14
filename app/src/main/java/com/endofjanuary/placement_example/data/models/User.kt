package com.endofjanuary.placement_example.data.models

data class User(
    val email: String,
    val displayName: String?,
    val profileUrl: String?,
    val phoneNumber: String?,
    val isEmailVerified: Boolean
)