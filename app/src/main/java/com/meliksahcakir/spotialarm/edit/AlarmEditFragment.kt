package com.meliksahcakir.spotialarm.edit

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isInvisible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meliksahcakir.spotialarm.data.Alarm
import com.meliksahcakir.spotialarm.databinding.FragmentAlarmEditBinding
import com.meliksahcakir.spotialarm.setup

class AlarmEditFragment : BottomSheetDialogFragment() {

    companion object {
        private const val HEIGHT_RATIO = 0.8f
    }

    private var _binding: FragmentAlarmEditBinding? = null
    private val binding: FragmentAlarmEditBinding get() = _binding!!

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
            val alarm = Alarm(
                1,
                0,
                false,
                Alarm.TUESDAY or Alarm.WEDNESDAY,
                vibrate = false,
                snooze = true
            )
            setupView(alarm)
        }
    }

    private fun setupView(alarm: Alarm) {
        binding.musicGroup.isInvisible = alarm.musicId == ""
        binding.setup(alarm)
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
