package com.example.model

data class MandiPrice(
    val id: String,
    val cropName: String,
    val tamilName: String,
    val category: String, // Grains, Vegetables, Fruits, Cash Crops, Spices, Pulses, Oilseeds, Plantation
    val marketName: String,
    val district: String,
    val minPrice: Int, // ₹
    val maxPrice: Int, // ₹
    val modalPrice: Int, // ₹ typical price
    val unit: String = "Quintal (100 kg)",
    val changeAmount: Int, // e.g. +150
    val isPositive: Boolean,
    val arrivalVolume: String, // e.g. "450 Bags"
    val lastUpdated: String,
    val priceHistory7Days: List<Int>, // for sparkline / trend graph
    val regionalComparisons: List<RegionalMandiComparison> = emptyList()
)

data class RegionalMandiComparison(
    val marketName: String,
    val district: String,
    val price: Int,
    val unit: String,
    val changeAmount: Int,
    val isPositive: Boolean,
    val arrivalVolume: String,
    val isHighestPrice: Boolean = false
)

data class MandiRegionalSummary(
    val totalCommodities: Int,
    val topGainerCrop: String,
    val topGainerGain: String,
    val topGainerMarket: String,
    val topArrivalMarket: String,
    val topArrivalVolume: String,
    val stateMarketStatusTamil: String,
    val stateMarketStatusEnglish: String
)

data class MandiSummary(
    val totalCommodities: Int,
    val topGainer: MandiPrice,
    val topLoser: MandiPrice,
    val averageIndexChange: Double
)

data class MandiSearchGroundingResult(
    val query: String,
    val summaryEnglish: String,
    val summaryTamil: String,
    val estimatedPriceRange: String,
    val topMarkets: List<String>,
    val marketTrend: String, // Bullish, Stable, Bearish
    val sources: List<String> = listOf("Google Search Grounding", "Agmarknet Tamil Nadu", "TNAU Market Intelligence (DEMIC)", "e-NAM Mandi Portal"),
    val timestamp: String,
    val isLiveGrounded: Boolean = true
)

