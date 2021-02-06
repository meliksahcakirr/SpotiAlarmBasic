package com.meliksahcakir.spotialarm.options

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
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.AlbumViewHolder
import com.meliksahcakir.spotialarm.music.ui.ArtistViewHolder
import com.meliksahcakir.spotialarm.music.ui.GenreViewHolder
import com.meliksahcakir.spotialarm.music.ui.MusicSearchDiffCallback
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.music.ui.MusicUIModelListener
import com.meliksahcakir.spotialarm.music.ui.MusicUIViewHolder
import com.meliksahcakir.spotialarm.music.ui.OptionViewHolder
import com.meliksahcakir.spotialarm.music.ui.PlaylistViewHolder
import com.meliksahcakir.spotialarm.music.ui.SeparatorViewHolder
import com.meliksahcakir.spotialarm.music.ui.TrackViewHolder
import com.meliksahcakir.spotialarm.tracks.TrackListener

class OptionsAdapter(
    private val modelListener: MusicUIModelListener,
    private val trackListener: TrackListener
) :
    ListAdapter<MusicUIModel, MusicUIViewHolder>(
        MusicSearchDiffCallback
    ) {

    private val _modelListener = object : MusicUIModelListener {
        override fun onClicked(model: MusicUIModel) {
            if (model is MusicUIModel.TrackItem) {
                val prev = selectedTrackId
                selectedTrackId = model.track.id
                if (selectedTrackId == prev) {
                    selectedTrackId = null
                    notifyItemChanged(
                        currentList.indexOfFirst {
                            it is MusicUIModel.TrackItem && it.track.id == prev
                        }
                    )
                } else {
                    notifyItemChanged(
                        currentList.indexOfFirst {
                            it is MusicUIModel.TrackItem && it.track.id == prev
                        }
                    )
                    notifyItemChanged(
                        currentList.indexOfFirst {
                            it is MusicUIModel.TrackItem && it.track.id == selectedTrackId
                        }
                    )
                }
            }
            modelListener.onClicked(model)
        }
    }

    private val _trackListener = object : TrackListener {
        override fun play(track: Track) {
            val prev = playedTrackId
            playedTrackId = track.id
            notifyItemChanged(
                currentList.indexOfFirst {
                    it is MusicUIModel.TrackItem && it.track.id == prev
                }
            )
            notifyItemChanged(
                currentList.indexOfFirst {
                    it is MusicUIModel.TrackItem && it.track.id == playedTrackId
                }
            )
            trackListener.play(track)
        }

        override fun stop(track: Track) {
            val prev = playedTrackId
            playedTrackId = null
            notifyItemChanged(
                currentList.indexOfFirst {
                    it is MusicUIModel.TrackItem && it.track.id == prev
                }
            )
            trackListener.stop(track)
        }

        override fun updateTrack(track: Track) {
            trackListener.updateTrack(track)
        }
    }

    private var playedTrackId: String? = null
    private var selectedTrackId: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MusicUIViewHolder {
        val inf = LayoutInflater.from(parent.context)
        return when (viewType) {
            R.layout.track_item -> TrackViewHolder(
                TrackItemBinding.inflate(inf, parent, false),
                _modelListener,
                _trackListener
            )
            R.layout.album_item -> AlbumViewHolder(
                AlbumItemBinding.inflate(inf, parent, false),
                _modelListener
            )
            R.layout.artist_item -> ArtistViewHolder(
                ArtistItemBinding.inflate(inf, parent, false),
                _modelListener
            )
            R.layout.genre_item -> GenreViewHolder(
                GenreItemBinding.inflate(inf, parent, false),
                _modelListener
            )
            R.layout.playlist_item -> PlaylistViewHolder(
                PlaylistItemBinding.inflate(
                    inf,
                    parent,
                    false
                ),
                _modelListener
            )
            R.layout.option_item -> OptionViewHolder(
                OptionItemBinding.inflate(inf, parent, false),
                _modelListener
            )
            else -> SeparatorViewHolder(
                SeparatorItemBinding.inflate(inf, parent, false)
            )
        }
    }

    override fun onBindViewHolder(holder: MusicUIViewHolder, position: Int) {
        val item = getItem(position)
        if (item is MusicUIModel.TrackItem) {
            item.track.isPlaying = item.track.id == playedTrackId
            holder.bind(item)
            (holder as? TrackViewHolder)?.updateView(
                item.track.id == selectedTrackId,
                item.track.isPlaying
            )
        } else {
            holder.bind(item)
        }
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
