package com.meliksahcakir.spotialarm.music.data

import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import timber.log.Timber

class MusicRepository(private val napsterService: NapsterService) {
    suspend fun search(query: String): Result<SearchResult> {
        return try {
            val obj = napsterService.search(query)
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getTracks(option: TrackOptions, id: String = ""): Result<Tracks> {
        return try {
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

    suspend fun getPlaylists(tag: String): Result<Playlists> {
        return try {
            val obj = napsterService.getPlaylistsForTag(tag)
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getTopAlbums(): Result<Albums> {
        return try {
            val obj = napsterService.getTopAlbums()
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getTopArtists(): Result<Artists> {
        return try {
            val obj = napsterService.getTopArtists()
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getGenres(): Result<Genres> {
        return try {
            val obj = napsterService.getGenres()
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }
}
