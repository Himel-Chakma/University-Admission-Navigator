package com.edgeproject.universityadmissionnavigator2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.Worker
import androidx.work.WorkerParameters

class NotificationWorker(context: Context, params: WorkerParameters) : Worker(context, params) {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun doWork(): Result {
        val universityName = inputData.getString("university_name") ?: "Unknown University"
        val message = inputData.getString("message") ?: "Remainder!"

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "application_remainder_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Application Remainder", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }

        val notification = android.app.Notification.Builder(applicationContext, channelId)
            .setContentTitle(universityName)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .build()

        notificationManager.notify(System.currentTimeMillis().toInt(), notification)

        return Result.success()

    }

}