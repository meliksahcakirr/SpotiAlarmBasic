package com.meliksahcakir.spotialarm.music.api

import androidx.annotation.Keep
import com.meliksahcakir.spotialarm.music.data.Albums
import com.meliksahcakir.spotialarm.music.data.Artists
import com.meliksahcakir.spotialarm.music.data.Genres
import com.meliksahcakir.spotialarm.music.data.Playlists
import com.meliksahcakir.spotialarm.music.data.SearchResult
import com.meliksahcakir.spotialarm.music.data.Tracks
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

enum class ArtistImageSize(val value: String) {
    SIZE_70X47("70x47"),
    SIZE_150X100("150x100"),
    SIZE_356X237("356x237"),
    SIZE_633X422("633x422")
}

enum class AlbumImageSize(val value: String) {
    SIZE_70X70("70x70"),
    SIZE_170X170("170x170"),
    SIZE_200X200("200x200"),
    SIZE_300X300("300x300"),
    SIZE_500X500("500x500")
}

enum class PlaylistImageSize(val value: String) {
    SIZE_230X153("230x153"),
    SIZE_1200X400("1200x400"),
    SIZE_1800X600("1800x600")
}

enum class GenreImageSize(val value: String) {
    SIZE_161X64("161x64"),
    SIZE_240X160("240x160")
}

@Keep
enum class TrackOptions {
    TOP_TRACKS,
    ARTIST_TRACKS,
    ALBUM_TRACKS,
    GENRE_TRACKS,
    PLAYLIST_TRACKS,
    FAVORITE_TRACKS
}

interface NapsterService {

    @Headers("apikey: $API_KEY")
    @GET("v2.2/artists/top")
    suspend fun getTopArtists(): Artists

    @Headers("apikey: $API_KEY")
    @GET("v2.2/albums/top")
    suspend fun getTopAlbums(): Albums

    @Headers("apikey: $API_KEY")
    @GET("v2.2/tracks/top")
    suspend fun getTopTracks(): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/genres")
    suspend fun getGenres(): Genres

    @Headers("apikey: $API_KEY")
    @GET("v2.2/tracks/{id}")
    suspend fun getTracks(@Path("id") trackId: String): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/artists/{id}/tracks/top")
    suspend fun getArtistTopTracks(@Path("id") artistId: String): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/albums/{id}/tracks")
    suspend fun getAlbumTracks(@Path("id") albumId: String): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/genres/{id}/tracks/top")
    suspend fun getGenreTopTracks(@Path("id") genreId: String): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/playlists/{id}/tracks?limit=15")
    suspend fun getPlaylistTracks(@Path("id") playlistId: String): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/tags/{id}/playlists")
    suspend fun getPlaylistsForTag(@Path("id") tagId: String): Playlists

    @Headers("apikey: $API_KEY")
    @GET("v2.2/search?type=track,artist,playlist,album&per_type_limit=7")
    suspend fun search(@Query("query") query: String): SearchResult

    companion object {
        const val BASE_URL = "https://api.napster.com/"
        private const val API_KEY = "MGNjZGU0ODMtMmJjYi00Yjk0LTgwMDEtNjdkZGNjMTNlN2E5"
        const val SEARCH_LIMIT = 20
        const val MOODS_TAG = "tag.156763216"
        const val FEATURED_TAG = "tag.156763213"

        fun createArtistImageUrl(id: String, size: ArtistImageSize): String {
            return "${BASE_URL}imageserver/v2/artists/$id/images/${size.value}.jpg"
        }

        fun createAlbumImageUrl(id: String, size: AlbumImageSize): String {
            return "${BASE_URL}imageserver/v2/albums/$id/images/${size.value}.jpg"
        }

        fun createPlaylistImageUrl(id: String, size: PlaylistImageSize): String {
            return "${BASE_URL}imageserver/v2/playlists/$id/artists/images/${size.value}.jpg"
        }

        fun createGenreImageUrl(id: String, size: GenreImageSize): String {
            return "${BASE_URL}imageserver/images/$id/${size.value}.jpg"
        }
    }
}
