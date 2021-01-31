package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName

data class SearchResult(
    @SerializedName("search")
    val search: Search,
    @SerializedName("meta")
    val meta: Meta
) {
    fun isEmpty(): Boolean {
        return when {
            !search.isTracksEmpty() -> false
            !search.isArtistsEmpty() -> false
            !search.isAlbumsEmpty() -> false
            !search.isPlaylistsEmpty() -> false
            else -> true
        }
    }
}

data class Search(
    val query: String,
    val data: SearchData
) {
    fun isTracksEmpty() = data.tracks.isEmpty()
    fun isArtistsEmpty() = data.artists.isEmpty()
    fun isPlaylistsEmpty() = data.playlists.isEmpty()
    fun isAlbumsEmpty() = data.albums.isEmpty()
}

data class SearchData(
    val tracks: List<Track>,
    val artists: List<Artist>,
    val playlists: List<Playlist>,
    val albums: List<Album>
)
