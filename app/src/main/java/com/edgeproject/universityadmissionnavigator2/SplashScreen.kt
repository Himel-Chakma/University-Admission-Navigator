package com.edgeproject.universityadmissionnavigator2

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Date
import java.util.concurrent.TimeUnit

class SplashScreen : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        android.os.Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            finish()
        }, 3000)

        fetchUniversitiesAndScheduleReminders()
    }

    private fun fetchUniversitiesAndScheduleReminders() {
        db.collection("universities").get().addOnSuccessListener { snapshot ->
            for (document in snapshot.documents) {
                val universityName = document.getString("name") ?: ""
                val applicationStartDate = document.getTimestamp("application_start_date")?.toDate()

                applicationStartDate?.let {
                    scheduleReminderNotifications(universityName, it)
                    schedulePostApplicationNotification(universityName, it)
                }
            }
        }.addOnFailureListener { e ->
            e.printStackTrace()
        }
    }

    private fun scheduleReminderNotifications(universityName: String, startDate: Date) {
        val currentTime = System.currentTimeMillis()

        // Calculate two reminder times (7 and 3 days before the start date)
        val sevenDaysBefore = startDate.time - TimeUnit.DAYS.toMillis(7)
        val threeDaysBefore = startDate.time - TimeUnit.DAYS.toMillis(3)

        // Schedule a notification 7 days before
        if (sevenDaysBefore > currentTime) {
            scheduleNotification(universityName, "Application starts in 7 days!", sevenDaysBefore)
        }

        // Schedule a notification 3 days before
        if (threeDaysBefore > currentTime) {
            scheduleNotification(universityName, "Application starts in 3 days!", threeDaysBefore)
        }
    }

    private fun schedulePostApplicationNotification(universityName: String, startDate: Date) {
        val currentTime = System.currentTimeMillis()

        // Calculate notification time 3 days after the application starts
        val threeDaysAfter = startDate.time + TimeUnit.DAYS.toMillis(3)

        // Schedule a notification 3 days after the application starts
        if (threeDaysAfter > currentTime) {
            scheduleNotification(
                universityName,
                "Application started 3 days ago! Check your status!",
                threeDaysAfter
            )
        }

        // Notification for application start
        if (startDate.time > currentTime) {
            scheduleNotification(
                universityName,
                "Application has started today! Don't miss out!",
                startDate.time
            )
        }
    }

    private fun scheduleNotification(universityName: String, message: String, triggerTime: Long) {
        val delay = triggerTime - System.currentTimeMillis()

        val inputData = androidx.work.Data.Builder()
            .putString("university_name", universityName)
            .putString("message", message)
            .build()

        val workRequest = androidx.work.OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(delay, TimeUnit.MILLISECONDS)
            .setInputData(inputData)
            .build()

        androidx.work.WorkManager.getInstance(applicationContext).enqueue(workRequest)
    }
}