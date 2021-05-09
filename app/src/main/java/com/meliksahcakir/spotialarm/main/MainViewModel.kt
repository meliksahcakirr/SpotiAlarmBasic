package com.meliksahcakir.spotialarm.main

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.calculateDurationString
import com.meliksahcakir.spotialarm.cancel
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.repository.AlarmRepository
import com.meliksahcakir.spotialarm.repository.MusicRepository
import com.meliksahcakir.spotialarm.schedule
import kotlinx.coroutines.Runnable
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainViewModel(
    private val alarmRepository: AlarmRepository,
    private val musicRepository: MusicRepository,
    private val app: Application
) : AndroidViewModel(app) {

    companion object {
        private const val TIME_INTERVAL = 10000L
    }

    private val _goToEditPageEvent = MutableLiveData<Event<Int>>()
    val goToEditPageEvent: LiveData<Event<Int>> get() = _goToEditPageEvent

    private val _goToPreferencesPageEvent = MutableLiveData<Event<Unit>>()
    val goToPreferencesPageEvent: LiveData<Event<Unit>> get() = _goToPreferencesPageEvent

    private val _alarms = MutableLiveData<List<Alarm>>()
    val alarms: LiveData<List<Alarm>> get() = _alarms

    val favoriteTracks = musicRepository.observeFavoriteTracks()

    private val _nearestAlarm = MutableLiveData<Alarm?>()
    val nearestAlarm: LiveData<Alarm?> get() = _nearestAlarm

    private val handler = Handler(Looper.getMainLooper())

    private var allAlarmsScheduled = false

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
        val result = alarmRepository.getAlarms()
        val list = if (result is Result.Success) {
            result.data
        } else {
            emptyList()
        }
        _alarms.value = list
        updateNearestDateTime()
        if (!allAlarmsScheduled) {
            allAlarmsScheduled = true
            list.forEach {
                if (it.enabled) {
                    it.schedule(app)
                }
            }
        }
    }

    private fun findNearestAlarm(alarms: List<Alarm>): Alarm? {
        var nearestAlarm: Alarm? = null
        var minDiff = Long.MAX_VALUE
        val now = LocalDateTime.now()
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

    fun onAlarmEnableStatusChanged(alarmId: Int, enabled: Boolean) {
        val alarm = _alarms.value?.find { it.alarmId == alarmId } ?: return
        alarm.enabled = enabled
        if (enabled) {
            val date = alarm.schedule(getApplication())
            date?.let {
                val text =
                    app.getString(R.string.alarm_go_off) + " " + calculateDurationString(app, date)
                Toast.makeText(app, text, Toast.LENGTH_SHORT).show()
            }
        } else {
            alarm.cancel(getApplication())
        }
        updateNearestDateTime()
        viewModelScope.launch {
            alarmRepository.updateAlarm(alarm.alarmId, enabled)
        }
    }

    fun onAlarmSelected(alarmId: Int? = -1) {
        _goToEditPageEvent.value = Event(alarmId ?: -1)
    }

    private fun updateNearestDateTime() {
        _nearestAlarm.value = findNearestAlarm(_alarms.value ?: emptyList())
    }

    fun onPreferencesButtonClicked() {
        _goToPreferencesPageEvent.value = Event(Unit)
    }

    fun onDisableButtonClicked() {
        viewModelScope.launch {
            alarmRepository.disableAllAlarms()
            refreshDataInternal()
            _alarms.value?.forEach {
                it.cancel(app)
            }
        }
    }

    fun onDeleteAllButtonClicked() {
        viewModelScope.launch {
            alarmRepository.deleteAllAlarms()
            refreshDataInternal()
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(tickRunnable)
    }
}
