package com.meliksahcakir.spotialarm.tracks

import android.app.Application
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
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class TracksViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app), MediaPlayer.OnCompletionListener {

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _tracks = MutableLiveData<List<MusicUIModel.TrackItem>>(emptyList())
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
            mediaPlayer?.let {
                _mediaPlayerProgress.value = Pair(it.currentPosition, trackDuration)
                if (it.isPlaying) {
                    handler.postDelayed(this, TimeUnit.SECONDS.toMillis(1))
                }
            }
        }
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
                killMediaPlayer()
                mediaPlayer = MediaPlayer()
                mediaPlayer?.setOnCompletionListener(this@TracksViewModel)
            }
            startMediaPlayer(track)
        }
    }

    private suspend fun startMediaPlayer(track: Track) {
        coroutineScope {
            launch(Dispatchers.IO) {
                try {
                    mediaPlayer?.let {
                        it.setDataSource(track.previewURL)
                        it.prepare()
                        it.start()
                    }
                    trackDuration = mediaPlayer?.duration ?: 0
                    handler.post(mediaPlayerRunnable)
                } catch (e: Exception) {
                    Timber.e(e)
                }
            }
        }
    }

    fun stop(track: Track) {
        killMediaPlayer()
    }

    override fun onCompletion(player: MediaPlayer?) {
        _mediaPlayerProgress.value = Pair(-1, trackDuration)
        killMediaPlayer()
    }

    private fun killMediaPlayer() {
        handler.removeCallbacks(mediaPlayerRunnable)
        mediaPlayer?.let {
            it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        killMediaPlayer()
    }
}
