package com.meliksahcakir.spotialarm

import android.content.Context
import androidx.room.Room
import com.meliksahcakir.spotialarm.data.AlarmDatabase
import com.meliksahcakir.spotialarm.data.AlarmRepository

object ServiceLocator {

    private var database: AlarmDatabase? = null
    var repository: AlarmRepository? = null
        private set

    fun provideAlarmRepository(context: Context): AlarmRepository {
        synchronized(this) {
            return repository ?: createAlarmRepository(context)
        }
    }

    private fun createAlarmRepository(context: Context): AlarmRepository {
        val repo = AlarmRepository(provideDatabase(context).alarmDao())
        repository = repo
        return repo
    }

    private fun provideDatabase(context: Context): AlarmDatabase {
        return database ?: createAlarmDatabase(context)
    }

    private fun createAlarmDatabase(context: Context): AlarmDatabase {
        val db = Room.databaseBuilder(
            context.applicationContext,
            AlarmDatabase::class.java,
            "Alarms.db"
        ).fallbackToDestructiveMigration().build()
        database = db
        return db
    }

    fun provideViewModelFactory(context: Context) =
        ViewModelFactory(provideAlarmRepository(context))
}
