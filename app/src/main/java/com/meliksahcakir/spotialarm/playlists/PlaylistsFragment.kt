package com.meliksahcakir.spotialarm.playlists

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.lifecycle.observe
import androidx.recyclerview.widget.DividerItemDecoration
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.BaseBottomSheetDialogFragment
import com.meliksahcakir.spotialarm.databinding.FragmentPlaylistsBinding
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.navigate
import org.koin.androidx.viewmodel.ext.android.viewModel

class PlaylistsFragment : BaseBottomSheetDialogFragment(), MusicUIModelListener {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModel()

    private val adapter = PlaylistsAdapter(this)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPlaylistsBinding.inflate(inflater)
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

        viewModel.playlists.observe(viewLifecycleOwner) {
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

        viewModel.goToTracksPageEvent.observe(
            viewLifecycleOwner,
            EventObserver {
                val direction =
                    PlaylistsFragmentDirections.actionPlaylistsFragmentToTracksFragment(
                        it.first,
                        it.second
                    )
                navigate(direction)
            }
        )

        arguments?.let {
            val args = PlaylistsFragmentArgs.fromBundle(it)
            viewModel.getPlaylists(args.tagId)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClicked(model: MusicUIModel) {
        viewModel.onMusicUIModelClicked(model)
    }
}
