package com.meliksahcakir.spotialarm

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.meliksahcakir.spotialarm.data.AlarmDatabase
import com.meliksahcakir.spotialarm.data.AlarmRepository
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.data.MusicRepository

object ServiceLocator {

    private var database: AlarmDatabase? = null
    var repository: AlarmRepository? = null
        private set

    var musicRepository: MusicRepository? = null
        private set

    private var napsterService: NapsterService? = null

    fun provideMusicRepository(): MusicRepository {
        synchronized(this) {
            return musicRepository ?: MusicRepository(provideNapsterService()).apply {
                musicRepository = this
            }
        }
    }

    private fun provideNapsterService(): NapsterService {
        synchronized(this) {
            return napsterService ?: NapsterService.create().apply {
                napsterService = this
            }
        }
    }

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

    fun provideViewModelFactory(application: Application) =
        ViewModelFactory(provideAlarmRepository(application), application)

    fun provideMusicViewModelFactory(application: Application) =
        MusicViewModelFactory(provideMusicRepository(), application)
}
