package com.example.mediaplayer.data

import android.content.ContentUris
import android.content.Context
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object MediaScanner {

    suspend fun scanVideos(context: Context): List<VideoFolder> = withContext(Dispatchers.IO) {
        val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            MediaStore.Video.Media._ID,
            MediaStore.Video.Media.DISPLAY_NAME,
            MediaStore.Video.Media.DURATION,
            MediaStore.Video.Media.SIZE,
            MediaStore.Video.Media.DATE_ADDED,
            MediaStore.Video.Media.DATA
        )
        val items = mutableListOf<Pair<String, VideoItem>>()

        context.contentResolver.query(
            collection, projection, null, null,
            "${MediaStore.Video.Media.DATE_ADDED} DESC"
        )?.use { c ->
            val idCol = c.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val nameCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
            val durCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION)
            val sizeCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)
            val dateCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATE_ADDED)
            val dataCol = c.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)

            while (c.moveToNext()) {
                val id = c.getLong(idCol)
                val uri = ContentUris.withAppendedId(collection, id)
                val data = c.getString(dataCol) ?: continue
                val parent = File(data).parent ?: "Unknown"
                items += parent to VideoItem(
                    id = id,
                    uri = uri,
                    displayName = c.getString(nameCol) ?: "Unknown",
                    durationMs = c.getLong(durCol),
                    sizeBytes = c.getLong(sizeCol),
                    dateAddedSec = c.getLong(dateCol)
                )
            }
        }

        items.groupBy { it.first }
            .map { (path, list) ->
                VideoFolder(
                    name = File(path).name.ifEmpty { path },
                    path = path,
                    dateAddedSec = list.maxOf { it.second.dateAddedSec },
                    videos = list.map { it.second }
                        .sortedByDescending { it.dateAddedSec }
                )
            }
            .sortedByDescending { it.dateAddedSec }
    }
}
