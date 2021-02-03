package com.meliksahcakir.spotialarm.playlists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.databinding.PlaylistItemBinding
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.music.ui.PlaylistDiffCallback
import com.meliksahcakir.spotialarm.music.ui.PlaylistViewHolder

class PlaylistsAdapter(private val modelListener: MusicUIModelListener) :
    ListAdapter<MusicUIModel.PlaylistItem, PlaylistViewHolder>(PlaylistDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlaylistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return PlaylistViewHolder(
            PlaylistItemBinding.inflate(inflater, parent, false),
            modelListener
        )
    }

    override fun onBindViewHolder(holder: PlaylistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
