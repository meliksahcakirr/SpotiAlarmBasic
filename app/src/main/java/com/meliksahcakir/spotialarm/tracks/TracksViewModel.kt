package com.meliksahcakir.spotialarm.tracks

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import com.meliksahcakir.spotialarm.music.data.MusicRepository
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import kotlinx.coroutines.launch

class TracksViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app) {

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _tracks = MutableLiveData<List<MusicUIModel.TrackItem>>(emptyList())
    val tracks: LiveData<List<MusicUIModel.TrackItem>> = _tracks

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    fun getTracks(options: TrackOptions, id: String) {
        _busy.value = true
        viewModelScope.launch {
            val result = repository.getTracks(options, id)
            if (result is Result.Error) {
                _warningEvent.value = Event(R.string.error_occurred)
                _tracks.value = emptyList()
                _busy.value = false
                return@launch
            }
            val list = (result as Result.Success).data.list
            _tracks.value = list.map { MusicUIModel.TrackItem(it) }
            _busy.value = false
        }
    }
}
