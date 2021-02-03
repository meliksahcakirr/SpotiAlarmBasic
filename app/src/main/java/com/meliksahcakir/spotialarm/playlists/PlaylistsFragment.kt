package com.meliksahcakir.spotialarm.playlists

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.meliksahcakir.androidutils.EventObserver
import com.meliksahcakir.spotialarm.ServiceLocator
import com.meliksahcakir.spotialarm.Utils
import com.meliksahcakir.spotialarm.databinding.FragmentPlaylistsBinding
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.options.OptionsFragmentDirections

class PlaylistsFragment : BottomSheetDialogFragment(), MusicUIModelListener {

    private var _binding: FragmentPlaylistsBinding? = null
    private val binding: FragmentPlaylistsBinding get() = _binding!!

    private val viewModel: PlaylistsViewModel by viewModels {
        ServiceLocator.provideMusicViewModelFactory(requireActivity().application)
    }

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
                    PlaylistsFragmentDirections.actionPlaylistsFragmentToTracksFragment(
                        it.first,
                        it.second
                    )
                findNavController().navigate(directions)
            }
        )

        arguments?.let {
            val args = PlaylistsFragmentArgs.fromBundle(it)
            viewModel.getPlaylists(args.tagId)
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
        viewModel.onMusicUIModelClicked(model)
    }
}
