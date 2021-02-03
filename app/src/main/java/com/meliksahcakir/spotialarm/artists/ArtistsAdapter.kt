package com.meliksahcakir.spotialarm.artists

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.databinding.ArtistItemBinding
import com.meliksahcakir.spotialarm.music.ui.ArtistDiffCallback
import com.meliksahcakir.spotialarm.music.ui.ArtistViewHolder
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener

class ArtistsAdapter(private val modelListener: MusicUIModelListener) :
    ListAdapter<MusicUIModel.ArtistItem, ArtistViewHolder>(ArtistDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArtistViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ArtistViewHolder(
            ArtistItemBinding.inflate(inflater, parent, false),
            modelListener
        )
    }

    override fun onBindViewHolder(holder: ArtistViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
