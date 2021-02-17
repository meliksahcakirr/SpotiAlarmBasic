package com.meliksahcakir.spotialarm.music.data

import androidx.annotation.Keep

@Keep
data class Meta(
    val totalCount: Int,
    val returnedCount: Int,
    val limit: Int = 0,
    val offset: Int = 0
)
