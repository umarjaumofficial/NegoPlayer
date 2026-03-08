package com.negoplayer.ui.common

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Gesture regions for the video player.
 * Left side: brightness, Right side: volume, Horizontal: seek
 */
enum class GestureRegion { LEFT, CENTER, RIGHT }

data class GestureState(
    val seekDelta: Long = 0L,           // Milliseconds
    val brightnessDelta: Float = 0f,    // -1.0 to 1.0
    val volumeDelta: Float = 0f,        // -1.0 to 1.0
    val isDoubleTapLeft: Boolean = false,
    val isDoubleTapRight: Boolean = false,
    val isSingleTap: Boolean = false
)

/**
 * Modifier extension that adds swipe gestures for:
 * - Horizontal swipe: seek forward/backward
 * - Left vertical swipe: brightness control
 * - Right vertical swipe: volume control
 * - Double tap left/right: seek ±10 seconds
 * - Single tap: toggle controls
 */
fun Modifier.playerGestures(
    screenWidth: Float,
    enabled: Boolean = true,
    onGesture: (GestureState) -> Unit
): Modifier {
    if (!enabled) return this

    return this
        .pointerInput(Unit) {
            detectTapGestures(
                onTap = { offset ->
                    onGesture(GestureState(isSingleTap = true))
                },
                onDoubleTap = { offset ->
                    val region = if (offset.x < screenWidth / 2f) GestureRegion.LEFT else GestureRegion.RIGHT
                    when (region) {
                        GestureRegion.LEFT -> onGesture(GestureState(isDoubleTapLeft = true))
                        GestureRegion.RIGHT -> onGesture(GestureState(isDoubleTapRight = true))
                        GestureRegion.CENTER -> {}
                    }
                }
            )
        }
        .pointerInput(Unit) {
            var startX = 0f
            detectHorizontalDragGestures(
                onDragStart = { offset -> startX = offset.x },
                onHorizontalDrag = { change, dragAmount ->
                    change.consume()
                    // 200dp swipe = ±60 seconds
                    val seekMs = (dragAmount / screenWidth * 120_000L).toLong()
                    onGesture(GestureState(seekDelta = seekMs))
                }
            )
        }
        .pointerInput(Unit) {
            var startX = 0f
            detectVerticalDragGestures(
                onDragStart = { offset -> startX = offset.x },
                onVerticalDrag = { change, dragAmount ->
                    change.consume()
                    // Normalize: full screen swipe = ±1.0
                    val normalized = -dragAmount / 1000f
                    val region = if (startX < screenWidth / 2f) GestureRegion.LEFT else GestureRegion.RIGHT
                    when (region) {
                        GestureRegion.LEFT -> onGesture(GestureState(brightnessDelta = normalized))
                        GestureRegion.RIGHT -> onGesture(GestureState(volumeDelta = normalized))
                        GestureRegion.CENTER -> {}
                    }
                }
            )
        }
}
