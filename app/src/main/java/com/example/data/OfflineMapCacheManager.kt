package com.example.data

import com.example.model.AgriStore
import com.example.model.AppLanguage
import kotlin.math.*

data class OfflineTileRegion(
    val regionId: String,
    val name: String,
    val district: String,
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
    val tileCount: Int,
    val cacheSizeMb: Double,
    val isDownloaded: Boolean,
    val lastUpdated: String
)

data class OfflineTurnInstruction(
    val stepNumber: Int,
    val distanceKm: Double,
    val instructionTamil: String,
    val instructionEnglish: String,
    val iconType: String // "START", "STRAIGHT", "LEFT", "RIGHT", "DESTINATION"
)

data class OfflineNavigationRoute(
    val storeId: String,
    val storeName: String,
    val storeTamilName: String,
    val totalDistanceKm: Double,
    val estimatedTimeMinutes: Int,
    val waypoints: List<Pair<Double, Double>>,
    val instructions: List<OfflineTurnInstruction>
)

object OfflineMapCacheManager {

    private val availableRegions = listOf(
        OfflineTileRegion(
            regionId = "tn_delta",
            name = "Cauvery Delta Region (Thanjavur, Tiruvarur, Nagapattinam)",
            district = "Thanjavur",
            minLat = 10.10,
            maxLat = 11.20,
            minLon = 78.80,
            maxLon = 79.90,
            tileCount = 1420,
            cacheSizeMb = 18.4,
            isDownloaded = true,
            lastUpdated = "2026-11-01 06:00 AM"
        ),
        OfflineTileRegion(
            regionId = "tn_west",
            name = "Western Agri Belt (Coimbatore, Erode, Salem)",
            district = "Coimbatore",
            minLat = 10.80,
            maxLat = 11.90,
            minLon = 76.80,
            maxLon = 78.30,
            tileCount = 1850,
            cacheSizeMb = 22.1,
            isDownloaded = true,
            lastUpdated = "2026-11-01 06:00 AM"
        ),
        OfflineTileRegion(
            regionId = "tn_south",
            name = "Southern Districts (Madurai, Dindigul, Tirunelveli)",
            district = "Madurai",
            minLat = 8.50,
            maxLat = 10.50,
            minLon = 77.10,
            maxLon = 78.50,
            tileCount = 2100,
            cacheSizeMb = 26.8,
            isDownloaded = false,
            lastUpdated = "Not Downloaded"
        ),
        OfflineTileRegion(
            regionId = "tn_north",
            name = "Northern Belt (Villupuram, Tiruvannamalai, Kanchipuram)",
            district = "Villupuram",
            minLat = 11.80,
            maxLat = 13.20,
            minLon = 78.80,
            maxLon = 80.20,
            tileCount = 1680,
            cacheSizeMb = 19.5,
            isDownloaded = false,
            lastUpdated = "Not Downloaded"
        )
    )

    fun getRegions(): List<OfflineTileRegion> = availableRegions

    fun calculateTotalCacheSizeMb(): Double {
        return availableRegions.filter { it.isDownloaded }.sumOf { it.cacheSizeMb }
    }

    fun calculateTotalCachedTiles(): Int {
        return availableRegions.filter { it.isDownloaded }.sumOf { it.tileCount }
    }

    fun generateOfflineTurnByTurnRoute(
        farmerLat: Double = 10.7870,
        farmerLon: Double = 79.1378,
        store: AgriStore,
        lang: AppLanguage
    ): OfflineNavigationRoute {
        val storeLat = store.latitude
        val storeLon = store.longitude

        // Calculate Haversine distance
        val distKm = AgriRepository.calculateDistanceKm(farmerLat, farmerLon, storeLat, storeLon)
        val timeMins = (distKm / 35.0 * 60).roundToInt().coerceAtLeast(3)

        // Interpolate 4 offline turn waypoints
        val w1 = Pair(farmerLat, farmerLon)
        val w2 = Pair(farmerLat + (storeLat - farmerLat) * 0.35, farmerLon + (storeLon - farmerLon) * 0.25)
        val w3 = Pair(farmerLat + (storeLat - farmerLat) * 0.70, farmerLon + (storeLon - farmerLon) * 0.75)
        val w4 = Pair(storeLat, storeLon)

        val storeDisplayName = if (lang == AppLanguage.TAMIL) store.tamilName else store.name

        val instructions = listOf(
            OfflineTurnInstruction(
                stepNumber = 1,
                distanceKm = (distKm * 0.35).let { (it * 10).roundToInt() / 10.0 },
                instructionTamil = "உங்கள் பண்ணை/கிராமத்திலிருந்து பிரதான சாலை நோக்கி 300 மீ செல்லவும்.",
                instructionEnglish = "Head out from your farm towards the main village road for 300m.",
                iconType = "START"
            ),
            OfflineTurnInstruction(
                stepNumber = 2,
                distanceKm = (distKm * 0.35).let { (it * 10).roundToInt() / 10.0 },
                instructionTamil = "மாநில நெடுஞ்சாலை / PACCS இணைப்புச் சாலையில் இடதுபுறம் திரும்பவும்.",
                instructionEnglish = "Turn LEFT onto the State Highway / PACCS feeder road.",
                iconType = "LEFT"
            ),
            OfflineTurnInstruction(
                stepNumber = 3,
                distanceKm = (distKm * 0.30).let { (it * 10).roundToInt() / 10.0 },
                instructionTamil = "அரசு தொடக்க வேளாண் கூட்டுறவு சங்கம்/சந்தை அருகில் வலதுபுறம் திரும்பவும்.",
                instructionEnglish = "Turn RIGHT near the Agro Cooperative market junction.",
                iconType = "RIGHT"
            ),
            OfflineTurnInstruction(
                stepNumber = 4,
                distanceKm = 0.1,
                instructionTamil = "சேருமிடம் வலதுபுறம் அமைந்துள்ளது: $storeDisplayName.",
                instructionEnglish = "Arrived at destination on your right: $storeDisplayName.",
                iconType = "DESTINATION"
            )
        )

        return OfflineNavigationRoute(
            storeId = store.id,
            storeName = store.name,
            storeTamilName = store.tamilName,
            totalDistanceKm = distKm,
            estimatedTimeMinutes = timeMins,
            waypoints = listOf(w1, w2, w3, w4),
            instructions = instructions
        )
    }
}
