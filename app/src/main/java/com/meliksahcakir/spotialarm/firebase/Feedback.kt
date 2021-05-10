package com.meliksahcakir.spotialarm.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp

@Keep
data class Feedback(
    val uid: String = "",
    val name: String = "",
    val email: String = "",
    val message: String = "",
    val timestamp: Timestamp = Timestamp.now()
)

fun Timestamp.diffInSeconds(timestamp: Timestamp) = seconds - timestamp.seconds
