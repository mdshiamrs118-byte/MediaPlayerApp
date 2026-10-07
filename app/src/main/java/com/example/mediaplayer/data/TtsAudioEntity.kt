package com.example.mediaplayer.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tts_audio")
data class TtsAudioEntity(
    @PrimaryKey val cacheKey: String,
    val videoTitle: String,
    val videoUri: String,
    val subtitleName: String,
    val subtitleUri: String,
    val filePath: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val generatedAt: Long
)
