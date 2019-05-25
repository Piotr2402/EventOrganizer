package com.example.eventorganizer

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import android.support.annotation.RequiresApi
import java.util.*

class Receiver : BroadcastReceiver()  {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onReceive(context: Context, intent: Intent) {

        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val channel = NotificationChannel("event", "Notify", NotificationManager.IMPORTANCE_LOW)
        manager.createNotificationChannel(channel)

        val myintent = Intent(context, MainActivity::class.java)
        val pending = PendingIntent.getActivity(context,0,myintent,0)

        val ran = Random()
        val notification = Notification.Builder(context, "event")
            .setContentTitle(intent.getStringExtra("name"))
            .setContentText("Za godzinę odbędzie się wydarzenie, którym jesteś zainteresowany")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setChannelId("event")
            .setContentIntent(pending)
            .build()
        manager.notify(ran.nextInt(),notification)
    }
}