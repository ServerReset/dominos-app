package com.dominos.app.data.api

import com.dominos.app.data.model.StoreInfo

data class StoreDetailResponse(
    val store: StoreInfo? = null,
    val message: String? = null
)
