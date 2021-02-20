package com.meliksahcakir.spotialarm.edit

import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.get
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.Utils
import com.meliksahcakir.spotialarm.calculateDurationString
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.setImageUrl
import com.meliksahcakir.spotialarm.setup
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.time.DayOfWeek
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class AlarmEditFragment : BaseBottomSheetDialogFragment() {

    private var _binding: FragmentAlarmEditBinding? = null
    private val binding: FragmentAlarmEditBinding get() = _binding!!
    override var alphaAnimationForFragmentTransitionEnabled = false

    private val editViewModel: EditViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
            setupView(!editViewModel.newAlarm, it)
        }

        editViewModel.alarmDateTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.durationTextView.text = calculateDurationString(requireContext(), it)
            }
        }

        editViewModel.track.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.musicGroup.isInvisible = false
                binding.addMusicTextView.isVisible = false
                binding.headerBackgroundImageView.setImageUrl(it.getImageUrl())
                binding.imageView.setImageUrl(it.getImageUrl())
                binding.songNameTextView.text = it.getTitle()
                binding.singerNameTextView.text = it.getSubTitle()
            } else {
                binding.musicGroup.isInvisible = true
                binding.addMusicTextView.isVisible = true
            }
        }

        editViewModel.goToMainPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToMainFragment()
            }
        )

        editViewModel.goToMusicPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                navigateToOptionsFragment()
            }
        )
    }

    private val daysToggleGroupListener =
        MaterialButtonToggleGroup.OnButtonCheckedListener { group, checkedId, isChecked ->
            val button: MaterialButton = group.findViewById(checkedId)
            val index = group.indexOfChild(button)
            editViewModel.updateAlarmDay(index, isChecked)
        }

    private fun setupView(exists: Boolean, alarm: Alarm) {
        binding.setup(exists, alarm)
        binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
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
            openTimePicker()
        }
        binding.weekDaysChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.MONDAY.ordinal..DayOfWeek.FRIDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.WEEKDAYS)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
        }
        binding.weekEndsChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.SATURDAY.ordinal..DayOfWeek.SUNDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.WEEKEND)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
        }
        binding.everydayChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            for (i in DayOfWeek.MONDAY.ordinal..DayOfWeek.SUNDAY.ordinal) {
                (binding.daysToggleGroup[i] as MaterialButton).isChecked = true
            }
            editViewModel.updateAlarmDay(Alarm.EVERYDAY)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
        }
        binding.onceChip.setOnClickListener {
            binding.daysToggleGroup.removeOnButtonCheckedListener(daysToggleGroupListener)
            binding.daysToggleGroup.clearChecked()
            editViewModel.updateAlarmDay(Alarm.ONCE)
            binding.daysToggleGroup.addOnButtonCheckedListener(daysToggleGroupListener)
        }
        binding.musicFab.setOnClickListener {
            editViewModel.onMusicFabClicked()
        }
    }

    private fun openTimePicker() {
        val now = editViewModel.alarmDateTime.value?.toLocalTime() ?: LocalTime.now()
        val picker = TimePickerDialog(
            requireContext(),
            R.style.TimePickerDialogTheme,
            TimePickerDialog.OnTimeSetListener { _, hour, minute ->
                val formatter = DateTimeFormatter.ofPattern("hh:mm")
                val time = LocalTime.of(hour, minute)
                binding.alarmTimeTextView.text = time.format(formatter)
                if (hour >= Utils.NOON) {
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

    private fun navigateToMainFragment() {
        val direction = AlarmEditFragmentDirections.actionAlarmEditFragmentToMainFragment()
        findNavController().navigate(direction)
    }

    private fun navigateToOptionsFragment() {
        val direction = AlarmEditFragmentDirections.actionAlarmEditFragmentToOptionsFragment()
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
