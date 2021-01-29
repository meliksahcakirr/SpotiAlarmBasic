package com.meliksahcakir.spotialarm

import android.app.Application
import com.chibatching.kotpref.Kotpref
import timber.log.Timber

class SpotiAlarmApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Kotpref.init(this)
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
