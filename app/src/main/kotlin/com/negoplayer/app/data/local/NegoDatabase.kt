/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: NegoDatabase.kt
 */

package com.negoplayer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * NegoPlayer Room Database.
 * Stores playback history, bookmarks, and stream history.
 *
 * Author: Muhammad Umar
 */
@Database(
    entities = [
        PlaybackHistoryEntity::class,
        BookmarkEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class NegoDatabase : RoomDatabase() {
    abstract fun playbackHistoryDao(): PlaybackHistoryDao
    abstract fun bookmarkDao(): BookmarkDao
}
