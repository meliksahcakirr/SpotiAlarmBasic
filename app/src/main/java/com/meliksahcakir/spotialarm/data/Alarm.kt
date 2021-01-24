package com.meliksahcakir.spotialarm.data

import android.os.Bundle
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit

@Entity(tableName = "alarms")
data class Alarm(
    @ColumnInfo(name = "hour")
    var hour: Int = 0,
    @ColumnInfo(name = "minute")
    var minute: Int = 0,
    @ColumnInfo(name = "enabled")
    var enabled: Boolean = false,
    @ColumnInfo(name = "days")
    var days: Int = 0,
    @ColumnInfo(name = "vibrate")
    var vibrate: Boolean = false,
    @ColumnInfo(name = "snooze")
    var snooze: Boolean = true,
    @ColumnInfo(name = "description")
    var description: String = "",
    @ColumnInfo(name = "musicId")
    var musicId: String = "",
    @ColumnInfo(name = "imageId")
    var imageId: String = "",
    @PrimaryKey
    @ColumnInfo(name = "alarmId")
    val alarmId: Int = (TimeUnit.MILLISECONDS.toSeconds(System.currentTimeMillis())).toInt()
) {

    companion object {
        const val ONCE = 0x00
        const val MONDAY = 0x01
        const val TUESDAY = 0x02
        const val WEDNESDAY = 0x04
        const val THURSDAY = 0x08
        const val FRIDAY = 0x10
        const val SATURDAY = 0x20
        const val SUNDAY = 0x40
        const val WEEKDAYS = 0x1F
        const val WEEKEND = 0x60
        const val ALL_DAYS = 0x7F

        private const val EXTRA_HOUR = "HOUR"
        private const val EXTRA_MINUTE = "MINUTE"
        private const val EXTRA_ENABLED = "ENABLED"
        private const val EXTRA_DAYS = "DAYS"
        private const val EXTRA_VIBRATE = "VIBRATE"
        private const val EXTRA_SNOOZE = "SNOOZE"
        private const val EXTRA_DESCRIPTION = "DESCRIPTION"
        private const val EXTRA_MUSIC_ID = "MUSIC_ID"
        private const val EXTRA_IMAGE_ID = "IMAGE_ID"
        private const val EXTRA_ALARM_ID = "ALARM_ID"

        fun defaultAlarm(): Alarm {
            val now = LocalTime.now()
            return Alarm(now.hour, now.minute, false, ONCE, vibrate = false, snooze = true)
        }

        fun fromBundle(bundle: Bundle?): Alarm? {
            if (bundle == null) return null
            return Alarm(
                hour = bundle.getInt(EXTRA_HOUR, 0),
                minute = bundle.getInt(EXTRA_MINUTE, 0),
                enabled = bundle.getBoolean(EXTRA_ENABLED, false),
                days = bundle.getInt(EXTRA_DAYS, 0),
                vibrate = bundle.getBoolean(EXTRA_VIBRATE, false),
                snooze = bundle.getBoolean(EXTRA_SNOOZE, true),
                description = bundle.getString(EXTRA_DESCRIPTION, ""),
                musicId = bundle.getString(EXTRA_MUSIC_ID, ""),
                imageId = bundle.getString(EXTRA_IMAGE_ID, ""),
                alarmId = bundle.getInt(EXTRA_ALARM_ID, 0),
            )
        }
    }

    val alarmTime: LocalTime get() = LocalTime.of(hour, minute)

    fun nearestDateTime(localDateTime: LocalDateTime? = null): LocalDateTime? {
        if (!enabled) return null
        val time = localDateTime ?: LocalDateTime.now()
        var alarmDate = time.withHour(hour).withMinute(minute).withSecond(0)
        if (days == 0) {
            if (alarmDate.isBefore(time)) {
                alarmDate = alarmDate.plusDays(1)
            }
            return alarmDate
        }
        var nearestDate: LocalDateTime? = null
        var minTimeDiff = Long.MAX_VALUE
        for (i in DayOfWeek.values().indices) {
            val day = alarmDate.dayOfWeek
            if (days and (1 shl day.ordinal) > 0) {
                val pair = getNormalizedDateAndTimeDiffPair(time, alarmDate)
                if (pair.second < minTimeDiff) {
                    minTimeDiff = pair.second
                    nearestDate = pair.first
                }
            }
            alarmDate = alarmDate.plusDays(1)
        }
        return nearestDate
    }

    private fun getNormalizedDateAndTimeDiffPair(
        first: LocalDateTime,
        second: LocalDateTime
    ): Pair<LocalDateTime, Long> {
        var temp = LocalDateTime.from(second)
        if (temp.isBefore(first)) {
            temp = temp.plusDays(DayOfWeek.values().size.toLong())
        }
        val timeDiff = first.until(temp, ChronoUnit.MINUTES)
        return Pair(temp, timeDiff)
    }

    fun toBundle(): Bundle {
        val bundle = Bundle()
        bundle.putInt(EXTRA_HOUR, hour)
        bundle.putInt(EXTRA_MINUTE, minute)
        bundle.putBoolean(EXTRA_ENABLED, enabled)
        bundle.putInt(EXTRA_DAYS, days)
        bundle.putBoolean(EXTRA_VIBRATE, vibrate)
        bundle.putBoolean(EXTRA_SNOOZE, snooze)
        bundle.putString(EXTRA_DESCRIPTION, description)
        bundle.putString(EXTRA_MUSIC_ID, musicId)
        bundle.putString(EXTRA_IMAGE_ID, imageId)
        bundle.putInt(EXTRA_ALARM_ID, alarmId)
        return bundle
    }
}
