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

/**
 * The four "shapes" a Neno-la-Siku widget can render as. [NENO_COMPACT] and
 * [NENO_WIDE] are word-only; [BOTH_COMPACT] and [BOTH_WIDE] add the Methali
 * ya Siku row and are only ever picked for the "full" widget family.
 */
enum class WidgetShape { NENO_COMPACT, NENO_WIDE, BOTH_COMPACT, BOTH_WIDE }

/**
 * Single place that knows how to fill in a "Neno la Siku" widget, shared by
 * [WidgetSmallReceiver] (word-only, resizable 1x2 <-> 1x4) and
 * [WidgetFullReceiver] (word + proverb, resizable across all four
 * shapes) so the two families never drift out of sync on data source, tap
 * target, or refresh timing - only which shapes are reachable differs.
 */
object WidgetContentRenderer {

    // Cell-size midpoints (dp) used to decide which layout best fits the
    // widget's current footprint - halfway between the 2-cell (110dp) and
    // 4-cell (250dp) widths, and the 1-row (40dp) and 2-row (110dp) heights.
    private const val WIDE_WIDTH_THRESHOLD_DP = 180
    private const val TALL_HEIGHT_THRESHOLD_DP = 75

    /**
     * Reads the widget's current allotted size and picks the best-fit shape.
     * [supportsBoth] gates whether a "tall" reading is even allowed to
     * request the Methali row (false for the Neno-only family, which is
     * never resized vertically in the first place).
     *
     * Falls back to [fallback] when the options bundle isn't populated yet
     * (e.g. right at placement, before the first onAppWidgetOptionsChanged),
     * so a freshly-placed widget doesn't briefly flash its smallest shape.
     */
    fun resolveShape(options: Bundle, supportsBoth: Boolean, fallback: WidgetShape): WidgetShape {
        val width = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH, -1)
        val height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, -1)
        if (width <= 0 || height <= 0) return fallback

        val isWide = width >= WIDE_WIDTH_THRESHOLD_DP
        val isTall = supportsBoth && height >= TALL_HEIGHT_THRESHOLD_DP

        return when {
            isWide && isTall -> WidgetShape.BOTH_WIDE
            isWide -> WidgetShape.NENO_WIDE
            isTall -> WidgetShape.BOTH_COMPACT
            else -> WidgetShape.NENO_COMPACT
        }
    }

    /** Convenience overload that reads the widget's current options itself. */
    fun render(context: Context, manager: AppWidgetManager, widgetId: Int, supportsBoth: Boolean) {
        val fallback = if (supportsBoth) WidgetShape.BOTH_WIDE else WidgetShape.NENO_WIDE
        val shape = resolveShape(manager.getAppWidgetOptions(widgetId), supportsBoth, fallback)
        render(context, manager, widgetId, shape)
    }

    fun render(context: Context, manager: AppWidgetManager, widgetId: Int, shape: WidgetShape) {
        val layoutRes = when (shape) {
            WidgetShape.NENO_COMPACT -> R.layout.widget_small_compact
            WidgetShape.NENO_WIDE -> R.layout.widget_small_wide
            WidgetShape.BOTH_COMPACT -> R.layout.widget_both_compact
            WidgetShape.BOTH_WIDE -> R.layout.widget_both_wide
        }

        // All shapes are "Neno la Siku" specific - they always open the
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
            // "Neno la Siku" / "Methali ya Siku" screens — generated once
            // per day and re-used everywhere, so every shape always agrees.
            val daily = DailyContentManager.getOrCreateToday(
                dailyContentDao = db.dailyContentDao(),
                wordDao = db.wordsDao(),
                proverbDao = db.proverbsDao(),
            )

            val word = daily.word
            views.setTextViewText(R.id.widget_title, word?.title ?: "—")

            when (shape) {
                WidgetShape.NENO_COMPACT -> {
                    views.setTextViewText(R.id.widget_english, word?.english?.trim().orEmpty())
                }
                WidgetShape.NENO_WIDE -> {
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
