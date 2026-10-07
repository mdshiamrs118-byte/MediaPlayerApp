package com.example.mediaplayer.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface TtsAudioDao {
    @Query("SELECT * FROM tts_audio ORDER BY generatedAt DESC")
    fun observeAll(): Flow<List<TtsAudioEntity>>

    @Query("SELECT * FROM tts_audio WHERE cacheKey = :key LIMIT 1")
    suspend fun findByKey(key: String): TtsAudioEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TtsAudioEntity)

    @Delete
    suspend fun delete(entity: TtsAudioEntity)
}
