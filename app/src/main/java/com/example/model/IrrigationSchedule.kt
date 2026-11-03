package com.example.model

import androidx.annotation.Keep

@Keep
data class IrrigationScheduleRequest(
    val cropType: String = "Paddy (நெல்)",
    val soilMoisturePercent: Float = 28f, // 0 to 100%
    val soilType: String = "Alluvial Loam (வண்டல் மண்)",
    val growthStage: String = "Flowering & Grain Filling (பூக்கும் & மணி பிடிக்கும் பருவம்)",
    val irrigationMethod: String = "Drip Irrigation (சொட்டு நீர் பாசனம்)",
    val landAreaAcres: Double = 2.0,
    val ambientTempC: Int = 32
) {
    fun getMoistureCategoryLabel(lang: AppLanguage): String = when {
        soilMoisturePercent < 20f -> if (lang == AppLanguage.TAMIL) "மிக வறண்ட நிலை (Critical Dry < 20%)" else "Critically Dry (< 20%)"
        soilMoisturePercent < 40f -> if (lang == AppLanguage.TAMIL) "மிதமான ஈரப்பதம் (Moderate 20-40%)" else "Moderate Moisture (20-40%)"
        soilMoisturePercent <= 65f -> if (lang == AppLanguage.TAMIL) "உகந்த ஈரப்பதம் (Optimal 40-65%)" else "Optimal Moisture (40-65%)"
        else -> if (lang == AppLanguage.TAMIL) "அதிக நீர் / தேங்கிய நிலை (Excess Water > 65%)" else "Saturated / Waterlogged (> 65%)"
    }

    fun getMoistureGaugeColor(): Long = when {
        soilMoisturePercent < 20f -> 0xFFD32F2F // Red
        soilMoisturePercent < 40f -> if (soilMoisturePercent < 30f) 0xFFF57C00 else 0xFFFBC02D // Orange / Yellow
        soilMoisturePercent <= 65f -> 0xFF388E3C // Green
        else -> 0xFF1976D2 // Blue
    }
}

@Keep
data class IrrigationScheduleRecommendation(
    val request: IrrigationScheduleRequest,
    val nextIrrigationDate: String, // e.g. "Tomorrow 06:00 AM" / "நாளை காலை 06:00 மணி"
    val recommendedWaterLitersPerAcre: Int, // e.g. 12,000 L / acre
    val totalWaterLiters: Int = (recommendedWaterLitersPerAcre * request.landAreaAcres).toInt(),
    val durationMinutes: Int, // e.g. 45 mins
    val optimalTimeOfDay: String, // e.g. "Early Morning (06:00 AM - 08:30 AM)"
    val waterSavingsPercentage: Int, // e.g. 38%
    val soilMoistureStatusLabel: String,
    val evapotranspirationRate: String = "4.6 mm/day",
    val aiAdviceTamil: String,
    val aiAdviceEnglish: String,
    val actionStepsTamil: List<String>,
    val actionStepsEnglish: List<String>
)
