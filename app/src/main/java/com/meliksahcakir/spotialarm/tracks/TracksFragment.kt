package com.meliksahcakir.spotialarm.tracks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.Utils
import com.meliksahcakir.spotialarm.databinding.FragmentTracksBinding
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.music.ui.TrackViewHolder
import com.meliksahcakir.spotialarm.setImageUrl
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class TracksFragment : BaseBottomSheetDialogFragment(), MusicUIModelListener, TrackListener {

    private var _binding: FragmentTracksBinding? = null
    private val binding: FragmentTracksBinding get() = _binding!!

    private val viewModel: TracksViewModel by viewModel()

    private val editViewModel: EditViewModel by sharedViewModel()

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
            binding.emptyGroup.isVisible = it.isEmpty()
        }

        viewModel.busy.observe(viewLifecycleOwner) {
            if (it) {
                adapter.submitList(emptyList())
            }
            binding.recyclerView.isInvisible = it
            binding.progressBar.isVisible = it
        }

        viewModel.selectedTrack.observe(viewLifecycleOwner) {
            binding.addToAlarmButton.isVisible = it != null
        }

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                editViewModel.onTrackUpdated(it)
                findNavController().popBackStack(R.id.alarmEditFragment, false)
            }
        )

        viewModel.mediaPlayerProgress.observe(viewLifecycleOwner) { pair ->
            val current = pair.first
            val duration = pair.second
            adapter.playedTrackId?.let { trackId ->
                val pos = adapter.currentList.indexOfFirst {
                    it is MusicUIModel.TrackItem && it.track.id == trackId
                }
                val vh =
                    binding.recyclerView.findViewHolderForAdapterPosition(pos) as? TrackViewHolder
                if (current == -1) {
                    vh?.stop()
                    adapter.playedTrackId = null
                    adapter.playedTrackProgress = 0f
                } else {
                    val progress =
                        if (duration == 0) 0f else Utils.PROGRESS_FULL * current / duration
                    vh?.updateProgress(progress)
                    adapter.playedTrackProgress = progress
                }
            }
        }

        binding.addToAlarmButton.setOnClickListener {
            viewModel.onAddToAlarmButtonClicked()
        }

        processArguments()
    }

    private fun processArguments() {
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
        viewModel.onModelClicked(model)
    }

    override fun updateTrack(track: Track) {
        viewModel.updateTrack(track)
    }

    override fun play(track: Track) {
        viewModel.play(track)
    }

    override fun stop(track: Track) {
        viewModel.stop()
    }
}
