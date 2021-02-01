package com.meliksahcakir.spotialarm.music.ui

import androidx.recyclerview.widget.DiffUtil

object MusicUIDiffCallback : DiffUtil.ItemCallback<MusicUIModel>() {
    override fun areItemsTheSame(o1: MusicUIModel, o2: MusicUIModel): Boolean {
        return when (o1) {
            is MusicUIModel.ArtistItem -> {
                (o2 is MusicUIModel.ArtistItem) && (o1.artist.id == o2.artist.id)
            }
            is MusicUIModel.PlaylistItem -> {
                (o2 is MusicUIModel.PlaylistItem) && (o1.playlist.id == o2.playlist.id)
            }
            is MusicUIModel.OptionItem -> {
                (o2 is MusicUIModel.OptionItem) && (o1.option == o2.option)
            }
            is MusicUIModel.SeparatorItem -> {
                (o2 is MusicUIModel.SeparatorItem) && (o1.description == o2.description)
            }
            is MusicUIModel.TrackItem -> {
                (o2 is MusicUIModel.TrackItem) && (o1.track.id == o2.track.id)
            }
            is MusicUIModel.AlbumItem -> {
                (o2 is MusicUIModel.AlbumItem) && (o1.album.id == o2.album.id)
            }
            is MusicUIModel.GenreItem -> {
                (o2 is MusicUIModel.GenreItem) && (o1.genre.id == o2.genre.id)
            }
        }
    }

    override fun areContentsTheSame(oldItem: MusicUIModel, newItem: MusicUIModel): Boolean {
        return oldItem == newItem
    }
}
