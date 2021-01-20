package com.meliksahcakir.spotialarm

import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.core.view.size
import com.meliksahcakir.androidutils.drawable
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.AlarmViewBinding
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

const val NOON = 12

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
    val formatter = DateTimeFormatter.ofPattern("hh:mm")
    alarmTimeTextView.text = alarm.alarmTime.format(formatter)
    if (alarm.hour >= NOON) {
        alarmTimePeriodTextView.text = context.getString(R.string.pm)
    } else {
        alarmTimePeriodTextView.text = context.getString(R.string.am)
    }
    val days = alarm.days
    for (i in 0 until daysToggleGroup.size) {
        val view = daysToggleGroup[i]
        if (days and (1 shl i) > 0) {
            daysToggleGroup.check(view.id)
        }
    }
    vibrationFab.isChecked = alarm.vibrate
    snoozeFab.isChecked = alarm.snooze
    descriptionEditText.setText(alarm.description)
}

fun FragmentMainBinding.setNearestAlarm(
    nearestAlarm: LocalDateTime?,
    now: LocalDateTime = LocalDateTime.now()
) {
    val context = root.context
    if (nearestAlarm == null) {
        allAlarmsOffTextView.isVisible = true
        nearestAlarmGroup.isInvisible = true
    } else {
        allAlarmsOffTextView.isInvisible = true
        nearestAlarmGroup.isVisible = true
        val durationText = calculateDurationString(context, nearestAlarm, now)
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