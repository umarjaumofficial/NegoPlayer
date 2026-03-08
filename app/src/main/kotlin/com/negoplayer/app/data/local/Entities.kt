/*
 * NegoPlayer - Advanced Android Media Platform
 * Author: Muhammad Umar
 * Project: NegoPlayer
 * File: Entities.kt & DAOs
 */

package com.negoplayer.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

// ============================================================
// ENTITIES
// ============================================================

/**
 * Stores playback history entries.
 */
@Entity(tableName = "playback_history")
data class PlaybackHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val uri: String,
    val title: String,
    val thumbnailUri: String?,
    val duration: Long,
    val lastPosition: Long = 0L,
    val lastPlayedAt: Long = System.currentTimeMillis()
)

/**
 * Stores user bookmarks for specific timestamps in media.
 */
@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val mediaUri: String,
    val mediaTitle: String,
    val position: Long,
    val label: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

// ============================================================
// DAOs
// ============================================================

@Dao
interface PlaybackHistoryDao {

    @Query("SELECT * FROM playback_history ORDER BY lastPlayedAt DESC LIMIT 50")
    fun getHistory(): Flow<List<PlaybackHistoryEntity>>

    @Query("SELECT * FROM playback_history WHERE uri = :uri LIMIT 1")
    suspend fun getByUri(uri: String): PlaybackHistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: PlaybackHistoryEntity)

    @Query("UPDATE playback_history SET lastPosition = :position, lastPlayedAt = :timestamp WHERE uri = :uri")
    suspend fun updatePosition(uri: String, position: Long, timestamp: Long)

    @Delete
    suspend fun delete(entity: PlaybackHistoryEntity)

    @Query("DELETE FROM playback_history")
    suspend fun clearAll()
}

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarks WHERE mediaUri = :uri ORDER BY position ASC")
    fun getBookmarksForMedia(uri: String): Flow<List<BookmarkEntity>>

    @Query("SELECT * FROM bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: BookmarkEntity)

    @Delete
    suspend fun delete(entity: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE mediaUri = :uri")
    suspend fun deleteForMedia(uri: String)
}
