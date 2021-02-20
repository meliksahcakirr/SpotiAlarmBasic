package com.meliksahcakir.spotialarm.active

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import com.meliksahcakir.spotialarm.bind
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityActiveAlarmBinding
import com.meliksahcakir.spotialarm.preferences.Preferences
import com.meliksahcakir.spotialarm.repository.AlarmRepository
import com.meliksahcakir.spotialarm.service.AlarmService
import com.meliksahcakir.spotialarm.snooze
import com.meliksahcakir.spotialarm.turnScreenOffAndKeyguardOn
import com.meliksahcakir.spotialarm.turnScreenOnAndKeyguardOff
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class ActiveAlarmActivity : AppCompatActivity() {

    companion object {
        private const val PROGRESS_LIMIT = 0.1f
    }

    private lateinit var binding: ActivityActiveAlarmBinding

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        binding = ActivityActiveAlarmBinding.inflate(layoutInflater)
        registerReceiver(receiver, IntentFilter(AlarmReceiver.ACTION_FINISH))
        setContentView(binding.root)
        val bundle = intent?.getBundleExtra(AlarmReceiver.EXTRA_ALARM)
        val alarm = Alarm.fromBundle(bundle)
        if (alarm != null) {
            binding.bind(alarm)
        } else {
            finish()
        }
        if (Preferences.slideToTurnOff) {
            binding.turnOffSlider.isVisible = true
            binding.turnOffTextView.isVisible = true
            binding.turnOffFab.isInvisible = true
        } else {
            binding.turnOffSlider.isVisible = false
            binding.turnOffTextView.isVisible = false
            binding.turnOffFab.isVisible = true
        }
        binding.turnOffFab.setOnClickListener {
            stopForegroundService()
            turnOffAlarm(alarm)
        }

        binding.snoozeFab.setOnClickListener {
            alarm?.snooze(this)
            stopForegroundService()
            finish()
        }

        binding.turnOffSlider.setOnSeekBarChangeListener(
            object : SeekBar.OnSeekBarChangeListener {
                private var continuous = false
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, user: Boolean) {
                    val max = binding.turnOffSlider.max
                    val ratio = progress.toFloat() / max
                    binding.turnOffTextView.alpha = 1f - ratio
                    if (progress < max * PROGRESS_LIMIT) {
                        continuous = true
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {
                    continuous = false
                }

                override fun onStopTrackingTouch(p0: SeekBar?) {
                    if (continuous) {
                        val progress = binding.turnOffSlider.progress
                        val max = binding.turnOffSlider.max
                        if (progress == max) {
                            stopForegroundService()
                            turnOffAlarm(alarm)
                        } else {
                            binding.turnOffSlider.progress = 0
                        }
                    } else {
                        binding.turnOffSlider.progress = 0
                    }
                }
            }
        )
    }

    private fun turnOffAlarm(alarm: Alarm?) {
        if (alarm != null && alarm.days == Alarm.ONCE) {
            MainScope().launch {
                val repository: AlarmRepository by inject()
                repository.updateAlarm(alarm.alarmId, false)
                finish()
            }
        } else {
            finish()
        }
    }

    private fun stopForegroundService() {
        val intent = Intent(this, AlarmService::class.java)
        stopService(intent)
    }

    override fun onDestroy() {
        unregisterReceiver(receiver)
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }
}
