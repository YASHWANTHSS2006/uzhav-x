package com.example.model

data class FertilizerProduct(
    val id: String,
    val name: String,
    val tamilName: String,
    val category: FertilizerType, // CHEMICAL, ORGANIC, BIO_PESTICIDE, SEEDS
    val brand: String,
    val bagSize: String, // e.g. "50 kg", "1 Liter", "25 kg"
    val standardPrice: Int, // ₹
    val subsidizedPrice: Int? = null, // ₹ if subsidized by govt
    val inStock: Boolean = true,
    val composition: String, // e.g. "46% N" or "Panchagavya organic solution"
    val usageNotes: String
)

enum class FertilizerType {
    CHEMICAL,
    ORGANIC,
    BIO_PESTICIDE,
    SEEDS;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        CHEMICAL -> if (lang == AppLanguage.TAMIL) "இரசாயன உரங்கள்" else "Chemical Fertilizers"
        ORGANIC -> if (lang == AppLanguage.TAMIL) "இயற்கை உரங்கள்" else "Organic Bio-Inputs"
        BIO_PESTICIDE -> if (lang == AppLanguage.TAMIL) "உயிரி பூச்சிக்கொல்லி" else "Bio-Pesticides"
        SEEDS -> if (lang == AppLanguage.TAMIL) "சான்றளிக்கப்பட்ட விதைகள்" else "Certified Seeds"
    }
}

data class AgriStore(
    val id: String,
    val name: String,
    val tamilName: String,
    val storeType: String, // PACCS Co-operative, TNAU Agro Center, Private Agro Dealer, FPO Organic Depot
    val address: String,
    val taluk: String,
    val district: String,
    val phone: String,
    val rating: Double,
    val reviewCount: Int,
    val distanceKm: Double,
    val isGovtAided: Boolean,
    val mapOffsetX: Float, // 0f to 1f on custom Map Canvas
    val mapOffsetY: Float, // 0f to 1f on custom Map Canvas
    val products: List<FertilizerProduct>,
    val latitude: Double = 10.7870,
    val longitude: Double = 79.1378
) {
    /**
     * Retrieves the best price to display on map pins based on filter
     */
    fun getDisplayPrice(
        categoryFilter: FertilizerType? = null,
        specificNameFilter: String? = null
    ): Pair<Int, Boolean>? {
        // Filter products matching criteria
        val matching = products.filter { prod ->
            val matchCategory = categoryFilter == null || prod.category == categoryFilter
            val matchName = specificNameFilter.isNullOrBlank() ||
                    prod.name.contains(specificNameFilter, ignoreCase = true) ||
                    prod.tamilName.contains(specificNameFilter, ignoreCase = true)
            matchCategory && matchName
        }

        if (matching.isEmpty()) {
            // If no exact match, fallback to lowest price in store
            val lowest = products.minByOrNull { it.subsidizedPrice ?: it.standardPrice }
            return lowest?.let { (it.subsidizedPrice ?: it.standardPrice) to it.inStock }
        }

        val best = matching.minByOrNull { it.subsidizedPrice ?: it.standardPrice }
        return best?.let { (it.subsidizedPrice ?: it.standardPrice) to it.inStock }
    }
}
