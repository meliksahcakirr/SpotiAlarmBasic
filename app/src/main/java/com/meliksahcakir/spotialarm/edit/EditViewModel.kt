package com.meliksahcakir.spotialarm.edit

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
import kotlinx.coroutines.launch
import timber.log.Timber
import java.time.LocalDateTime
import java.time.LocalTime

class EditViewModel(private val repository: AlarmRepository) : ViewModel() {

    companion object {
        private const val INITIAL_TIME_INTERVAL = 3000L
        private const val TIME_INTERVAL = 10000L
    }

    private val _goToMainPageEvent = MutableLiveData<Event<Unit>>()
    val goToMainPageEvent: LiveData<Event<Unit>> get() = _goToMainPageEvent

    private val _alarmDateTime = MutableLiveData<LocalDateTime?>()
    val alarmDateTime: LiveData<LocalDateTime?> = _alarmDateTime

    private val _selectedAlarm = MutableLiveData<Alarm>()
    val selectedAlarm: LiveData<Alarm> = _selectedAlarm
    private var newAlarm = false

    private val handler = Handler(Looper.getMainLooper())

    private val tickRunnable = object : Runnable {
        override fun run() {
            handler.postDelayed(this, TIME_INTERVAL)
        }
    }

    init {
        Timber.d("initialized")
        handler.postDelayed(tickRunnable, INITIAL_TIME_INTERVAL)
    }

    fun retrieveAlarm(alarmId: String?) {
        var alarm: Alarm
        viewModelScope.launch {
            if (alarmId == null || alarmId == "") {
                alarm = defaultAlarm()
                newAlarm = true
            } else {
                val result = repository.getAlarmById(alarmId)
                if (result is Result.Success) {
                    alarm = result.data
                    newAlarm = false
                } else {
                    alarm = defaultAlarm()
                    newAlarm = true
                }
            }
            _selectedAlarm.value = alarm.apply { enabled = true }
            _alarmDateTime.value = alarm.nearestDateTime()
        }
    }

    fun updateAlarmTime(hour: Int, minute: Int) {
        selectedAlarm.value?.apply {
            this.hour = hour
            this.minute = minute
            _alarmDateTime.value = nearestDateTime()
        }
    }

    fun updateAlarmDay(day: Int, isChecked: Boolean) {
        selectedAlarm.value?.apply {
            days = if (isChecked) {
                days or (1 shl day)
            } else {
                days and (1 shl day).inv()
            }
            _alarmDateTime.value = nearestDateTime()
        }
    }

    fun onDeleteClicked() {
        viewModelScope.launch {
            selectedAlarm.value?.let {
                repository.deleteAlarm(it)
            }
            _goToMainPageEvent.value = Event(Unit)
        }
    }

    fun onSaveClicked(description: String, vibrate: Boolean, snooze: Boolean) {
        val alarm = selectedAlarm.value
        if (alarm == null) {
            _goToMainPageEvent.value = Event(Unit)
            return
        }
        alarm.description = description
        alarm.vibrate = vibrate
        alarm.snooze = snooze
        viewModelScope.launch {
            if (newAlarm) {
                repository.insertAlarm(alarm)
            } else {
                repository.updateAlarm(alarm)
            }
            _goToMainPageEvent.value = Event(Unit)
        }
    }

    override fun onCleared() {
        super.onCleared()
        handler.removeCallbacks(tickRunnable)
    }

    private fun defaultAlarm(): Alarm {
        val now = LocalTime.now()
        return Alarm(now.hour, now.minute, false, Alarm.ONCE, vibrate = false, snooze = true)
    }
}
