package com.meliksahcakir.spotialarm.music.data

import androidx.annotation.Keep
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.meliksahcakir.spotialarm.music.api.AlbumImageSize
import com.meliksahcakir.spotialarm.music.api.NapsterService
import kotlinx.android.parcel.Parcelize

@Keep
data class Tracks(
    @SerializedName("tracks")
    var list: List<Track>,
    @SerializedName("meta")
    val meta: Meta? = null
)

@Keep
@Parcelize
@Entity(tableName = "tracks")
data class Track(
    val type: String,
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val href: String,
    val playbackSeconds: Int,
    val name: String,
    val artistName: String,
    val artistId: String,
    val previewURL: String,
    val albumId: String,
    @SerializedName("isStreamable")
    val streamable: Boolean,
    var favorite: Boolean = false
) : ITrackSource {
    @Ignore
    var isPlaying = false

    override fun getSourceId() = id

    override fun getTitle() = name

    override fun getSubTitle() = artistName

    override fun getImageUrl() =
        NapsterService.createAlbumImageUrl(albumId, AlbumImageSize.SIZE_500X500)
}
