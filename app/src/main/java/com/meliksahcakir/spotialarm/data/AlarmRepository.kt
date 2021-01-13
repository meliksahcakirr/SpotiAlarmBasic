package com.meliksahcakir.spotialarm.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meliksahcakir.androidutils.Result
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AlarmRepository(
    private val alarmDao: AlarmDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    fun observeAlarms(): LiveData<Result<List<Alarm>>> {
        return alarmDao.observeAlarms().map {
            Result.Success(it)
        }
    }

    suspend fun getAlarms(): Result<List<Alarm>> = withContext(ioDispatcher) {
        return@withContext try {
            Result.Success(alarmDao.getAlarms())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAlarmById(alarmId: String): Result<Alarm> = withContext(ioDispatcher) {
        return@withContext try {
            val alarm = alarmDao.getAlarmById(alarmId)
            if (alarm != null) {
                Result.Success(alarm)
            } else {
                Result.Error(Exception("Alarm not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun insertAlarm(alarm: Alarm) = withContext(ioDispatcher) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) = withContext(ioDispatcher) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarm(alarm: Alarm) = withContext(ioDispatcher) {
        alarmDao.deleteAlarm(alarm)
    }

    suspend fun deleteAlarm(alarmId: String) = withContext(ioDispatcher) {
        alarmDao.deleteAlarmById(alarmId)
    }

    suspend fun disableAllAlarms() = withContext(ioDispatcher) {
        alarmDao.disableAllAlarms()
    }

    suspend fun updateAlarm(alarmId: String, enable: Boolean) = withContext(ioDispatcher) {
        alarmDao.updateAlarmEnableStatus(alarmId, enable)
    }
}
