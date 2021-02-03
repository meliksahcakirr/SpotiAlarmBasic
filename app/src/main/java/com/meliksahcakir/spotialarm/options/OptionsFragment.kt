package com.meliksahcakir.spotialarm.options

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.androidutils.hideKeyboard
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.databinding.FragmentOptionsBinding
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.tracks.TrackListener

class OptionsFragment : BaseBottomSheetDialogFragment(), MusicUIModelListener, TrackListener {

    private var _binding: FragmentOptionsBinding? = null
    private val binding: FragmentOptionsBinding get() = _binding!!

    private val viewModel: OptionsViewModel by viewModels {
        ServiceLocator.provideMusicViewModelFactory(requireActivity().application)
    }

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

        binding.recyclerView.adapter = adapter
        val dividerItemDecoration = DividerItemDecoration(context, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)

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

        viewModel.goToTracksPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val directions =
                    OptionsFragmentDirections.actionOptionsFragmentToTracksFragment(
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
                val directions =
                    OptionsFragmentDirections.actionOptionsFragmentToAlbumsFragment()
                findNavController().navigate(directions)
            }
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClicked(model: MusicUIModel) {
        viewModel.onMusicUIModelClicked(model)
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
