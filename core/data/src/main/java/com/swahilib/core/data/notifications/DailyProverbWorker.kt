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

import com.swahilib.core.common.utils.DeepLinkConstants
import com.swahilib.core.common.utils.NotifConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.data.repos.DailyContentRepo
import dagger.assisted.Assisted
import com.swahilib.core.common.R
import dagger.assisted.AssistedInject

@HiltWorker
class DailyProverbWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dailyContentRepo: DailyContentRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val (proverb, meaning) = dailyContentRepo.getDailyProverb()
        if (proverb == null) return Result.success()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotifConstants.CHANNEL_PROVERB_ID,
            NotifConstants.CHANNEL_PROVERB_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Methali mpya kila siku" }
        manager.createNotificationChannel(channel)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, Routes.DAILY_PROVERB)
            }
        val pendingIntent = PendingIntent.getActivity(
            context, NotifConstants.NOTIF_PROVERB_ID, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val displayMeaning = meaning.take(160)

        val notification = NotificationCompat.Builder(context, NotifConstants.CHANNEL_PROVERB_ID)
            .setSmallIcon(R.drawable.ic_swahilib_notification)
            .setContentTitle("🌿 Methali ya Siku")
            .setContentText(proverb.title)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle("🌿 ${proverb.title}")
                    .bigText(displayMeaning)
            )
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NotifConstants.NOTIF_PROVERB_ID, notification)
        return Result.success()
    }
}
