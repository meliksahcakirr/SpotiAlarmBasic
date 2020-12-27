package com.meliksahcakir.spotialarm.main

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityMainBinding

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
    }

    override fun onClick(alarm: Alarm) {
        // TODO not implemented yet
    }

    override fun onAlarmEnableStatusChanged(enabled: Boolean) {
        // TODO not implemented yet
    }
}
