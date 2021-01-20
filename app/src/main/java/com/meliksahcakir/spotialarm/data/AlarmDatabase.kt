package com.meliksahcakir.spotialarm.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [Alarm::class], version = 2, exportSchema = false)
abstract class AlarmDatabase : RoomDatabase() {

    abstract fun alarmDao(): AlarmDao
}
