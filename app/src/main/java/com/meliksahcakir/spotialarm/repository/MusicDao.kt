package com.meliksahcakir.spotialarm.repository

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.meliksahcakir.spotialarm.music.data.Track

@Dao
interface MusicDao {
    @Query("SELECT * FROM tracks")
    fun observeTracks(): LiveData<List<Track>>

    @Query("SELECT * FROM tracks")
    suspend fun getTracks(): List<Track>

    @Query("SELECT * FROM tracks WHERE id = :trackId")
    suspend fun getTrackById(trackId: String): Track?

    @Query("SELECT * FROM tracks WHERE favorite = 1")
    suspend fun getFavoriteTracks(): List<Track>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track)

    @Update
    suspend fun updateTrack(track: Track)

    @Delete
    suspend fun deleteTrack(track: Track)

    @Query("DELETE FROM tracks WHERE id = :trackId")
    suspend fun deleteTrackById(trackId: String)

    @Query("UPDATE tracks SET favorite = :favorite WHERE id = :trackId")
    suspend fun updateTrackFavoriteStatus(trackId: String, favorite: Boolean)

    @Transaction
    suspend fun insertOrUpdate(track: Track) {
        val existingTrack = try {
            getTrackById(track.id)
        } catch (e: Exception) {
            null
        }
        if (existingTrack == null) {
            insertTrack(track)
        } else {
            updateTrack(track)
        }
    }
}
