package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

/**
 * Refreshes every placed SwahiLib widget instance from the same
 * [com.swahilib.core.database.daily.DailyContentManager] row the notifications
 * and Daily Word / Daily Proverb screens read from.
 *
 * This exists because [android.appwidget.AppWidgetProvider]'s own
 * `updatePeriodMillis` is unreliable for "exactly at day rollover" timing —
 * it's anchored to whenever the widget was placed (not wall-clock time), gets
 * rounded up by the OS, and is commonly deferred for hours under battery
 * optimization/Doze. Scheduling this via WorkManager for a fixed 1:00 AM slot
 * (see [WidgetScheduler]) is what keeps the widget in sync with the
 * notification content instead of lagging behind it.
 */
class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {

    // No Hilt injection needed here - WidgetContentRenderer.render() already
    // opens its own DB handle and is safe to call directly.
    private val sizesByReceiver = mapOf(
        SwahiLibWidgetSmallReceiver::class.java to WidgetSize.SMALL,
        SwahiLibWidgetMediumReceiver::class.java to WidgetSize.MEDIUM,
        SwahiLibWidgetLargeReceiver::class.java to WidgetSize.LARGE,
    )

    override suspend fun doWork(): Result {
        val manager = AppWidgetManager.getInstance(applicationContext)
        sizesByReceiver.forEach { (receiverClass, size) ->
            val ids = manager.getAppWidgetIds(ComponentName(applicationContext, receiverClass))
            ids.forEach { id -> WidgetContentRenderer.render(applicationContext, manager, id, size) }
        }
        return Result.success()
    }
}
