package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Tracks(
    @SerializedName("tracks")
    val list: List<Track>,
    @SerializedName("meta")
    val meta: Meta? = null
)

data class Track(
    val type: String,
    val id: String,
    val href: String,
    val playbackSeconds: Int,
    val name: String,
    val artistName: String,
    val artistId: String,
    val previewURL: String,
    val albumId: String,
    val isStreamable: Boolean
)
