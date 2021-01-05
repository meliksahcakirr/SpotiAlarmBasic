package com.meliksahcakir.spotialarm

import android.content.Context
import androidx.core.view.get
import androidx.core.view.isVisible
import androidx.core.view.size
import com.google.android.material.button.MaterialButton
import com.meliksahcakir.androidutils.drawable
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.AlarmViewBinding
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private const val NOON = 12

fun AlarmViewBinding.bind(alarm: Alarm) {
    val context = root.context
    val time = alarm.alarmTime
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = time.format(formatter)
    if (alarm.hour >= NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
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

fun FragmentAlarmEditBinding.setup(alarm: Alarm) {
    val context = root.context
    alarm.enabled = true
    var date = alarm.nearestDate() ?: LocalDateTime.of(LocalDate.now(), alarm.alarmTime)
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = alarm.alarmTime.format(formatter)
    if (alarm.hour >= NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
    }
    durationTextView.text = calculateDurationString(context, LocalDateTime.now(), date)
    val days = alarm.days
    for (i in 0 until daysToggleGroup.size) {
        val view = daysToggleGroup[i]
        if (days and (1 shl i) > 0) {
            daysToggleGroup.check(view.id)
        }
    }
    vibrationFab.isChecked = alarm.vibrate
    snoozeFab.isChecked = alarm.snooze
    daysToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
        val button: MaterialButton = group.findViewById(checkedId)
        val index = group.indexOfChild(button)
        if (isChecked) {
            alarm.days = alarm.days or (1 shl index)
        } else {
            alarm.days = alarm.days and (1 shl index).inv()
        }
        date = alarm.nearestDate() ?: LocalDate.now().atTime(alarm.hour, alarm.minute)
        durationTextView.text = calculateDurationString(context, LocalDateTime.now(), date)
    }
}

fun FragmentMainBinding.setNearestAlarm(now: LocalDateTime, nearestAlarm: LocalDateTime?) {
    val context = root.context
    if (nearestAlarm == null) {
        allAlarmsOffTextView.isVisible = true
        nearestAlarmGroup.isVisible = false
    } else {
        allAlarmsOffTextView.isVisible = false
        nearestAlarmGroup.isVisible = true
        val durationText = calculateDurationString(context, now, nearestAlarm)
        durationTextView.text = durationText
        val formatter = DateTimeFormatter.ofPattern("hh:mm")
        nearestAlarmTextView.text = nearestAlarm.format(formatter)
        if (nearestAlarm.hour >= NOON) {
            nearestAlarmPeriodTextView.text = context.getString(R.string.pm)
        } else {
            nearestAlarmPeriodTextView.text = context.getString(R.string.am)
        }
    }
}

fun calculateDurationString(
    context: Context,
    now: LocalDateTime,
    alarm: LocalDateTime
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
