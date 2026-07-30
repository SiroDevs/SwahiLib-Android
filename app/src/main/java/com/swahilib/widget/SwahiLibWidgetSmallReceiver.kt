package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

/** 1x2 pill: just today's word, no label - see swahilib_widget_small_info.xml. */
class SwahiLibWidgetSmallReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            WidgetContentRenderer.render(context, appWidgetManager, id, WidgetSize.SMALL)
        }
    }
}
