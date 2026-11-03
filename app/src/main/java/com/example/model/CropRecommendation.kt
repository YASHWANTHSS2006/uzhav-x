package com.example.model

data class CropRecommendationRequest(
    val state: String = "Tamil Nadu",
    val district: String = "Thanjavur",
    val taluka: String = "Orathanadu",
    val village: String = "Pudur",
    val soilType: String = "Alluvial Loam (வண்டல் மண்)",
    val season: String = "Samba / Rabi (சம்பா பருவம் - Oct to Jan)",
    val waterSource: String = "Canal & Borewell (வாய்க்கால் & ஆழ்துளை)",
    val landSizeAcres: Double = 3.0,
    val soilTestResult: SoilTestResult? = null
)

data class RecommendedCrop(
    val cropName: String,
    val tamilName: String,
    val category: String, // Grain, Cash Crop, Pulse, Oilseed, Spices, Vegetable, Millet
    val matchScorePercent: Int, // e.g. 96%
    val estimatedYieldQuintalPerAcre: Double,
    val costOfCultivationPerAcre: Int, // ₹
    val marketPricePerQuintal: Int, // ₹ from current Mandi
    val estimatedGrossRevenue: Int, // ₹
    val estimatedNetProfitPerAcre: Int, // ₹
    val durationDays: Int,
    val waterNeed: String, // Low, Moderate, High
    val bestSowingWindow: String,
    val suitableSoilSummary: String,
    val climateSuitability: String,
    val marketTrendReasoning: String,
    val tnauexpertTips: List<String>,
    val riskFactor: String, // Low, Medium, High
    val recommendedVarieties: String = "",
    val fertilizerSchedule: String = ""
) {
    fun calculateTotalNetProfit(acres: Double): Int = (estimatedNetProfitPerAcre * acres).toInt()
    fun calculateTotalGrossRevenue(acres: Double): Int = (estimatedGrossRevenue * acres).toInt()
    fun calculateTotalCost(acres: Double): Int = (costOfCultivationPerAcre * acres).toInt()
    val roiPercent: Int
        get() = if (costOfCultivationPerAcre > 0) ((estimatedNetProfitPerAcre.toDouble() / costOfCultivationPerAcre) * 100).toInt() else 0
}

data class RecommendationResult(
    val request: CropRecommendationRequest,
    val topCrops: List<RecommendedCrop>,
    val climateSummary: String,
    val generalAdviceTamil: String,
    val generalAdviceEnglish: String,
    val topProfitableCrop: RecommendedCrop? = topCrops.firstOrNull(),
    val totalEstimatedFarmProfit: Int = ((topCrops.firstOrNull()?.estimatedNetProfitPerAcre ?: 0) * request.landSizeAcres).toInt()
)
