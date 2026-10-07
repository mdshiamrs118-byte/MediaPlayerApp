package com.example.mediaplayer.tts

import android.content.Context
import android.net.Uri
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.Locale

object SubtitleParser {

    fun parse(context: Context, uri: Uri): List<SubtitleCue> {
        val name = uri.lastPathSegment?.lowercase(Locale.ROOT) ?: ""
        val text = context.contentResolver.openInputStream(uri)?.use { input ->
            BufferedReader(InputStreamReader(input, Charsets.UTF_8)).readText()
        } ?: return emptyList()

        return when {
            name.endsWith(".vtt") -> parseVtt(text)
            name.endsWith(".ass") || name.endsWith(".ssa") -> parseAss(text)
            else -> parseSrt(text)
        }.sortedBy { it.startMs }
    }

    private fun parseSrt(text: String): List<SubtitleCue> {
        val cues = mutableListOf<SubtitleCue>()
        val blocks = text.replace("\r\n", "\n").split(Regex("\n\\s*\n"))
        for (b in blocks) {
            val lines = b.trim().lines()
            if (lines.size < 2) continue
            val timeLine = lines.firstOrNull { it.contains("-->") } ?: continue
            val (s, e) = parseTimes(timeLine) ?: continue
            val body = lines.dropWhile { !it.contains("-->") }
                .drop(1).joinToString(" ").trim()
            if (body.isNotEmpty()) cues += SubtitleCue(s, e, body)
        }
        return cues
    }

    private fun parseVtt(text: String): List<SubtitleCue> {
        val cues = mutableListOf<SubtitleCue>()
        val lines = text.replace("\r\n", "\n").lines()
        var i = 0
        while (i < lines.size) {
            val line = lines[i]
            if (line.contains("-->")) {
                val (s, e) = parseTimes(line) ?: run { i++; continue }
                val sb = StringBuilder()
                i++
                while (i < lines.size && lines[i].isNotBlank() && !lines[i].contains("-->")) {
                    sb.append(lines[i]).append(" ")
                    i++
                }
                val body = sb.toString().trim()
                if (body.isNotEmpty()) cues += SubtitleCue(s, e, body)
            } else i++
        }
        return cues
    }

    private fun parseAss(text: String): List<SubtitleCue> {
        val cues = mutableListOf<SubtitleCue>()
        for (line in text.replace("\r\n", "\n").lines()) {
            if (!line.startsWith("Dialogue:")) continue
            val parts = line.substringAfter("Dialogue:").split(",", limit = 10)
            if (parts.size < 10) continue
            val s = parseAssTime(parts[1].trim()) ?: continue
            val e = parseAssTime(parts[2].trim()) ?: continue
            val body = parts[9]
                .replace(Regex("\\{[^}]*\\}"), "")
                .replace("\\N", " ")
                .replace("\\n", " ")
                .trim()
            if (body.isNotEmpty()) cues += SubtitleCue(s, e, body)
        }
        return cues
    }

    private fun parseTimes(line: String): Pair<Long, Long>? {
        val parts = line.split("-->")
        if (parts.size < 2) return null
        val s = parseTime(parts[0].trim()) ?: return null
        val e = parseTime(parts[1].trim().split(" ").first()) ?: return null
        return s to e
    }

    private fun parseTime(t: String): Long? {
        return try {
            val clean = t.replace(',', '.')
            val segs = clean.split(":")
            val h: Int; val m: Int; val sMs: Double
            when (segs.size) {
                3 -> { h = segs[0].toInt(); m = segs[1].toInt(); sMs = segs[2].toDouble() }
                2 -> { h = 0; m = segs[0].toInt(); sMs = segs[1].toDouble() }
                else -> return null
            }
            ((h * 3600 + m * 60) * 1000 + (sMs * 1000)).toLong()
        } catch (_: Exception) { null }
    }

    private fun parseAssTime(t: String): Long? {
        return try {
            val segs = t.split(":")
            if (segs.size < 3) return null
            val h = segs[0].toInt()
            val m = segs[1].toInt()
            val s = segs[2].toDouble()
            ((h * 3600 + m * 60) * 1000 + (s * 1000)).toLong()
        } catch (_: Exception) { null }
    }
}
