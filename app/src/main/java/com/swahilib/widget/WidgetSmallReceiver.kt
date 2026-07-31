package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

/**
 * Word-only widget, resizable between 1x2 (compact pill) and 1x4 (wide, with
 * meaning) - see swahilib_widget_neno_info.xml for the resize bounds.
 */
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
            fallback = WidgetShape.NENO_WIDE,
        )
        WidgetContentRenderer.render(context, appWidgetManager, appWidgetId, shape)
    }
}
