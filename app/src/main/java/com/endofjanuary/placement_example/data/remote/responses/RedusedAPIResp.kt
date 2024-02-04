package com.endofjanuary.placement_example.data.remote.responses

data class RedusedAPIResp(
    val modelURLs : Map<String, String>,
    val thumbnailURL : String,
    val prompt : String,
    val status : String
)