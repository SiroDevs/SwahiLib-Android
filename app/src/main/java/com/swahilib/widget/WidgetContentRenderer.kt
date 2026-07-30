package com.swahilib.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.swahilib.R
import com.swahilib.core.common.utils.DeepLinkConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.AppDatabase
import com.swahilib.core.database.daily.DailyContentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/** Which of the three "Neno la Siku" widget layouts is being rendered. */
enum class WidgetSize { SMALL, MEDIUM, LARGE }

/**
 * Single place that knows how to fill in a "Neno la Siku" widget, shared by
 * [SwahiLibWidgetSmallReceiver], [SwahiLibWidgetMediumReceiver], and
 * [SwahiLibWidgetLargeReceiver] so the three sizes never drift out of sync on
 * data source, tap target, or refresh timing - only the layout/fields differ.
 */
object WidgetContentRenderer {

    fun render(context: Context, manager: AppWidgetManager, widgetId: Int, size: WidgetSize) {
        val layoutRes = when (size) {
            WidgetSize.SMALL -> R.layout.widget_swahilib_small
            WidgetSize.MEDIUM -> R.layout.widget_swahilib_medium
            WidgetSize.LARGE -> R.layout.widget_swahilib_large
        }

        // All three sizes are "Neno la Siku" specific - they always open the
        // Daily Word screen, not just Home.
        val launchIntent = context.packageManager
            .getLaunchIntentForPackage(context.packageName)
            ?.apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, Routes.DAILY_WORD)
            }
        val pendingIntent = PendingIntent.getActivity(
            context, widgetId, launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        val db = AppDatabase.getInstanceForWidget(context)

        CoroutineScope(Dispatchers.IO).launch {
            val views = RemoteViews(context.packageName, layoutRes)
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // Same source of truth as the notifications and the in-app
            // "Neno la Siku" screen — generated once per day and re-used
            // everywhere, so all three widget sizes always agree.
            val daily = DailyContentManager.getOrCreateToday(
                dailyContentDao = db.dailyContentDao(),
                wordDao = db.wordsDao(),
                proverbDao = db.proverbsDao(),
            )

            val word = daily.word
            views.setTextViewText(R.id.widget_title, word?.title ?: "—")

            if (size == WidgetSize.MEDIUM || size == WidgetSize.LARGE) {
                views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
            }
            if (size == WidgetSize.LARGE) {
                views.setTextViewText(R.id.widget_meaning, daily.entity.wordMeaning)
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
