package com.meliksahcakir.spotialarm.service

import android.app.Service
import android.content.Intent
import androidx.lifecycle.LifecycleService
import androidx.lifecycle.observe
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.schedule

class RescheduleAlarmsService : LifecycleService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        super.onStartCommand(intent, flags, startId)
        val repository = ServiceLocator.provideAlarmRepository(application)
        repository.observeAlarms().observe(this) {
            val alarms = (it as? Result.Success)?.data ?: return@observe
            alarms.forEach { alarm ->
                alarm.schedule(applicationContext)
            }
        }
        return Service.START_STICKY
    }
}
