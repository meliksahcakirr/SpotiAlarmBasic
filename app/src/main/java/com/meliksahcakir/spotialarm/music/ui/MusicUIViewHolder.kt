package com.meliksahcakir.spotialarm.music.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.databinding.AlbumItemBinding
import com.meliksahcakir.spotialarm.databinding.ArtistItemBinding
import com.meliksahcakir.spotialarm.databinding.GenreItemBinding
import com.meliksahcakir.spotialarm.databinding.OptionItemBinding
import com.meliksahcakir.spotialarm.databinding.PlaylistItemBinding
import com.meliksahcakir.spotialarm.databinding.SeparatorItemBinding
import com.meliksahcakir.spotialarm.databinding.TrackItemBinding
import com.meliksahcakir.spotialarm.music.api.AlbumImageSize
import com.meliksahcakir.spotialarm.music.api.ArtistImageSize
import com.meliksahcakir.spotialarm.music.api.GenreImageSize
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.PlaylistImageSize
import com.meliksahcakir.spotialarm.setImageUrl
import com.meliksahcakir.spotialarm.tracks.TrackListener

abstract class MusicUIViewHolder(view: View, private var listener: MusicUIModelListener? = null) :
    RecyclerView.ViewHolder(view) {
    open fun bind(model: MusicUIModel) {
        itemView.setOnClickListener {
            listener?.onClicked(model)
        }
    }
}

class TrackViewHolder(
    private val binding: TrackItemBinding,
    modelListener: MusicUIModelListener,
    private val trackListener: TrackListener
) :
    MusicUIViewHolder(binding.root, modelListener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val track = (model as MusicUIModel.TrackItem).track
        binding.trackTextView.text = track.name
        binding.artistTextView.text = track.artistName
        binding.imageView.setImageUrl(
            NapsterService.createAlbumImageUrl(
                track.albumId,
                AlbumImageSize.SIZE_200X200
            )
        )
        binding.favImageView.setOnClickListener {
            track.favorite = !track.favorite
            if (track.favorite) {
                binding.favImageView.setImageResource(R.drawable.ic_favorite)
            } else {
                binding.favImageView.setImageResource(R.drawable.ic_favorite_empty)
            }
            trackListener.updateTrack(track)
        }
        binding.playerImageView.setOnClickListener {
            if (track.isPlaying) {
                stop()
                trackListener.stop(track)
            } else {
                play(true)
                trackListener.play(track)
            }
            track.isPlaying = !track.isPlaying
        }
        if (track.favorite) {
            binding.favImageView.setImageResource(R.drawable.ic_favorite)
        } else {
            binding.favImageView.setImageResource(R.drawable.ic_favorite_empty)
        }
    }

    fun updateView(selected: Boolean, playing: Boolean, progress: Float) {
        binding.trackCardView.isActivated = selected
        if (playing) {
            play()
            updateProgress(progress)
        } else {
            stop()
        }
    }

    private fun play(indeterminateMode: Boolean = false) {
        binding.playerImageView.setImageResource(R.drawable.ic_pause)
        if (binding.progressBar.indeterminateMode != indeterminateMode) {
            binding.progressBar.indeterminateMode = indeterminateMode
        }
    }

    fun stop() {
        binding.playerImageView.setImageResource(R.drawable.ic_play)
        binding.progressBar.indeterminateMode = false
        binding.progressBar.progress = 0f
    }

    fun updateProgress(progress: Float) {
        if (progress == -1f) {
            binding.progressBar.indeterminateMode = true
        } else {
            binding.progressBar.indeterminateMode = false
            binding.progressBar.progress = progress
        }
    }
}

class AlbumViewHolder(private val binding: AlbumItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val album = (model as MusicUIModel.AlbumItem).album
        binding.albumTextView.text = album.name
        binding.artistTextView.text = album.artistName
        binding.imageView.setImageUrl(
            NapsterService.createAlbumImageUrl(
                album.id,
                AlbumImageSize.SIZE_200X200
            )
        )
    }
}

class ArtistViewHolder(private val binding: ArtistItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val artist = (model as MusicUIModel.ArtistItem).artist
        binding.artistTextView.text = artist.name
        binding.imageView.setImageUrl(
            NapsterService.createArtistImageUrl(
                artist.id,
                ArtistImageSize.SIZE_356X237
            )
        )
    }
}

class GenreViewHolder(private val binding: GenreItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val genre = (model as MusicUIModel.GenreItem).genre
        binding.genreTextView.text = genre.name
        binding.imageView.setImageUrl(
            NapsterService.createGenreImageUrl(
                genre.id,
                GenreImageSize.SIZE_240X160
            )
        )
    }
}

class PlaylistViewHolder(private val binding: PlaylistItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val list = (model as MusicUIModel.PlaylistItem).playlist
        binding.playlistTextView.text = list.name
        binding.imageView.setImageUrl(
            NapsterService.createPlaylistImageUrl(
                list.id,
                PlaylistImageSize.SIZE_230X153
            )
        )
    }
}

class OptionViewHolder(private val binding: OptionItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val option = (model as MusicUIModel.OptionItem).option
        binding.imageView.setImageResource(option.icon)
        binding.textView.setText(option.description)
    }
}

class SeparatorViewHolder(private val binding: SeparatorItemBinding) :
    MusicUIViewHolder(binding.root) {
    override fun bind(model: MusicUIModel) {
        binding.descriptionTextView.text = (model as MusicUIModel.SeparatorItem).description
    }
}

interface MusicUIModelListener {
    fun onClicked(model: MusicUIModel)
}
