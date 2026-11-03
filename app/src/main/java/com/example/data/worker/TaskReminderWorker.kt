package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AgriTipNotificationHelper
import com.example.data.local.UzhavDatabase
import com.example.model.AppLanguage

class TaskReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val taskTitle = inputData.getString("TASK_TITLE") ?: "விவசாய பாசனம் & உரமிடல் நினைவுறுத்தல்"
            val cropName = inputData.getString("CROP_NAME") ?: "சம்பா நெல்"
            val category = inputData.getString("TASK_CATEGORY") ?: "பாசனம்"

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
            AgriTipNotificationHelper.createNotificationChannel(context)

            val title = "🌾 $cropName - $category நினைவுறுத்தல்"
            val body = "$taskTitle. குறித்த காலத்தில் விவசாய பணியை பூர்த்தி செய்ய உழவன் X நினைவூட்டுகிறது."

            val intent = android.content.Intent(context, com.example.MainActivity::class.java).apply {
                flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            val pendingIntent = android.app.PendingIntent.getActivity(
                context,
                0,
                intent,
                android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
            )

            val builder = androidx.core.app.NotificationCompat.Builder(context, AgriTipNotificationHelper.CHANNEL_ID)
                .setSmallIcon(com.example.R.mipmap.ic_launcher)
                .setContentTitle(title)
                .setContentText(body)
                .setStyle(androidx.core.app.NotificationCompat.BigTextStyle().bigText(body))
                .setPriority(androidx.core.app.NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)

            notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }
}
