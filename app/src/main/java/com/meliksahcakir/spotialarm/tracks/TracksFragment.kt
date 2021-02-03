package com.meliksahcakir.spotialarm.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.databinding.FragmentTracksBinding
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.setImageUrl

class TracksFragment : BaseBottomSheetDialogFragment(), MusicUIModelListener, TrackListener {

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
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

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
            viewModel.getTracks(args.options, args.source?.getSourceId() ?: "")
            if (args.source == null) {
                binding.headerGroup.isVisible = false
                return@let
            }
            binding.headerGroup.isVisible = true
            binding.headerBackgroundImageView.setImageUrl(args.source.getImageUrl())
            binding.headerImageView.setImageUrl(args.source.getImageUrl())
            binding.headerTitleTextView.text = args.source.getTitle()
            binding.headerSubTitleTextView.text = args.source.getSubTitle()
            binding.headerSubTitleTextView.isVisible = args.source.getSubTitle() != null
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
