package com.meliksahcakir.spotialarm.music.data

import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class MusicRepository(
    private val napsterService: NapsterService,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {
    suspend fun search(query: String): Result<SearchResult> = withContext(ioDispatcher) {
        return@withContext try {
            val obj = napsterService.search(query)
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getTracks(option: TrackOptions, id: String = ""): Result<Tracks> =
        withContext(ioDispatcher) {
            return@withContext try {
                val obj = when (option) {
                    TrackOptions.TOP_TRACKS -> napsterService.getTopTracks()
                    TrackOptions.ARTIST_TRACKS -> napsterService.getArtistTopTracks(id)
                    TrackOptions.ALBUM_TRACKS -> napsterService.getAlbumTracks(id)
                    TrackOptions.GENRE_TRACKS -> napsterService.getGenreTopTracks(id)
                    TrackOptions.PLAYLIST_TRACKS -> napsterService.getPlaylistTracks(id)
                }
                Result.Success(obj)
            } catch (e: Exception) {
                Timber.e(e)
                Result.Error(e)
            }
        }
}
