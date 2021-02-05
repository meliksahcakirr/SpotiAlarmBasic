package com.meliksahcakir.spotialarm.artists

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

class ArtistsViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app) {

    private val _goToTracksPageEvent = MutableLiveData<Event<Pair<TrackOptions, ITrackSource?>>>()
    val goToTracksPageEvent: LiveData<Event<Pair<TrackOptions, ITrackSource?>>>
        get() = _goToTracksPageEvent

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _artists = MutableLiveData<List<MusicUIModel.ArtistItem>>(emptyList())
    val artists: LiveData<List<MusicUIModel.ArtistItem>> = _artists

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    fun getArtists() {
        _busy.value = true
        viewModelScope.launch {
            val result = repository.getTopArtists()
            if (result is Result.Error) {
                _warningEvent.value = Event(R.string.error_occurred)
                _artists.value = emptyList()
                _busy.value = false
                return@launch
            }
            val list = (result as Result.Success).data.list
            _artists.value = list.map { MusicUIModel.ArtistItem(it) }
            _busy.value = false
        }
    }

    fun onMusicUIModelClicked(model: MusicUIModel) {
        _goToTracksPageEvent.value = Event(
            Pair(
                TrackOptions.ARTIST_TRACKS,
                (model as MusicUIModel.ArtistItem).artist
            )
        )
    }
}
