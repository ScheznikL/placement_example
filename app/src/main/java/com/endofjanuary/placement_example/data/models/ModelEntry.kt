package com.endofjanuary.placement_example.data.models

data class ModelEntry(
    val id: Int,
    val modelPath: String,
    val modelImageUrl: String,
    val modelDescription: String,
    val meshyId: String,
    val progress: Int = 0,
    val isFromText: Boolean = true,
    val modelMode: ModelMode = ModelMode.Preview,
    val isExpired: Boolean
) {
    constructor() : this(
        0,
        "https://assets.meshy.ai/google-oauth2%7C107069207183755263308/tasks/018fc0ea-b457-7d71-bb00-dfe748ccd18c/output/model.glb?Expires=4870454400&Signature=LDKj3Yx3KdlALrUgafBt~vH5HXt5cXCMpxaw78yIhfKG21nae0JWaabhgnaSuafrADX9YJ6whAaSKrXS5rl7oj-qprXLCKvIqjkwdGCxha7DRa69HI5hLRyqM-TPPupzo0wHHlfbOg9pcFIf-mo7BL2Htvl3AvKe1oxYC3yFuw4VtPoDQPt7HaoGL~M6zbw4ggUhJns8cEKxljIct7Fw3oh8sGeDVhbu2sFOyPNX0nA5Ot7aMzkCeMyzlTJqrqImAPmxGUd9TVZWRGuAjxVo3E5RZIS5DP0tKMOqyMWU3tgEXa9orEyNFg0Ug7QfcCoqeLlOYKcrFsvzaBUxTQ4VdQ__&Key-Pair-Id=KL5I0C8H7HX83",
        "https://www.google.com/url?sa=i&url=https%3A%2F%2Fuk.wikipedia.org%2Fwiki%2F%25D0%25A4%25D0%25B0%25D0%25B9%25D0%25BB%3AEmpty_set.svg&psig=AOvVaw10i9B2Bx2W-HpRdiKwyBgz&ust=1711958368644000&source=images&cd=vfe&opi=89978449&ved=0CBIQjRxqFwoTCNiUt4aEnoUDFQAAAAAdAAAAABAE",
        "temp model",
        "1111",
        isExpired = false
    )
}

enum class ModelMode {
    Preview,
    Refine
}