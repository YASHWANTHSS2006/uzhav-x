package com.example.model

import androidx.annotation.Keep

@Keep
data class SoilTestResult(
    val isLogged: Boolean = false,
    val testDate: String = "2026-10-15",
    val labName: String = "TNAU KVK Lab, Thanjavur",
    val phLevel: Double = 6.8, // 0 to 14
    val nitrogenKgPerHa: Double = 280.0, // Available N (kg/ha)
    val phosphorusKgPerHa: Double = 18.5, // Available P (kg/ha)
    val potassiumKgPerHa: Double = 220.0, // Available K (kg/ha)
    val organicCarbonPercent: Double = 0.65, // OC % (0 - 2.5%)
    val micronutrientsNotes: String = "Zinc deficiency detected (ஜிங்க் பற்றாக்குறை)",
    val electricalConductivity: Double = 0.45 // EC dS/m
) {
    fun getPhCategory(lang: AppLanguage): String = when {
        phLevel < 5.5 -> if (lang == AppLanguage.TAMIL) "அதி அமிலத்தன்மை (Strongly Acidic)" else "Strongly Acidic (< 5.5)"
        phLevel < 6.5 -> if (lang == AppLanguage.TAMIL) "மிதமான அமிலத்தன்மை (Slightly Acidic)" else "Slightly Acidic (5.5 - 6.5)"
        phLevel <= 7.5 -> if (lang == AppLanguage.TAMIL) "சிறந்த நடுநிலை மண் (Optimal Neutral)" else "Optimal Neutral (6.5 - 7.5)"
        phLevel <= 8.5 -> if (lang == AppLanguage.TAMIL) "மிதமான காரத்தன்மை (Slightly Alkaline)" else "Slightly Alkaline (7.5 - 8.5)"
        else -> if (lang == AppLanguage.TAMIL) "அதி காரத்தன்மை (Strongly Alkaline)" else "Strongly Alkaline (> 8.5)"
    }

    fun getNitrogenStatus(): SoilNutrientStatus = when {
        nitrogenKgPerHa < 240 -> SoilNutrientStatus.LOW
        nitrogenKgPerHa <= 450 -> SoilNutrientStatus.MEDIUM
        else -> SoilNutrientStatus.HIGH
    }

    fun getPhosphorusStatus(): SoilNutrientStatus = when {
        phosphorusKgPerHa < 11 -> SoilNutrientStatus.LOW
        phosphorusKgPerHa <= 22 -> SoilNutrientStatus.MEDIUM
        else -> SoilNutrientStatus.HIGH
    }

    fun getPotassiumStatus(): SoilNutrientStatus = when {
        potassiumKgPerHa < 118 -> SoilNutrientStatus.LOW
        potassiumKgPerHa <= 280 -> SoilNutrientStatus.MEDIUM
        else -> SoilNutrientStatus.HIGH
    }

    fun getOrganicCarbonStatus(): SoilNutrientStatus = when {
        organicCarbonPercent < 0.5 -> SoilNutrientStatus.LOW
        organicCarbonPercent <= 0.75 -> SoilNutrientStatus.MEDIUM
        else -> SoilNutrientStatus.HIGH
    }

    fun getTnauAmendmentAdvice(lang: AppLanguage): String = when {
        phLevel < 6.0 -> if (lang == AppLanguage.TAMIL)
            "அமிலத்தன்மை சீரமைப்பு: ஏக்கருக்கு 500 கிலோ விவசாய சுண்ணம் (Agricultural Lime) இட்டு உழவு செய்யவும்."
            else "Acidic Soil Amendment: Apply 500 kg/acre Agricultural Lime (Calcium Carbonate) before sowing."
        phLevel > 8.0 -> if (lang == AppLanguage.TAMIL)
            "காரத்தன்மை சீரமைப்பு: ஏக்கருக்கு 200 கிலோ ஜிப்சம் (Gypsum) மற்றும் தொழுஉரம் (FYM) கலந்து இடவும்."
            else "Alkaline Soil Amendment: Apply 200 kg/acre Gypsum with Farm Yard Manure."
        else -> if (lang == AppLanguage.TAMIL)
            "மண் கார அமிலத்தன்மை (pH 6.5-7.5) பயிர் வளர்ச்சிக்கு மிகவும் உகந்த நிலையில் உள்ளது."
            else "Soil pH is at optimal neutral range (6.5 - 7.5) ideal for maximum nutrient intake."
    }
}

enum class SoilNutrientStatus {
    LOW,
    MEDIUM,
    HIGH;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        LOW -> if (lang == AppLanguage.TAMIL) "குறைவு (Low)" else "Low (< Target)"
        MEDIUM -> if (lang == AppLanguage.TAMIL) "மிதமான அளவு (Optimal)" else "Medium (Optimal)"
        HIGH -> if (lang == AppLanguage.TAMIL) "அதிகம் (High)" else "High (> Surplus)"
    }
}
