package com.negoplayer

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.negoplayer.utils.Constants

/**
 * Application class for NegoPlayer.
 * Initializes app-wide resources including notification channels
 * and the global player manager.
 */
class NegoPlayerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        instance = this
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Media playback channel
            val playbackChannel = NotificationChannel(
                Constants.PLAYBACK_NOTIFICATION_CHANNEL_ID,
                "Media Playback",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Controls for media playback"
                setShowBadge(false)
            }
            notificationManager.createNotificationChannel(playbackChannel)
        }
    }

    companion object {
        lateinit var instance: NegoPlayerApplication
            private set
    }
}
