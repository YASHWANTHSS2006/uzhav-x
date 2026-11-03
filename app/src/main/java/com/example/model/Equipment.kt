package com.example.model

data class AgriEquipment(
    val id: String,
    val name: String,
    val tamilName: String,
    val category: EquipmentCategory,
    val brand: String,
    val modelNumber: String,
    val originalPrice: Int, // ₹
    val subsidyApplicable: Boolean,
    val subsidyPercentText: String, // e.g. "50% SMAM Subsidy (₹45,000 off)"
    val effectivePrice: Int, // ₹
    val specifications: List<String>,
    val suitableForCrops: String,
    val dealerName: String,
    val dealerPhone: String,
    val dealerLocation: String,
    val warranty: String,
    val emiStartsFrom: String
)

enum class EquipmentCategory {
    TRACTOR_ATTACHMENTS,
    SPRAYERS,
    WEEDERS_HARVESTERS,
    SOLAR_IRRIGATION,
    SOIL_TEST_TOOLS;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        TRACTOR_ATTACHMENTS -> if (lang == AppLanguage.TAMIL) "டிராக்டர் & ரோட்டவேட்டர்" else "Tractor & Implements"
        SPRAYERS -> if (lang == AppLanguage.TAMIL) "பேட்டரி ஸ்ப்ரேயர்கள்" else "Battery & Power Sprayers"
        WEEDERS_HARVESTERS -> if (lang == AppLanguage.TAMIL) "களை எடுக்கும் கருவி" else "Power Weeders & Cutters"
        SOLAR_IRRIGATION -> if (lang == AppLanguage.TAMIL) "சூரிய மின் மோட்டார் & சொட்டு நீர்" else "Solar Pumps & Drip Kits"
        SOIL_TEST_TOOLS -> if (lang == AppLanguage.TAMIL) "மண் பரிசோதனை கருவிகள்" else "Soil Testing & Sensors"
    }
}
