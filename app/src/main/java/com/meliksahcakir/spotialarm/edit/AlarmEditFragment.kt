package com.meliksahcakir.spotialarm.edit

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
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.calculateDurationString
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.setup

class AlarmEditFragment : BottomSheetDialogFragment() {

    companion object {
        private const val HEIGHT_RATIO = 0.8f
    }

    private var _binding: FragmentAlarmEditBinding? = null
    private val binding: FragmentAlarmEditBinding get() = _binding!!

    private val viewModel: EditViewModel by viewModels {
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
            viewModel.retrieveAlarm(alarmId)
        }

        viewModel.selectedAlarm.observe(viewLifecycleOwner) {
            setupView(it)
        }

        viewModel.alarmDateTime.observe(viewLifecycleOwner) {
            if (it != null) {
                binding.durationTextView.text = calculateDurationString(requireContext(), it)
            }
        }

        viewModel.goToMainPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
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
            viewModel.updateAlarmDay(index, isChecked)
        }
        binding.deleteFab.setOnClickListener {
            viewModel.onDeleteClicked()
        }
        binding.saveFab.setOnClickListener {
            val description = binding.descriptionEditText.text.toString()
            val vibrate = binding.vibrationFab.isChecked
            val snooze = binding.snoozeFab.isChecked
            viewModel.onSaveClicked(description, vibrate, snooze)
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
