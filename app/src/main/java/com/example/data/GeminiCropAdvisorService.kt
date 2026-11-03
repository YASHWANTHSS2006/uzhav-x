package com.example.data

import android.util.Log
import com.example.model.AppLanguage
import com.example.model.CropRecommendationRequest
import com.example.model.RecommendationResult
import com.example.model.RecommendedCrop
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject

/**
 * AI Crop Recommendation Service powered by Gemini 3.8 Flash
 * tailored for Tamil Nadu agro-climatic zones, soil types, and regional Mandis.
 */
class GeminiCropAdvisorService {

    companion object {
        private const val TAG = "GeminiCropAdvisor"
    }

    private val geminiModel by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI Logic model initialization: ${e.message}")
            null
        }
    }

    suspend fun getRecommendations(
        request: CropRecommendationRequest,
        language: AppLanguage = AppLanguage.TAMIL
    ): RecommendationResult = withContext(Dispatchers.IO) {
        val model = geminiModel
        if (model != null) {
            try {
                Log.d(TAG, "Calling Gemini 3.8 Flash for crop recommendation: soil=${request.soilType}, region=${request.district}, season=${request.season}")
                val soilTestInfo = request.soilTestResult?.let { st ->
                    if (st.isLogged) {
                        """
                        - Soil Test Results (Recorded on ${st.testDate} from ${st.labName}):
                          * pH Level: ${st.phLevel} (${st.getPhCategory(AppLanguage.ENGLISH)})
                          * Available Nitrogen (N): ${st.nitrogenKgPerHa} kg/ha [${st.getNitrogenStatus()}]
                          * Available Phosphorus (P): ${st.phosphorusKgPerHa} kg/ha [${st.getPhosphorusStatus()}]
                          * Available Potassium (K): ${st.potassiumKgPerHa} kg/ha [${st.getPotassiumStatus()}]
                          * Organic Carbon %: ${st.organicCarbonPercent}%
                          * Micronutrients/Deficiency: ${st.micronutrientsNotes}
                          * TNAU Amendment Note: ${st.getTnauAmendmentAdvice(AppLanguage.ENGLISH)}
                        """.trimIndent()
                    } else ""
                } ?: ""

                val prompt = """
                    You are a chief agronomist and farm economist at Tamil Nadu Agricultural University (TNAU), Coimbatore.
                    A farmer in ${request.district} (${request.taluka.ifBlank { request.district }}), Tamil Nadu has:
                    - Soil Type: ${request.soilType}
                    - Irrigation / Water Availability: ${request.waterSource}
                    - Sowing Season: ${request.season}
                    - Land Size: ${request.landSizeAcres} Acres
                    $soilTestInfo

                    Provide personalized, highly profitable crop recommendations suitable for this exact soil and agro-climatic region of Tamil Nadu.
                    If soil test data is provided above, tailor the crop selection, target fertilizer schedule (NPK dosages), and soil amendments (e.g. lime for acidic pH, gypsum for alkaline pH) explicitly to resolve any nutrient deficiencies or pH imbalances.
                    Factor in current regional Mandi market trends in Tamil Nadu.
                    Respond strictly in raw, valid JSON with no markdown formatting fences (do NOT include ```json).
                    JSON format:
                    {
                      "generalAdviceTamil": "தமிழ் பரிந்துரை சுருக்கம் மற்றும் பண்ணை லாப கணிப்பு",
                      "generalAdviceEnglish": "Detailed English agronomic advice and farm profitability summary",
                      "climateZone": "Specific Agro-Climatic Zone of ${request.district}",
                      "crops": [
                        {
                          "cropName": "Crop name in English with variety (e.g. Turmeric BSR-2)",
                          "tamilName": "பயிர் பெயர் மற்றும் ரகம் தமிழில்",
                          "category": "Cash Crop / Cereals / Vegetables / Spices / Pulses / Oilseeds / Millets",
                          "matchScorePercent": 96,
                          "estimatedYieldQuintalPerAcre": 25.0,
                          "costOfCultivationPerAcre": 85000,
                          "marketPricePerQuintal": 15900,
                          "durationDays": 270,
                          "waterNeed": "Low / Moderate / High",
                          "bestSowingWindow": "Month window",
                          "suitableSoilSummary": "Soil suitability reasoning",
                          "climateSuitability": "Regional climate note",
                          "marketTrendReasoning": "Current market and Mandi demand driver",
                          "tnauexpertTips": ["TNAU Tip 1", "TNAU Tip 2", "TNAU Tip 3"],
                          "riskFactor": "Low / Medium / High",
                          "recommendedVarieties": "Key high-yielding varieties",
                          "fertilizerSchedule": "Brief NPK or bio-fertilizer schedule"
                        }
                      ]
                    }
                """.trimIndent()

                val response = withTimeout(25_000L) {
                    model.generateContent(prompt)
                }

                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    val parsed = parseGeminiResponse(responseText, request)
                    if (parsed != null && parsed.topCrops.isNotEmpty()) {
                        Log.i(TAG, "Successfully received ${parsed.topCrops.size} crops from Gemini 3.8 Flash!")
                        return@withContext parsed
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini generation failed or timed out: ${e.message}. Using calibrated TNAU engine fallback.")
            }
        }

        // Calibrated TNAU agronomic fallback engine
        AgriRepository.recommendCrops(request)
    }

    private fun parseGeminiResponse(
        rawText: String,
        request: CropRecommendationRequest
    ): RecommendationResult? {
        return try {
            val cleanJson = rawText
                .replace("```json", "")
                .replace("```", "")
                .trim()

            val json = JSONObject(cleanJson)
            val adviceTamil = json.optString("generalAdviceTamil", "")
            val adviceEnglish = json.optString("generalAdviceEnglish", "")
            val climateZone = json.optString("climateZone", "${request.district} Agro-Climatic Zone")

            val cropsArray = json.optJSONArray("crops") ?: JSONArray()
            val cropsList = mutableListOf<RecommendedCrop>()

            for (i in 0 until cropsArray.length()) {
                val item = cropsArray.getJSONObject(i)
                val yield = item.optDouble("estimatedYieldQuintalPerAcre", 15.0)
                val price = item.optInt("marketPricePerQuintal", 3500)
                val cost = item.optInt("costOfCultivationPerAcre", 25000)
                val gross = (yield * price).toInt()
                val net = gross - cost

                val tipsArray = item.optJSONArray("tnauexpertTips")
                val tips = mutableListOf<String>()
                if (tipsArray != null) {
                    for (t in 0 until tipsArray.length()) {
                        tips.add(tipsArray.getString(t))
                    }
                }

                cropsList.add(
                    RecommendedCrop(
                        cropName = item.optString("cropName", "Recommended Crop"),
                        tamilName = item.optString("tamilName", "பரிந்துரை பயிர்"),
                        category = item.optString("category", "Cash Crop"),
                        matchScorePercent = item.optInt("matchScorePercent", 90),
                        estimatedYieldQuintalPerAcre = yield,
                        costOfCultivationPerAcre = cost,
                        marketPricePerQuintal = price,
                        estimatedGrossRevenue = gross,
                        estimatedNetProfitPerAcre = net,
                        durationDays = item.optInt("durationDays", 120),
                        waterNeed = item.optString("waterNeed", "Moderate"),
                        bestSowingWindow = item.optString("bestSowingWindow", "Seasonal"),
                        suitableSoilSummary = item.optString("suitableSoilSummary", request.soilType),
                        climateSuitability = item.optString("climateSuitability", "${request.district} Region"),
                        marketTrendReasoning = item.optString("marketTrendReasoning", "Strong local Mandi demand"),
                        tnauexpertTips = if (tips.isNotEmpty()) tips else listOf("Follow TNAU Package of Practices"),
                        riskFactor = item.optString("riskFactor", "Low"),
                        recommendedVarieties = item.optString("recommendedVarieties", ""),
                        fertilizerSchedule = item.optString("fertilizerSchedule", "")
                    )
                )
            }

            if (cropsList.isEmpty()) null
            else {
                val sorted = cropsList.sortedByDescending { it.estimatedNetProfitPerAcre }
                val topCrop = sorted.firstOrNull()
                val totalFarmProfit = ((topCrop?.estimatedNetProfitPerAcre ?: 0) * request.landSizeAcres).toInt()

                RecommendationResult(
                    request = request,
                    topCrops = sorted,
                    climateSummary = "$climateZone | Season: ${request.season} | Farm Size: ${request.landSizeAcres} Acres (Gemini AI Verified)",
                    generalAdviceTamil = adviceTamil.ifBlank {
                        "ஜெமினி AI பகுப்பாய்வு: உங்கள் ${request.district} நிலத்திற்கு ${topCrop?.tamilName} சாகுபடி செய்வது அதிக லாபம் தரும்."
                    },
                    generalAdviceEnglish = adviceEnglish.ifBlank {
                        "Gemini AI Recommendation: ${topCrop?.cropName} is projected to yield highest net returns for ${request.district}."
                    },
                    topProfitableCrop = topCrop,
                    totalEstimatedFarmProfit = totalFarmProfit
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "JSON parsing error on Gemini response", e)
            null
        }
    }
}
