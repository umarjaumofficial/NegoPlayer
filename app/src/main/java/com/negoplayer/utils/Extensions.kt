package com.negoplayer.utils

import android.content.Context
import android.content.res.Resources
import android.net.Uri
import android.text.format.Formatter
import android.util.TypedValue
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Extension functions used throughout NegoPlayer.
 */

// Duration formatting
fun Long.toReadableDuration(): String {
    val hours = TimeUnit.MILLISECONDS.toHours(this)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(this) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(this) % 60
    return if (hours > 0) {
        String.format("%d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}

// File size formatting
fun Long.toReadableSize(context: Context): String =
    Formatter.formatFileSize(context, this)

// File extension
fun String.extension(): String = substringAfterLast('.', "").lowercase()

fun Uri.extension(): String = toString().substringAfterLast('.', "").lowercase()

fun File.isVideoFile(): Boolean = extension.lowercase() in Constants.VIDEO_EXTENSIONS

fun File.isAudioFile(): Boolean = extension.lowercase() in Constants.AUDIO_EXTENSIONS

// DP to pixels
val Int.dp: Int get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
).toInt()

val Float.dp: Float get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_DIP, this, Resources.getSystem().displayMetrics
)

// SP to pixels
val Int.sp: Float get() = TypedValue.applyDimension(
    TypedValue.COMPLEX_UNIT_SP, this.toFloat(), Resources.getSystem().displayMetrics
)

// Clamp a value between min and max
fun Float.clamp(min: Float, max: Float): Float = this.coerceIn(min, max)

// Percentage calculation
fun Long.toPercent(total: Long): Float =
    if (total <= 0L) 0f else (this.toFloat() / total.toFloat()).clamp(0f, 1f)
