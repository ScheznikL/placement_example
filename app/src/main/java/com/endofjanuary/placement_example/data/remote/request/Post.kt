package com.endofjanuary.placement_example.data.remote.request

data class Post(val prompt : String,
                val mode : String, // preview / refine
                val artStyle : String = "realistic",
                val negativePrompt : String = "low quality, low resolution,")