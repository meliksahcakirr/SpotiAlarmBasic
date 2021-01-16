package com.meliksahcakir.spotialarm.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

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
    var imageId: String = ""
) {
    @PrimaryKey(autoGenerate = false)
    @ColumnInfo(name = "alarmId")
    var alarmId: String = java.util.UUID.randomUUID().toString()

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

        fun defaultAlarm(): Alarm {
            val now = LocalTime.now()
            return Alarm(now.hour, now.minute, false, ONCE, vibrate = false, snooze = true)
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
}
