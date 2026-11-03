package com.example.data.local

import android.content.Context
import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "mandi_prices")
data class CachedMandiPriceEntity(
    @PrimaryKey val id: String,
    val cropName: String,
    val tamilName: String,
    val category: String,
    val marketName: String,
    val district: String,
    val minPrice: Int,
    val maxPrice: Int,
    val modalPrice: Int,
    val unit: String,
    val changeAmount: Int,
    val isPositive: Boolean,
    val arrivalVolume: String,
    val lastUpdated: String,
    val priceHistory7DaysCsv: String = "",
    val regionalComparisonsJson: String = ""
)

@Entity(tableName = "subsidies")
data class CachedSubsidyEntity(
    @PrimaryKey val id: String,
    val schemeCode: String = "",
    val yearValidity: String = "",
    val title: String,
    val tamilTitle: String,
    val department: String = "",
    val category: String,
    val subsidyPercentage: String = "",
    val maxFinancialBenefit: String = "",
    val description: String = "",
    val tamilDescription: String = "",
    val eligibilityCsv: String = "",
    val requiredDocumentsCsv: String = "",
    val howToApplyStepsCsv: String = "",
    val portalUrl: String = "",
    val helplineNumber: String = "",
    val isPanchayatScheme: Boolean = false,
    val isNewFor2026: Boolean = false
)

@Entity(tableName = "sync_queue")
data class SyncQueueEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val actionType: String,
    val payloadJson: String,
    val timestamp: Long
)

@Dao
interface MandiDao {
    @Query("SELECT * FROM mandi_prices")
    fun getAllPricesFlow(): Flow<List<CachedMandiPriceEntity>>

    @Query("SELECT * FROM mandi_prices")
    suspend fun getAllPrices(): List<CachedMandiPriceEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(prices: List<CachedMandiPriceEntity>)

    @Query("DELETE FROM mandi_prices")
    suspend fun clearAll()
}

@Dao
interface SubsidyDao {
    @Query("SELECT * FROM subsidies")
    fun getAllSubsidiesFlow(): Flow<List<CachedSubsidyEntity>>

    @Query("SELECT * FROM subsidies")
    suspend fun getAllSubsidies(): List<CachedSubsidyEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(subsidies: List<CachedSubsidyEntity>)

    @Query("DELETE FROM subsidies")
    suspend fun clearAll()
}

@Dao
interface SyncQueueDao {
    @Query("SELECT * FROM sync_queue ORDER BY timestamp ASC")
    suspend fun getPendingItems(): List<SyncQueueEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun enqueue(item: SyncQueueEntity)

    @Delete
    suspend fun delete(item: SyncQueueEntity)
}

@Entity(tableName = "task_reminders")
data class TaskReminderEntity(
    @PrimaryKey val id: String,
    val cropName: String,
    val cropTamilName: String,
    val titleTamil: String,
    val titleEnglish: String,
    val categoryName: String,
    val recurrenceName: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val isCompleted: Boolean,
    val growthStageTamil: String,
    val growthStageEnglish: String,
    val notesTamil: String,
    val notesEnglish: String,
    val isNotificationEnabled: Boolean
)

@Dao
interface TaskReminderDao {
    @Query("SELECT * FROM task_reminders ORDER BY scheduledDate ASC, scheduledTime ASC")
    suspend fun getAllReminders(): List<TaskReminderEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: TaskReminderEntity)

    @Query("UPDATE task_reminders SET isCompleted = :completed WHERE id = :id")
    suspend fun markCompleted(id: String, completed: Boolean)

    @Query("DELETE FROM task_reminders WHERE id = :id")
    suspend fun deleteReminder(id: String)

    @Query("DELETE FROM task_reminders")
    suspend fun clearAll()
}

@Database(entities = [CachedMandiPriceEntity::class, CachedSubsidyEntity::class, SyncQueueEntity::class, TaskReminderEntity::class], version = 3, exportSchema = false)
abstract class UzhavDatabase : RoomDatabase() {
    abstract fun mandiDao(): MandiDao
    abstract fun subsidyDao(): SubsidyDao
    abstract fun syncQueueDao(): SyncQueueDao
    abstract fun taskReminderDao(): TaskReminderDao

    companion object {
        @Volatile
        private var INSTANCE: UzhavDatabase? = null

        fun getDatabase(context: Context): UzhavDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    UzhavDatabase::class.java,
                    "uzhav_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

