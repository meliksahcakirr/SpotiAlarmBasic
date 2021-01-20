package com.meliksahcakir.spotialarm.edit

import android.app.TimePickerDialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.NOON
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.calculateDurationString
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.main.MainViewModel
import com.meliksahcakir.spotialarm.setup
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmEditFragment : BottomSheetDialogFragment() {

    companion object {
        private const val HEIGHT_RATIO = 0.8f
    }

    private var _binding: FragmentAlarmEditBinding? = null
    private val binding: FragmentAlarmEditBinding get() = _binding!!

    private val mainViewModel: MainViewModel by viewModels(ownerProducer = { requireActivity() }) {
        ServiceLocator.provideViewModelFactory(requireContext())
    }

    private val editViewModel: EditViewModel by viewModels {
        ServiceLocator.provideViewModelFactory(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAlarmEditBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        arguments?.let {
            val args = AlarmEditFragmentArgs.fromBundle(it)
            val alarmId = args.alarmId
            editViewModel.retrieveAlarm(alarmId)
        }

        editViewModel.selectedAlarm.observe(viewLifecycleOwner) {
            setupView(it)
        }

        editViewModel.alarmDateTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.durationTextView.text = calculateDurationString(requireContext(), it)
            }
        }

        editViewModel.goToMainPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                mainViewModel.refreshData()
                navigateToMainFragment()
            }
        )
    }

    private fun setupView(alarm: Alarm) {
        binding.musicGroup.isInvisible = alarm.musicId == ""
        binding.setup(alarm)
        binding.daysToggleGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            val button: MaterialButton = group.findViewById(checkedId)
            val index = group.indexOfChild(button)
            editViewModel.updateAlarmDay(index, isChecked)
        }
        binding.deleteFab.setOnClickListener {
            editViewModel.onDeleteClicked()
        }
        binding.saveFab.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val vibrate = binding.vibrationFab.isChecked
            val snooze = binding.snoozeFab.isChecked
            editViewModel.onSaveClicked(description, vibrate, snooze)
        }
        binding.alarmTimeTextView.setOnClickListener {
            val now = LocalTime.now()
            val picker = TimePickerDialog(
                requireContext(),
                R.style.TimePickerDialogTheme,
                TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                    val formatter = DateTimeFormatter.ofPattern("hh:mm")
                    val time = LocalTime.of(hour, minute)
                    binding.alarmTimeTextView.text = time.format(formatter)
                    if (hour >= NOON) {
                        binding.alarmTimePeriodTextView.text = context?.getString(R.string.pm)
                    } else {
                        binding.alarmTimePeriodTextView.text = context?.getString(R.string.am)
                    }
                    editViewModel.updateAlarmTime(hour, minute)
                },
                now.hour,
                now.minute,
                false
            )
            picker.show()
        }
    }

    private fun navigateToMainFragment() {
        val direction = AlarmEditFragmentDirections.actionAlarmEditFragmentToMainFragment()
        findNavController().navigate(direction)
    }

    override fun onStart() {
        super.onStart()
        val height = (Resources.getSystem().displayMetrics.heightPixels * HEIGHT_RATIO).toInt()
        dialog?.let {
            val bs = it.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)!!
            bs.layoutParams.height = height
            with(BottomSheetBehavior.from(bs)) {
                peekHeight = height
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
