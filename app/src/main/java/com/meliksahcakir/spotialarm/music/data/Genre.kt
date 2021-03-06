package com.meliksahcakir.spotialarm.music.data

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName
import com.meliksahcakir.spotialarm.music.api.GenreImageSize
import com.meliksahcakir.spotialarm.music.api.NapsterService
import kotlinx.android.parcel.Parcelize

@Keep
data class Genres(
    @SerializedName("genres")
    val list: List<Genre>
)

@Keep
@Parcelize
data class Genre(
    val type: String,
    val id: String,
    val href: String,
    val name: String
) : ITrackSource {

    override fun getSourceId() = id

    override fun getTitle() = name

    override fun getSubTitle(): String? = null

    override fun getImageUrl() = NapsterService.createGenreImageUrl(id, GenreImageSize.SIZE_240X160)
}
