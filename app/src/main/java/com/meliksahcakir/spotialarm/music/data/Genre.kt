package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Genres(
    @SerializedName("genres")
    val list: List<Genre>
)

data class Genre(
    val type: String,
    val id: String,
    val href: String,
    val name: String
)
