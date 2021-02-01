package com.meliksahcakir.spotialarm.music.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.databinding.AlbumItemBinding
import com.meliksahcakir.spotialarm.databinding.ArtistItemBinding
import com.meliksahcakir.spotialarm.databinding.GenreItemBinding
import com.meliksahcakir.spotialarm.databinding.OptionItemBinding
import com.meliksahcakir.spotialarm.databinding.PlaylistItemBinding
import com.meliksahcakir.spotialarm.databinding.SeparatorItemBinding
import com.meliksahcakir.spotialarm.databinding.TrackItemBinding

class OptionsAdapter(private val listener: MusicUIModelListener) :
    ListAdapter<MusicUIModel, MusicUIViewHolder>(MusicSearchDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicUIViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.track_item -> TrackViewHolder(
                TrackItemBinding.inflate(inf, parent, false),
                listener
            )
            R.layout.album_item -> AlbumViewHolder(
                AlbumItemBinding.inflate(inf, parent, false),
                listener
            )
            R.layout.artist_item -> ArtistViewHolder(
                ArtistItemBinding.inflate(inf, parent, false),
                listener
            )
            R.layout.genre_item -> GenreViewHolder(
                GenreItemBinding.inflate(inf, parent, false),
                listener
            )
            R.layout.playlist_item -> PlaylistViewHolder(
                PlaylistItemBinding.inflate(
                    inf,
                    parent,
                    false
                ),
                listener
            )
            R.layout.option_item -> OptionViewHolder(
                OptionItemBinding.inflate(inf, parent, false),
                listener
            )
            else -> SeparatorViewHolder(SeparatorItemBinding.inflate(inf, parent, false))
        }
    }

    override fun onBindViewHolder(holder: MusicUIViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is MusicUIModel.TrackItem -> R.layout.track_item
            is MusicUIModel.AlbumItem -> R.layout.album_item
            is MusicUIModel.ArtistItem -> R.layout.artist_item
            is MusicUIModel.GenreItem -> R.layout.genre_item
            is MusicUIModel.PlaylistItem -> R.layout.playlist_item
            is MusicUIModel.OptionItem -> R.layout.option_item
            is MusicUIModel.SeparatorItem -> R.layout.separator_item
        }
    }
}
