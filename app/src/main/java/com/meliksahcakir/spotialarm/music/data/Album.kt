package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Albums(
    @SerializedName("albums")
    val list: List<Album>,
    @SerializedName("meta")
    val meta: Meta? = null
)

data class Album(
    val type: String,
    val id: String,
    val href: String,
    val name: String,
    val trackCount: Int,
    val artistName: String
)
