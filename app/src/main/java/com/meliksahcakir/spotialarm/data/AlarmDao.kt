package com.meliksahcakir.spotialarm.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface AlarmDao {

    @Query("SELECT * FROM alarms")
    fun observeAlarms(): LiveData<List<Alarm>>

    @Query("SELECT * FROM alarms")
    suspend fun getAlarms(): List<Alarm>

    @Query("SELECT * FROM alarms WHERE alarmId = :alarmId")
    suspend fun getAlarmById(alarmId: String): Alarm?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlarm(alarm: Alarm)

    @Update
    suspend fun updateAlarm(alarm: Alarm)

    @Delete
    suspend fun deleteAlarm(alarm: Alarm)

    @Query("DELETE FROM alarms WHERE alarmId = :alarmId")
    suspend fun deleteAlarmById(alarmId: String)

    @Query("UPDATE alarms SET enabled = :enable WHERE alarmId = :alarmId")
    suspend fun updateAlarmEnableStatus(alarmId: String, enable: Boolean)

    @Query("UPDATE alarms SET enabled = 0")
    suspend fun disableAllAlarms()
}
