package com.swahilib.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.swahilib.R
import com.swahilib.core.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SwahiLibWidgetReceiver : AppWidgetProvider() {

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

            val launchIntent = context.packageManager
                .getLaunchIntentForPackage(context.packageName)
                ?.apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
            val pendingIntent = PendingIntent.getActivity(
                context, widgetId, launchIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

            val db = AppDatabase.getInstanceForWidget(context)

            CoroutineScope(Dispatchers.IO).launch {
                val views = RemoteViews(context.packageName, R.layout.widget_swahilib)
                views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

                if (isLarge) {
                    val proverb = db.proverbsDao().getRandomProverb()
                    views.setTextViewText(R.id.widget_label, "🌿 Methali ya Siku")
                    views.setTextViewText(R.id.widget_title, proverb?.title ?: "—")
                    // ONE random meaning — split on "|" or "#"
                    val meaning = proverb?.meaning
                        ?.split("|", "#")
                        ?.map { it.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?.randomOrNull() ?: ""
                    views.setTextViewText(R.id.widget_meaning, meaning)
                } else {
                    val word = db.wordsDao().getRandomWord()
                    views.setTextViewText(R.id.widget_label, "📖 Neno la Siku")
                    views.setTextViewText(R.id.widget_title, word?.title ?: "—")
                    // ONE random meaning — split on "|"
                    val meaning = word?.meaning
                        ?.split("|")
                        ?.map { it.trim() }
                        ?.filter { it.isNotEmpty() }
                        ?.randomOrNull()
                        ?.take(120) ?: ""
                    views.setTextViewText(R.id.widget_meaning, meaning)
                }

                manager.updateAppWidget(widgetId, views)
            }
        }
    }
}
