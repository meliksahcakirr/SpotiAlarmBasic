package com.meliksahcakir.spotialarm.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Rating(
    val uid: String = "",
    val rating: Int = -1,
    val timestamp: Timestamp = Timestamp.now()
)
