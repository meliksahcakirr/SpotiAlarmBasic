package com.meliksahcakir.spotialarm.active

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.bind
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityActiveAlarmBinding
import com.meliksahcakir.spotialarm.service.AlarmService
import com.meliksahcakir.spotialarm.snooze
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class ActiveAlarmActivity : AppCompatActivity() {

    private lateinit var binding: ActivityActiveAlarmBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityActiveAlarmBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bundle = intent?.getBundleExtra(AlarmReceiver.EXTRA_ALARM)
        val alarm = Alarm.fromBundle(bundle)
        if (alarm != null) {
            binding.bind(alarm)
        } else {
            finish()
        }
        binding.turnOffFab.setOnClickListener {
            val intent = Intent(this, AlarmService::class.java)
            stopService(intent)
            if (alarm != null && alarm.days == Alarm.ONCE) {
                MainScope().launch {
                    val repository = ServiceLocator.provideAlarmRepository(applicationContext)
                    repository.updateAlarm(alarm.alarmId, false)
                    finish()
                }
            } else {
                finish()
            }
        }

        binding.snoozeFab.setOnClickListener {
            alarm?.snooze(this)
            val intent = Intent(this, AlarmService::class.java)
            stopService(intent)
            finish()
        }
    }
}
