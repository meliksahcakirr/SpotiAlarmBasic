package com.meliksahcakir.spotialarm.genres

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.databinding.GenreItemBinding
import com.meliksahcakir.spotialarm.music.ui.GenreDiffCallback
import com.meliksahcakir.spotialarm.music.ui.GenreViewHolder
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener

class GenresAdapter(private val modelListener: MusicUIModelListener) :
    ListAdapter<MusicUIModel.GenreItem, GenreViewHolder>(GenreDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return GenreViewHolder(
            GenreItemBinding.inflate(inflater, parent, false),
            modelListener
        )
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}
