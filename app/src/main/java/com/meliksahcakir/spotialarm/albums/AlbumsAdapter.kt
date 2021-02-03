package com.meliksahcakir.spotialarm.albums

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.databinding.AlbumItemBinding
import com.meliksahcakir.spotialarm.music.ui.AlbumDiffCallback
import com.meliksahcakir.spotialarm.music.ui.AlbumViewHolder
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener

class AlbumsAdapter(private val modelListener: MusicUIModelListener) :
    ListAdapter<MusicUIModel.AlbumItem, AlbumViewHolder>(AlbumDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlbumViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return AlbumViewHolder(
            AlbumItemBinding.inflate(inflater, parent, false),
            modelListener
        )
    }

    override fun onBindViewHolder(holder: AlbumViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
