package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.PlaylistImageSize
import kotlinx.android.parcel.Parcelize

data class Playlists(
    @SerializedName("playlists")
    val list: List<Playlist>
)

@Parcelize
data class Playlist(
    val type: String,
    val id: String,
    val name: String,
    val href: String,
    val trackCount: Int
) : ITrackSource {

    override fun getSourceId() = id

    override fun getTitle() = name

    override fun getSubTitle(): String? = null

    override fun getImageUrl() =
        NapsterService.createPlaylistImageUrl(id, PlaylistImageSize.SIZE_1200X400)
}
