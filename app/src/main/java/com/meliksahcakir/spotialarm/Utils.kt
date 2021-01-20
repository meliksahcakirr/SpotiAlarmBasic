package com.meliksahcakir.spotialarm

import android.content.Context
import java.time.Duration
import java.time.LocalDateTime

fun calculateDurationString(
    context: Context,
    alarm: LocalDateTime,
    now: LocalDateTime = LocalDateTime.now()
): String {
    val resources = context.resources
    var duration = Duration.between(now, alarm)
    val days = duration.toDays().toInt()
    duration = duration.minusDays(days.toLong())
    val hours = duration.toHours().toInt()
    duration = duration.minusHours(hours.toLong())
    val minutes = duration.toMinutes().toInt()
    val dayStr = resources.getQuantityString(R.plurals.number_of_days, days, days)
    val hourStr = resources.getQuantityString(R.plurals.number_of_hours, hours, hours)
    val minuteStr = resources.getQuantityString(R.plurals.number_of_minutes, minutes, minutes)
    return when {
        days > 0 ->
            context.getString(R.string.in_days_hours, dayStr, hourStr)
        hours > 0 ->
            context.getString(R.string.in_hours_minutes, hourStr, minuteStr)
        minutes > 0 ->
            context.getString(R.string.in_minutes, minuteStr)
        else -> context.getString(R.string.now)
    }
}
