package com.meliksahcakir.spotialarm.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.cancel
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.data.AlarmRepository
import com.meliksahcakir.spotialarm.schedule
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainViewModel(private val repository: AlarmRepository, app: Application) :
    AndroidViewModel(app) {

    companion object {
        private const val TIME_INTERVAL = 10000L
    }

    private val _goToEditPageEvent = MutableLiveData<Event<Int>>()
    val goToEditPageEvent: LiveData<Event<Int>> get() = _goToEditPageEvent

    private val _goToPreferencesPageEvent = MutableLiveData<Event<Unit>>()
    val goToPreferencesPageEvent: LiveData<Event<Unit>> get() = _goToPreferencesPageEvent

    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: LiveData<List<Alarm>> get() = _alarms

    private val _nearestAlarm = MutableLiveData<Alarm?>()
    val nearestAlarm: LiveData<Alarm?> get() = _nearestAlarm

    private val handler = Handler(Looper.getMainLooper())

    private val tickRunnable = object : Runnable {
        override fun run() {
            _nearestAlarm.value = findNearestAlarm(_alarms.value ?: emptyList())
            handler.postDelayed(this, TIME_INTERVAL)
        }
    }

    init {
        // refreshData()
        handler.postDelayed(tickRunnable, TIME_INTERVAL)
    }

    fun refreshData() {
        viewModelScope.launch {
            refreshDataInternal()
        }
    }

    private suspend fun refreshDataInternal() {
        val result = repository.getAlarms()
        val list = if (result is Result.Success) {
            result.data
        } else {
            emptyList()
        }
        _alarms.value = list
        updateNearestDateTime()
    }

    private fun findNearestAlarm(alarms: List<Alarm>): Alarm? {
        var nearestAlarm: Alarm? = null
        var minDiff = Long.MAX_VALUE
        val now = LocalDateTime.now().withSecond(0)
        for (alarm in alarms) {
            if (!alarm.enabled) continue
            val dateTime = alarm.nearestDateTime(now)
            val diff = ChronoUnit.MINUTES.between(now, dateTime)
            if (diff < minDiff) {
                minDiff = diff
                nearestAlarm = alarm
            }
        }
        return nearestAlarm
    }

    fun onAlarmEnableStatusChanged(alarm: Alarm, enabled: Boolean) {
        alarm.enabled = enabled
        if (enabled) {
            alarm.schedule(getApplication())
        } else {
            alarm.cancel(getApplication())
        }
        updateNearestDateTime()
        viewModelScope.launch {
            repository.updateAlarm(alarm.alarmId, enabled)
        }
    }

    fun onAlarmSelected(alarm: Alarm? = null) {
        _goToEditPageEvent.value = Event(alarm?.alarmId ?: -1)
    }

    private fun updateNearestDateTime() {
        _nearestAlarm.value = findNearestAlarm(_alarms.value ?: emptyList())
    }

    fun onPreferencesButtonClicked() {
        _goToPreferencesPageEvent.value = Event(Unit)
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(tickRunnable)
    }
}
