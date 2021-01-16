package com.meliksahcakir.spotialarm.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentMainBinding
import com.meliksahcakir.spotialarm.setNearestAlarm

class MainFragment : Fragment(), AlarmListener {

    private lateinit var alarmAdapter: AlarmAdapter
    private var _binding: FragmentMainBinding? = null
    private val binding: FragmentMainBinding get() = _binding!!

    private val viewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() }) {
        ServiceLocator.provideViewModelFactory(requireContext())
    }

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
        }

        viewModel.nearestAlarmDateTime.observe(viewLifecycleOwner) {
            binding.setNearestAlarm(it)
        }

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToEditFragment(it)
            }
        )

        binding.fab.setOnClickListener {
            viewModel.onAlarmSelected()
        }
    }

    override fun onClick(alarm: Alarm) {
        viewModel.onAlarmSelected(alarm)
    }

    override fun onAlarmEnableStatusChanged(alarm: Alarm, enabled: Boolean) {
        viewModel.onAlarmEnableStatusChanged(alarm, enabled)
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
