package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("albums")
    val search: Search,
    @SerializedName("meta")
    val meta: Meta
)

data class Search(
    val query: String,
    val data: SearchData
)

data class SearchData(
    val tracks: Tracks,
    val artists: Artists,
    val playlists: Playlists
)
