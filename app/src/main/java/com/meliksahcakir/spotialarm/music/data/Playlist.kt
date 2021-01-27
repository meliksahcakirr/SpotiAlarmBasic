package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Playlists(
    @SerializedName("playlists")
    val list: List<Track>? = null
)

data class Playlist(
    val type: String,
    val id: String,
    val name: String,
    val href: String,
    val trackCount: Int,
    val images: List<PlaylistImage>,
    val links: PlaylistLinks
)

data class PlaylistImage(
    val imageId: String,
    val url: String
)

data class PlaylistLinks(
    val tracks: PlaylistTracksLink
)

data class PlaylistTracksLink(val href: String)
