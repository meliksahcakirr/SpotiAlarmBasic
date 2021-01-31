package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Artists(
    @SerializedName("artists")
    val list: List<Artist>,
    @SerializedName("meta")
    val meta: Meta? = null
)

data class Artist(
    val type: String,
    val id: String,
    val href: String,
    val name: String
)
