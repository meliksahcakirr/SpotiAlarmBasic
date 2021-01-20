package com.meliksahcakir.spotialarm.main

import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.data.AlarmRepository
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainViewModel(private val repository: AlarmRepository) : ViewModel() {

    companion object {
        private const val TIME_INTERVAL = 10000L
    }

    private val _goToEditPageEvent = MutableLiveData<Event<Int>>()
    val goToEditPageEvent: LiveData<Event<Int>> get() = _goToEditPageEvent

    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: MutableLiveData<List<Alarm>> get() = _alarms

    private val _nearestAlarmDateTime = MutableLiveData<LocalDateTime?>()
    val nearestAlarmDateTime: LiveData<LocalDateTime?> get() = _nearestAlarmDateTime

    private val handler = Handler(Looper.getMainLooper())

    private val tickRunnable = object : Runnable {
        override fun run() {
            _nearestAlarmDateTime.value = findNearestDateTime(_alarms.value ?: emptyList())
            handler.postDelayed(this, TIME_INTERVAL)
        }
    }

    init {
        refreshData()
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

    private fun findNearestDateTime(alarms: List<Alarm>): LocalDateTime? {
        var nearestDateTime: LocalDateTime? = null
        var minDiff = Long.MAX_VALUE
        val now = LocalDateTime.now()
        for (alarm in alarms) {
            if (!alarm.enabled) continue
            val dateTime = alarm.nearestDateTime(now)
            val diff = ChronoUnit.MINUTES.between(now, dateTime)
            if (diff < minDiff) {
                minDiff = diff
                nearestDateTime = dateTime
            }
        }
        return nearestDateTime
    }

    fun onAlarmEnableStatusChanged(alarm: Alarm, enabled: Boolean) {
        alarm.enabled = enabled
        updateNearestDateTime()
        viewModelScope.launch {
            repository.updateAlarm(alarm.alarmId, enabled)
        }
    }

    fun onAlarmSelected(alarm: Alarm? = null) {
        _goToEditPageEvent.value = Event(alarm?.alarmId ?: -1)
    }

    private fun updateNearestDateTime() {
        _nearestAlarmDateTime.value = findNearestDateTime(_alarms.value ?: emptyList())
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(tickRunnable)
    }
}
