package com.example.data

import com.example.model.AppLanguage
import com.example.model.FertilizerPriceItem
import com.example.model.FertilizerType
import com.example.model.StockStatus
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object FertilizerPriceService {

    val sampleFertilizerPrices: List<FertilizerPriceItem> = listOf(
        // THANJAVUR
        FertilizerPriceItem(
            id = "fp1",
            storeId = "s1",
            storeName = "Orathanadu Primary Agri Co-operative (PACCS)",
            storeTamilName = "ஒரத்தநாடு தொடக்க வேளாண்மை கூட்டுறவு கடன் சங்கம்",
            storeType = "PACCS Govt Co-operative",
            productName = "Neem Coated Urea (45 kg)",
            productTamilName = "நீம் கோட்டட் யூரியா (45 கிலோ)",
            category = FertilizerType.CHEMICAL,
            brand = "IFFCO",
            bagSize = "45 kg",
            standardGovtPrice = 266,
            retailerPrice = 266,
            stockStatus = StockStatus.IN_STOCK,
            district = "Thanjavur",
            taluk = "Orathanadu",
            phone = "04372-234120",
            distanceKm = 1.4,
            rating = 4.8,
            isGovtSubsidized = true,
            composition = "46% Nitrogen (Basal & Top Dressing)",
            lastUpdated = "Today 08:30 AM"
        ),
        FertilizerPriceItem(
            id = "fp2",
            storeId = "s1",
            storeName = "Orathanadu Primary Agri Co-operative (PACCS)",
            storeTamilName = "ஒரத்தநாடு தொடக்க வேளாண்மை கூட்டுறவு கடன் சங்கம்",
            storeType = "PACCS Govt Co-operative",
            productName = "DAP 18:46:00 (Di-Ammonium Phosphate)",
            productTamilName = "டை அம்மோனியம் பாஸ்பேட் (DAP)",
            category = FertilizerType.CHEMICAL,
            brand = "SPIC",
            bagSize = "50 kg",
            standardGovtPrice = 1350,
            retailerPrice = 1350,
            stockStatus = StockStatus.IN_STOCK,
            district = "Thanjavur",
            taluk = "Orathanadu",
            phone = "04372-234120",
            distanceKm = 1.4,
            rating = 4.8,
            isGovtSubsidized = true,
            composition = "18% N + 46% P2O5 (Root Development)",
            lastUpdated = "Today 08:30 AM"
        ),
        FertilizerPriceItem(
            id = "fp3",
            storeId = "s_tj_pvt1",
            storeName = "Kaveri Delta Agro Inputs Private Store",
            storeTamilName = "காவேரி டெல்டா அக்ரோ இன்ஃபட்ஸ் (தனியார் சில்லறை)",
            storeType = "Authorized Private Dealer",
            productName = "DAP 18:46:00 (Di-Ammonium Phosphate)",
            productTamilName = "டை அம்மோனியம் பாஸ்பேட் (DAP)",
            category = FertilizerType.CHEMICAL,
            brand = "Coromandel Gromor",
            bagSize = "50 kg",
            standardGovtPrice = 1350,
            retailerPrice = 1420,
            stockStatus = StockStatus.IN_STOCK,
            district = "Thanjavur",
            taluk = "Thanjavur South",
            phone = "04362-278190",
            distanceKm = 4.2,
            rating = 4.3,
            isGovtSubsidized = true,
            composition = "18% N + 46% P2O5",
            lastUpdated = "Today 09:15 AM"
        ),
        FertilizerPriceItem(
            id = "fp4",
            storeId = "s1",
            storeName = "Orathanadu Primary Agri Co-operative (PACCS)",
            storeTamilName = "ஒரத்தநாடு தொடக்க வேளாண்மை கூட்டுறவு கடன் சங்கம்",
            storeType = "PACCS Govt Co-operative",
            productName = "MOP Muriate of Potash (60% K)",
            productTamilName = "பொட்டாஷ் உரம் (MOP 60% Potash)",
            category = FertilizerType.CHEMICAL,
            brand = "IPL (Indian Potash)",
            bagSize = "50 kg",
            standardGovtPrice = 1700,
            retailerPrice = 1700,
            stockStatus = StockStatus.LIMITED_STOCK,
            district = "Thanjavur",
            taluk = "Orathanadu",
            phone = "04372-234120",
            distanceKm = 1.4,
            rating = 4.8,
            isGovtSubsidized = true,
            composition = "60% Water Soluble Potash (K2O)",
            lastUpdated = "Today 08:30 AM"
        ),

        // MADURAI
        FertilizerPriceItem(
            id = "fp5",
            storeId = "s2",
            storeName = "SPIC Agro Farmers Service Center Madurai",
            storeTamilName = "ஸ்பிக் வேளாண் சேவை மையம் - மதுரை",
            storeType = "TNAU / Factory Outlet",
            productName = "Neem Coated Urea (45 kg)",
            productTamilName = "நீம் கோட்டட் யூரியா (45 கிலோ)",
            category = FertilizerType.CHEMICAL,
            brand = "SPIC Urea",
            bagSize = "45 kg",
            standardGovtPrice = 266,
            retailerPrice = 266,
            stockStatus = StockStatus.IN_STOCK,
            district = "Madurai",
            taluk = "Madurai West",
            phone = "0452-2534110",
            distanceKm = 2.8,
            rating = 4.6,
            isGovtSubsidized = true,
            composition = "46% Nitrogen",
            lastUpdated = "Today 09:00 AM"
        ),
        FertilizerPriceItem(
            id = "fp6",
            storeId = "s2",
            storeName = "SPIC Agro Farmers Service Center Madurai",
            storeTamilName = "ஸ்பிக் வேளாண் சேவை மையம் - மதுரை",
            storeType = "TNAU / Factory Outlet",
            productName = "Complex NPK 20:20:0:13 (Sulphur)",
            productTamilName = "காம்ப்ளக்ஸ் NPK 20:20:0:13 உரம்",
            category = FertilizerType.CHEMICAL,
            brand = "SPIC Jothi",
            bagSize = "50 kg",
            standardGovtPrice = 1200,
            retailerPrice = 1200,
            stockStatus = StockStatus.IN_STOCK,
            district = "Madurai",
            taluk = "Madurai West",
            phone = "0452-2534110",
            distanceKm = 2.8,
            rating = 4.6,
            isGovtSubsidized = true,
            composition = "20% N, 20% P, 13% Sulphur",
            lastUpdated = "Today 09:00 AM"
        ),
        FertilizerPriceItem(
            id = "fp7",
            storeId = "s_mad_pvt",
            storeName = "Meenakshi Agro Traders Madurai",
            storeTamilName = "மீனாட்சி அக்ரோ டிரேடர்ஸ் மதுரை",
            storeType = "Authorized Private Dealer",
            productName = "Complex NPK 20:20:0:13 (Sulphur)",
            productTamilName = "காம்ப்ளக்ஸ் NPK 20:20:0:13 உரம்",
            category = FertilizerType.CHEMICAL,
            brand = "Coromandel",
            bagSize = "50 kg",
            standardGovtPrice = 1200,
            retailerPrice = 1280,
            stockStatus = StockStatus.IN_STOCK,
            district = "Madurai",
            taluk = "Melur",
            phone = "0452-2410982",
            distanceKm = 6.5,
            rating = 4.4,
            isGovtSubsidized = true,
            composition = "20% N, 20% P, 13% Sulphur",
            lastUpdated = "Today 10:10 AM"
        ),

        // SALEM / KONGU REGION
        FertilizerPriceItem(
            id = "fp8",
            storeId = "s3",
            storeName = "Kongu Natural Organic Agro Depot Salem",
            storeTamilName = "கொங்கு இயற்கை உரங்கள் நிலையம் - சேலம்",
            storeType = "FPO Organic Depot",
            productName = "Enriched Vermicompost (மண்புழு உரம்)",
            productTamilName = "செறிவூட்டப்பட்ட மண்புழு உரம்",
            category = FertilizerType.ORGANIC,
            brand = "Kongu Bio Organic",
            bagSize = "50 kg",
            standardGovtPrice = 400,
            retailerPrice = 380,
            stockStatus = StockStatus.IN_STOCK,
            district = "Salem",
            taluk = "Attur",
            phone = "0427-2490112",
            distanceKm = 3.1,
            rating = 4.9,
            isGovtSubsidized = false,
            composition = "Humic acid 8%, Organic carbon 16%, Microbes",
            lastUpdated = "Today 08:45 AM"
        ),
        FertilizerPriceItem(
            id = "fp9",
            storeId = "s3",
            storeName = "Kongu Natural Organic Agro Depot Salem",
            storeTamilName = "கொங்கு இயற்கை உரங்கள் நிலையம் - சேலம்",
            storeType = "FPO Organic Depot",
            productName = "Panchagavya Organic Bio Liquid (1 Litre)",
            productTamilName = "பஞ்சகவ்யா இயற்கை உயிர் உரம் (1 லிட்டர்)",
            category = FertilizerType.ORGANIC,
            brand = "TNAU Certified Bio Organic",
            bagSize = "1 Liter",
            standardGovtPrice = 220,
            retailerPrice = 200,
            stockStatus = StockStatus.IN_STOCK,
            district = "Salem",
            taluk = "Attur",
            phone = "0427-2490112",
            distanceKm = 3.1,
            rating = 4.9,
            isGovtSubsidized = false,
            composition = "Traditional 9 ingredients, Auxin & Cytokinin growth promoter",
            lastUpdated = "Today 08:45 AM"
        ),
        FertilizerPriceItem(
            id = "fp10",
            storeId = "s_salem_paccs",
            storeName = "Attur Primary Agri Co-operative (PACCS)",
            storeTamilName = "ஆத்தூர் தொடக்க வேளாண்மை கூட்டுறவு சங்கம்",
            storeType = "PACCS Govt Co-operative",
            productName = "Single Super Phosphate SSP (16% P)",
            productTamilName = "சிங்கிள் சூப்பர் பாஸ்பேட் (SSP 16% P)",
            category = FertilizerType.CHEMICAL,
            brand = "Coromandel",
            bagSize = "50 kg",
            standardGovtPrice = 650,
            retailerPrice = 650,
            stockStatus = StockStatus.IN_STOCK,
            district = "Salem",
            taluk = "Attur",
            phone = "04282-221045",
            distanceKm = 2.0,
            rating = 4.7,
            isGovtSubsidized = true,
            composition = "16% P2O5, 11% Sulphur, 21% Calcium",
            lastUpdated = "Today 07:50 AM"
        ),

        // TIRUCHIRAPPALLI
        FertilizerPriceItem(
            id = "fp11",
            storeId = "s4",
            storeName = "Tiruchirappalli Agro Service Farmers Co-op Depot",
            storeTamilName = "திருச்சிராப்பள்ளி விவசாயிகள் கூட்டுறவு உரக் கிடங்கு",
            storeType = "PACCS Govt Co-operative",
            productName = "Neem Coated Urea (45 kg)",
            productTamilName = "நீம் கோட்டட் யூரியா (45 கிலோ)",
            category = FertilizerType.CHEMICAL,
            brand = "KRIBHCO",
            bagSize = "45 kg",
            standardGovtPrice = 266,
            retailerPrice = 266,
            stockStatus = StockStatus.IN_STOCK,
            district = "Tiruchirappalli",
            taluk = "Lalgudi",
            phone = "0431-2704980",
            distanceKm = 3.5,
            rating = 4.7,
            isGovtSubsidized = true,
            composition = "46% Nitrogen",
            lastUpdated = "Today 08:15 AM"
        ),
        FertilizerPriceItem(
            id = "fp12",
            storeId = "s4",
            storeName = "Tiruchirappalli Agro Service Farmers Co-op Depot",
            storeTamilName = "திருச்சிராப்பள்ளி விவசாயிகள் கூட்டுறவு உரக் கிடங்கு",
            storeType = "PACCS Govt Co-operative",
            productName = "MOP Muriate of Potash (60% K)",
            productTamilName = "பொட்டாஷ் உரம் (MOP 60% Potash)",
            category = FertilizerType.CHEMICAL,
            brand = "IPL",
            bagSize = "50 kg",
            standardGovtPrice = 1700,
            retailerPrice = 1700,
            stockStatus = StockStatus.IN_STOCK,
            district = "Tiruchirappalli",
            taluk = "Lalgudi",
            phone = "0431-2704980",
            distanceKm = 3.5,
            rating = 4.7,
            isGovtSubsidized = true,
            composition = "60% Potash (K2O)",
            lastUpdated = "Today 08:15 AM"
        ),

        // COIMBATORE
        FertilizerPriceItem(
            id = "fp13",
            storeId = "s_cbe_tnau",
            storeName = "TNAU Campus Agro Inputs Outlet Coimbatore",
            storeTamilName = "தமிழ்நாடு வேளாண் பல்கலைக்கழக விற்பனை மையம் - கோவை",
            storeType = "TNAU / Govt Center",
            productName = "Azospirillum & Phosphobacteria Bio-Fertilizer",
            productTamilName = "அசோஸ்பைரில்லம் & பாஸ்போபாக்டீரியா உயிரி உரம்",
            category = FertilizerType.ORGANIC,
            brand = "TNAU AgriTech",
            bagSize = "1 kg Pack",
            standardGovtPrice = 90,
            retailerPrice = 90,
            stockStatus = StockStatus.IN_STOCK,
            district = "Coimbatore",
            taluk = "Coimbatore South",
            phone = "0422-6611200",
            distanceKm = 4.0,
            rating = 4.9,
            isGovtSubsidized = true,
            composition = "Nitrogen fixing & Phosphate solubilizing bio-agent",
            lastUpdated = "Today 09:30 AM"
        ),
        FertilizerPriceItem(
            id = "fp14",
            storeId = "s_cbe_pvt",
            storeName = "Kongu Agro Chemicals Pollachi",
            storeTamilName = "கொங்கு அக்ரோ கெமிக்கல்ஸ் பொள்ளாச்சி",
            storeType = "Authorized Private Dealer",
            productName = "DAP 18:46:00 (Di-Ammonium Phosphate)",
            productTamilName = "டை அம்மோனியம் பாஸ்பேட் (DAP)",
            category = FertilizerType.CHEMICAL,
            brand = "IPL DAP",
            bagSize = "50 kg",
            standardGovtPrice = 1350,
            retailerPrice = 1400,
            stockStatus = StockStatus.LIMITED_STOCK,
            district = "Coimbatore",
            taluk = "Pollachi",
            phone = "04259-223490",
            distanceKm = 7.8,
            rating = 4.2,
            isGovtSubsidized = true,
            composition = "18% N + 46% P2O5",
            lastUpdated = "Today 10:00 AM"
        ),

        // TIRUVARUR / DELTA
        FertilizerPriceItem(
            id = "fp15",
            storeId = "s_tvr_paccs",
            storeName = "Needamangalam Primary Agri Co-op Depot",
            storeTamilName = "நீடாமங்கலம் தொடக்க வேளாண்மை கூட்டுறவு வங்கி",
            storeType = "PACCS Govt Co-operative",
            productName = "Neem Coated Urea (45 kg)",
            productTamilName = "நீம் கோட்டட் யூரியா (45 கிலோ)",
            category = FertilizerType.CHEMICAL,
            brand = "IFFCO",
            bagSize = "45 kg",
            standardGovtPrice = 266,
            retailerPrice = 266,
            stockStatus = StockStatus.IN_STOCK,
            district = "Tiruvarur",
            taluk = "Needamangalam",
            phone = "04367-252100",
            distanceKm = 1.9,
            rating = 4.8,
            isGovtSubsidized = true,
            composition = "46% Nitrogen",
            lastUpdated = "Today 08:00 AM"
        )
    )

    data class FertilizerRequirement(
        val cropName: String,
        val acres: Double,
        val ureaBagsCount: Int,
        val dapBagsCount: Int,
        val mopBagsCount: Int,
        val sspBagsCount: Int,
        val totalEstimatedCostGovt: Int,
        val totalEstimatedCostRetail: Int,
        val potentialSavings: Int,
        val applicationAdviceTamil: String,
        val applicationAdviceEnglish: String
    )

    fun calculateFertilizerRequirement(crop: String, acres: Double): FertilizerRequirement {
        val safeAcres = acres.coerceAtLeast(0.5)
        
        val (uPerAcre, dPerAcre, mPerAcre, sspPerAcre) = when {
            crop.contains("Paddy", ignoreCase = true) || crop.contains("நெல்", ignoreCase = true) -> {
                Tuple4(2.5, 1.2, 1.0, 0.0)
            }
            crop.contains("Sugarcane", ignoreCase = true) || crop.contains("கரும்பு", ignoreCase = true) -> {
                Tuple4(4.0, 2.0, 2.5, 0.0)
            }
            crop.contains("Cotton", ignoreCase = true) || crop.contains("பருத்தி", ignoreCase = true) -> {
                Tuple4(2.0, 1.0, 1.0, 0.0)
            }
            crop.contains("Maize", ignoreCase = true) || crop.contains("மக்காச்சோளம்", ignoreCase = true) -> {
                Tuple4(2.0, 1.0, 0.8, 0.0)
            }
            crop.contains("Groundnut", ignoreCase = true) || crop.contains("நிலக்கடலை", ignoreCase = true) -> {
                Tuple4(0.8, 0.5, 0.8, 2.0)
            }
            else -> Tuple4(1.5, 1.0, 0.8, 0.0)
        }

        val ureaBags = Math.round(uPerAcre * safeAcres).toInt().coerceAtLeast(1)
        val dapBags = Math.round(dPerAcre * safeAcres).toInt().coerceAtLeast(1)
        val mopBags = Math.round(mPerAcre * safeAcres).toInt()
        val sspBags = Math.round(sspPerAcre * safeAcres).toInt()

        // Rates: Urea 266, DAP 1350, MOP 1700, SSP 650
        val govtTotal = (ureaBags * 266) + (dapBags * 1350) + (mopBags * 1700) + (sspBags * 650)
        val retailTotal = (ureaBags * 275) + (dapBags * 1420) + (mopBags * 1800) + (sspBags * 700)
        val savings = retailTotal - govtTotal

        val (adviceTa, adviceEn) = if (crop.contains("Paddy", ignoreCase = true) || crop.contains("நெல்", ignoreCase = true)) {
            "அடிப்படை உரமளிப்பாக DAP முழுவதையும், யூரியா மற்றும் பொட்டாஷை 3 சம தவணைகளாக (நட்ட 15, 35, 55 நாட்களில்) இடவும்." to
                    "Apply all DAP as basal dose. Split Urea and MOP into 3 equal doses (15, 35, 55 days after planting)."
        } else {
            "அடிப்படை உரமளிப்பாக பாஸ்பேட் உரங்களை இடவும். யூரியாவை மேலுரமாக 20-25 நாட்கள் இடைவெளியில் பிரிக்கவும்." to
                    "Apply phosphate fertilizers as basal dose. Split urea into top dressings at 20-25 day intervals."
        }

        return FertilizerRequirement(
            cropName = crop,
            acres = safeAcres,
            ureaBagsCount = ureaBags,
            dapBagsCount = dapBags,
            mopBagsCount = mopBags,
            sspBagsCount = sspBags,
            totalEstimatedCostGovt = govtTotal,
            totalEstimatedCostRetail = retailTotal,
            potentialSavings = savings,
            applicationAdviceTamil = adviceTa,
            applicationAdviceEnglish = adviceEn
        )
    }

    suspend fun fetchAiFertilizerAdvisory(
        district: String,
        selectedFertilizer: String,
        lang: AppLanguage
    ): String = withContext(Dispatchers.IO) {
        try {
            val generativeModel = Firebase.ai.generativeModel("gemini-2.5-flash")
            val isTamil = lang == AppLanguage.TAMIL
            val prompt = if (isTamil) {
                "நீ தமிழக வேளாண்மைத்துறை உர ஆலோசகர். மாவட்டம்: $district. உரம்/தேவை: $selectedFertilizer. " +
                        "விவசாயிகளுக்கு கூட்டுறவு சங்கம் (PACCS) மூலம் நியாயமான அரசு மானிய விலையில் உரங்கள் வாங்குவது, " +
                        "தேவையான அளவு, மற்றும் போலி உரங்களை கண்டறிவது பற்றிய சுருக்கமான 3 முக்கிய ஆலோசனைகளை தமிழில் வழங்கவும்."
            } else {
                "You are an agricultural fertilizer expert for Tamil Nadu, India. District: $district. Fertilizer query: $selectedFertilizer. " +
                        "Provide 3 concise practical advice points for farmers regarding statutory subsidised price (MRP ₹266 Urea, ₹1,350 DAP), PACCS co-operative stock, and avoiding over-application."
            }

            val response = generativeModel.generateContent(prompt)
            response.text?.take(300) ?: getDefaultAdvisory(district, selectedFertilizer, lang)
        } catch (e: Exception) {
            getDefaultAdvisory(district, selectedFertilizer, lang)
        }
    }

    private fun getDefaultAdvisory(district: String, selectedFertilizer: String, lang: AppLanguage): String {
        return if (lang == AppLanguage.TAMIL) {
            "💡 $district மாவட்டத்தில் அரசு தொடக்க வேளாண்மை கூட்டுறவு சங்கங்களில் (PACCS) நீம் கோட்டட் யூரியா ₹266 மற்றும் DAP ₹1,350 நிர்ணயிக்கப்பட்ட அரசு மானிய விலையில் மட்டுமே விற்கப்படுகிறது. தனியார் கடைகளில் கூடுதல் விலை வசூலித்தால் வேளாண் உதவி இயக்குனரிடம் புகார் தெரிவிக்கலாம்."
        } else {
            "💡 In $district district, Neem Coated Urea is available at ₹266 (45kg) and DAP at ₹1,350 (50kg) via PACCS Co-operative centers. Always verify the POS receipt for government subsidized rates."
        }
    }
}

private data class Tuple4<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)
