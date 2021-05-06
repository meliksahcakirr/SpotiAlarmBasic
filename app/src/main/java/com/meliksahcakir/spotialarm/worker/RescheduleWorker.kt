package com.meliksahcakir.spotialarm.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.meliksahcakir.spotialarm.repository.AlarmRepository
import com.meliksahcakir.spotialarm.schedule
import kotlinx.coroutines.coroutineScope
import org.koin.core.component.KoinApiExtension
import org.koin.core.component.KoinComponent
import org.koin.core.component.get

@KoinApiExtension
class RescheduleWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params), KoinComponent {

    companion object {
        const val UNIQUE_NAME = "RescheduleWorker"
    }

    override suspend fun doWork() = coroutineScope {
        val repository: AlarmRepository = get()
        val result = repository.getAlarms()
        val alarms = (result as? com.meliksahcakir.androidutils.Result.Success)?.data
            ?: return@coroutineScope Result.failure()
        alarms.forEach { alarm ->
            alarm.schedule(applicationContext)
        }
        Result.success()
    }
}
