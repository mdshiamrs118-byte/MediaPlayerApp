package com.example.mediaplayer

import android.app.Application
import com.example.mediaplayer.data.AppDatabase
import com.example.mediaplayer.data.TtsAudioRepository

class MediaPlayerApp : Application() {
    val database: AppDatabase by lazy { AppDatabase.get(this) }
    val ttsRepository: TtsAudioRepository by lazy {
        TtsAudioRepository(database.ttsAudioDao())
    }
}
