package com.meliksahcakir.spotialarm.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import com.meliksahcakir.spotialarm.setNearestAlarm
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

class MainFragment : Fragment(), AlarmListener {

    companion object {
        private const val ALARM_HOUR_1 = 8
        private const val ALARM_MINUTE_1 = 45
        private const val ALARM_HOUR_2 = 13
        private const val ALARM_MINUTE_2 = 0
    }

    private lateinit var alarmAdapter: AlarmAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val alarms = arrayListOf(
            Alarm(ALARM_HOUR_1, ALARM_MINUTE_1, false, Alarm.WEEKDAYS),
            Alarm(ALARM_HOUR_2, ALARM_MINUTE_2, false, Alarm.WEEKEND)
        )
        alarmAdapter = AlarmAdapter(this)
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = alarmAdapter
            addItemDecoration(
                DividerItemDecoration(
                    requireContext(),
                    DividerItemDecoration.VERTICAL
                )
            )
        }
        alarmAdapter.submitList(alarms)
        findNearestAlarm()
        binding.fab.setOnClickListener {
            navigateToEditFragment()
        }
    }

    override fun onClick(alarm: Alarm) {
        navigateToEditFragment(alarm.alarmId)
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

    private fun navigateToEditFragment(id: String? = null) {
        val direction = MainFragmentDirections.actionMainFragmentToAlarmEditFragment(id)
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
