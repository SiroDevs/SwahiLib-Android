package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

class WidgetFullReceiver : AppWidgetProvider() {

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        appWidgetIds.forEach { id ->
            WidgetContentRenderer.render(context, appWidgetManager, id, supportsBoth = true)
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
            supportsBoth = true,
            fallback = WidgetShape.BOTH_WIDE,
        )
        WidgetContentRenderer.render(context, appWidgetManager, appWidgetId, shape)
    }
}
