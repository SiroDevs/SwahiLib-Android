package com.swahilib.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle

/**
 * Word + proverb widget, resizable across all four shapes: 2x4 (both, wide)
 * <-> 1x4 (word only, wide) <-> 2x2 (both, compact) <-> 1x2 (word only,
 * compact) - see swahilib_widget_full_info.xml for the resize bounds.
 */
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
