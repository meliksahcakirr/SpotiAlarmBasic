package com.meliksahcakir.spotialarm

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.repository.AlarmRepository
import com.meliksahcakir.spotialarm.repository.MusicRepository
import com.meliksahcakir.spotialarm.repository.SpotiAlarmDatabase

object ServiceLocator {

    private var database: SpotiAlarmDatabase? = null

    var repository: AlarmRepository? = null
        private set

    var musicRepository: MusicRepository? = null
        private set

    private var napsterService: NapsterService? = null

    private fun provideMusicRepository(context: Context): MusicRepository {
        synchronized(this) {
            return musicRepository ?: MusicRepository(
                provideNapsterService(),
                provideDatabase(context).musicDao()
            ).apply {
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
        val repo = AlarmRepository(
            provideDatabase(context).alarmDao()
        )
        repository = repo
        return repo
    }

    private fun provideDatabase(context: Context): SpotiAlarmDatabase {
        return database ?: createAlarmDatabase(context)
    }

    private fun createAlarmDatabase(context: Context): SpotiAlarmDatabase {
        val db = Room.databaseBuilder(
            context.applicationContext,
            SpotiAlarmDatabase::class.java,
            "Alarms.db"
        ).fallbackToDestructiveMigration().build()
        database = db
        return db
    }

    fun provideViewModelFactory(application: Application) =
        ViewModelFactory(provideAlarmRepository(application), application)

    fun provideMusicViewModelFactory(application: Application) =
        MusicViewModelFactory(provideMusicRepository(application.applicationContext), application)
}
