package com.meliksahcakir.spotialarm.active

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.meliksahcakir.spotialarm.bind
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.ActivityActiveAlarmBinding

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
    }
}
