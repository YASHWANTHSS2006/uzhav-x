package com.example.model

data class DailyAgriTip(
    val id: String,
    val month: String, // e.g., "November"
    val seasonNameTamil: String, // e.g., "சம்பா பருவம் & வடகிழக்கு பருவமழை"
    val seasonNameEnglish: String, // e.g., "Samba Season & NE Monsoon"
    val titleTamil: String,
    val titleEnglish: String,
    val category: TipCategory,
    val contentTamil: String,
    val contentEnglish: String,
    val targetedCropsTamil: List<String>,
    val targetedCropsEnglish: List<String>,
    val keyActionStepsTamil: List<String>,
    val keyActionStepsEnglish: List<String>,
    val tnRegions: String = "All Tamil Nadu Districts (அனைத்து மாவட்டங்கள்)",
    val timestamp: String,
    val isAiGenerated: Boolean = true
)

enum class TipCategory {
    PEST_AND_DISEASE,
    IRRIGATION_AND_RAIN,
    FERTILIZER_AND_NUTRITION,
    HARVEST_AND_STORAGE,
    SOIL_HEALTH;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        PEST_AND_DISEASE -> if (lang == AppLanguage.TAMIL) "பூச்சி & நோய் மேலாண்மை" else "Pest & Disease Control"
        IRRIGATION_AND_RAIN -> if (lang == AppLanguage.TAMIL) "பாசனம் & பருவமழை பராமரிப்பு" else "Irrigation & Rain Management"
        FERTILIZER_AND_NUTRITION -> if (lang == AppLanguage.TAMIL) "உரம் & ஊட்டச்சத்து ஆலோசனை" else "Fertilizer & Nutrition"
        HARVEST_AND_STORAGE -> if (lang == AppLanguage.TAMIL) "அறுவடை & தானிய சேமிப்பு" else "Harvest & Post-Harvest Storage"
        SOIL_HEALTH -> if (lang == AppLanguage.TAMIL) "மண் வளம் & ஆர்கானிக் மேலாண்மை" else "Soil Health & Organic Care"
    }
}
