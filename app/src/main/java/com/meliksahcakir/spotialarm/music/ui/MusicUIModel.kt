package com.meliksahcakir.spotialarm.music.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.music.data.Album
import com.meliksahcakir.spotialarm.music.data.Artist
import com.meliksahcakir.spotialarm.music.data.Genre
import com.meliksahcakir.spotialarm.music.data.Playlist
import com.meliksahcakir.spotialarm.music.data.Track

enum class MusicOptions(@StringRes val description: Int, @DrawableRes val icon: Int) {
    FAVORITES(R.string.favs, R.drawable.ic_favorite),
    FEATURED(R.string.featured, R.drawable.ic_featured),
    MOODS(R.string.moods, R.drawable.ic_mood),
    GENRES(R.string.genres, R.drawable.ic_genre),
    TRACKS(R.string.top_songs, R.drawable.ic_music),
    ALBUMS(R.string.top_albums, R.drawable.ic_album),
    ARTIST(R.string.top_artists, R.drawable.ic_artist)
}

sealed class MusicUIModel {
    data class TrackItem(val track: Track) : MusicUIModel()
    data class AlbumItem(val album: Album) : MusicUIModel()
    data class ArtistItem(val artist: Artist) : MusicUIModel()
    data class GenreItem(val genre: Genre) : MusicUIModel()
    data class PlaylistItem(val playlist: Playlist) : MusicUIModel()
    data class OptionItem(val option: MusicOptions) : MusicUIModel()
    data class SeparatorItem(val description: String) : MusicUIModel()
}
