package com.example.model

enum class TaskCategory {
    IRRIGATION,     // பாசனம்
    FERTILIZER,     // உரமிடல் / மேலுரம்
    PEST_SPRAY,     // பூச்சி / நோய் தடுப்பு
    WEEDING,        // களை எடுத்தல்
    HARVEST;        // அறுவடை

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        IRRIGATION -> if (lang == AppLanguage.TAMIL) "பாசனம்" else "Irrigation"
        FERTILIZER -> if (lang == AppLanguage.TAMIL) "உரமிடல்" else "Fertilizer"
        PEST_SPRAY -> if (lang == AppLanguage.TAMIL) "பூச்சி தெளிப்பு" else "Pest Spray"
        WEEDING -> if (lang == AppLanguage.TAMIL) "களை எடுத்தல்" else "Weeding"
        HARVEST -> if (lang == AppLanguage.TAMIL) "அறுவடை" else "Harvest"
    }
}

enum class RecurrencePattern {
    DAILY,
    EVERY_3_DAYS,
    EVERY_5_DAYS,
    WEEKLY,
    BI_WEEKLY,
    MONTHLY,
    ONCE;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        DAILY -> if (lang == AppLanguage.TAMIL) "தினமும்" else "Daily"
        EVERY_3_DAYS -> if (lang == AppLanguage.TAMIL) "3 நாட்களுக்கு ஒருமுறை" else "Every 3 Days"
        EVERY_5_DAYS -> if (lang == AppLanguage.TAMIL) "5 நாட்களுக்கு ஒருமுறை" else "Every 5 Days"
        WEEKLY -> if (lang == AppLanguage.TAMIL) "வாரத்திற்கு ஒருமுறை" else "Weekly"
        BI_WEEKLY -> if (lang == AppLanguage.TAMIL) "2 வாரத்திற்கு ஒருமுறை" else "Bi-Weekly"
        MONTHLY -> if (lang == AppLanguage.TAMIL) "மாதத்திற்கு ஒருமுறை" else "Monthly"
        ONCE -> if (lang == AppLanguage.TAMIL) "ஒருமுறை மட்டும்" else "One-Time"
    }

    fun getIntervalDays(): Int = when (this) {
        DAILY -> 1
        EVERY_3_DAYS -> 3
        EVERY_5_DAYS -> 5
        WEEKLY -> 7
        BI_WEEKLY -> 14
        MONTHLY -> 30
        ONCE -> 0
    }
}

data class GrowthStage(
    val stageId: String,
    val stageNameTamil: String,
    val stageNameEnglish: String,
    val dayStart: Int,
    val dayEnd: Int,
    val descriptionTamil: String,
    val descriptionEnglish: String,
    val recommendedTasks: List<TemplateTask>
)

data class TemplateTask(
    val titleTamil: String,
    val titleEnglish: String,
    val category: TaskCategory,
    val recurrence: RecurrencePattern,
    val notesTamil: String,
    val notesEnglish: String
)

data class FarmTaskReminder(
    val id: String,
    val cropName: String,
    val cropTamilName: String,
    val titleTamil: String,
    val titleEnglish: String,
    val category: TaskCategory,
    val recurrence: RecurrencePattern,
    val scheduledDate: String, // YYYY-MM-DD
    val scheduledTime: String, // e.g. "06:00 AM"
    val isCompleted: Boolean = false,
    val growthStageNameTamil: String,
    val growthStageNameEnglish: String,
    val notesTamil: String = "",
    val notesEnglish: String = "",
    val isNotificationEnabled: Boolean = true
)

data class CropGrowthProgress(
    val cropName: String,
    val cropTamilName: String,
    val sowingDate: String, // YYYY-MM-DD
    val daysAfterSowing: Int,
    val totalCropDurationDays: Int,
    val currentStage: GrowthStage,
    val overallProgressPercent: Float,
    val nextUpcomingTask: FarmTaskReminder? = null
)
