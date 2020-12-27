package com.meliksahcakir.spotialarm

import androidx.core.view.isVisible
import com.meliksahcakir.androidutils.drawable
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding
import com.meliksahcakir.spotialarm.databinding.AlarmViewBinding
import java.time.Duration
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val NOON = 12

fun AlarmViewBinding.bind(alarm: Alarm) {
    val context = root.context
    val time = alarm.alarmDate
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = time.format(formatter)
    if (alarm.hour >= NOON) {
        alarmTimeAmPmTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimeAmPmTextView.text = context.getString(R.string.am)
    }
    val drawable = context.drawable(R.drawable.ic_round_alarm)
    imageView.setImageDrawable(drawable)
    val days = alarm.days
    mondayTextView.isEnabled = (days and Alarm.MONDAY) > 0
    tuesdayTextView.isEnabled = (days and Alarm.TUESDAY) > 0
    wednesdayTextView.isEnabled = (days and Alarm.WEDNESDAY) > 0
    thursdayTextView.isEnabled = (days and Alarm.THURSDAY) > 0
    fridayTextView.isEnabled = (days and Alarm.FRIDAY) > 0
    saturdayTextView.isEnabled = (days and Alarm.SATURDAY) > 0
    sundayTextView.isEnabled = (days and Alarm.SUNDAY) > 0
    alarmSwitch.isChecked = alarm.enabled
}

fun ActivityMainBinding.setNearestAlarm(now: LocalDateTime, nearestAlarm: LocalDateTime?) {
    val context = root.context
    val resources = context.resources
    if (nearestAlarm == null) {
        allAlarmsOffTextView.isVisible = true
        nearestAlarmGroup.isVisible = false
    } else {
        allAlarmsOffTextView.isVisible = false
        nearestAlarmGroup.isVisible = true
        var duration = Duration.between(now, nearestAlarm)
        val days = duration.toDays()
        duration = duration.minusDays(days)
        val hours = duration.toHours()
        duration = duration.minusHours(hours)
        val minutes = duration.toMinutes()
        val dayStr = resources.getQuantityString(R.plurals.number_of_days, days.toInt())
        val hourStr = resources.getQuantityString(R.plurals.number_of_hours, hours.toInt())
        val minuteStr = resources.getQuantityString(R.plurals.number_of_minutes, minutes.toInt())
        when {
            days > 0 ->
                durationTextView.text =
                    context.getString(R.string.in_days_hours, dayStr, hourStr)
            hours > 0 ->
                durationTextView.text =
                    context.getString(R.string.in_hours_minutes, hourStr, minuteStr)
            minutes > 0 ->
                durationTextView.text =
                    context.getString(R.string.in_minutes, minuteStr)
            else -> durationTextView.text = context.getString(R.string.now)
        }
        val formatter = DateTimeFormatter.ofPattern("hh:mm")
        nearestAlarmTextView.text = nearestAlarm.format(formatter)
        if (nearestAlarm.hour >= NOON) {
            nearestAlarmPeriodTextView.text = context.getString(R.string.pm)
        } else {
            nearestAlarmPeriodTextView.text = context.getString(R.string.am)
        }
    }
}
