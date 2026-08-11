package com.swahilib.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.RemoteViews
import com.swahilib.R
import com.swahilib.core.common.utils.DeepLinkConstants
import com.swahilib.core.common.utils.Routes
import com.swahilib.core.database.AppDatabase
import com.swahilib.core.database.daily.DailyContentManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

enum class WidgetShape { WIDGET_COMPACT, WIDGET_WIDE, BOTH_COMPACT, BOTH_WIDE }

object WidgetContentRenderer {
    private const val WIDE_WIDTH_THRESHOLD_DP = 180
    private const val TALL_HEIGHT_THRESHOLD_DP = 75

    fun resolveShape(options: Bundle, supportsBoth: Boolean, fallback: WidgetShape): WidgetShape {
        val width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, -1)
        val height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, -1)
        if (width <= 0 || height <= 0) return fallback

        val isWide = width >= WIDE_WIDTH_THRESHOLD_DP
        val isTall = supportsBoth && height >= TALL_HEIGHT_THRESHOLD_DP

        return when {
            isWide && isTall -> WidgetShape.BOTH_WIDE
            isWide -> WidgetShape.WIDGET_WIDE
            isTall -> WidgetShape.BOTH_COMPACT
            else -> WidgetShape.WIDGET_COMPACT
        }
    }

    fun render(context: Context, manager: AppWidgetManager, widgetId: Int, supportsBoth: Boolean) {
        val fallback = if (supportsBoth) WidgetShape.BOTH_WIDE else WidgetShape.WIDGET_WIDE
        val shape = resolveShape(manager.getAppWidgetOptions(widgetId), supportsBoth, fallback)
        render(context, manager, widgetId, shape)
    }

    fun render(context: Context, manager: AppWidgetManager, widgetId: Int, shape: WidgetShape) {
        val layoutRes = when (shape) {
            WidgetShape.WIDGET_COMPACT -> R.layout.widget_small_compact
            WidgetShape.WIDGET_WIDE -> R.layout.widget_small_wide
            WidgetShape.BOTH_COMPACT -> R.layout.widget_both_compact
            WidgetShape.BOTH_WIDE -> R.layout.widget_both_wide
        }

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

            val daily = DailyContentManager.getOrCreateToday(
                dailyContentDao = db.dailyContentDao(),
                wordDao = db.wordsDao(),
                proverbDao = db.proverbsDao(),
            )

            val word = daily.word
            views.setTextViewText(R.id.widget_title, word?.title ?: "—")

            when (shape) {
                WidgetShape.WIDGET_COMPACT -> {
                    views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
                }
                WidgetShape.WIDGET_WIDE -> {
                    views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
                    views.setTextViewText(R.id.widget_meaning, daily.entity.wordMeaning)
                }
                WidgetShape.BOTH_COMPACT -> {
                    views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
                    views.setTextViewText(R.id.widget_proverb, daily.proverb?.title ?: "—")
                }
                WidgetShape.BOTH_WIDE -> {
                    views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
                    views.setTextViewText(R.id.widget_meaning, daily.entity.wordMeaning)
                    views.setTextViewText(R.id.widget_proverb, daily.proverb?.title ?: "—")
                }
            }

            manager.updateAppWidget(widgetId, views)
        }
    }
}
