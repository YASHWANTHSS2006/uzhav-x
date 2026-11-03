package com.example.data

import android.util.Log
import com.example.model.AppLanguage
import com.example.model.DailyAgriTip
import com.example.model.TipCategory
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DailyAgriTipService {

    companion object {
        private const val TAG = "DailyAgriTipService"
    }

    private val geminiModel by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.w(TAG, "Gemini initialization for Daily Agri Tip: ${e.message}")
            null
        }
    }

    suspend fun fetchDailyAgriTip(
        district: String = "Thanjavur",
        soilType: String = "Alluvial Loam",
        forceRefresh: Boolean = false
    ): DailyAgriTip = withContext(Dispatchers.IO) {
        val calendar = Calendar.getInstance()
        val monthName = SimpleDateFormat("MMMM", Locale.ENGLISH).format(calendar.time)
        val monthNumber = calendar.get(Calendar.MONTH) // 0-indexed (10 = November)

        val model = geminiModel
        if (model != null && forceRefresh) {
            try {
                Log.d(TAG, "Generating Gemini AI Daily Agri-Tip for month: $monthName, district: $district")
                val prompt = """
                    You are a senior agriculturalextension specialist at Tamil Nadu Agricultural University (TNAU).
                    Generate a high-priority 'Daily Agri-Tip of the Day' for farmers in $district, Tamil Nadu for the month of $monthName.
                    Soil Type: $soilType.

                    Strict JSON format with no markdown wrappers:
                    {
                      "seasonNameTamil": "பருவ பெயர் (எ.கா. சம்பா பருவம் & வடகிழக்கு பருவமழை)",
                      "seasonNameEnglish": "Season Name in English (e.g. Samba Season & NE Monsoon Care)",
                      "titleTamil": "தமிழ் தலைப்பு (சுருக்கமானது, 8 வார்த்தைகளுக்குள்)",
                      "titleEnglish": "English Title (short action tip)",
                      "category": "PEST_AND_DISEASE / IRRIGATION_AND_RAIN / FERTILIZER_AND_NUTRITION / HARVEST_AND_STORAGE / SOIL_HEALTH",
                      "contentTamil": "விவசாயிகளுக்கான விரிவான செயல்முறை வழிகாட்டல் தமிழில் (3-4 வாக்கியங்கள்).",
                      "contentEnglish": "Detailed practical step-by-step guidance in English (3-4 sentences).",
                      "targetedCropsTamil": ["நெல்", "மஞ்சள்", "கரும்பு"],
                      "targetedCropsEnglish": ["Paddy", "Turmeric", "Sugarcane"],
                      "keyActionStepsTamil": ["படி 1", "படி 2", "படி 3"],
                      "keyActionStepsEnglish": ["Step 1", "Step 2", "Step 3"]
                    }
                """.trimIndent()

                val response = withTimeout(15_000L) {
                    model.generateContent(prompt)
                }

                val text = response.text
                if (!text.isNullOrBlank()) {
                    val parsed = parseGeminiTip(text, monthName, district)
                    if (parsed != null) {
                        return@withContext parsed
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini daily tip generation failed: ${e.message}, using seasonal repository fallback.")
            }
        }

        // Seasonal Month-based Fallback
        getSeasonalFallbackTip(monthNumber, monthName, district)
    }

    private fun parseGeminiTip(rawText: String, monthName: String, district: String): DailyAgriTip? {
        return try {
            val clean = rawText.replace("```json", "").replace("```", "").trim()
            val json = JSONObject(clean)

            val catString = json.optString("category", "PEST_AND_DISEASE")
            val cat = try { TipCategory.valueOf(catString) } catch (_: Exception) { TipCategory.PEST_AND_DISEASE }

            val cropsTa = json.optJSONArray("targetedCropsTamil")?.toStringList() ?: listOf("நெல்", "பயறு", "காய்கறிகள்")
            val cropsEn = json.optJSONArray("targetedCropsEnglish")?.toStringList() ?: listOf("Paddy", "Pulses", "Vegetables")
            val stepsTa = json.optJSONArray("keyActionStepsTamil")?.toStringList() ?: listOf("வயலில் தேங்கிய மழைநீரை வடித்திடுக", "இலைக்கருகல் நோய்க்கு மருந்து தெளிக்க")
            val stepsEn = json.optJSONArray("keyActionStepsEnglish")?.toStringList() ?: listOf("Drain stagnant rainwater from fields", "Spray Tricyclazole for leaf blast protection")

            DailyAgriTip(
                id = "tip_ai_${System.currentTimeMillis()}",
                month = monthName,
                seasonNameTamil = json.optString("seasonNameTamil", "சம்பா பருவம் & வடகிழக்கு பருவமழை"),
                seasonNameEnglish = json.optString("seasonNameEnglish", "Samba Season & Monsoon Advisory"),
                titleTamil = json.optString("titleTamil", "சம்பா நெல் இலைக்கருகல் நோய் தடுப்பு பாதுகாப்பு"),
                titleEnglish = json.optString("titleEnglish", "Samba Rice Blast & Rainwater Drainage Advisory"),
                category = cat,
                contentTamil = json.optString("contentTamil", "தற்போது வடகிழக்கு பருவமழை பெய்து வருவதால் சம்பா நெற்பயிரில் இலைக்கருகல் மற்றும் பாக்டீரியா இலைக்கருகல் நோய் பரவ வாய்ப்புள்ளது. வயல்களில் அதிகப்படியான நீர் தேங்காமல் வடித்துவிட்டு, டிரைசைக்ளசோல் 75% WP @ 0.6 கிராம்/லிட்டர் நீரில் கலந்து தெளிக்கவும்."),
                contentEnglish = json.optString("contentEnglish", "Due to active North-East Monsoon rains across Tamil Nadu, inspect Samba paddy for rice blast and bacterial leaf blight. Ensure immediate field drainage and apply recommended TNAU foliar sprays."),
                targetedCropsTamil = cropsTa,
                targetedCropsEnglish = cropsEn,
                keyActionStepsTamil = stepsTa,
                keyActionStepsEnglish = stepsEn,
                tnRegions = "$district & Surrounding TN Districts",
                timestamp = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date()),
                isAiGenerated = true
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing Gemini tip JSON", e)
            null
        }
    }

    private fun getSeasonalFallbackTip(monthNumber: Int, monthName: String, district: String): DailyAgriTip {
        val dateFormatted = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date())

        return when (monthNumber) {
            Calendar.NOVEMBER -> DailyAgriTip(
                id = "tip_nov",
                month = "November",
                seasonNameTamil = "சம்பா பருவம் & வடகிழக்கு பருவமழை",
                seasonNameEnglish = "Samba Paddy & NE Monsoon Season",
                titleTamil = "சம்பா நெல் மழை நீர் வடிகால் & இலைக்கருகல் பாதுகாப்பு",
                titleEnglish = "Samba Rice Drainage & Blast Prevention",
                category = TipCategory.PEST_AND_DISEASE,
                contentTamil = "நவம்பர் மாத வடகிழக்கு பருவமழை காலத்தில் சம்பா நெற்பயிரில் நீர் தேங்குவதை தவிர். வயலில் வடிகால் வாய்க்கால்களை வெட்டி அதிக நீரை வெளியேற்றவும். தொடர் மழைக்காலத்தில் இலைக்கருகல் நோய் தாக்கினால் 10 லிட்டர் தண்ணீரில் 6 கிராம் டிரைசைக்ளசோல் அல்லது 10% மாட்டுச் சிறுநீர் கரைசல் தெளிக்கவும்.",
                contentEnglish = "During November NE Monsoon rains, clear drainage channels in Samba paddy fields to prevent waterlogging. Inspect crop for blast lesions and spray Tricyclazole 75% WP @ 0.6g/L or 10% cow urine extract for organic protection.",
                targetedCropsTamil = listOf("சம்பா நெல்", "மஞ்சள்", "கரும்பு", "பருத்தி"),
                targetedCropsEnglish = listOf("Samba Paddy", "Turmeric", "Sugarcane", "Cotton"),
                keyActionStepsTamil = listOf(
                    "வயல் ஓரங்களில் 30 செ.மீ ஆழமுள்ள வடி வாய்க்கால் அமைத்தல்",
                    "நைட்ரஜன் உரங்களை மழையின் போது இடாமல் தள்ளி வைத்தல்",
                    "இலைக்கருகல் நோய் கண்டால் 3% பஞ்சகாவ்யா அல்லது ட்ரைசைக்ளசோல் தெளித்தல்"
                ),
                keyActionStepsEnglish = listOf(
                    "Construct 30cm deep drainage ditches along field borders",
                    "Postpone top-dressing Nitrogen fertilizers during continuous heavy rainfall",
                    "Foliar spray of 3% Panchagavya or Tricyclazole upon detecting blast spots"
                ),
                tnRegions = "$district, Thanjavur, Tiruvarur, Nagapattinam & Delta Belt",
                timestamp = dateFormatted,
                isAiGenerated = false
            )
            Calendar.DECEMBER -> DailyAgriTip(
                id = "tip_dec",
                month = "December",
                seasonNameTamil = "தாளடி நெல் & பனி கால பராமரிப்பு",
                seasonNameEnglish = "Thaladi Rice & Winter Dew Advisory",
                titleTamil = "குளிர் பனி காலத்தில் பொட்டாஷ் உரம் & பூச்சி பாதுகாப்பு",
                titleEnglish = "Winter Dew Care: Potash & Pest Protection",
                category = TipCategory.FERTILIZER_AND_NUTRITION,
                contentTamil = "டிசம்பர் பனி காலத்தில் பயிர்களுக்கு குளிர் தணிக்கும் ஆற்றலை வழங்க பொட்டாஷ் உரம் (MOP) ஏக்கருக்கு 25 கிலோ இட வேண்டும். மிளகாய் மற்றும் பருத்தியில் இலைச்சுருட்டு மற்றும் அசுவினி தாக்குதலை தடுக்க வேப்பெண்ணெய் 3% தெளிக்கவும்.",
                contentEnglish = "During December winter dew, top dress Potash (MOP) @ 25kg/acre to strengthen cold tolerance in standing crops. For chilli and cotton thrips, apply 3% cold-pressed neem oil spray.",
                targetedCropsTamil = listOf("தாளடி நெல்", "மிளகாய்", "சின்ன வெங்காயம்", "வாழை"),
                targetedCropsEnglish = listOf("Thaladi Paddy", "Chilli", "Shallots", "Banana"),
                keyActionStepsTamil = listOf(
                    "ஏக்கருக்கு 25 கிலோ பொட்டாஷ் உரம் மேலுரமாக இடல்",
                    "காலை பனி பெய்யும் போது பயிரில் பூச்சிக்கொல்லி தெளிப்பதை தவிர்",
                    "மிளகாய்க்கு வேப்பெண்ணெய் 10,000 ppm தெளித்தல்"
                ),
                keyActionStepsEnglish = listOf(
                    "Top dress 25 kg MOP per acre during boot-leaf stage",
                    "Avoid chemical spraying during heavy morning dew hours",
                    "Apply Neem oil 10,000 ppm for chilli thrips and whiteflies"
                ),
                tnRegions = "$district & All Tamil Nadu Agro Districts",
                timestamp = dateFormatted,
                isAiGenerated = false
            )
            else -> DailyAgriTip(
                id = "tip_general",
                month = monthName,
                seasonNameTamil = "வேளாண் சுழற்சி & மண் வள பராமரிப்பு",
                seasonNameEnglish = "Seasonal Crop Rotation & Soil Health",
                titleTamil = "மண் பரிசோதனை & இயற்கை உரம் இடுதல்",
                titleEnglish = "Soil Testing & Bio-Fertilizer Application",
                category = TipCategory.SOIL_HEALTH,
                contentTamil = "பயிர் சாகுபடி தொடங்குவதற்கு முன் மண் மாதிரி எடுத்து TNAU வேளாண் அறிவியல் மையத்தில் பரிசோதனை செய்யவும். ஏக்கருக்கு 5 டன் தொழு உரம் மற்றும் 2 கிலோ அசோஸ்பைரில்லம் இடுவதன் மூலம் 25% இரசாயன உரச் செலவை குறைக்கலாம்.",
                contentEnglish = "Test soil samples at your nearest KVK before sowing season. Incorporating 5 tonnes Farm Yard Manure (FYM) and 2kg Azospirillum per acre reduces synthetic fertilizer cost by 25%.",
                targetedCropsTamil = listOf("நெல்", "தானியங்கள்", "பயறு வகை", "காய்கறி"),
                targetedCropsEnglish = listOf("Paddy", "Millets", "Pulses", "Vegetables"),
                keyActionStepsTamil = listOf(
                    "அடி உரமாக இயற்கை தொழு உரம் இடல்",
                    "உயிரி உரம் அசோஸ்பைரில்லம் விதை நேர்த்தி செய்தல்",
                    "சொட்டு நீர் பாசனத்தில் உரம் கரைத்து அளித்தல்"
                ),
                keyActionStepsEnglish = listOf(
                    "Apply organic compost as basal dose",
                    "Seed treatment with Azospirillum bio-fertilizer",
                    "Utilize fertigation for micro-nutrient application"
                ),
                tnRegions = "$district, Tamil Nadu",
                timestamp = dateFormatted,
                isAiGenerated = false
            )
        }
    }

    private fun JSONArray.toStringList(): List<String> {
        val list = mutableListOf<String>()
        for (i in 0 until length()) {
            list.add(getString(i))
        }
        return list
    }
}
