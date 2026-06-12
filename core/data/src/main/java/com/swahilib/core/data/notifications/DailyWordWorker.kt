package com.swahilib.core.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.data.repos.WordRepo
import dagger.assisted.Assisted
import com.swahilib.core.common.R
import dagger.assisted.AssistedInject

@HiltWorker
class DailyWordWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val wordRepo: WordRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val word = wordRepo.getRandomWord() ?: return Result.success()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotifConstants.CHANNEL_WORD_ID,
            NotifConstants.CHANNEL_WORD_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Neno jipya kila siku" }
        manager.createNotificationChannel(channel)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pendingIntent = PendingIntent.getActivity(
            context, NotifConstants.NOTIF_WORD_ID, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val meaning = word.meaning
            ?.split("|")?.firstOrNull()?.trim()
            ?.take(120) ?: ""

        val notification = NotificationCompat.Builder(context, NotifConstants.CHANNEL_WORD_ID)
            .setSmallIcon(R.drawable.ic_swahilib_notification)
            .setContentTitle("📖 Neno la Siku: ${word.title}")
            .setContentText(meaning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(meaning))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NotifConstants.NOTIF_WORD_ID, notification)
        return Result.success()
    }
}
