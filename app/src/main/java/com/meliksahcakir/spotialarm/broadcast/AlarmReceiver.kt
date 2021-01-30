package com.meliksahcakir.spotialarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.schedule
import com.meliksahcakir.spotialarm.service.AlarmService
import com.meliksahcakir.spotialarm.snooze
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val EXTRA_ALARM = "SPOTIALARM"
        const val EXTRA_ALARM_SNOOZE = "SPOTIALARM_SNOOZE"

        const val ACTION_SNOOZE = "ACTION_SNOOZE"
        const val ACTION_TURN_OFF = "ACTION_TURN_OFF"
        const val ACTION_ALARM_FIRED = "ACTION_ALARM_FIRED"
        const val ACTION_FINISH = "ACTION_FINISH"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_TURN_OFF -> {
                stopAlarmService(context)
                context.sendBroadcast(Intent(ACTION_FINISH))
            }
            ACTION_SNOOZE -> {
                val bundle = intent.getBundleExtra(EXTRA_ALARM)
                val alarm = Alarm.fromBundle(bundle)
                stopAlarmService(context)
                alarm?.snooze(context)
                context.sendBroadcast(Intent(ACTION_FINISH))
            }
            ACTION_ALARM_FIRED -> {
                val bundle = intent.getBundleExtra(EXTRA_ALARM)
                val alarm = Alarm.fromBundle(bundle) ?: return
                val isSnoozed = intent.getBooleanExtra(EXTRA_ALARM_SNOOZE, false)
                onAlarmFired(context, alarm, isSnoozed)
            }
        }
    }

    private fun onAlarmFired(context: Context, alarm: Alarm, isSnoozed: Boolean) {
        val now = LocalDateTime.now()
        val day = now.dayOfWeek
        when {
            alarm.days == Alarm.ONCE -> {
                startAlarmService(context, alarm)
                GlobalScope.launch {
                    disableAlarm(context, alarm.alarmId)
                }
            }
            isSnoozed -> {
                startAlarmService(context, alarm)
            }
            else -> {
                if (alarm.days and (1 shl day.ordinal) > 0) {
                    startAlarmService(context, alarm)
                }
                alarm.schedule(context)
            }
        }
    }

    private suspend fun disableAlarm(context: Context, alarmId: Int) {
        val repository = ServiceLocator.provideAlarmRepository(context.applicationContext)
        repository.updateAlarm(alarmId, false)
    }

    private fun startAlarmService(context: Context, alarm: Alarm) {
        val intent = Intent(context.applicationContext, AlarmService::class.java)
        intent.putExtra(EXTRA_ALARM, alarm.toBundle())
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    private fun stopAlarmService(context: Context) {
        val intent = Intent(context.applicationContext, AlarmService::class.java)
        context.stopService(intent)
    }
}
