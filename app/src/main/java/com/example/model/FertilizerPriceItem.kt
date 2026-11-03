package com.example.model

/**
 * Model representing local retailer fertilizer prices for cost comparison across Tamil Nadu districts.
 */
data class FertilizerPriceItem(
    val id: String,
    val storeId: String,
    val storeName: String,
    val storeTamilName: String,
    val storeType: String, // PACCS Co-op, TNAU Agro Center, Authorized Private Dealer, FPO Organic Depot
    val productName: String,
    val productTamilName: String,
    val category: FertilizerType,
    val brand: String, // SPIC, IFFCO, Coromandel, IPL, FACT, Organic FPO
    val bagSize: String, // "45 kg", "50 kg", "1 Liter", "25 kg"
    val standardGovtPrice: Int, // Statutory Govt Subsidized Max Retail Price (MRP ₹)
    val retailerPrice: Int, // Retailer Selling Price (₹)
    val stockStatus: StockStatus,
    val district: String,
    val taluk: String,
    val phone: String,
    val distanceKm: Double,
    val rating: Double,
    val isGovtSubsidized: Boolean = true,
    val composition: String, // e.g. "46% Nitrogen"
    val lastUpdated: String = "Today 08:30 AM"
) {
    val savingsVsRetail: Int
        get() = (retailerPrice - standardGovtPrice).coerceAtLeast(0)

    val isGovtPrice: Boolean
        get() = retailerPrice <= standardGovtPrice
}

enum class StockStatus {
    IN_STOCK,
    LIMITED_STOCK,
    OUT_OF_STOCK;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        IN_STOCK -> if (lang == AppLanguage.TAMIL) "இருப்பில் உள்ளது" else "In Stock"
        LIMITED_STOCK -> if (lang == AppLanguage.TAMIL) "குறைந்த இருப்பு" else "Limited Stock"
        OUT_OF_STOCK -> if (lang == AppLanguage.TAMIL) "இருப்பில் இல்லை" else "Out of Stock"
    }
}
