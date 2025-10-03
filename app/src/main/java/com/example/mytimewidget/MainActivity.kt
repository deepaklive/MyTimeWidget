package com.example.mytimewidget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.mytimewidget.ui.theme.MyTimeWidgetTheme


import android.os.Build

import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyTimeWidgetTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }

        // Check permission right when the app's main screen loads
        checkAndRequestExactAlarmPermission()
    }


    /**
     * Checks if the app has permission to schedule exact alarms (required on Android 12/API 31+).
     * If not, it directs the user to the system settings to grant it.
     */
    private fun checkAndRequestExactAlarmPermission() {
        // Only perform the check on Android 12 (API 31) and higher
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {

            val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (!alarmManager.canScheduleExactAlarms()) {

                // Permission is not granted, prompt the user
                val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM).apply {
                    data = Uri.fromParts("package", packageName, null)
                }

                // NOTE: Starting this intent will pause your current activity.
                startActivity(intent)

                // OPTIONAL: Display a message (e.g., a Toast or dialog) here
                // explaining *why* they need to enable this permission
                // ("to make sure your time widget updates every minute").
            }
        }
    }


}



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "\n\n Hello !  \n\n This is a Time Widget. \n Developed by Deepak Verma\n\n Permissions Required: \n  - Alarm and Reminders \n\n\n\n Hope you like it. ",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    MyTimeWidgetTheme {
        Greeting("Android")
    }
}