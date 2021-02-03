package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName
import com.meliksahcakir.spotialarm.music.api.AlbumImageSize
import com.meliksahcakir.spotialarm.music.api.NapsterService
import kotlinx.android.parcel.Parcelize

data class Albums(
    @SerializedName("albums")
    val list: List<Album>,
    @SerializedName("meta")
    val meta: Meta? = null
)

@Parcelize
data class Album(
    val type: String,
    val id: String,
    val href: String,
    val name: String,
    val trackCount: Int,
    val artistName: String
) : ITrackSource {

    override fun getSourceId() = id

    override fun getTitle() = name

    override fun getSubTitle() = artistName

    override fun getImageUrl() = NapsterService.createAlbumImageUrl(id, AlbumImageSize.SIZE_500X500)
}
