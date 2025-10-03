package com.example.mytimewidget

import android.Manifest
import android.app.*
import android.app.AlarmManager
import android.app.PendingIntent

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import androidx.annotation.RequiresPermission
import java.text.SimpleDateFormat
import java.util.*


/**
 * Implementation of App Widget functionality.
 */
class TimeWidget : AppWidgetProvider() {

    companion object {
        private const val FORMAT = "HH:mm"
        private const val DATE_FORMAT = "dd MMMM"
        private const val DAY_FORMAT = "EEEE"

        private const val ACTION_WIDGET_UPDATE = "com.example.MyTimeWidget.ACTION_WIDGET_UPDATE"

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

            // Date for India
            sdfDate.timeZone = TimeZone.getTimeZone("Asia/Kolkata")
            views.setTextViewText(R.id.date_india, sdfDate.format(now))

            // Day for India
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

            // Chicago
            sdf.timeZone = TimeZone.getTimeZone("America/Chicago")
            views.setTextViewText(R.id.time_chicago, "${sdf.format(Date())}")


            // Instruct the widget manager to update the widget
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // Schedule the alarm for all instances
        for (appWidgetId in appWidgetIds) {
            // 1. Update the widget immediately
            updateAppWidget(context, appWidgetManager, appWidgetId)

            // 2. Schedule the next update via AlarmManager
            scheduleNextUpdate(context, appWidgetId)
        }
    }

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // Cancel the alarm when any widget instance is deleted
        for (appWidgetId in appWidgetIds) {
            cancelUpdate(context, appWidgetId)
        }
    }

    /**
     * Schedules an exact alarm to fire in 60 seconds.
     */
    @RequiresPermission(Manifest.permission.SCHEDULE_EXACT_ALARM)
    private fun scheduleNextUpdate(context: Context, appWidgetId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Intent to send a broadcast back to this AppWidgetProvider
        val intent = Intent(context, TimeWidget::class.java).apply {
            action = ACTION_WIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        // PendingIntent to wrap the Intent
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId, // Use the widget ID as the request code
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Calculate time 60 seconds (1 minute) from now
        val nextUpdate = System.currentTimeMillis() + 10000

        // Schedule the alarm:
        // RTC: Uses the device's wall clock time.
        // setExactAndAllowWhileIdle: Recommended for precise timing, even in Doze mode.
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC,
            nextUpdate,
            pendingIntent
        )
    }

    /**
     * Cancels the scheduled alarm.
     */

    private fun cancelUpdate(context: Context, appWidgetId: Int) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        // Use the same Intent/PendingIntent setup to identify the existing alarm
        val intent = Intent(context, TimeWidget::class.java).apply {
            action = ACTION_WIDGET_UPDATE
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            appWidgetId,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE // FLAG_NO_CREATE checks if it exists
        )

        // If the pending intent exists, cancel the alarm
        pendingIntent?.let {
            alarmManager.cancel(it)
        }
    }

    // B. Handling the Alarm and Self-Scheduling

    override fun onReceive(context: Context, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == ACTION_WIDGET_UPDATE) {
            val appWidgetId = intent.getIntExtra(
                AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID
            )

            if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                val appWidgetManager = AppWidgetManager.getInstance(context)

                // 1. Update the widget with the current time
                updateAppWidget(context, appWidgetManager, appWidgetId)

                // 2. CRITICAL: Schedule the *next* update for one minute from now,
                // creating the continuous loop.
                scheduleNextUpdate(context, appWidgetId)
            }
        }
    }


}

