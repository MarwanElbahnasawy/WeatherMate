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
import android.view.Gravity
import android.view.LayoutInflater
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.example.weathermate.MainActivity
import com.example.weathermate.MyApp
import com.example.weathermate.R
import com.example.weathermate.data.model.AlertItem
import com.example.weathermate.data.remote.RetrofitStateWeather
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest

class AlertsManager(private val context: Context) {

    private val TAG = "commonnn"

    fun fireAlert(alert: AlertItem) {


        val unixTime = alert.startDT.toLong()

        if ((unixTime * 1000) > System.currentTimeMillis()) {

            if (alert.alertType == "notification") {
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, MyBroadcastReceiver::class.java)
                intent.putExtra("ALERT_TYPE", alert.alertType)
                intent.putExtra("ALERT_LATITUDE", alert.latitudeString)
                intent.putExtra("ALERT_LONGITUDE", alert.longitudeString)
                intent.putExtra("ALERT_ADDRESS", alert.address)
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
                val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(context, MyBroadcastReceiver::class.java)
                intent.putExtra("ALERT_TYPE", alert.alertType)
                intent.putExtra("ALERT_LATITUDE", alert.latitudeString)
                intent.putExtra("ALERT_LONGITUDE", alert.longitudeString)
                intent.putExtra("ALERT_ADDRESS", alert.address)
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

    fun cancelAlert(alert: AlertItem) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(context, MyBroadcastReceiver::class.java)
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
        alarmManager.cancel(pendingIntent)
    }

}

class MyBroadcastReceiver : BroadcastReceiver() {

    private val TAG = "commonnn"

    override fun onReceive(context: Context?, intent: Intent?) {
        val alertType = intent!!.getStringExtra("ALERT_TYPE")!!
        val longitude = intent.getStringExtra("ALERT_LONGITUDE")!!
        val latitude = intent.getStringExtra("ALERT_LATITUDE")!!
        val address = intent.getStringExtra("ALERT_ADDRESS")!!

        val retrofitStateWeather = MutableStateFlow<RetrofitStateWeather>(RetrofitStateWeather.Loading)

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



            myCoroutineScope.launch {


                val data = MyApp.getInstanceRepository().getWeatherDataOnline(lat = latitude.toDouble(),
                    lon = longitude.toDouble(), language = "en")

                data.catch {
                    retrofitStateWeather.value = RetrofitStateWeather.OnFail(Throwable("Error retrieving data"))
                }
                    .collectLatest{
                        retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                    }


                retrofitStateWeather.collectLatest {

                    when (it) {
                        is RetrofitStateWeather.OnSuccess -> {
                            var eventWeather = context?.getString(R.string.noWarnings)
                            if (it.weatherData.alerts != null) {
                                eventWeather = context?.getString(R.string.warningAlert) + it.weatherData.alerts.get(0).event
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

                            val intent = Intent(context, MainActivity::class.java)
                            val pendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )

                            val builder = NotificationCompat.Builder(context!!, "my_channel_id")
                                .setSmallIcon(R.drawable.weathermateicongenerated)
                                .setContentTitle("Weather Alert")
                                .setContentText("$eventWeather ${context?.getString(R.string.at)} $address.")
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .addAction(R.drawable.baseline_close_24, context?.getString(R.string.dismissAlert), dismissPendingIntent)


                            notificationManager.notify(0, builder.build())

                            myCoroutineScope.cancel()
                        }
                        is RetrofitStateWeather.OnFail -> {

                            val eventWeather = context?.getString(R.string.internetDisconnectedAlert)

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

                            val intent = Intent(context, MainActivity::class.java)
                            val pendingIntent = PendingIntent.getActivity(
                                context,
                                0,
                                intent,
                                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                            )

                            val builder = NotificationCompat.Builder(context!!, "my_channel_id")
                                .setSmallIcon(R.drawable.weathermateicongenerated)
                                .setContentTitle("Weather Alert")
                                .setContentText(eventWeather)
                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                .setContentIntent(pendingIntent)
                                .addAction(R.drawable.baseline_close_24, context?.getString(R.string.dismissAlert), dismissPendingIntent)


                            notificationManager.notify(0, builder.build())

                            myCoroutineScope.cancel()
                        }
                        else -> {}
                    }
                }
            }

        } else if (alertType == "alarm") {
            var ringtone: Ringtone? = null

            myCoroutineScope.launch {

                val data = MyApp.getInstanceRepository().getWeatherDataOnline(lat = latitude.toDouble(),
                    lon = longitude.toDouble(), language = "en")

                data.catch {
                    retrofitStateWeather.value = RetrofitStateWeather.OnFail(Throwable("Error retrieving data"))
                }
                    .collectLatest{
                        retrofitStateWeather.value = RetrofitStateWeather.OnSuccess(it)
                    }


                retrofitStateWeather.collectLatest {

                    when (it) {
                        is RetrofitStateWeather.OnSuccess -> {
                            var eventWeather = context?.getString(R.string.noWarnings)
                            if (it.weatherData.alerts != null) {
                                eventWeather = context?.getString(R.string.warningAlert) + it.weatherData.alerts.get(0).event
                            }

                            val inflater = LayoutInflater.from(context)
                            val alarmLayout = inflater.inflate(R.layout.alarm_layout, null)

                            val params = WindowManager.LayoutParams(
                                dpToPx(350, context!!),
                                dpToPx(150, context!!),
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                                PixelFormat.TRANSLUCENT
                            )
                            params.gravity = Gravity.TOP or Gravity.CENTER
                            params.y = dpToPx(50, context!!)
                            alarmLayout.findViewById<ImageView>(R.id.imgViewWeatherMate).setImageResource(R.drawable.weathermateicongenerated)


                            val dismissButton = alarmLayout.findViewById<Button>(R.id.dismissButton)
                            dismissButton.setOnClickListener {

                                ringtone?.stop()

                                val windowManager =
                                    context?.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                                windowManager.removeViewImmediate(alarmLayout)
                            }

                            val tv_alarm = alarmLayout.findViewById<TextView>(R.id.tv_alarm)
                            tv_alarm.text = "$eventWeather ${context?.getString(R.string.at)} $address."

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
                        is RetrofitStateWeather.OnFail -> {
                            val eventWeather = context?.getString(R.string.internetDisconnectedAlert)

                            val inflater = LayoutInflater.from(context)
                            val alarmLayout = inflater.inflate(R.layout.alarm_layout, null)

                            val params = WindowManager.LayoutParams(
                                dpToPx(350, context!!),
                                dpToPx(150, context!!),
                                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON or WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON,
                                PixelFormat.TRANSLUCENT
                            )
                            params.gravity = Gravity.TOP or Gravity.CENTER
                            params.y = dpToPx(50, context!!)
                            alarmLayout.findViewById<ImageView>(R.id.imgViewWeatherMate).setImageResource(R.drawable.weathermateicongenerated)

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
                        else -> {}
                    }
                }
            }
        }



    }

    fun dpToPx(dp: Int , context: Context): Int {
        val density = context.resources.displayMetrics.density
        return (dp * density).toInt()
    }
}

class DismissReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(intent.getIntExtra("notification_id", 0))

    }
}