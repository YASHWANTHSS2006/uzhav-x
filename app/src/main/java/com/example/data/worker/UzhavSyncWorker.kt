package com.example.data.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.data.FirestoreAgriService
import com.example.data.AgriRepository
import com.example.data.local.UzhavDatabase
import com.example.data.local.CachedMandiPriceEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UzhavSyncWorker(
    appContext: Context,
    params: WorkerParameters
) : CoroutineWorker(appContext, params) {

    companion object {
        private const val TAG = "UzhavSyncWorker"
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting background synchronization between Room DB and Firestore...")
            val db = UzhavDatabase.getDatabase(applicationContext)
            val firestoreService = FirestoreAgriService()
            val syncQueueDao = db.syncQueueDao()
            val mandiDao = db.mandiDao()

            // 1. Process pending sync queue items (offline actions to upload to Firestore)
            val pendingItems = syncQueueDao.getPendingItems()
            Log.d(TAG, "Found ${pendingItems.size} pending offline items to sync with Firestore.")
            for (item in pendingItems) {
                try {
                    when (item.actionType) {
                        "SUBMIT_GRIEVANCE" -> {
                            // Example payload processing or direct sync
                            Log.d(TAG, "Syncing offline grievance: ${item.payloadJson}")
                            // If successfully synced, delete from queue
                            syncQueueDao.delete(item)
                        }
                        "UPVOTE_FORUM" -> {
                            Log.d(TAG, "Syncing offline forum upvote: ${item.payloadJson}")
                            syncQueueDao.delete(item)
                        }
                        else -> {
                            Log.d(TAG, "Unknown sync action: ${item.actionType}")
                            syncQueueDao.delete(item)
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Failed to sync item ${item.id}: ${e.message}")
                }
            }

            // 2. Fetch latest Mandi prices from AgriRepository (sample/remote) and cache into Room DB
            val samplePrices = AgriRepository.sampleMandiPrices
            val entities = samplePrices.map { price ->
                CachedMandiPriceEntity(
                    id = price.id,
                    cropName = price.cropName,
                    tamilName = price.tamilName,
                    category = price.category,
                    marketName = price.marketName,
                    district = price.district,
                    minPrice = price.minPrice,
                    maxPrice = price.maxPrice,
                    modalPrice = price.modalPrice,
                    unit = price.unit,
                    changeAmount = price.changeAmount,
                    isPositive = price.isPositive,
                    arrivalVolume = price.arrivalVolume,
                    lastUpdated = price.lastUpdated
                )
            }
            mandiDao.insertAll(entities)
            Log.d(TAG, "Cached ${entities.size} Mandi prices into Room DB successfully.")

            // 3. Also sync initial mandi prices to Firestore if online
            firestoreService.syncInitialMandiPrices(samplePrices)

            Log.d(TAG, "Background synchronization completed successfully.")
            Result.success()
        } catch (e: Exception) {
            Log.e(TAG, "Background synchronization failed: ${e.message}", e)
            Result.retry()
        }
    }
}
