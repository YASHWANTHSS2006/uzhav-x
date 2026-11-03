package com.example.data

import com.example.model.AppLanguage
import com.example.model.IrrigationScheduleRecommendation
import com.example.model.IrrigationScheduleRequest
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class IrrigationAdvisorService {

    fun generateLocalIrrigationSchedule(request: IrrigationScheduleRequest): IrrigationScheduleRecommendation {
        val moisture = request.soilMoisturePercent
        val crop = request.cropType
        val isDrip = request.irrigationMethod.contains("Drip", ignoreCase = true) || request.irrigationMethod.contains("சொட்டு", ignoreCase = true)
        val isSprinkler = request.irrigationMethod.contains("Sprinkler", ignoreCase = true) || request.irrigationMethod.contains("தெளிப்பு", ignoreCase = true)

        // Calculate water savings vs traditional flood
        val waterSavingsPercent = when {
            isDrip -> 42
            isSprinkler -> 28
            else -> 12
        }

        // Base duration and water volume calculation
        val baseWaterLitersPerAcre = when {
            crop.contains("Paddy", ignoreCase = true) || crop.contains("நெல்", ignoreCase = true) -> {
                if (moisture < 25f) 22000 else if (moisture < 50f) 14000 else 6000
            }
            crop.contains("Sugarcane", ignoreCase = true) || crop.contains("கரும்பு", ignoreCase = true) -> {
                if (moisture < 25f) 18000 else if (moisture < 50f) 11000 else 4000
            }
            crop.contains("Banana", ignoreCase = true) || crop.contains("வாழை", ignoreCase = true) -> {
                if (moisture < 25f) 16000 else if (moisture < 50f) 9500 else 3500
            }
            crop.contains("Cotton", ignoreCase = true) || crop.contains("பருத்தி", ignoreCase = true) -> {
                if (moisture < 25f) 12000 else if (moisture < 50f) 7000 else 2500
            }
            crop.contains("Groundnut", ignoreCase = true) || crop.contains("நிலக்கடலை", ignoreCase = true) -> {
                if (moisture < 25f) 10000 else if (moisture < 50f) 6000 else 2000
            }
            else -> {
                if (moisture < 25f) 11000 else if (moisture < 50f) 6500 else 2200
            }
        }

        // Duration in minutes
        val duration = when {
            moisture < 20f -> if (isDrip) 75 else 120
            moisture < 40f -> if (isDrip) 45 else 80
            moisture <= 60f -> if (isDrip) 25 else 40
            else -> 0 // Saturated - delay irrigation
        }

        val nextSchedule = when {
            moisture < 20f -> "உடனடியாக இன்று மாலை (Today 05:00 PM)"
            moisture < 40f -> "நாளை காலை (Tomorrow 06:00 AM)"
            moisture <= 60f -> "2 நாட்கள் கழித்து (In 2 Days - 06:00 AM)"
            else -> "4 நாட்கள் கழித்து (In 4 Days - Moisture High)"
        }

        val optimalTime = if (request.ambientTempC > 30) {
            "அதிகாலை 05:30 - 08:00 AM (சூரிய வெப்ப ஆவியாதலைத் தவிர்க்க)"
        } else {
            "காலை 06:00 - 08:30 AM அல்லது மாலை 05:00 - 06:30 PM"
        }

        val statusLabel = request.getMoistureCategoryLabel(AppLanguage.TAMIL)

        val adviceTamil = when {
            moisture < 20f -> "மண் ஈரப்பதம் ${moisture.toInt()}% மிகக் குறைவாக உள்ளது. $crop பயிருக்கு உடனடியாக நீர்ப்பாசனம் தேவை. $duration நிமிடங்கள் நீர் பாய்ச்ச பரிந்துரைக்கப்படுகிறது."
            moisture < 40f -> "மண் ஈரப்பதம் ${moisture.toInt()}% மிதமான நிலையில் உள்ளது. $crop பயிருக்கு நாளை காலை $duration நிமிடங்கள் பாசனம் செய்வது வேர்களுக்கு உகந்தது."
            moisture <= 60f -> "மண் ஈரப்பதம் ${moisture.toInt()}% உகந்த அளவில் உள்ளது. தற்சமயம் பாசனம் தேவையில்லை. இன்னும் 2 நாட்கள் கழித்து பரிசோதிக்கவும்."
            else -> "மண் ஈரப்பதம் ${moisture.toInt()}% அதிகமாக தேங்கியுள்ளது! பாசனத்தை உடனடியாக நிறுத்தவும். வடிகால் வசதியை சரிபார்க்கவும்."
        }

        val adviceEnglish = when {
            moisture < 20f -> "Soil moisture at ${moisture.toInt()}% is critically low. Immediate irrigation required for $crop. Recommended runtime: $duration mins."
            moisture < 40f -> "Soil moisture at ${moisture.toInt()}% is in moderate range. Schedule irrigation tomorrow morning for $duration mins."
            moisture <= 60f -> "Soil moisture at ${moisture.toInt()}% is in optimal condition. No immediate watering required. Re-check in 2 days."
            else -> "Soil moisture at ${moisture.toInt()}% is excessive/saturated. Hold irrigation immediately and ensure field drainage."
        }

        val stepsTamil = listOf(
            "சொட்டு நீர்/தெளிப்பு நீர் குழாய்களில் அடைப்பு உள்ளதா என சரிபார்க்கவும்.",
            "காலை 8:30 மணிக்கு முன் பாசனம் செய்து நீர் ஆவியாதலை 30% தவிர்க்கவும்.",
            if (isDrip) "நீருடன் 19:19:19 அல்லது யூரியா உரத்தை கரைசலாக கலந்து பாசனத்தில் அளிக்கலாம் (Fertigation)." else "பாசன வாய்க்கால்களில் களைகளை நீக்கி நீர் இழப்பை குறைக்கவும்.",
            "பாசனத்திற்கு பின் 24 மணி நேரத்திற்குள் மண் ஈரப்பதத்தை மீண்டும் கணக்கிடவும்."
        )

        val stepsEnglish = listOf(
            "Inspect drip emitters or sprinkler nozzles for clogging before running pump.",
            "Irrigate before 8:30 AM to prevent up to 30% water evaporation loss.",
            if (isDrip) "Apply water-soluble fertilizers (19-19-19 or Urea) via fertigation during the last 15 mins." else "Clear channel weeds to maximize flow efficiency and minimize seepage.",
            "Re-assess soil moisture 24 hours after irrigation cycle."
        )

        return IrrigationScheduleRecommendation(
            request = request,
            nextIrrigationDate = nextSchedule,
            recommendedWaterLitersPerAcre = baseWaterLitersPerAcre,
            durationMinutes = duration,
            optimalTimeOfDay = optimalTime,
            waterSavingsPercentage = waterSavingsPercent,
            soilMoistureStatusLabel = statusLabel,
            evapotranspirationRate = "4.5 mm/day",
            aiAdviceTamil = adviceTamil,
            aiAdviceEnglish = adviceEnglish,
            actionStepsTamil = stepsTamil,
            actionStepsEnglish = stepsEnglish
        )
    }

    suspend fun getAiEnhancedIrrigationAdvice(request: IrrigationScheduleRequest): IrrigationScheduleRecommendation {
        val baseRec = generateLocalIrrigationSchedule(request)
        return try {
            withContext(Dispatchers.IO) {
                val prompt = """
                    Act as a TNAU Senior Agricultural Water Management Specialist for Tamil Nadu farmers.
                    Crop: ${request.cropType}
                    Soil Moisture: ${request.soilMoisturePercent}%
                    Soil Type: ${request.soilType}
                    Growth Stage: ${request.growthStage}
                    Irrigation Method: ${request.irrigationMethod}
                    Field Size: ${request.landAreaAcres} acres
                    Temperature: ${request.ambientTempC}°C

                    Provide concise, practical irrigation advisory in 2 bullet points for Tamil farmers in Tamil and English. Focus on water conservation, timing, and fertigation.
                """.trimIndent()

                val model = Firebase.ai.generativeModel("gemini-2.5-flash")
                val response = model.generateContent(prompt)
                val text = response.text
                if (!text.isNullOrEmpty()) {
                    val snippet = text.take(250)
                    baseRec.copy(
                        aiAdviceTamil = "உழவன் AI நீர் ஆலோசனைக் குறிப்பு:\n" + baseRec.aiAdviceTamil + "\n\n" + snippet,
                        aiAdviceEnglish = "Uzhav AI Smart Water Advisory:\n" + baseRec.aiAdviceEnglish + "\n\n" + snippet
                    )
                } else {
                    baseRec
                }
            }
        } catch (_: Exception) {
            baseRec
        }
    }
}
