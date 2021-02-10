package com.meliksahcakir.spotialarm.tracks

import android.app.Application
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.repository.MusicRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TracksViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app) {

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _goToEditPageEvent = MutableLiveData<Event<String>>()
    val goToEditPageEvent: LiveData<Event<String>> get() = _goToEditPageEvent

    private val _tracks = MutableLiveData<List<MusicUIModel.TrackItem>>()
    val tracks: LiveData<List<MusicUIModel.TrackItem>> = _tracks

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    private val _selectedTrack = MutableLiveData<Track?>(null)
    val selectedTrack: LiveData<Track?> = _selectedTrack

    private val _mediaPlayerProgress = MutableLiveData<Pair<Int, Int>>()
    val mediaPlayerProgress: LiveData<Pair<Int, Int>> = _mediaPlayerProgress

    private var mediaPlayer: MediaPlayer? = null

    private var trackDuration = 0

    private val handler = Handler(Looper.getMainLooper())

    private val mediaPlayerRunnable = object : Runnable {
        override fun run() {
            try {
                mediaPlayer?.let {
                    _mediaPlayerProgress.value = Pair(it.currentPosition, trackDuration)
                    if (it.isPlaying) {
                        handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
                    }
                }
            } catch (e: Exception) {
                Timber.e(e)
            }
        }
    }

    private val completionListener = MediaPlayer.OnCompletionListener {
        _mediaPlayerProgress.value = Pair(-1, trackDuration)
        stop()
    }

    private val preparedListener = MediaPlayer.OnPreparedListener {
        it?.start()
        trackDuration = it?.duration ?: 0
        handler.post(mediaPlayerRunnable)
    }

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

    fun updateTrack(track: Track) {
        viewModelScope.launch {
            if (track.favorite) {
                repository.insertTrack(track)
            } else {
                repository.deleteTrack(track)
            }
        }
    }

    fun onModelClicked(model: MusicUIModel) {
        val track = (model as MusicUIModel.TrackItem).track
        if (track.id == _selectedTrack.value?.id) {
            _selectedTrack.value = null
        } else {
            _selectedTrack.value = track
        }
    }

    fun play(track: Track) {
        viewModelScope.launch {
            if (mediaPlayer == null || mediaPlayer?.isPlaying == true) {
                stop()
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
                mediaPlayer?.setOnPreparedListener(preparedListener)
                mediaPlayer?.setOnCompletionListener(completionListener)
            }
            launch(Dispatchers.IO) {
                try {
                    mediaPlayer?.let {
                        it.setDataSource(track.previewURL)
                        it.prepareAsync()
                    }
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    fun stop() {
        handler.removeCallbacks(mediaPlayerRunnable)
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun onAddToAlarmButtonClicked() {
        viewModelScope.launch {
            _selectedTrack.value?.let {
                repository.insertTrack(it)
                _goToEditPageEvent.value = Event(it.id)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        stop()
    }
}
