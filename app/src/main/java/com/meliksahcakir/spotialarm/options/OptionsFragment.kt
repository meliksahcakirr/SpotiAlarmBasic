package com.meliksahcakir.spotialarm.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.androidutils.hideKeyboard
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.Utils
import com.meliksahcakir.spotialarm.databinding.FragmentOptionsBinding
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.music.ui.TrackViewHolder
import com.meliksahcakir.spotialarm.tracks.TrackListener
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber

class OptionsFragment : BaseBottomSheetDialogFragment(), MusicUIModelListener, TrackListener {

    private var _binding: FragmentOptionsBinding? = null
    private val binding: FragmentOptionsBinding get() = _binding!!

    private val viewModel: OptionsViewModel by viewModel()

    private val editViewModel: EditViewModel by sharedViewModel()

    private val adapter = OptionsAdapter(this, this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOptionsBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.searchEditText.setText(viewModel.latestQuery)

        binding.searchEditText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                binding.root.hideKeyboard()
                viewModel.search(binding.searchEditText.text.toString())
            }
            true
        }

        binding.addToAlarmButton.setOnClickListener {
            viewModel.onAddToAlarmButtonClicked()
        }

        binding.recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

        observeViewModel()
    }

    private fun observeNavigationEvents() {
        viewModel.goToTracksPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions = OptionsFragmentDirections.actionOptionsFragmentToTracksFragment(
                    it.first,
                    it.second
                )
                findNavController().navigate(directions)
            }
        )

        viewModel.goToPlayListsPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions =
                    OptionsFragmentDirections.actionOptionsFragmentToPlaylistsFragment(it)
                findNavController().navigate(directions)
            }
        )

        viewModel.goToAlbumsPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions = OptionsFragmentDirections.actionOptionsFragmentToAlbumsFragment()
                findNavController().navigate(directions)
            }
        )

        viewModel.goToArtistsPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions = OptionsFragmentDirections.actionOptionsFragmentToArtistsFragment()
                findNavController().navigate(directions)
            }
        )

        viewModel.goToGenresPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions = OptionsFragmentDirections.actionOptionsFragmentToGenresFragment()
                findNavController().navigate(directions)
            }
        )

        viewModel.goToEditPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                editViewModel.onTrackUpdated(it)
                findNavController().popBackStack(R.id.alarmEditFragment, false)
            }
        )
    }

    private fun observeViewModel() {
        observeNavigationEvents()
        viewModel.warningEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        )

        viewModel.musicUIModels.observe(viewLifecycleOwner) {
            adapter.submitList(it)
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

        viewModel.mediaPlayerProgress.observe(viewLifecycleOwner) { pair ->
            val current = pair.first
            val duration = pair.second
            Timber.d("MELIK mediaPlayerProgress current = $current  duration = $duration")
            Timber.d("MELIK mediaPlayerProgress playedTrackId = ${adapter.playedTrackId}")
            adapter.playedTrackId?.let { trackId ->
                val pos = adapter.currentList.indexOfFirst {
                    it is MusicUIModel.TrackItem && it.track.id == trackId
                }
                Timber.d("MELIK mediaPlayerProgress pos = $pos")
                val vh =
                    binding.recyclerView.findViewHolderForAdapterPosition(pos) as? TrackViewHolder
                if (current == -1) {
                    vh?.stop()
                    adapter.playedTrackId = null
                } else {
                    val progress =
                        if (duration == 0) 0f else Utils.PROGRESS_FULL * current / duration
                    vh?.updateProgress(progress)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClicked(model: MusicUIModel) {
        viewModel.onMusicUIModelClicked(model)
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
