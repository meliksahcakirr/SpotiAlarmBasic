package com.meliksahcakir.spotialarm.playlists

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import com.meliksahcakir.spotialarm.music.data.ITrackSource
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.repository.MusicRepository
import kotlinx.coroutines.launch

class PlaylistsViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app) {

    private val _goToTracksPageEvent = MutableLiveData<Event<Pair<TrackOptions, ITrackSource?>>>()
    val goToTracksPageEvent: LiveData<Event<Pair<TrackOptions, ITrackSource?>>>
        get() = _goToTracksPageEvent

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _playlists = MutableLiveData<List<MusicUIModel.PlaylistItem>>()
    val playlists: LiveData<List<MusicUIModel.PlaylistItem>> = _playlists

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    fun getPlaylists(tag: String) {
        _busy.value = true
        viewModelScope.launch {
            val result = repository.getPlaylists(tag)
            if (result is Result.Error) {
                _warningEvent.value = Event(R.string.error_occurred)
                _playlists.value = emptyList()
                _busy.value = false
                return@launch
            }
            val list = (result as Result.Success).data.list
            _playlists.value = list.map { MusicUIModel.PlaylistItem(it) }
            _busy.value = false
        }
    }

    fun onMusicUIModelClicked(model: MusicUIModel) {
        _goToTracksPageEvent.value = Event(
            Pair(
                TrackOptions.PLAYLIST_TRACKS,
                (model as MusicUIModel.PlaylistItem).playlist
            )
        )
    }
}
