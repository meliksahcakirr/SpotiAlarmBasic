package com.meliksahcakir.spotialarm.repository

import com.meliksahcakir.androidutils.Result
import com.meliksahcakir.spotialarm.music.api.NapsterService
import com.meliksahcakir.spotialarm.music.api.TrackOptions
import com.meliksahcakir.spotialarm.music.data.Albums
import com.meliksahcakir.spotialarm.music.data.Artists
import com.meliksahcakir.spotialarm.music.data.Genres
import com.meliksahcakir.spotialarm.music.data.Playlists
import com.meliksahcakir.spotialarm.music.data.SearchResult
import com.meliksahcakir.spotialarm.music.data.Track
import com.meliksahcakir.spotialarm.music.data.Tracks
import timber.log.Timber

class MusicRepository(private val napsterService: NapsterService, private val musicDao: MusicDao) {
    suspend fun search(query: String): Result<SearchResult> {
        return try {
            val obj = napsterService.search(query)
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    suspend fun getTrack(id: String = ""): Result<Track> {
        return try {
            var track = musicDao.getTrackById(id)
            if (track != null) {
                Result.Success(track)
            } else {
                track = napsterService.getTracks(id).list.firstOrNull()
                if (track != null) {
                    musicDao.insertTrack(track)
                    Result.Success(track)
                } else {
                    Result.Error(Exception("Track not found"))
                }
            }
        } catch (e: Exception) {
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
                TrackOptions.FAVORITE_TRACKS -> Tracks(musicDao.getFavoriteTracks())
            }
            val list: List<Track>
            if (option != TrackOptions.FAVORITE_TRACKS) {
                val saved = musicDao.getTracks()
                val set = saved.filter { it.favorite }.map { it.id }.toSet()
                list = obj.list.filter { it.streamable }
                list.forEach {
                    it.favorite = set.contains(it.id)
                }
            } else {
                list = obj.list
            }
            Result.Success(Tracks(list, obj.meta))
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

    suspend fun insertTrack(track: Track) {
        musicDao.insertTrack(track)
    }

    suspend fun deleteTrack(track: Track) {
        musicDao.deleteTrackById(track.id)
    }

    suspend fun getFavoriteTracks(): Result<List<Track>> {
        return try {
            val obj = musicDao.getFavoriteTracks()
            Result.Success(obj)
        } catch (e: Exception) {
            Timber.e(e)
            Result.Error(e)
        }
    }

    fun observeFavoriteTracks() = musicDao.observeFavoriteTracks()
}
