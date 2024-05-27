package com.endofjanuary.placement_example.data.models

data class User(
    val email: String,
    val displayName: String?,
    val profilePictureUrl: String?,
//    val phoneNumber: String?,
    val isEmailVerified: Boolean

){
    constructor() : this(
        "email@gmail.com",
        "User Name",
        "https://images.unsplash.com/photo-1538991383142-36c4edeaffde?q=80&w=1771&auto=format&fit=crop&ixlib=rb-4.0.3&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
        false,
    )
}