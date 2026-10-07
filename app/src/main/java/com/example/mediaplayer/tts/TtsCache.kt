package com.example.mediaplayer.tts

import java.security.MessageDigest

object TtsCache {
    fun cacheKey(videoUri: String, subtitleUri: String): String {
        val md = MessageDigest.getInstance("SHA-1")
        md.update(videoUri.toByteArray())
        md.update(subtitleUri.toByteArray())
        return md.digest().joinToString("") { "%02x".format(it) }
    }
}
