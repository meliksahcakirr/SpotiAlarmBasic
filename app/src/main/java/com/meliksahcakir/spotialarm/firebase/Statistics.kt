package com.meliksahcakir.spotialarm.firebase

import androidx.annotation.Keep
import com.google.firebase.Timestamp
import com.meliksahcakir.spotialarm.preferences.Preferences

@Keep
data class Statistics(
    var lastActiveAt: Timestamp = Timestamp.now(),
    var numberOfAlarmWentOff: Int = 0,
    var favoriteTracks: MutableList<String> = mutableListOf(),
    var alarmTracks: MutableList<String> = mutableListOf(),
    var readAlarmTimeLoud: Boolean = false,
    var faceDownToSnooze: Boolean = false,
    var slideToTurnOff: Boolean = true,
    var snoozeDuration: Int = 0,
    var listenOffline: Boolean = false,
    var fadeInDuration: Int = 0,
    var useDeviceAlarmVolume: Boolean = true,
    var customVolume: Int = 0
) {
    fun updateWithPreferences() {
        readAlarmTimeLoud = Preferences.readAlarmTimeLoud
        faceDownToSnooze = Preferences.faceDownToSnooze
        slideToTurnOff = Preferences.slideToTurnOff
        snoozeDuration = Preferences.snoozeDuration
        listenOffline = Preferences.listenOffline
        fadeInDuration = Preferences.fadeInDuration
        useDeviceAlarmVolume = Preferences.useDeviceAlarmVolume
        customVolume = Preferences.customVolume
    }
}
