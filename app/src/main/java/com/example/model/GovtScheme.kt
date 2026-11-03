package com.example.model

data class GovtScheme(
    val id: String,
    val schemeCode: String, // e.g. "TN-2026-KAVIADP"
    val yearValidity: String = "2026-2027 (Active)",
    val title: String,
    val tamilTitle: String,
    val department: String, // TN Agriculture Dept, NABARD, Central Govt, Cooperative Bank
    val category: SchemeCategory,
    val subsidyPercentage: String, // e.g. "100% for Small Farmers, 75% for Others"
    val maxFinancialBenefit: String, // e.g. "Up to ₹1,35,000 / ha"
    val summaryEnglish: String,
    val summaryTamil: String,
    val eligibility: List<String>,
    val requiredDocuments: List<String>,
    val howToApplySteps: List<String>,
    val officialPortalUrl: String,
    val helplineNumber: String,
    val isPanchayatScheme: Boolean = false,
    val isNewFor2026: Boolean = false
)

enum class SchemeCategory {
    ALL,
    IRRIGATION_SUBSIDY,
    CROP_LOANS,
    MACHINERY_RENTAL,
    SOLAR_POWER,
    ORGANIC_FARMING,
    INSURANCE_RELIEF,
    MILLET_MISSION,
    DRYLAND_DEVELOPMENT;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        ALL -> if (lang == AppLanguage.TAMIL) "அனைத்து திட்டங்கள்" else "All Schemes"
        IRRIGATION_SUBSIDY -> if (lang == AppLanguage.TAMIL) "பாசன மானியம் (சொட்டு நீர்)" else "Irrigation Subsidies"
        CROP_LOANS -> if (lang == AppLanguage.TAMIL) "வங்கி & கூட்டுறவு பயிர் கடன்" else "Bank & Crop Loans"
        MACHINERY_RENTAL -> if (lang == AppLanguage.TAMIL) "இயந்திர மையம் & டிராக்டர்" else "Machinery & Rental"
        SOLAR_POWER -> if (lang == AppLanguage.TAMIL) "சூரிய மின்சார பம்பு செட்" else "Solar Agri Pumps"
        ORGANIC_FARMING -> if (lang == AppLanguage.TAMIL) "இயற்கை வேளாண்மை திட்டம்" else "Organic Agriculture"
        INSURANCE_RELIEF -> if (lang == AppLanguage.TAMIL) "பயிர் காப்பீடு (PMFBY)" else "Crop Insurance"
        MILLET_MISSION -> if (lang == AppLanguage.TAMIL) "தமிழ்நாடு சிறுதானிய இயக்கம்" else "TN Millet Mission"
        DRYLAND_DEVELOPMENT -> if (lang == AppLanguage.TAMIL) "மானாவாரி நில மேம்பாடு" else "Dryland Agriculture"
    }
}
