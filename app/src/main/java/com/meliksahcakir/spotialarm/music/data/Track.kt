package com.meliksahcakir.spotialarm.music.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

data class Tracks(
    @SerializedName("tracks")
    var list: List<Track>,
    @SerializedName("meta")
    val meta: Meta? = null
)

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
)
