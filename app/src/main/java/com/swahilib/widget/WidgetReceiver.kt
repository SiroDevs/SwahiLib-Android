package com.swahilib.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
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

class WidgetReceiver : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray,
    ) {
        appWidgetIds.forEach { id -> updateWidget(context, appWidgetManager, id) }
    }

    companion object {
        fun updateWidget(context: Context, manager: AppWidgetManager, widgetId: Int) {
            val options   = manager.getAppWidgetOptions(widgetId)
            val minHeight = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT, 40)
            val isLarge   = minHeight >= 110

            val targetRoute = if (isLarge) Routes.DAILY_PROVERB else Routes.DAILY_WORD

            val launchIntent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    putExtra(DeepLinkConstants.EXTRA_NAVIGATE_TO, targetRoute)
                }
            val pendingIntent = PendingIntent.getActivity(
                context, widgetId, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val db = AppDatabase.getInstanceForWidget(context)

            CoroutineScope(Dispatchers.IO).launch {
                val views = RemoteViews(context.packageName, R.layout.widget_large)
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                val daily = DailyContentManager.getOrCreateToday(
                    dailyContentDao = db.dailyContentDao(),
                    wordDao = db.wordsDao(),
                    proverbDao = db.proverbsDao(),
                )

                if (isLarge) {
                    val proverb = daily.proverb
                    views.setTextViewText(R.id.widget_label, "🌿 Methali ya Siku")
                    views.setTextViewText(R.id.widget_title, proverb?.title ?: "—")
                    views.setTextViewText(R.id.widget_meaning, daily.entity.proverbMeaning)
                } else {
                    val word = daily.word
                    views.setTextViewText(R.id.widget_label, "📖 Neno la Siku")
                    views.setTextViewText(R.id.widget_title, word?.title ?: "—")
                    views.setTextViewText(R.id.widget_meaning, daily.entity.wordMeaning.take(120))
                }

                manager.updateAppWidget(widgetId, views)
            }
        }
    }
}
