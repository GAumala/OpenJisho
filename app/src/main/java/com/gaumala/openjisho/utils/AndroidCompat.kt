package com.gaumala.openjisho.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.widget.ProgressBar
import android.widget.TextView

fun ProgressBar.setProgressCompat(progress: Int, animated: Boolean) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        this.setProgress(progress, animated)
    else
        this.progress = progress
}

@Suppress("DEPRECATION")
fun TextView.setTextAppearanceCompat(ctx: Context, styleResId: Int) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        this.setTextAppearance(styleResId)
    else
        this.setTextAppearance(ctx, styleResId)
}

@Suppress("DEPRECATION")
fun notificationBuilderCompat(ctx: Context,
                              channelId: String): Notification.Builder {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        Notification.Builder(ctx, channelId)
    else
        Notification.Builder(ctx)

}

@Suppress("DEPRECATION")
fun Notification.Builder.setSoundCompat(sound: Uri?): Notification.Builder {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        this.setSound(sound)
    else
        this
}

fun getMediumPriorityConst(): Int =
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        Notification.PRIORITY_DEFAULT
    else
        NotificationManager.IMPORTANCE_DEFAULT

@Suppress("DEPRECATION")
fun Notification.Builder.setPriorityCompat(priority: Int): Notification.Builder {
    return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
        this.setPriority(priority)
    else
        this
}

fun Service.registerNotificationChannelCompat(
    channelId: String,
    sound: Uri?,
    audioAttributes: AudioAttributes?,
    priority: Int,
    name: String,
    description: String) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val mChannel = NotificationChannel(channelId, name, priority)
        mChannel.description = description
        mChannel.setSound(sound, audioAttributes)

        val notificationManager = this.getSystemService(Context.NOTIFICATION_SERVICE)
                as NotificationManager
        notificationManager.createNotificationChannel(mChannel)
    }
}
