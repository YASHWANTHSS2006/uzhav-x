package com.example.data

import android.content.Context
import com.example.data.local.CachedMandiPriceEntity
import com.example.data.local.CachedSubsidyEntity
import com.example.data.local.UzhavDatabase
import com.example.model.GovtScheme
import com.example.model.MandiPrice
import com.example.model.RegionalMandiComparison
import com.example.model.SchemeCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Repository pattern for Room Database local offline caching of
 * Mandi Crop Prices and Government Subsidy Information.
 */
class RoomCacheRepository(context: Context) {

    private val db = UzhavDatabase.getDatabase(context)
    private val mandiDao = db.mandiDao()
    private val subsidyDao = db.subsidyDao()

    // --- Mandi Prices Cache ---

    val cachedMandiPricesFlow: Flow<List<MandiPrice>> = mandiDao.getAllPricesFlow().map { list ->
        list.map { it.toDomainModel() }
    }

    suspend fun getCachedMandiPrices(): List<MandiPrice> = withContext(Dispatchers.IO) {
        mandiDao.getAllPrices().map { it.toDomainModel() }
    }

    suspend fun cacheMandiPrices(prices: List<MandiPrice>) = withContext(Dispatchers.IO) {
        if (prices.isNotEmpty()) {
            val entities = prices.map { it.toEntity() }
            mandiDao.insertAll(entities)
        }
    }

    // --- Government Subsidies / Schemes Cache ---

    val cachedSubsidiesFlow: Flow<List<GovtScheme>> = subsidyDao.getAllSubsidiesFlow().map { list ->
        list.map { it.toDomainModel() }
    }

    suspend fun getCachedSubsidies(): List<GovtScheme> = withContext(Dispatchers.IO) {
        subsidyDao.getAllSubsidies().map { it.toDomainModel() }
    }

    suspend fun cacheSubsidies(schemes: List<GovtScheme>) = withContext(Dispatchers.IO) {
        if (schemes.isNotEmpty()) {
            val entities = schemes.map { it.toEntity() }
            subsidyDao.insertAll(entities)
        }
    }

    companion object {
        // --- Mapping Extension Helpers ---

        fun MandiPrice.toEntity(): CachedMandiPriceEntity {
            val historyCsv = priceHistory7Days.joinToString(",")
            val regionalJson = regionalComparisons.joinToString(";") { comp ->
                "${comp.marketName}|${comp.district}|${comp.price}|${comp.unit}|${comp.changeAmount}|${comp.isPositive}|${comp.arrivalVolume}|${comp.isHighestPrice}"
            }
            return CachedMandiPriceEntity(
                id = id,
                cropName = cropName,
                tamilName = tamilName,
                category = category,
                marketName = marketName,
                district = district,
                minPrice = minPrice,
                maxPrice = maxPrice,
                modalPrice = modalPrice,
                unit = unit,
                changeAmount = changeAmount,
                isPositive = isPositive,
                arrivalVolume = arrivalVolume,
                lastUpdated = lastUpdated,
                priceHistory7DaysCsv = historyCsv,
                regionalComparisonsJson = regionalJson
            )
        }

        fun CachedMandiPriceEntity.toDomainModel(): MandiPrice {
            val history = if (priceHistory7DaysCsv.isNotBlank()) {
                priceHistory7DaysCsv.split(",").mapNotNull { it.trim().toIntOrNull() }
            } else emptyList()

            val comps = if (regionalComparisonsJson.isNotBlank()) {
                regionalComparisonsJson.split(";").mapNotNull { part ->
                    val fields = part.split("|")
                    if (fields.size >= 8) {
                        RegionalMandiComparison(
                            marketName = fields[0],
                            district = fields[1],
                            price = fields[2].toIntOrNull() ?: 0,
                            unit = fields[3],
                            changeAmount = fields[4].toIntOrNull() ?: 0,
                            isPositive = fields[5].toBooleanStrictOrNull() ?: false,
                            arrivalVolume = fields[6],
                            isHighestPrice = fields[7].toBooleanStrictOrNull() ?: false
                        )
                    } else null
                }
            } else emptyList()

            return MandiPrice(
                id = id,
                cropName = cropName,
                tamilName = tamilName,
                category = category,
                marketName = marketName,
                district = district,
                minPrice = minPrice,
                maxPrice = maxPrice,
                modalPrice = modalPrice,
                unit = unit,
                changeAmount = changeAmount,
                isPositive = isPositive,
                arrivalVolume = arrivalVolume,
                lastUpdated = lastUpdated,
                priceHistory7Days = if (history.isNotEmpty()) history else listOf(modalPrice, modalPrice),
                regionalComparisons = comps
            )
        }

        fun GovtScheme.toEntity(): CachedSubsidyEntity {
            return CachedSubsidyEntity(
                id = id,
                schemeCode = schemeCode,
                yearValidity = yearValidity,
                title = title,
                tamilTitle = tamilTitle,
                department = department,
                category = category.name,
                subsidyPercentage = subsidyPercentage,
                maxFinancialBenefit = maxFinancialBenefit,
                description = summaryEnglish,
                tamilDescription = summaryTamil,
                eligibilityCsv = eligibility.joinToString("|||"),
                requiredDocumentsCsv = requiredDocuments.joinToString("|||"),
                howToApplyStepsCsv = howToApplySteps.joinToString("|||"),
                portalUrl = officialPortalUrl,
                helplineNumber = helplineNumber,
                isPanchayatScheme = isPanchayatScheme,
                isNewFor2026 = isNewFor2026
            )
        }

        fun CachedSubsidyEntity.toDomainModel(): GovtScheme {
            val catEnum = try {
                SchemeCategory.valueOf(category)
            } catch (_: Exception) {
                SchemeCategory.ALL
            }

            return GovtScheme(
                id = id,
                schemeCode = schemeCode.ifBlank { "TN-2026-SUBSIDY" },
                yearValidity = yearValidity.ifBlank { "2026-2027 (Active)" },
                title = title,
                tamilTitle = tamilTitle,
                department = department.ifBlank { "TN Agriculture Dept" },
                category = catEnum,
                subsidyPercentage = subsidyPercentage.ifBlank { "100% Subsidy" },
                maxFinancialBenefit = maxFinancialBenefit.ifBlank { "As per Govt Norms" },
                summaryEnglish = description,
                summaryTamil = tamilDescription,
                eligibility = if (eligibilityCsv.isNotBlank()) eligibilityCsv.split("|||") else emptyList(),
                requiredDocuments = if (requiredDocumentsCsv.isNotBlank()) requiredDocumentsCsv.split("|||") else emptyList(),
                howToApplySteps = if (howToApplyStepsCsv.isNotBlank()) howToApplyStepsCsv.split("|||") else emptyList(),
                officialPortalUrl = portalUrl,
                helplineNumber = helplineNumber.ifBlank { "1800-180-1551" },
                isPanchayatScheme = isPanchayatScheme,
                isNewFor2026 = isNewFor2026
            )
        }
    }
}
