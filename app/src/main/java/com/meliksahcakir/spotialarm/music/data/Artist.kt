package com.meliksahcakir.spotialarm.music.data

import com.google.gson.annotations.SerializedName
import com.meliksahcakir.spotialarm.music.api.ArtistImageSize
import com.meliksahcakir.spotialarm.music.api.NapsterService
import kotlinx.android.parcel.Parcelize

data class Artists(
    @SerializedName("artists")
    val list: List<Artist>,
    @SerializedName("meta")
    val meta: Meta? = null
)

@Parcelize
data class Artist(
    val type: String,
    val id: String,
    val href: String,
    val name: String
) : ITrackSource {

    override fun getSourceId() = id

    override fun getTitle() = name

    override fun getSubTitle(): String? = null

    override fun getImageUrl() =
        NapsterService.createArtistImageUrl(id, ArtistImageSize.SIZE_633X422)
}
