package com.example.mediaplayer.tts

data class SubtitleCue(
    val startMs: Long,
    val endMs: Long,
    val text: String
)
