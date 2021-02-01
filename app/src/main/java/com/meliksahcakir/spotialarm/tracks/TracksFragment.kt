package com.meliksahcakir.spotialarm.tracks

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.Utils
import com.meliksahcakir.spotialarm.databinding.FragmentTracksBinding
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener

class TracksFragment : BottomSheetDialogFragment(), MusicUIModelListener, TrackListener {

    private var _binding: FragmentTracksBinding? = null
    private val binding: FragmentTracksBinding get() = _binding!!

    private val viewModel: TracksViewModel by viewModels {
        ServiceLocator.provideMusicViewModelFactory(requireActivity().application)
    }

    private val adapter = TracksAdapter(this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTracksBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.adapter = adapter

        viewModel.warningEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )

        viewModel.tracks.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        viewModel.busy.observe(viewLifecycleOwner) {
            if (it) {
                adapter.submitList(emptyList())
            }
            binding.recyclerView.isInvisible = it
            binding.progressBar.isVisible = it
        }

        arguments?.let {
            val args = TracksFragmentArgs.fromBundle(it)
            viewModel.getTracks(args.options, args.id)
        }
    }

    override fun onStart() {
        super.onStart()
        val height =
            (Resources.getSystem().displayMetrics.heightPixels * Utils.HEIGHT_RATIO).toInt()
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

    override fun onClicked(model: MusicUIModel) {
        TODO("Not yet implemented")
    }

    override fun play(track: Track) {
        TODO("Not yet implemented")
    }

    override fun stop(track: Track) {
        TODO("Not yet implemented")
    }

    override fun addToAlarm(track: Track) {
        TODO("Not yet implemented")
    }
}
