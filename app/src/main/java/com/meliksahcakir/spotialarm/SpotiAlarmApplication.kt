package com.meliksahcakir.spotialarm

import android.app.Application
import com.chibatching.kotpref.Kotpref
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import timber.log.Timber

class SpotiAlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@SpotiAlarmApplication)
            modules(modules)
        }
    }
}
