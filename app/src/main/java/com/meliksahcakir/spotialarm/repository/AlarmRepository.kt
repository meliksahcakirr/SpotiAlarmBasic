package com.meliksahcakir.spotialarm.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.data.Alarm

class AlarmRepository(private val alarmDao: AlarmDao) {

    fun observeAlarms(): LiveData<Result<List<Alarm>>> {
        return alarmDao.observeAlarms().map {
            Result.Success(it)
        }
    }

    suspend fun getAlarms(): Result<List<Alarm>> {
        return try {
            Result.Success(alarmDao.getAlarms())
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun getAlarmById(alarmId: Int): Result<Alarm> {
        return try {
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

    suspend fun insertAlarm(alarm: Alarm) {
        alarmDao.insertAlarm(alarm)
    }

    suspend fun updateAlarm(alarm: Alarm) {
        alarmDao.updateAlarm(alarm)
    }

    suspend fun deleteAlarms(vararg alarm: Alarm) {
        alarmDao.deleteAlarms(*alarm)
    }

    suspend fun deleteAllAlarms() {
        alarmDao.deleteAllAlarms()
    }

    suspend fun disableAllAlarms() {
        alarmDao.disableAllAlarms()
    }

    suspend fun updateAlarm(alarmId: Int, enable: Boolean) {
        alarmDao.updateAlarmEnableStatus(alarmId, enable)
    }
}
