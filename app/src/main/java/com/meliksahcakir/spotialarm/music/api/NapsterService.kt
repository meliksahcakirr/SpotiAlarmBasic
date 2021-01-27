package com.meliksahcakir.spotialarm.music.api

import com.meliksahcakir.spotialarm.music.data.Albums
import com.meliksahcakir.spotialarm.music.data.Artists
import com.meliksahcakir.spotialarm.music.data.Playlists
import com.meliksahcakir.spotialarm.music.data.SearchResult
import com.meliksahcakir.spotialarm.music.data.Tracks
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

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
    @GET("v2.2/artists/{id}/tracks/top")
    suspend fun getArtistTopTracks(
        @Path("id") artistId: String
    ): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/albums/{id}/tracks")
    suspend fun getAlbumTracks(
        @Path("id") albumId: String
    ): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/genres/{id}/tracks/top")
    suspend fun getGenreTopTracks(
        @Path("id") genreId: String
    ): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/genres/{id}/tracks/top")
    suspend fun getPlaylistTracks(
        @Path("id") playlistId: String
    ): Tracks

    @Headers("apikey: $API_KEY")
    @GET("v2.2/tags/{id}/tracks/playlists")
    suspend fun getPlaylistsForTag(
        @Path("id") tagId: String
    ): Playlists

    @Headers("apikey: $API_KEY")
    @GET("v2.2/tags/search?type=track,artist,playlist&per_type_limit=8")
    suspend fun search(
        @Query("query") query: String
    ): SearchResult

    companion object {
        private const val BASE_URL = "https://api.napster.com/"
        private const val API_KEY = "MGNjZGU0ODMtMmJjYi00Yjk0LTgwMDEtNjdkZGNjMTNlN2E5"
        const val SEARCH_LIMIT = 20
        const val MOODS_TAG = "tag.156763216"
        const val FEATURED_TAG = "tag.156763213"

        fun create(): NapsterService {
            val logger = HttpLoggingInterceptor()
            logger.level = HttpLoggingInterceptor.Level.BASIC

            val client = OkHttpClient.Builder()
                .addInterceptor(logger).build()
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(NapsterService::class.java)
        }
    }
}
