package com.example.data

import android.content.Context
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.data.local.TaskReminderEntity
import com.example.data.local.UzhavDatabase
import com.example.data.worker.TaskReminderWorker
import com.example.model.*
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class CropTaskSchedulerHelper(private val context: Context) {

    private val db by lazy { UzhavDatabase.getDatabase(context) }
    private val taskDao by lazy { db.taskReminderDao() }

    // Master TNAU Crop Growth Stages Templates
    fun getCropGrowthStages(cropName: String): List<GrowthStage> {
        val cName = cropName.lowercase()
        return when {
            cName.contains("paddy") || cName.contains("நெல்") || cName.contains("rice") -> listOf(
                GrowthStage(
                    stageId = "paddy_1",
                    stageNameTamil = "1. நாற்றுப் பருவம் (Nursery Stage)",
                    stageNameEnglish = "1. Nursery Stage (Day 0 to 25)",
                    dayStart = 0,
                    dayEnd = 25,
                    descriptionTamil = "விதை நேர்த்தி, நாற்றங்கால் தயாரிப்பு மற்றும் பாசன பராமரிப்பு.",
                    descriptionEnglish = "Seed treatment with Azospirillum and nursery water management.",
                    recommendedTasks = listOf(
                        TemplateTask("நாற்றங்கால் நீர் பாசனம்", "Nursery Water Management", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_3_DAYS, "காலை 6 மணிக்கு நீர் பாய்ச்சவும்", "Irrigate nursery bed early morning"),
                        TemplateTask("உயிரி உரம் அசோஸ்பைரில்லம் இடல்", "Apply Azospirillum Bio-fertilizer", TaskCategory.FERTILIZER, RecurrencePattern.ONCE, "ஏக்கருக்கு 1 கிலோ உயிரி உரம்", "1 kg per acre bio-fertilizer")
                    )
                ),
                GrowthStage(
                    stageId = "paddy_2",
                    stageNameTamil = "2. தூர் கட்டும் பருவம் (Tillering Stage)",
                    stageNameEnglish = "2. Tillering Stage (Day 26 to 55)",
                    dayStart = 26,
                    dayEnd = 55,
                    descriptionTamil = "நட்ட பின் அதிக கிளைகள் மற்றும் தூர்கள் உருவெடுக்கும் பருவம். யூரியா மேலுரம்.",
                    descriptionEnglish = "Active tillering window requiring split Nitrogen fertilizer.",
                    recommendedTasks = listOf(
                        TemplateTask("காய்ச்சலும் பாய்ச்சலுமாக நீர் பாசனம்", "Alternate Wetting & Drying Irrigation", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_3_DAYS, "மண் லேசான விரிசல் கண்ட பின் பாசனம்", "Irrigate when soil surface forms fine hairline cracks"),
                        TemplateTask("யூரியா & பொட்டாஷ் மேலுரம் இடல்", "Top Dress Urea & MOP", TaskCategory.FERTILIZER, RecurrencePattern.BI_WEEKLY, "ஏக்கருக்கு 22 கிலோ யூரியா + 10 கிலோ பொட்டாஷ்", "22 kg Urea + 10 kg Potash per acre"),
                        TemplateTask("இலைச்சுருட்டு புழு பூச்சி கண்காணிப்பு", "Leaf Folder Pest Inspection", TaskCategory.PEST_SPRAY, RecurrencePattern.WEEKLY, "வேப்பெண்ணெய் 3% தெளிக்கவும்", "Foliar spray 3% cold-pressed Neem oil")
                    )
                ),
                GrowthStage(
                    stageId = "paddy_3",
                    stageNameTamil = "3. கதிர் உருவாகும் பருவம் (Panicle Initiation)",
                    stageNameEnglish = "3. Panicle Initiation (Day 56 to 85)",
                    dayStart = 56,
                    dayEnd = 85,
                    descriptionTamil = "நெற்கதிர் உருவாகும் முக்கிய பருவம். வயலில் 5 செ.மீ நீர் தேங்க வேண்டும்.",
                    descriptionEnglish = "Critical reproductive stage. Maintain 5cm standing water.",
                    recommendedTasks = listOf(
                        TemplateTask("தொடர் 5 செ.மீ நீர் பாசனம்", "Maintain 5cm Standing Water", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_3_DAYS, "வயலில் நீர் வற்றாமல் பராமரிக்க", "Ensure no water stress during flowering"),
                        TemplateTask("பொட்டாஷ் மேலுரம் இடுதல்", "Final Potash Top Dressing", TaskCategory.FERTILIZER, RecurrencePattern.ONCE, "மணிச்சத்து மற்றும் பொட்டாஷ் உரம்", "Apply remaining Potash for grain weight")
                    )
                ),
                GrowthStage(
                    stageId = "paddy_4",
                    stageNameTamil = "4. முதிர்ச்சி & அறுவடை பருவம் (Maturity & Harvest)",
                    stageNameEnglish = "4. Maturity & Harvest (Day 86 to 130)",
                    dayStart = 86,
                    dayEnd = 130,
                    descriptionTamil = "நெற்கதிர்கள் பொன்நிறமாக மாறும். அறுவடைக்கு 10 நாள் முன் நீரை வடிக்கவும்.",
                    descriptionEnglish = "Grains turn golden yellow. Drain field 10 days before combine harvesting.",
                    recommendedTasks = listOf(
                        TemplateTask("வயல் நீரை முழுமையாக வடித்தல்", "Drain Field Water Completely", TaskCategory.IRRIGATION, RecurrencePattern.ONCE, "அறுவடை இயந்திரம் எளிதில் இறங்க", "Allow soil firming for combine harvester"),
                        TemplateTask("இயந்திர அறுவடை & தானிய சேமிப்பு", "Combine Harvest & Grain Drying", TaskCategory.HARVEST, RecurrencePattern.ONCE, "தானிய ஈர்ப்பு பதம் 14% குறைக்க உலர்த்துதல்", "Sun dry grains to achieve 14% moisture level")
                    )
                )
            )
            cName.contains("turmeric") || cName.contains("மஞ்சள்") -> listOf(
                GrowthStage(
                    stageId = "turmeric_1",
                    stageNameTamil = "1. முளைப்பு & இலை பருவம் (Sprouting Stage)",
                    stageNameEnglish = "1. Sprouting & Vegetative (Day 0 to 60)",
                    dayStart = 0,
                    dayEnd = 60,
                    descriptionTamil = "மஞ்சள் விதை கிழங்கு முளைத்தல் மற்றும் இலைகள் உருவாதல்.",
                    descriptionEnglish = "Rhizome germination and early canopy setup.",
                    recommendedTasks = listOf(
                        TemplateTask("சொட்டு நீர் பாசனம்", "Drip Fertigation & Irrigation", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_3_DAYS, "ஈரப்பதம் குறையாமல் பாசனம்", "Maintain optimum root zone moisture"),
                        TemplateTask("வேப்பம் புண்ணாக்கு இடல்", "Apply Neem Cake & Bio-Inputs", TaskCategory.FERTILIZER, RecurrencePattern.MONTHLY, "கிழங்கு அழுகல் நோய் த தவிர்க்க", "Prevents rhizome rot fungal pathogen")
                    )
                ),
                GrowthStage(
                    stageId = "turmeric_2",
                    stageNameTamil = "2. கிழங்கு பெருக்கம் பருவம் (Rhizome Development)",
                    stageNameEnglish = "2. Rhizome Development (Day 61 to 210)",
                    dayStart = 61,
                    dayEnd = 210,
                    descriptionTamil = "மஞ்சள் கிழங்குகள் பருத்து அதிக அளவு குர்குமின் சேரும் பருவம்.",
                    descriptionEnglish = "Active rhizome bulking and curcumin synthesis phase.",
                    recommendedTasks = listOf(
                        TemplateTask("மண் அணைத்தல் & உரம் இடல்", "Earthing Up & NPK Top Dressing", TaskCategory.FERTILIZER, RecurrencePattern.MONTHLY, "கிழங்குகள் வெளியே தெரியாமல் மண் அணைக்க", "Earth up soil around clumps with NPK"),
                        TemplateTask("கிழங்கு அழுகல் நோய் தெளிப்பு", "Foliar Spray for Rhizome Rot", TaskCategory.PEST_SPRAY, RecurrencePattern.BI_WEEKLY, "சூடோமோனாஸ் 0.2% தெளித்தல்", "Biocontrol agent Pseudomonas fluorescens")
                    )
                )
            )
            else -> listOf(
                GrowthStage(
                    stageId = "gen_1",
                    stageNameTamil = "1. தொடக்க பயிர் வளர்ச்சி (Vegetative Stage)",
                    stageNameEnglish = "1. Vegetative Stage (Day 0 to 45)",
                    dayStart = 0,
                    dayEnd = 45,
                    descriptionTamil = "வேர் மற்றும் இலைகள் தீவிரமாக வளரும் பருவம்.",
                    descriptionEnglish = "Root establishment and canopy vegetative growth.",
                    recommendedTasks = listOf(
                        TemplateTask("வழக்கமான நீர் பாசனம்", "Regular Scheduled Irrigation", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_3_DAYS, "மண் ஈரப்பதம் காக்கவும்", "Check soil moisture level"),
                        TemplateTask("களை எடுத்தல் & உரம்", "Weeding & Top Dressing", TaskCategory.WEEDING, RecurrencePattern.BI_WEEKLY, "களைகளை அகற்றி உரம் இடவும்", "Remove weeds and apply balanced nutrients")
                    )
                ),
                GrowthStage(
                    stageId = "gen_2",
                    stageNameTamil = "2. பூத்தல் & விளைச்சல் பருவம் (Flowering & Harvest)",
                    stageNameEnglish = "2. Flowering & Harvest (Day 46 to 120)",
                    dayStart = 46,
                    dayEnd = 120,
                    descriptionTamil = "பூக்கள் பூத்து காய் / பழங்கள் முற்றும் பருவம்.",
                    descriptionEnglish = "Flowering, fruit set, and harvesting phase.",
                    recommendedTasks = listOf(
                        TemplateTask("பாசன பராமரிப்பு", "Flowering Irrigation Care", TaskCategory.IRRIGATION, RecurrencePattern.EVERY_5_DAYS, "பூ உதிர்வை தடுக்க பாசனம்", "Avoid extreme water stress during bloom"),
                        TemplateTask("அறுவடை பணியை தொடங்கல்", "Harvesting Pickings", TaskCategory.HARVEST, RecurrencePattern.WEEKLY, "பழுத்த காய்களை அறுவடை செய்ய", "Harvest mature crop produce")
                    )
                )
            )
        }
    }

    // Auto-generate Stage-Wise Recurring Tasks from Sowing Date
    fun generateTasksForSowingDate(
        cropName: String,
        cropTamilName: String,
        sowingDateStr: String // YYYY-MM-DD
    ): List<FarmTaskReminder> {
        val stages = getCropGrowthStages(cropName)
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sowingDate = try { sdf.parse(sowingDateStr) ?: Date() } catch (_: Exception) { Date() }

        val generatedList = mutableListOf<FarmTaskReminder>()

        for (stage in stages) {
            val cal = Calendar.getInstance()
            cal.time = sowingDate
            cal.add(Calendar.DAY_OF_YEAR, stage.dayStart)

            for (template in stage.recommendedTasks) {
                val taskCal = cal.clone() as Calendar
                val scheduledDateText = sdf.format(taskCal.time)

                val id = "task_${cropName.lowercase()}_${stage.stageId}_${template.category.name}_${System.currentTimeMillis()}_${(100..999).random()}"
                generatedList.add(
                    FarmTaskReminder(
                        id = id,
                        cropName = cropName,
                        cropTamilName = cropTamilName,
                        titleTamil = template.titleTamil,
                        titleEnglish = template.titleEnglish,
                        category = template.category,
                        recurrence = template.recurrence,
                        scheduledDate = scheduledDateText,
                        scheduledTime = "06:30 AM",
                        isCompleted = false,
                        growthStageNameTamil = stage.stageNameTamil,
                        growthStageNameEnglish = stage.stageNameEnglish,
                        notesTamil = template.notesTamil,
                        notesEnglish = template.notesEnglish,
                        isNotificationEnabled = true
                    )
                )
            }
        }
        return generatedList
    }

    // Room DB Operations
    suspend fun loadAllRemindersFromDb(): List<FarmTaskReminder> {
        return try {
            val entities = taskDao.getAllReminders()
            entities.map { entity ->
                FarmTaskReminder(
                    id = entity.id,
                    cropName = entity.cropName,
                    cropTamilName = entity.cropTamilName,
                    titleTamil = entity.titleTamil,
                    titleEnglish = entity.titleEnglish,
                    category = try { TaskCategory.valueOf(entity.categoryName) } catch (_: Exception) { TaskCategory.IRRIGATION },
                    recurrence = try { RecurrencePattern.valueOf(entity.recurrenceName) } catch (_: Exception) { RecurrencePattern.EVERY_3_DAYS },
                    scheduledDate = entity.scheduledDate,
                    scheduledTime = entity.scheduledTime,
                    isCompleted = entity.isCompleted,
                    growthStageNameTamil = entity.growthStageTamil,
                    growthStageNameEnglish = entity.growthStageEnglish,
                    notesTamil = entity.notesTamil,
                    notesEnglish = entity.notesEnglish,
                    isNotificationEnabled = entity.isNotificationEnabled
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveReminderToDb(reminder: FarmTaskReminder) {
        val entity = TaskReminderEntity(
            id = reminder.id,
            cropName = reminder.cropName,
            cropTamilName = reminder.cropTamilName,
            titleTamil = reminder.titleTamil,
            titleEnglish = reminder.titleEnglish,
            categoryName = reminder.category.name,
            recurrenceName = reminder.recurrence.name,
            scheduledDate = reminder.scheduledDate,
            scheduledTime = reminder.scheduledTime,
            isCompleted = reminder.isCompleted,
            growthStageTamil = reminder.growthStageNameTamil,
            growthStageEnglish = reminder.growthStageNameEnglish,
            notesTamil = reminder.notesTamil,
            notesEnglish = reminder.notesEnglish,
            isNotificationEnabled = reminder.isNotificationEnabled
        )
        taskDao.insertReminder(entity)
        if (reminder.isNotificationEnabled && !reminder.isCompleted) {
            scheduleWorkManagerAlert(reminder)
        }
    }

    suspend fun markTaskCompletedAndReschedule(reminder: FarmTaskReminder): FarmTaskReminder {
        val updated = reminder.copy(isCompleted = true)
        taskDao.markCompleted(reminder.id, true)

        // If recurring task (e.g. Every 3 days), auto-schedule the next occurrence!
        val intervalDays = reminder.recurrence.getIntervalDays()
        if (intervalDays > 0) {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val cal = Calendar.getInstance()
            try {
                val currentSched = sdf.parse(reminder.scheduledDate)
                if (currentSched != null) {
                    cal.time = currentSched
                }
            } catch (_: Exception) {}

            cal.add(Calendar.DAY_OF_YEAR, intervalDays)
            val nextDateStr = sdf.format(cal.time)

            val nextReminder = reminder.copy(
                id = "task_recur_${System.currentTimeMillis()}",
                scheduledDate = nextDateStr,
                isCompleted = false
            )
            saveReminderToDb(nextReminder)
        }

        return updated
    }

    suspend fun deleteTask(id: String) {
        taskDao.deleteReminder(id)
    }

    private fun scheduleWorkManagerAlert(reminder: FarmTaskReminder) {
        try {
            val data = Data.Builder()
                .putString("TASK_TITLE", reminder.titleTamil)
                .putString("CROP_NAME", reminder.cropTamilName)
                .putString("TASK_CATEGORY", reminder.category.getDisplayName(AppLanguage.TAMIL))
                .build()

            val workRequest = OneTimeWorkRequestBuilder<TaskReminderWorker>()
                .setInputData(data)
                .setInitialDelay(2, TimeUnit.HOURS) // Simulates background reminder queue
                .build()

            WorkManager.getInstance(context).enqueue(workRequest)
        } catch (_: Exception) {}
    }
}
