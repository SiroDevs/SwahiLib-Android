package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context

/** 2x2: header + word + English gloss - see swahilib_widget_medium_info.xml. */
class SwahiLibWidgetMediumReceiver : AppWidgetProvider() {
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            WidgetContentRenderer.render(context, appWidgetManager, id, WidgetSize.MEDIUM)
        }
    }
}
