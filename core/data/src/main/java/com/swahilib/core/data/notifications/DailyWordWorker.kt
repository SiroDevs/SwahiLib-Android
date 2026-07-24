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
import com.swahilib.core.data.repos.PrefsRepo
import dagger.assisted.Assisted
import com.swahilib.core.common.R
import dagger.assisted.AssistedInject

@HiltWorker
class DailyWordWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val dailyContentRepo: DailyContentRepo,
    private val prefsRepo: PrefsRepo,
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val (word, meaning) = dailyContentRepo.getDailyWord()
        if (word == null) return Result.success()

        val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channel = NotificationChannel(
            NotifConstants.CHANNEL_WORD_ID,
            NotifConstants.CHANNEL_WORD_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply { description = "Neno jipya kila siku" }
        manager.createNotificationChannel(channel)

        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, Routes.DAILY_WORD)
            }
        val pendingIntent = PendingIntent.getActivity(
            context, NotifConstants.NOTIF_WORD_ID, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val displayMeaning = meaning.take(120)

        // Loss aversion nudge: a live streak is a much stronger reason to open
        // the app than the word itself, so lead the expanded body with it.
        val streak = prefsRepo.currentStreak
        val bigText = if (streak > 1) {
            "$displayMeaning\n\n🔥 Usivunje mfuatano wako wa siku $streak!"
        } else {
            displayMeaning
        }

        val notification = NotificationCompat.Builder(context, NotifConstants.CHANNEL_WORD_ID)
            .setSmallIcon(R.drawable.ic_swahilib_notification)
            .setContentTitle("📖 Neno la Siku: ${word.title}")
            .setContentText(displayMeaning)
            .setStyle(NotificationCompat.BigTextStyle().bigText(bigText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        manager.notify(NotifConstants.NOTIF_WORD_ID, notification)
        return Result.success()
    }
}
