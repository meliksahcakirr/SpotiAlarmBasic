package com.meliksahcakir.spotialarm.music.data

import android.os.Parcelable

interface ITrackSource : Parcelable {
    fun getSourceId(): String
    fun getTitle(): String
    fun getSubTitle(): String?
    fun getImageUrl(): String
}
