package com.example.lifelinepro.presentation.comman

fun formatDurationVideo(durationMs: Int): String {
    val totalSeconds = durationMs / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds) // hh:mm:ss
    } else {
        String.format("%02d:%02d", minutes, seconds) // mm:ss
    }
}
fun formatDurationAudio(durationMs: Long): String {
    val totalSeconds = durationMs / 1000
    val seconds = totalSeconds % 60
    val minutes = (totalSeconds / 60) % 60
    val hours = totalSeconds / 3600

    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds) // hh:mm:ss
    } else {
        String.format("%02d:%02d", minutes, seconds) // mm:ss
    }
}
