package com.example.model

data class AgriNews(
    val id: String,
    val title: String,
    val tamilTitle: String,
    val summaryEnglish: String,
    val summaryTamil: String,
    val category: NewsCategory,
    val source: String, // e.g. "TN Agri Dept", "TNAU News", "PIB Chennai", "Mandi Bureau"
    val timeAgo: String,
    val readTimeMinutes: Int,
    val isBreaking: Boolean = false,
    val imageUrlTag: String = "general"
)

enum class NewsCategory {
    ALL,
    GOVT_POLICY,
    MARKET_PRICES,
    WEATHER_WATER,
    PEST_ALERT,
    TECHNOLOGY;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        ALL -> if (lang == AppLanguage.TAMIL) "அனைத்தும்" else "All News"
        GOVT_POLICY -> if (lang == AppLanguage.TAMIL) "அரசு அறிவிப்புகள்" else "Govt Policy"
        MARKET_PRICES -> if (lang == AppLanguage.TAMIL) "சந்தை நிலவரம்" else "Market Trends"
        WEATHER_WATER -> if (lang == AppLanguage.TAMIL) "பருவமழை & மேட்டூர் அணை" else "Weather & Dams"
        PEST_ALERT -> if (lang == AppLanguage.TAMIL) "பூச்சி & நோய் எச்சரிக்கை" else "Pest Advisories"
        TECHNOLOGY -> if (lang == AppLanguage.TAMIL) "நவீன வேளாண் தொழில்நுட்பம்" else "Agri Tech"
    }
}
