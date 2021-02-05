package com.meliksahcakir.spotialarm.main

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.broadcast.AlarmReceiver
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import com.meliksahcakir.spotialarm.setNearestAlarm

class MainFragment : Fragment(), AlarmListener {

    private lateinit var alarmAdapter: AlarmAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val viewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() }) {
        ServiceLocator.provideViewModelFactory(requireActivity().application)
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            viewModel.refreshData()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater)
        requireActivity().registerReceiver(receiver, IntentFilter(AlarmReceiver.ACTION_FINISH))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
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

        viewModel.alarms.observe(viewLifecycleOwner) {
            val list = it ?: emptyList()
            alarmAdapter.submitList(list)
            binding.recyclerView.isVisible = list.isNotEmpty()
            binding.emptyGroup.isVisible = list.isEmpty()
        }

        viewModel.nearestAlarm.observe(viewLifecycleOwner) {
            binding.setNearestAlarm(it)
        }

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToEditFragment(it)
            }
        )

        viewModel.goToPreferencesPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToPreferencesFragment()
            }
        )

        binding.fab.setOnClickListener {
            viewModel.onAlarmSelected()
        }

        binding.preferencesButton.setOnClickListener {
            viewModel.onPreferencesButtonClicked()
        }
        viewModel.refreshData()
    }

    override fun onClick(alarmId: Int) {
        viewModel.onAlarmSelected(alarmId)
    }

    override fun onAlarmEnableStatusChanged(alarmId: Int, enabled: Boolean) {
        viewModel.onAlarmEnableStatusChanged(alarmId, enabled)
    }

    private fun navigateToEditFragment(id: Int) {
        val direction = MainFragmentDirections.actionMainFragmentToAlarmEditFragment(id)
        findNavController().navigate(direction)
    }

    private fun navigateToPreferencesFragment() {
        val direction = MainFragmentDirections.actionMainFragmentToPreferencesFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        activity?.unregisterReceiver(receiver)
        _binding = null
    }
}
