package com.meliksahcakir.spotialarm.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.meliksahcakir.spotialarm.worker.RescheduleWorker

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            startRescheduleAlarmsService(context)
        }
    }

    private fun startRescheduleAlarmsService(context: Context) {
        val workRequest = OneTimeWorkRequestBuilder<RescheduleWorker>()
            .build()
        WorkManager.getInstance(context)
            .enqueueUniqueWork(RescheduleWorker.UNIQUE_NAME, ExistingWorkPolicy.KEEP, workRequest)
    }
}
