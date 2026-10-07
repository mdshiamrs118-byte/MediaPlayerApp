package com.example.mediaplayer.data

import kotlinx.coroutines.flow.Flow

class TtsAudioRepository(private val dao: TtsAudioDao) {
    fun observeAll(): Flow<List<TtsAudioEntity>> = dao.observeAll()
    suspend fun findByKey(key: String): TtsAudioEntity? = dao.findByKey(key)
    suspend fun insert(e: TtsAudioEntity) = dao.insert(e)
    suspend fun delete(e: TtsAudioEntity) = dao.delete(e)
}
