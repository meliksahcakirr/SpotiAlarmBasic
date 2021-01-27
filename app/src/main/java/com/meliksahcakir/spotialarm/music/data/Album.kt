package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class Albums(
    @SerializedName("albums")
    val list: List<Track>,
    @SerializedName("meta")
    val meta: Meta? = null
)

data class Album(
    val type: String,
    val id: String,
    val href: String,
    val name: String,
    val trackCount: Int,
    val artistName: String,
    val links: AlbumLinks
)

data class AlbumLinks(
    val images: AlbumImagesLink,
    val tracks: AlbumTracksLink,
)

data class AlbumImagesLink(val href: String)
data class AlbumTracksLink(val href: String)
// data class ArtistsLink(val ids: List<String>, val href: String)
// data class GenresLink(val ids: List<String>, val href: String)
