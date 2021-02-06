package com.meliksahcakir.spotialarm.options

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.meliksahcakir.androidutils.Event
import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.R
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import com.meliksahcakir.spotialarm.music.data.ITrackSource
import com.meliksahcakir.spotialarm.music.data.SearchResult
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.ui.MusicOptions
import com.meliksahcakir.spotialarm.music.ui.MusicUIModel
import com.meliksahcakir.spotialarm.repository.MusicRepository
import kotlinx.coroutines.launch

class OptionsViewModel(private val repository: MusicRepository, private val app: Application) :
    AndroidViewModel(app) {

    private val _goToAlbumsPageEvent = MutableLiveData<Event<Unit>>()
    val goToAlbumsPageEvent: LiveData<Event<Unit>> get() = _goToAlbumsPageEvent

    private val _goToTracksPageEvent = MutableLiveData<Event<Pair<TrackOptions, ITrackSource?>>>()
    val goToTracksPageEvent: LiveData<Event<Pair<TrackOptions, ITrackSource?>>>
        get() = _goToTracksPageEvent

    private val _goToArtistsPageEvent = MutableLiveData<Event<Unit>>()
    val goToArtistsPageEvent: LiveData<Event<Unit>> get() = _goToArtistsPageEvent

    private val _goToGenresPageEvent = MutableLiveData<Event<Unit>>()
    val goToGenresPageEvent: LiveData<Event<Unit>> get() = _goToGenresPageEvent

    private val _goToPlayListsPageEvent = MutableLiveData<Event<String>>()
    val goToPlayListsPageEvent: LiveData<Event<String>> get() = _goToPlayListsPageEvent

    private val _warningEvent = MutableLiveData<Event<Int>>()
    val warningEvent: LiveData<Event<Int>> get() = _warningEvent

    private val _musicUIModels = MutableLiveData<List<MusicUIModel>>(emptyList())
    val musicUIModels: LiveData<List<MusicUIModel>> = _musicUIModels

    private val _busy = MutableLiveData<Boolean>(false)
    val busy: LiveData<Boolean> = _busy

    private val _selectedTrack = MutableLiveData<Track?>(null)
    val selectedTrack: LiveData<Track?> = _selectedTrack

    var latestQuery = ""
        private set

    init {
        prepareInitialOptions()
    }

    fun search(query: String) {
        if (query == latestQuery) return
        latestQuery = query
        if (query == "") {
            prepareInitialOptions()
            return
        }
        _busy.value = true
        viewModelScope.launch {
            val result = repository.search(query)
            if (result is Result.Error) {
                _warningEvent.value = Event(R.string.error_occurred)
                prepareInitialOptions()
                _busy.value = false
                return@launch
            }
            val searchResult = (result as Result.Success).data
            if (searchResult.isEmpty()) {
                prepareInitialOptions()
                _busy.value = false
                return@launch
            }
            _musicUIModels.value = createSearchList(searchResult)
            _busy.value = false
        }
    }

    private suspend fun createSearchList(searchResult: SearchResult): List<MusicUIModel> {
        val list = mutableListOf<MusicUIModel>()
        val search = searchResult.search
        if (!search.isTracksEmpty()) {
            val favResult = repository.getFavoriteTracks()
            val set: Set<String> = if (favResult is Result.Success) {
                favResult.data.map { it.id }.toSet()
            } else {
                emptySet()
            }
            search.data.tracks.forEach {
                it.favorite = set.contains(it.id)
            }
            list.add(MusicUIModel.SeparatorItem(app.getString(R.string.tracks)))
            for (track in search.data.tracks) {
                list.add(MusicUIModel.TrackItem(track))
            }
        }
        if (!search.isArtistsEmpty()) {
            list.add(MusicUIModel.SeparatorItem(app.getString(R.string.artists)))
            for (artist in search.data.artists) {
                list.add(MusicUIModel.ArtistItem(artist))
            }
        }
        if (!search.isPlaylistsEmpty()) {
            list.add(MusicUIModel.SeparatorItem(app.getString(R.string.playlists)))
            for (playlist in search.data.playlists) {
                list.add(MusicUIModel.PlaylistItem(playlist))
            }
        }
        if (!search.isAlbumsEmpty()) {
            list.add(MusicUIModel.SeparatorItem(app.getString(R.string.albums)))
            for (album in search.data.albums) {
                list.add(MusicUIModel.AlbumItem(album))
            }
        }
        return list
    }

    fun onMusicUIModelClicked(model: MusicUIModel) {
        when (model) {
            is MusicUIModel.OptionItem -> onOptionItemSelected(model.option)
            is MusicUIModel.ArtistItem ->
                _goToTracksPageEvent.value =
                    Event(Pair(TrackOptions.ARTIST_TRACKS, model.artist))
            is MusicUIModel.AlbumItem ->
                _goToTracksPageEvent.value =
                    Event(Pair(TrackOptions.ALBUM_TRACKS, model.album))
            is MusicUIModel.PlaylistItem ->
                _goToTracksPageEvent.value =
                    Event(Pair(TrackOptions.PLAYLIST_TRACKS, model.playlist))
            is MusicUIModel.TrackItem -> onTrackItemClicked(model.track)
            else -> {
                // TODO not supported
            }
        }
    }

    private fun onOptionItemSelected(options: MusicOptions) {
        when (options) {
            MusicOptions.FAVORITES ->
                _goToTracksPageEvent.value =
                    Event(Pair(TrackOptions.FAVORITE_TRACKS, null))
            MusicOptions.FEATURED ->
                _goToPlayListsPageEvent.value =
                    Event(NapsterService.FEATURED_TAG)
            MusicOptions.MOODS -> _goToPlayListsPageEvent.value = Event(NapsterService.MOODS_TAG)
            MusicOptions.GENRES -> _goToGenresPageEvent.value = Event(Unit)
            MusicOptions.ARTIST -> _goToArtistsPageEvent.value = Event(Unit)
            MusicOptions.ALBUMS -> _goToAlbumsPageEvent.value = Event(Unit)
            MusicOptions.TRACKS ->
                _goToTracksPageEvent.value =
                    Event(Pair(TrackOptions.TOP_TRACKS, null))
        }
    }

    private fun prepareInitialOptions() {
        val list = mutableListOf<MusicUIModel>()
        list.add(MusicUIModel.SeparatorItem(app.getString(R.string.library)))
        list.add(MusicUIModel.OptionItem(MusicOptions.FAVORITES))
        list.add(MusicUIModel.SeparatorItem(app.getString(R.string.explore)))
        list.add(MusicUIModel.OptionItem(MusicOptions.FEATURED))
        list.add(MusicUIModel.OptionItem(MusicOptions.MOODS))
        list.add(MusicUIModel.OptionItem(MusicOptions.GENRES))
        list.add(MusicUIModel.OptionItem(MusicOptions.ARTIST))
        list.add(MusicUIModel.OptionItem(MusicOptions.ALBUMS))
        list.add(MusicUIModel.OptionItem(MusicOptions.TRACKS))
        _musicUIModels.value = list
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

    private fun onTrackItemClicked(track: Track) {
        if (track.id == _selectedTrack.value?.id) {
            _selectedTrack.value = null
        } else {
            _selectedTrack.value = track
        }
    }
}
