package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

class WidgetSmallReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            WidgetContentRenderer.render(context, appWidgetManager, id, supportsBoth = false)
        }
    }

    override fun onAppWidgetOptionsChanged(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int,
        newOptions: Bundle,
    ) {
        val shape = WidgetContentRenderer.resolveShape(
            options = newOptions,
            supportsBoth = false,
            fallback = WidgetShape.WIDGET_WIDE,
        )
        WidgetContentRenderer.render(context, appWidgetManager, appWidgetId, shape)
    }
}
