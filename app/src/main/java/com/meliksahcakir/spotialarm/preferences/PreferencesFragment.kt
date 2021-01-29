package com.meliksahcakir.spotialarm.preferences

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import androidx.core.net.toUri
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.databinding.FragmentPreferencesBinding
import permissions.dispatcher.NeedsPermission
import permissions.dispatcher.RuntimePermissions
import timber.log.Timber

@RuntimePermissions
class PreferencesFragment : BottomSheetDialogFragment() {

    companion object {
        private const val HEIGHT_RATIO = 0.9f
        private const val DISABLED_ALPHA = 0.4f
        private const val ENABLED_ALPHA = 1f
        private const val STEP_VALUE = 5
        private const val AUDIO_REQUEST = 123
    }

    private var _binding: FragmentPreferencesBinding? = null
    private val binding: FragmentPreferencesBinding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPreferencesBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViews()
        setupListeners()
    }

    private fun setupViews() {
        binding.readAlarmSwitch.isChecked = Preferences.readAlarmTimeLoud
        binding.faceDownToSnoozeSwitch.isChecked = Preferences.faceDownToSnooze
        binding.slideToTurnOffSwitch.isChecked = Preferences.slideToTurnOff
        val snoozeItems = requireContext().resources.getStringArray(R.array.snooze_intervals)
        val fadeItems = requireContext().resources.getStringArray(R.array.fade_intervals)
        val snoozePos = Preferences.snoozeDuration / STEP_VALUE - 1
        val fadeInPos = Preferences.fadeInDuration / STEP_VALUE - 1
        binding.snoozeValueTextView.text = snoozeItems[snoozePos]
        binding.fadeValueTextView.text = fadeItems[fadeInPos]
        if (Preferences.useDeviceAlarmVolume) {
            binding.useAlarmVolumeSwitch.isChecked = true
            binding.volumeSlider.isEnabled = false
            binding.volumeSlider.alpha = DISABLED_ALPHA
            binding.volumeTextView.alpha = DISABLED_ALPHA
        } else {
            binding.useAlarmVolumeSwitch.isChecked = false
            binding.volumeSlider.isEnabled = true
            binding.volumeSlider.alpha = ENABLED_ALPHA
            binding.volumeTextView.alpha = ENABLED_ALPHA
        }
        binding.volumeSlider.value = Preferences.customVolume.toFloat()
        binding.fallbackValueTextView.text =
            getFallbackMusicName(Preferences.fallbackAudioContentUri?.toUri())
    }

    private fun setupListeners() {
        binding.readAlarmSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.readAlarmTimeLoud = isChecked
        }
        binding.faceDownToSnoozeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.faceDownToSnooze = isChecked
        }
        binding.slideToTurnOffSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.slideToTurnOff = isChecked
        }
        binding.useAlarmVolumeSwitch.setOnCheckedChangeListener { _, isChecked ->
            Preferences.useDeviceAlarmVolume = isChecked
            binding.volumeSlider.isEnabled = !isChecked
            binding.volumeSlider.alpha = if (isChecked) DISABLED_ALPHA else ENABLED_ALPHA
            binding.volumeTextView.alpha = if (isChecked) DISABLED_ALPHA else ENABLED_ALPHA
        }
        binding.volumeSlider.addOnChangeListener { _, value, _ ->
            Preferences.customVolume = value.toInt()
        }

        val snoozePopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)
        val fadePopupWindow = ListPopupWindow(requireContext(), null, R.attr.listPopupWindowStyle)
        snoozePopupWindow.anchorView = binding.snoozeValueTextView
        fadePopupWindow.anchorView = binding.fadeValueTextView
        val snoozeItems = requireContext().resources.getStringArray(R.array.snooze_intervals)
        val fadeItems = requireContext().resources.getStringArray(R.array.fade_intervals)
        val snoozeAdapter = ArrayAdapter(requireContext(), R.layout.popup_item, snoozeItems)
        val fadeAdapter = ArrayAdapter(requireContext(), R.layout.popup_item, fadeItems)
        snoozePopupWindow.setAdapter(snoozeAdapter)
        fadePopupWindow.setAdapter(fadeAdapter)
        snoozePopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, p: Int, _: Long ->
            binding.snoozeValueTextView.text = snoozeItems[p]
            Preferences.snoozeDuration = STEP_VALUE * (p + 1)
            snoozePopupWindow.dismiss()
        }
        fadePopupWindow.setOnItemClickListener { _: AdapterView<*>?, _: View?, p: Int, _: Long ->
            binding.fadeValueTextView.text = fadeItems[p]
            Preferences.fadeInDuration = STEP_VALUE * (p + 1)
            fadePopupWindow.dismiss()
        }

        binding.snoozeValueTextView.setOnClickListener { snoozePopupWindow.show() }
        binding.fadeValueTextView.setOnClickListener { fadePopupWindow.show() }
        binding.fallbackValueTextView.setOnClickListener { requestAudioFilesWithPermissionCheck() }
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

    private fun getFallbackMusicName(uri: Uri?): String {
        val default = requireContext().getString(R.string.fallback_default)
        if (uri == null) return default
        return try {
            uri.let {
                requireContext().contentResolver.query(it, null, null, null, null)
            }?.use { cursor ->
                val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                cursor.moveToFirst()
                cursor.getString(nameIndex).substringBeforeLast(".")
            } ?: default
        } catch (e: Exception) {
            Timber.e(e)
            default
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    @NeedsPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun requestAudioFiles() {
        val intent = Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_ALL)
            putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Tone")
        }
        startActivityForResult(
            Intent.createChooser(
                intent,
                getString(R.string.complete_action_using)
            ),
            AUDIO_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == AUDIO_REQUEST && data != null) {
            val uri: Uri? = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
            Preferences.fallbackAudioContentUri = uri?.toString()
            binding.fallbackValueTextView.text = getFallbackMusicName(uri)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        onRequestPermissionsResult(requestCode, grantResults)
    }
}
