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
    val links: TrackLinks,
    val previewURL: String,
    val isStreamable: Boolean
)

data class TrackLinks(
    val albums: TrackAlbumsLink
)

data class TrackAlbumsLink(val ids: List<String>, val href: String)
