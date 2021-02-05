package com.meliksahcakir.spotialarm.repository

import androidx.room.Database
import androidx.room.RoomDatabase
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.music.data.Track

@Database(entities = [Alarm::class, Track::class], version = 1, exportSchema = false)
abstract class SpotiAlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
    abstract fun musicDao(): MusicDao
}
