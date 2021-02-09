package com.meliksahcakir.spotialarm.music.ui

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
        Glide
            .with(binding.root.context)
            .load(NapsterService.createAlbumImageUrl(track.albumId, AlbumImageSize.SIZE_200X200))
            .centerCrop()
            .placeholder(R.drawable.alarm_background)
            .into(binding.imageView)
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
                play()
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

    fun updateView(selected: Boolean, playing: Boolean) {
        binding.trackCardView.isActivated = selected
        if (playing) {
            play()
        } else {
            stop()
        }
    }

    fun play() {
        binding.playerImageView.setImageResource(R.drawable.ic_pause)
    }

    fun stop() {
        binding.playerImageView.setImageResource(R.drawable.ic_play)
        binding.progressBar.progress = 0f
    }

    fun updateProgress(progress: Float) {
        binding.progressBar.progress = progress
    }
}

class AlbumViewHolder(private val binding: AlbumItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val album = (model as MusicUIModel.AlbumItem).album
        binding.albumTextView.text = album.name
        binding.artistTextView.text = album.artistName
        Glide
            .with(binding.root.context)
            .load(NapsterService.createAlbumImageUrl(album.id, AlbumImageSize.SIZE_200X200))
            .centerCrop()
            .placeholder(R.drawable.alarm_background)
            .into(binding.imageView)
    }
}

class ArtistViewHolder(private val binding: ArtistItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val artist = (model as MusicUIModel.ArtistItem).artist
        binding.artistTextView.text = artist.name
        Glide
            .with(binding.root.context)
            .load(NapsterService.createArtistImageUrl(artist.id, ArtistImageSize.SIZE_356X237))
            .centerCrop()
            .placeholder(R.drawable.alarm_background)
            .into(binding.imageView)
    }
}

class GenreViewHolder(private val binding: GenreItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val genre = (model as MusicUIModel.GenreItem).genre
        binding.genreTextView.text = genre.name
        Glide
            .with(binding.root.context)
            .load(NapsterService.createGenreImageUrl(genre.id, GenreImageSize.SIZE_240X160))
            .centerCrop()
            .placeholder(R.drawable.alarm_background)
            .into(binding.imageView)
    }
}

class PlaylistViewHolder(private val binding: PlaylistItemBinding, listener: MusicUIModelListener) :
    MusicUIViewHolder(binding.root, listener) {
    override fun bind(model: MusicUIModel) {
        super.bind(model)
        val list = (model as MusicUIModel.PlaylistItem).playlist
        binding.playlistTextView.text = list.name
        Glide
            .with(binding.root.context)
            .load(NapsterService.createPlaylistImageUrl(list.id, PlaylistImageSize.SIZE_230X153))
            .centerCrop()
            .placeholder(R.drawable.alarm_background)
            .into(binding.imageView)
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
