package com.example.model

data class DailyForecast(
    val dayName: String,
    val tamilDay: String,
    val tempMax: Int,
    val tempMin: Int,
    val condition: String,
    val tamilCondition: String,
    val rainProbPercent: Int,
    val windKmph: Int,
    val advisoryNote: String,
    val tamilAdvisoryNote: String
)

data class AgriWeatherReport(
    val district: String,
    val taluka: String,
    val currentTempC: Int,
    val condition: String,
    val tamilCondition: String,
    val humidityPercent: Int,
    val rainProbabilityPercent: Int,
    val windSpeedKmh: Int,
    val soilMoistureLevel: String,
    val solarRadiation: String,
    val currentAdvisoryTamil: String,
    val currentAdvisoryEnglish: String,
    val forecast7Days: List<DailyForecast>
)

data class FeedbackReport(
    val id: String,
    val farmerName: String,
    val district: String,
    val taluka: String,
    val village: String,
    val phone: String,
    val category: ReportCategory,
    val description: String,
    val timestamp: String,
    val status: ReportStatus = ReportStatus.SUBMITTED
)

enum class ReportCategory {
    PEST_OUTBREAK,
    FERTILIZER_SHORTAGE,
    CANAL_WATER_ISSUE,
    SCHEME_SUBSIDY_DELAY,
    APP_FEEDBACK;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        PEST_OUTBREAK -> if (lang == AppLanguage.TAMIL) "தீவிர பூச்சி/நோய் தாக்குதல்" else "Pest / Disease Outbreak"
        FERTILIZER_SHORTAGE -> if (lang == AppLanguage.TAMIL) "கூட்டுறவு சங்கத்தில் உர தட்டுப்பாடு" else "PACCS Fertilizer Shortage"
        CANAL_WATER_ISSUE -> if (lang == AppLanguage.TAMIL) "பாசன வாய்க்கால் நீர் வரத்து பிரச்சனை" else "Canal Water Supply Issue"
        SCHEME_SUBSIDY_DELAY -> if (lang == AppLanguage.TAMIL) "மானியம் விடுவிப்பதில் தாமதம்" else "Scheme Subsidy Delay"
        APP_FEEDBACK -> if (lang == AppLanguage.TAMIL) "செயலி கருத்து & யோசனைகள்" else "App Feedback & Suggestion"
    }
}

enum class ReportStatus {
    SUBMITTED,
    INVESTIGATING,
    RESOLVED;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        SUBMITTED -> if (lang == AppLanguage.TAMIL) "பதிவு செய்யப்பட்டது" else "Submitted"
        INVESTIGATING -> if (lang == AppLanguage.TAMIL) "பரிசீலனையில் உள்ளது" else "Under Review"
        RESOLVED -> if (lang == AppLanguage.TAMIL) "தீர்வு காணப்பட்டது" else "Resolved"
    }
}
