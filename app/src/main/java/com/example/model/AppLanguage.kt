package com.example.model

enum class AppLanguage {
    ENGLISH,
    TAMIL;

    val displayName: String
        get() = when (this) {
            ENGLISH -> "English"
            TAMIL -> "தமிழ்"
        }
}

/**
 * High quality localization dictionary for Tamil Nadu Agri farmers.
 */
object AgriStrings {
    fun t(key: String, lang: AppLanguage): String {
        return when (lang) {
            AppLanguage.TAMIL -> tamilMap[key] ?: englishMap[key] ?: key
            AppLanguage.ENGLISH -> englishMap[key] ?: key
        }
    }

    private val englishMap = mapOf(
        "app_name" to "Uzhavan AI",
        "tagline" to "Smart Agriculture Companion for Tamil Nadu",
        "home" to "Home",
        "mandi" to "Mandi Prices",
        "stores" to "Agri Stores & Map",
        "schemes" to "Govt Schemes",
        "forum" to "Farmers Forum",
        "doctor" to "Crop Doctor",
        "crop_advisor" to "AI Crop Advisor",
        "ai_chat" to "Uzhavan AI Chat",
        "profile" to "Farmer Profile",
        "language_switch" to "Switch to தமிழ்",
        "today_prices" to "Daily Market Prices",
        "view_all" to "View All",
        "recommend_crop_btn" to "Find Most Profitable Crop",
        "scan_crop_btn" to "Scan Diseased Leaf",
        "compare_stores_btn" to "Compare Fertilizer Prices",
        "subsidies_title" to "TN Agri Schemes & Loans (Maniyam)",
        "weather_summary" to "Monsoon Outlook: Favorable for Samba Season (29°C, 78% Humidity)",
        "state_tn" to "Tamil Nadu",
        "district" to "District",
        "taluka" to "Taluk / Block",
        "village" to "Village",
        "soil_type" to "Soil Type",
        "land_size" to "Land Size (Acres)",
        "water_source" to "Water Availability",
        "organic_fertilizer" to "Organic Bio-Inputs",
        "chemical_fertilizer" to "Chemical Fertilizers",
        "in_stock" to "In Stock",
        "limited_stock" to "Limited Stock",
        "govt_subsidized" to "Govt Subsidized",
        "save_profile" to "Save Profile",
        "ask_question" to "Ask Expert or Community",
        "send_message" to "Ask Uzhavan AI anything in Tamil or English..."
    )

    private val tamilMap = mapOf(
        "app_name" to "உழவன் AI",
        "tagline" to "தமிழ்நாடு விவசாயிகளின் நவீன வழிகாட்டி",
        "home" to "முகப்பு",
        "mandi" to "மண்டி விலைகள்",
        "stores" to "உர கடைகள் & வரைபடம்",
        "schemes" to "அரசு திட்டங்கள் & மானியம்",
        "forum" to "விவசாயிகள் மன்றம்",
        "doctor" to "பயிர் மருத்துவர்",
        "crop_advisor" to "AI பயிர் பரிந்துரை",
        "ai_chat" to "உழவன் AI உரையாடல்",
        "profile" to "விவசாயி சுயவிவரம்",
        "language_switch" to "Switch to English",
        "today_prices" to "இன்றைய தினசரி சந்தை நிலவரம்",
        "view_all" to "அனைத்தும் காண்க",
        "recommend_crop_btn" to "அதிக லாபம் தரும் பயிரை கண்டறி",
        "scan_crop_btn" to "நோய் தாக்கிய இலையை ஸ்கேன் செய்",
        "compare_stores_btn" to "உர விலைகளை ஒப்பிடுக",
        "subsidies_title" to "தமிழக மானிய திட்டங்கள் & கடன்கள்",
        "weather_summary" to "பருவநிலை முன்னறிவிப்பு: சம்பா சாகுபடிக்கு உகந்தது (29°C, 78% ஈரப்பதம்)",
        "state_tn" to "தமிழ்நாடு",
        "district" to "மாவட்டம்",
        "taluka" to "வட்டம் / தாலுகா",
        "village" to "கிராமம்",
        "soil_type" to "மண் வகை",
        "land_size" to "நில அளவு (ஏக்கர்)",
        "water_source" to "நீர் ஆதாரம்",
        "organic_fertilizer" to "இயற்கை உரங்கள்",
        "chemical_fertilizer" to "இரசாயன உரங்கள்",
        "in_stock" to "இருப்பில் உள்ளது",
        "limited_stock" to "குறைந்த அளவு இருப்பு",
        "govt_subsidized" to "அரசு மானிய விலை",
        "save_profile" to "சுயவிவரத்தை சேமி",
        "ask_question" to "விவசாய நிபுணரிடம் கேளுங்கள்",
        "send_message" to "உழவன் AI-யிடம் தமிழில் அல்லது ஆங்கிலத்தில் கேளுங்கள்..."
    )
}
