package com.meliksahcakir.spotialarm

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.meliksahcakir.spotialarm.albums.AlbumsViewModel
import com.meliksahcakir.spotialarm.artists.ArtistsViewModel
import com.meliksahcakir.spotialarm.data.AlarmRepository
import com.meliksahcakir.spotialarm.edit.EditViewModel
import com.meliksahcakir.spotialarm.main.MainViewModel
import com.meliksahcakir.spotialarm.music.data.MusicRepository
import com.meliksahcakir.spotialarm.options.OptionsViewModel
import com.meliksahcakir.spotialarm.playlists.PlaylistsViewModel
import com.meliksahcakir.spotialarm.tracks.TracksViewModel

@Suppress("UNCHECKED_CAST")
class ViewModelFactory(
    private val repository: AlarmRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(EditViewModel::class.java) -> {
                EditViewModel(repository, application) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}

@Suppress("UNCHECKED_CAST")
class MusicViewModelFactory(
    private val repository: MusicRepository,
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(OptionsViewModel::class.java) -> {
                OptionsViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(TracksViewModel::class.java) -> {
                TracksViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(PlaylistsViewModel::class.java) -> {
                PlaylistsViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(AlbumsViewModel::class.java) -> {
                AlbumsViewModel(repository, application) as T
            }
            modelClass.isAssignableFrom(ArtistsViewModel::class.java) -> {
                ArtistsViewModel(repository, application) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}
