package com.example.weathermate.alerts.view

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.weathermate.R
import com.example.weathermate.model.AlertItem
import com.example.weathermate.repository.remote.ApiService
import com.example.weathermate.repository.remote.RetrofitHelper
import kotlinx.coroutines.*

class AlertsManager(private var context: Context) {

    private val TAG = "commonnn"

    fun fireAlert(alert: AlertItem) {


        val unixTime = alert.startDT.toLong()

        if ((unixTime * 1000) > System.currentTimeMillis()) {
            if (alert.alertType == "notification") {

                Log.i(TAG, "fireAlert: notification")

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, MyBroadcastReceiver::class.java)
                intent.putExtra("ALERT_TYPE", alert.alertType)
                intent.putExtra("ALERT_LATITUDE", alert.latitudeString)
                intent.putExtra("ALERT_LONGITUDE", alert.longitudeString)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        context,
                        alert.idHashLongFromLonLatStartStringEndStringAlertType.toInt(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getBroadcast(
                        context,
                        alert.idHashLongFromLonLatStartStringEndStringAlertType.toInt(),
                        intent,
                        0
                    )
                }

                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    unixTime * 1000L,
                    pendingIntent
                )

            } else if (alert.alertType == "alarm") {

                Log.i(TAG, "fireAlert: alarm")

                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, MyBroadcastReceiver::class.java)
                intent.putExtra("ALERT_TYPE", alert.alertType)
                intent.putExtra("ALERT_LATITUDE", alert.latitudeString)
                intent.putExtra("ALERT_LONGITUDE", alert.longitudeString)
                val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        context,
                        alert.idHashLongFromLonLatStartStringEndStringAlertType.toInt(),
                        intent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getBroadcast(
                        context,
                        alert.idHashLongFromLonLatStartStringEndStringAlertType.toInt(),
                        intent,
                        0
                    )
                }
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    unixTime * 1000L,
                    pendingIntent
                )

            }
        }


    }

}

class MyBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "commonnn"

    override fun onReceive(context: Context?, intent: Intent?) {

        Log.i(TAG, "onReceive: 1")

        val alertType = intent!!.getStringExtra("ALERT_TYPE")!!
        val longitude = intent.getStringExtra("ALERT_LONGITUDE")!!
        val latitude = intent.getStringExtra("ALERT_LATITUDE")!!

        val myCoroutineScope = CoroutineScope(Dispatchers.IO)

        if (alertType == "notification") {
            val notificationManager =
                context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                val channel = NotificationChannel(
                    "my_channel_id",
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
                notificationManager.createNotificationChannel(channel)
            }

            val retrofit = RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_WEATHER)
            val service = retrofit.create(ApiService::class.java)



            myCoroutineScope.launch {

                Log.i(TAG, "onReceive: 2")


                val deferredResponse = async {
                    service.getWeatherData(
                        context.resources.getString(R.string.openweathermap_key),
                        lat = latitude.toDouble(),
                        lon = longitude.toDouble(), language = "en"
                    )
                }

                val response = deferredResponse.await()


                var eventWeather = "There are no warnings at the moment."
                if (response.alerts != null) {
                    eventWeather = "Warning: " + response.alerts.get(0).event
                }


                val dismissIntent = Intent(context, DismissReceiver::class.java).apply {
                    putExtra("notification_id", 0)
                }


                val dismissPendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        dismissIntent,
                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                    )
                } else {
                    PendingIntent.getBroadcast(
                        context,
                        0,
                        dismissIntent,
                        0
                    )
                }


                val builder = NotificationCompat.Builder(context!!, "my_channel_id")
                    .setSmallIcon(R.drawable.baseline_add_alert_24)
                    .setContentTitle("Weather Alert")
                    .setContentText(eventWeather)
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .addAction(R.drawable.baseline_close_24, "Dismiss", dismissPendingIntent)


                notificationManager.notify(0, builder.build())

                myCoroutineScope.cancel()
            }

        } else if (alertType == "alarm") {
            var ringtone: Ringtone? = null
            val retrofit = RetrofitHelper.getRetrofitInstance(ApiService.BASE_URL_WEATHER)
            val service = retrofit.create(ApiService::class.java)

            myCoroutineScope.launch {

                val deferredResponse = async {
                    service.getWeatherData(
                        context!!.resources.getString(R.string.openweathermap_key),
                        lat = latitude.toDouble(),
                        lon = longitude.toDouble(), language = "en"
                    )
                }

                val response = deferredResponse.await()

                var eventWeather = "There are no warnings at the moment."
                if (response.alerts != null) {
                    eventWeather = "Warning: " + response.alerts.get(0).event
                }

                val inflater = LayoutInflater.from(context)
                val alarmLayout = inflater.inflate(R.layout.alarm_layout, null)

                val params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.MATCH_PARENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                    PixelFormat.TRANSLUCENT
                )
                params.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL

                val dismissButton = alarmLayout.findViewById<Button>(R.id.dismissButton)
                dismissButton.setOnClickListener {

                    ringtone?.stop()

                    val windowManager =
                        context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                    windowManager.removeViewImmediate(alarmLayout)
                }

                val tv_alarm = alarmLayout.findViewById<TextView>(R.id.tv_alarm)
                tv_alarm.text = eventWeather


                val windowManager =
                    context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager

                withContext(Dispatchers.Main) {
                    windowManager.addView(alarmLayout, params)
                }


                val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
                ringtone = RingtoneManager.getRingtone(context, alarmUri)
                ringtone?.play()

                myCoroutineScope.cancel()

            }
        }


    }
}

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(intent.getIntExtra("notification_id", 0))

    }
}