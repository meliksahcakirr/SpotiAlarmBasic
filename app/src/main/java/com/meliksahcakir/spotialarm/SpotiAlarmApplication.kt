package com.meliksahcakir.spotialarm

import android.app.Application
import timber.log.Timber

class SpotiAlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
