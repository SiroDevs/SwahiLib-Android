package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class WidgetRefreshWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    private val supportsBothByReceiver = mapOf(
        WidgetSmallReceiver::class.java to false,
        WidgetFullReceiver::class.java to true,
    )

    override suspend fun doWork(): Result {
        val manager = AppWidgetManager.getInstance(applicationContext)
        supportsBothByReceiver.forEach { (receiverClass, supportsBoth) ->
            val ids = manager.getAppWidgetIds(ComponentName(applicationContext, receiverClass))
            ids.forEach { id ->
                WidgetContentRenderer.render(applicationContext, manager, id, supportsBoth)
            }
        }
        return Result.success()
    }
}
