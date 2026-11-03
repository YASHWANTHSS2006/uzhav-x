package com.example.data.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.AgriTipNotificationHelper
import com.example.data.DailyAgriTipService
import com.example.model.AppLanguage

class DailyAgriTipWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val service = DailyAgriTipService()
            val tip = service.fetchDailyAgriTip(forceRefresh = true)
            AgriTipNotificationHelper.showDailyTipNotification(
                context = context,
                tip = tip,
                language = AppLanguage.TAMIL
            )
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
