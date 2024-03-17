package com.endofjanuary.placement_example.data.remote.meshy.request

data class PostFromText(val prompt : String,
                        val mode : String, // preview / refine
                        val artStyle : String = "realistic",
                        val negativePrompt : String = "low quality, low resolution,")