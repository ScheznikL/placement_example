package com.endofjanuary.placement_example.data.models

data class ModelEntry(
    val id: Int,
    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
    val meshyId: String,
    val progress: Int = 0,
    val isFromText: Boolean = true,
    val modelMode: ModelMode = ModelMode.Preview
) {
    constructor() : this(
        0,
        "models/model_v2_chair.glb",
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fuk.wikipedia.org%2Fwiki%2F%25D0%25A4%25D0%25B0%25D0%25B9%25D0%25BB%3AEmpty_set.svg&psig=AOvVaw10i9B2Bx2W-HpRdiKwyBgz&ust=1711958368644000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCNiUt4aEnoUDFQAAAAAdAAAAABAE",
        "",
        ""
    )
}

enum class ModelMode {
    Preview,
    Refine
}