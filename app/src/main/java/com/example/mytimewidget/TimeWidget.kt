package com.example.mytimewidget

import android.app.*
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.SystemClock
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class TimeWidget : AppWidgetProvider() {

    companion object {
        private const val FORMAT = "HH:mm"
        private const val DATE_FORMAT = "dd MMM"
        private const val DAY_FORMAT = "EEEE"

        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val widgetText = context.getString(R.string.appwidget_text)
            // Construct the RemoteViews object
            val views = RemoteViews(context.packageName, R.layout.time_widget)

            val now = Date()

            val sdfDate = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
            val sdfDay = SimpleDateFormat(DAY_FORMAT, Locale.getDefault())

            val sdf = SimpleDateFormat(FORMAT, Locale.getDefault())

            //Date for India
            sdfDate.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            views.setTextViewText(R.id.date_india, sdfDate.format(now))

            //Day for India
            sdfDay.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            views.setTextViewText(R.id.day_india, sdfDay.format(now))

            // Singapore
            sdf.timeZone = TimeZone.getTimeZone("Asia/Singapore")
            views.setTextViewText(R.id.time_singapore, "${sdf.format(Date())}")

            // India
            sdf.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            views.setTextViewText(R.id.time_india, "${sdf.format(Date())}")
            //views.setTextViewText(R.id.time_india, "India: ${sdfIndia.format(Date())}");

            // London
            sdf.timeZone = TimeZone.getTimeZone("Europe/London")
            views.setTextViewText(R.id.time_london, "${sdf.format(Date())}")

            // Zurich
            sdf.timeZone = TimeZone.getTimeZone("Europe/Zurich")
            views.setTextViewText(R.id.time_zurich, "${sdf.format(Date())}")

            //Chicago
            sdf.timeZone = TimeZone.getTimeZone("America/Chicago")
            views.setTextViewText(R.id.time_chicago, "${sdf.format(Date())}")




            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

     fun scheduleNextUpdate(context: Context, appWidgetId: Int) {
        val intent = Intent(context, TimeWidget::class.java).apply {
            action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, intArrayOf(appWidgetId))
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context, appWidgetId, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.ELAPSED_REALTIME,
            SystemClock.elapsedRealtime() + 60000, // 30 sec
            pendingIntent
        )
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
            scheduleNextUpdate(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

