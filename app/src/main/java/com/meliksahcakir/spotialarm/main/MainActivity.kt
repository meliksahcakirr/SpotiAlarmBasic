package com.meliksahcakir.spotialarm.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding
import com.meliksahcakir.spotialarm.setNearestAlarm
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainActivity : AppCompatActivity(), AlarmListener {

    companion object {
        private const val ALARM_HOUR_1 = 8
        private const val ALARM_MINUTE_1 = 45
        private const val ALARM_HOUR_2 = 13
        private const val ALARM_MINUTE_2 = 0
    }

    private lateinit var alarmAdapter: AlarmAdapter
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val alarms = arrayListOf(
            Alarm(ALARM_HOUR_1, ALARM_MINUTE_1, false, Alarm.WEEKDAYS),
            Alarm(ALARM_HOUR_2, ALARM_MINUTE_2, false, Alarm.WEEKEND)
        )
        alarmAdapter = AlarmAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = alarmAdapter
            addItemDecoration(
                DividerItemDecoration(
                    this@MainActivity,
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        alarmAdapter.submitList(alarms)
        findNearestAlarm()
    }

    override fun onClick(alarm: Alarm) {
    }

    override fun onAlarmEnableStatusChanged(alarm: Alarm, enabled: Boolean) {
        alarm.enabled = enabled
        findNearestAlarm()
    }

    private fun findNearestAlarm() {
        var nearestAlarm: LocalDateTime? = null
        var minDiff = Long.MAX_VALUE
        val now = LocalDateTime.now()
        for (alarm in alarmAdapter.currentList) {
            if (!alarm.enabled) continue
            val date = alarm.nearestDate(now)
            val diff = ChronoUnit.MINUTES.between(now, date)
            if (diff < minDiff) {
                minDiff = diff
                nearestAlarm = date
            }
        }
        binding.setNearestAlarm(now, nearestAlarm)
    }
}
