package com.gaumala.openjisho.backend.setup

import android.app.*
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.graphics.BitmapFactory
import com.gaumala.openjisho.MainActivity
import com.gaumala.openjisho.R
import com.gaumala.openjisho.common.SetupStep
import com.gaumala.openjisho.utils.*

/**
 * Helper class to run a service in the Foreground so that
 * it is not killed by the OS and it can post progress in
 * a continuous notification.
 */
class ForegroundManager(private val service: Service) {

    companion object {
        private const val notificationId = 7456
        private const val channelId = "SetupProgressChannel"
        private val priority = getMediumPriorityConst()
    }

    private var hasPermissions = true

    init {
        val name = service.getString(R.string.setup_progress)
        val descriptionText =
            service.getString(R.string.setup_channel_desc)
        service.registerNotificationChannelCompat(
            channelId = channelId,
            priority = priority,
            description = descriptionText,
            name = name,
            sound = null,
            audioAttributes = null)
    }

    private fun createNotification(step: SetupStep,
                                   progress: Int): Notification {
        val pendingIntent: PendingIntent =
            Intent(service, MainActivity::class.java)
                .let { notificationIntent ->
                    PendingIntent.getActivity(
                        service,
                        0,
                        notificationIntent,
                        0)
                }
        val largeIconBitmap = BitmapFactory.decodeResource(
            service.resources, R.drawable.notification_large_icon)

        return notificationBuilderCompat(service, channelId)
            .setContentTitle(service.getText(R.string.running_setup))
            .setContentText(step.toString(service))
            .setContentIntent(pendingIntent)
            .setSmallIcon(R.drawable.setup_status_bar_icon_24)
            .setLargeIcon(largeIconBitmap)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setProgress(100, progress, progress < 0)
            .setSoundCompat(null)
            .setPriorityCompat(priority)
            .build()
    }

    fun updateNotification(step: SetupStep,
                           progress: Int) {
        if (hasPermissions) {
            val notificationManager: NotificationManager =
                service.getSystemService(NOTIFICATION_SERVICE)
                        as NotificationManager
            val notification = createNotification(step, progress)
            notificationManager.notify(notificationId, notification)
        }
    }

    fun start() {
        val notification = createNotification(
            SetupStep.initializing, 0)
        try {
            service.startForeground(notificationId, notification)
            hasPermissions = true
        } catch (ex: SecurityException) {
            hasPermissions = false
        }
    }

    fun stop() {
        if (hasPermissions)
            service.stopForeground(true)
    }

}