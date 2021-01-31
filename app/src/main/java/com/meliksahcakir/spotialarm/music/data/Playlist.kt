package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Playlists(
    @SerializedName("playlists")
    val list: List<Playlist>
)

data class Playlist(
    val type: String,
    val id: String,
    val name: String,
    val href: String,
    val trackCount: Int
)
