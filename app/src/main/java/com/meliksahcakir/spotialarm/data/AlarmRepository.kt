package com.meliksahcakir.spotialarm.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.map
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.data.SearchResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class AlarmRepository(
    private val alarmDao: AlarmDao,
    private val napsterService: NapsterService,
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

    suspend fun getAlarmById(alarmId: Int): Result<Alarm> = withContext(ioDispatcher) {
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

    suspend fun deleteAlarm(alarmId: Int) = withContext(ioDispatcher) {
        alarmDao.deleteAlarmById(alarmId)
    }

    suspend fun disableAllAlarms() = withContext(ioDispatcher) {
        alarmDao.disableAllAlarms()
    }

    suspend fun updateAlarm(alarmId: Int, enable: Boolean) = withContext(ioDispatcher) {
        alarmDao.updateAlarmEnableStatus(alarmId, enable)
    }

    suspend fun search(query: String): Result<SearchResult> = withContext(ioDispatcher) {
        return@withContext try {
            val obj = napsterService.search(query)
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }
}
