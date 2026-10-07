package com.example.mediaplayer.data

import android.net.Uri

data class VideoItem(
    val id: Long,
    val uri: Uri,
    val displayName: String,
    val durationMs: Long,
    val sizeBytes: Long,
    val dateAddedSec: Long
)

data class VideoFolder(
    val name: String,
    val path: String,
    val dateAddedSec: Long,
    val videos: List<VideoItem>
)
