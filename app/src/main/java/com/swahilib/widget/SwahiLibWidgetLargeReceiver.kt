package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

/** 2x4: sidebar + word + English gloss + full meaning - see swahilib_widget_large_info.xml. */
class SwahiLibWidgetLargeReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            WidgetContentRenderer.render(context, appWidgetManager, id, WidgetSize.LARGE)
        }
    }
}
