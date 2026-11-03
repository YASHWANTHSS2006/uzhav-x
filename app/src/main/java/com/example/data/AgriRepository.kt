package com.example.data

import com.example.model.*

object AgriRepository {

    val sampleNews = listOf(
        AgriNews(
            id = "n1",
            title = "Tamil Nadu Govt Announces Additional ₹100/Quintal Incentive on Paddy MSP for Samba Season",
            tamilTitle = "சம்பா நெல் கொள்முதலுக்கு குவிண்டாலுக்கு கூடுதலாக ₹100 ஊக்கத்தொகை - தமிழக அரசு அறிவிப்பு",
            summaryEnglish = "The Government of Tamil Nadu has announced an exclusive incentive bonus over the Central Minimum Support Price (MSP) for the 2026-27 Samba harvest. Grade-A paddy will now fetch ₹2,450/quintal at all Direct Purchase Centres (DPCs).",
            summaryTamil = "தமிழகத்தில் நடப்பு சம்பா பருவத்தில் நேரடி நெல் கொள்முதல் நிலையங்களில் சன்ன ரக நெல்லுக்கு மத்திய அரசின் குறைந்தபட்ச ஆதார விலையுடன் கூடுதலாக ₹100 சேர்த்து குவிண்டாலுக்கு ₹2,450 வழங்க முதலமைச்சர் உத்தரவிட்டுள்ளார்.",
            category = NewsCategory.GOVT_POLICY,
            source = "TN Agriculture Dept",
            timeAgo = "1 hour ago",
            readTimeMinutes = 2,
            isBreaking = true
        ),
        AgriNews(
            id = "n2",
            title = "Mettur Dam Water Release Maintained at 16,000 Cusecs for Delta Irrigation",
            tamilTitle = "டெல்டா பாசனத்திற்காக மேட்டூர் அணையிலிருந்து வினாடிக்கு 16,000 கனஅடி நீர் திறப்பு தொடர்கிறது",
            summaryEnglish = "Water Resources Department engineers reported water storage at Stanley Reservoir, Mettur stands at 116.4 feet. Steady release ensures uninterrupted tail-end supply for Thanjavur, Tiruvarur, and Nagapattinam samba fields.",
            summaryTamil = "மேட்டூர் அணை நீர்மட்டம் 116.4 அடியாக உள்ள நிலையில், டெல்டா மாவட்டங்களில் சம்பா மற்றும் தாளடி பயிர்களின் பாசன தேவைக்காக வினாடிக்கு 16,000 கனஅடி நீர் தொடர்ந்து ஆற்றில் திறந்து விடப்பட்டு வருகிறது.",
            category = NewsCategory.WEATHER_WATER,
            source = "TN Water Resources Dept",
            timeAgo = "3 hours ago",
            readTimeMinutes = 3,
            isBreaking = false
        ),
        AgriNews(
            id = "n3",
            title = "Turmeric Touches ₹16,800/Quintal at Erode Regulated Market on Strong Export Orders",
            tamilTitle = "ஈரோடு ஒழுங்குமுறை விற்பனைக்கூடத்தில் விரலி மஞ்சள் விலை புதிய உச்சம் - குவிண்டால் ₹16,800!",
            summaryEnglish = "Unprecedented demand from spice exporters in Europe and the Gulf has driven finger turmeric prices up by 15% this week. Daily arrivals crossed 4,000 bags with brisk bidding across all 4 marketing societies in Erode.",
            summaryTamil = "ஐரோப்பிய மற்றும் வளைகுடா நாடுகளின் ஏற்றுமதி ஆர்டர்கள் அதிகரித்ததால் ஈரோடு மஞ்சள் சந்தையில் விரலி மஞ்சள் விலை குவிண்டாலுக்கு ₹16,800 வரை எட்டியுள்ளது. விவசாயிகள் மகிழ்ச்சி அடைந்துள்ளனர்.",
            category = NewsCategory.MARKET_PRICES,
            source = "Erode Agri Marketing Society",
            timeAgo = "5 hours ago",
            readTimeMinutes = 2,
            isBreaking = false
        ),
        AgriNews(
            id = "n4",
            title = "TNAU Issues Pest Alert: Advisory to Combat Fall Armyworm in Maize and Leaf Folder in Paddy",
            tamilTitle = "தமிழ்நாடு வேளாண் பல்கலைக்கழகம் எச்சரிக்கை: மக்காச்சோள படைப்புழு & நெல் இலைசுருட்டு தடுப்பு முறைகள்",
            summaryEnglish = "Tamil Nadu Agricultural University (TNAU) Coimbatore scientists advised farmers to deploy pheromone traps and spray Chlorantraniliprole 18.5% SC or biological Beauveria bassiana to protect young crops.",
            summaryTamil = "பருவமழைக்கு பின் நிலவும் ஈரப்பதம் காரணமாக மக்காச்சோளத்தில் படைப்புழுவும், நெல்லில் இலைசுருட்டு புழுவும் தாக்க வாய்ப்புள்ளதால் உரிய பாதுகாப்பு மருந்துகளை தெளிக்க TNAU விஞ்ஞானிகள் அறிவுறுத்தியுள்ளனர்.",
            category = NewsCategory.PEST_ALERT,
            source = "TNAU Coimbatore",
            timeAgo = "Yesterday",
            readTimeMinutes = 3,
            isBreaking = false
        ),
        AgriNews(
            id = "n5",
            title = "PM-Kisan 19th Installment of ₹2,000 to be Credited Directly via DBT Next Week",
            tamilTitle = "பி.எம்-கிசான் 19வது தவணை ₹2,000 அடுத்த வாரம் விவசாயிகளின் வங்கிக் கணக்கில் வரவு வைக்கப்படும்",
            summaryEnglish = "Over 26 lakh eligible small and marginal farmers across Tamil Nadu will receive the 19th installment under the Pradhan Mantri Kisan Samman Nidhi directly through Aadhaar-enabled DBT.",
            summaryTamil = "பிரதம மந்திரி கிசான் சம்மான் நிதி திட்டத்தின் கீழ் தமிழகத்தை சேர்ந்த 26 லட்சத்திற்கும் மேற்பட்ட சிறு, குறு விவசாயிகளுக்கு தலா ₹2,000 அடுத்த வாரம் நேரடி வங்கி பரிவர்த்தனை மூலம் வரவு வைக்கப்படுகிறது.",
            category = NewsCategory.GOVT_POLICY,
            source = "PIB Agriculture",
            timeAgo = "Yesterday",
            readTimeMinutes = 2,
            isBreaking = false
        )
    )

    val sampleMandiPrices = listOf(
        MandiPrice(
            id = "m1",
            cropName = "Paddy (Common CR 1009 / ADT 53)",
            tamilName = "நெல் (சன்ன ரகம் - CR 1009 / ADT 53)",
            category = "Grains",
            marketName = "Thanjavur DPC Yard",
            district = "Thanjavur",
            minPrice = 2320,
            maxPrice = 2480,
            modalPrice = 2450,
            unit = "Quintal (100 kg)",
            changeAmount = 80,
            isPositive = true,
            arrivalVolume = "1,800 Bags",
            lastUpdated = "Today 08:30 AM",
            priceHistory7Days = listOf(2350, 2370, 2360, 2400, 2410, 2420, 2450)
        ),
        MandiPrice(
            id = "m2",
            cropName = "Turmeric (Finger / விரலி மஞ்சள்)",
            tamilName = "விரலி மஞ்சள் (ஈரோடு தரம்)",
            category = "Spices",
            marketName = "Erode Regulated Market",
            district = "Erode",
            minPrice = 14200,
            maxPrice = 16800,
            modalPrice = 15900,
            unit = "Quintal (100 kg)",
            changeAmount = 350,
            isPositive = true,
            arrivalVolume = "4,200 Quintals",
            lastUpdated = "Today 09:15 AM",
            priceHistory7Days = listOf(14800, 15100, 15000, 15300, 15500, 15700, 15900)
        ),
        MandiPrice(
            id = "m3",
            cropName = "Tender Coconut (இளநீர்)",
            tamilName = "பொள்ளாச்சி செவ்விளநீர்",
            category = "Plantation",
            marketName = "Pollachi Coconut Market",
            district = "Coimbatore",
            minPrice = 32,
            maxPrice = 45,
            modalPrice = 42,
            unit = "Per Nut (ஒன்று)",
            changeAmount = 3,
            isPositive = true,
            arrivalVolume = "28,000 Nuts",
            lastUpdated = "Today 07:45 AM",
            priceHistory7Days = listOf(36, 38, 37, 39, 40, 40, 42)
        ),
        MandiPrice(
            id = "m4",
            cropName = "Tomato (நாட்டுத் தக்காளி)",
            tamilName = "நாட்டுத் தக்காளி (ஒட்டன்சத்திரம்)",
            category = "Vegetables",
            marketName = "Oddanchatram Market",
            district = "Dindigul",
            minPrice = 18,
            maxPrice = 28,
            modalPrice = 24,
            unit = "Per Kg (கிலோ)",
            changeAmount = -2,
            isPositive = false,
            arrivalVolume = "65 Tonnes",
            lastUpdated = "Today 06:30 AM",
            priceHistory7Days = listOf(32, 30, 28, 26, 25, 26, 24)
        ),
        MandiPrice(
            id = "m5",
            cropName = "Small Onion (சின்ன வெங்காயம் - சாம்பார்)",
            tamilName = "சின்ன வெங்காயம் (திண்டுக்கல் தரம்)",
            category = "Vegetables",
            marketName = "Dindigul Onion Mandi",
            district = "Dindigul",
            minPrice = 42,
            maxPrice = 58,
            modalPrice = 52,
            unit = "Per Kg (கிலோ)",
            changeAmount = 6,
            isPositive = true,
            arrivalVolume = "120 Quintals",
            lastUpdated = "Today 09:00 AM",
            priceHistory7Days = listOf(40, 42, 45, 46, 48, 50, 52)
        ),
        MandiPrice(
            id = "m6",
            cropName = "Cotton (பருத்தி - RCH 659)",
            tamilName = "பருத்தி (ஆர்.சி.எச் - ராஜபாளையம்)",
            category = "Cash Crops",
            marketName = "Rajapalayam Cotton Yard",
            district = "Virudhunagar",
            minPrice = 7100,
            maxPrice = 7950,
            modalPrice = 7700,
            unit = "Quintal (100 kg)",
            changeAmount = 120,
            isPositive = true,
            arrivalVolume = "850 Quintals",
            lastUpdated = "Today 10:00 AM",
            priceHistory7Days = listOf(7300, 7400, 7350, 7500, 7550, 7600, 7700)
        ),
        MandiPrice(
            id = "m7",
            cropName = "Banana (பூவன் & நேந்திரம்)",
            tamilName = "வாழைப்பழம் (பூவன் தார் - தேனி)",
            category = "Fruits",
            marketName = "Theni Uzhavar Sandhai",
            district = "Theni",
            minPrice = 350,
            maxPrice = 480,
            modalPrice = 440,
            unit = "Per Bunch (தார்)",
            changeAmount = 20,
            isPositive = true,
            arrivalVolume = "1,400 Bunches",
            lastUpdated = "Today 07:15 AM",
            priceHistory7Days = listOf(400, 410, 415, 420, 430, 435, 440)
        ),
        MandiPrice(
            id = "m8",
            cropName = "Groundnut (மணிலா நிலக்கடலை)",
            tamilName = "நிலக்கடலை (விழுப்புரம் தரம்)",
            category = "Oilseeds",
            marketName = "Villupuram Regulated Market",
            district = "Villupuram",
            minPrice = 6400,
            maxPrice = 7200,
            modalPrice = 6950,
            unit = "Quintal (100 kg)",
            changeAmount = 90,
            isPositive = true,
            arrivalVolume = "950 Bags",
            lastUpdated = "Today 08:45 AM",
            priceHistory7Days = listOf(6600, 6700, 6750, 6800, 6850, 6900, 6950)
        )
    )

    /**
     * Overall Tamil Nadu Agro Stores (Covering Delta, Kongu, South, North/Central regions)
     */
    val initialStores = listOf(
        // DELTA REGION: Thanjavur
        AgriStore(
            id = "s1",
            name = "Orathanadu Primary Agri Co-operative (PACCS)",
            tamilName = "ஒரத்தநாடு தொடக்க வேளாண்மை கூட்டுறவு கடன் சங்கம்",
            storeType = "PACCS Govt Co-operative",
            address = "Kovilur Road, Orathanadu, Thanjavur",
            taluk = "Orathanadu",
            district = "Thanjavur",
            phone = "04372-234120",
            rating = 4.8,
            reviewCount = 142,
            distanceKm = 1.4,
            isGovtAided = true,
            mapOffsetX = 0.58f,
            mapOffsetY = 0.48f,
            products = listOf(
                FertilizerProduct(
                    id = "p1",
                    name = "Neem Coated Urea (வேப்பம்பூசப்பட்ட யூரியா)",
                    tamilName = "யூரியா (நீம் கோட்டட்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "IFFCO",
                    bagSize = "45 kg",
                    standardPrice = 266,
                    subsidizedPrice = 266,
                    inStock = true,
                    composition = "46% Nitrogen",
                    usageNotes = "Basal and top dressing for paddy tillering stage"
                ),
                FertilizerProduct(
                    id = "p2",
                    name = "DAP 18:46:00 (டி.ஏ.பி உரம்)",
                    tamilName = "டை அம்மோனியம் பாஸ்பேட் (DAP)",
                    category = FertilizerType.CHEMICAL,
                    brand = "SPIC",
                    bagSize = "50 kg",
                    standardPrice = 1350,
                    subsidizedPrice = 1350,
                    inStock = true,
                    composition = "18% N, 46% P2O5",
                    usageNotes = "Root establishment for all field crops"
                ),
                FertilizerProduct(
                    id = "p3",
                    name = "MOP Potash 00:00:60 (பொட்டாஷ் உரம்)",
                    tamilName = "மியூரேட் ஆஃப் பொட்டாஷ் (MOP)",
                    category = FertilizerType.CHEMICAL,
                    brand = "IPL",
                    bagSize = "50 kg",
                    standardPrice = 1680,
                    subsidizedPrice = 1680,
                    inStock = true,
                    composition = "60% K2O",
                    usageNotes = "Grain filling and drought resistance"
                )
            )
        ),
        // DELTA REGION: Organic Thanjavur
        AgriStore(
            id = "s2",
            name = "Annai Organic Bio-Inputs & Seed Center",
            tamilName = "அன்னை இயற்கை உரம் & பஞ்சகாவ்யா மையம்",
            storeType = "Certified Organic Agro FPO",
            address = "Main Bazaar, Thanjavur West",
            taluk = "Thanjavur",
            district = "Thanjavur",
            phone = "9842891234",
            rating = 4.9,
            reviewCount = 98,
            distanceKm = 3.2,
            isGovtAided = false,
            mapOffsetX = 0.60f,
            mapOffsetY = 0.46f,
            products = listOf(
                FertilizerProduct(
                    id = "p4",
                    name = "Desi Cow Panchakavya (பஞ்சகாவ்யா)",
                    tamilName = "பாரம்பரிய நாட்டு மாட்டு பஞ்சகாவ்யா",
                    category = FertilizerType.ORGANIC,
                    brand = "Cauvery Organic Farm",
                    bagSize = "5 Liters",
                    standardPrice = 650,
                    subsidizedPrice = 520,
                    inStock = true,
                    composition = "Desi Cow Dung, Urine, Milk, Curd, Ghee, Jaggery, Tender Coconut",
                    usageNotes = "3% foliar spray for immunity & growth"
                ),
                FertilizerProduct(
                    id = "p5",
                    name = "Pure Vermicompost (மண்புழு உரம்)",
                    tamilName = "செறிவூட்டப்பட்ட மண்புழு உரம்",
                    category = FertilizerType.ORGANIC,
                    brand = "Pasumai FPO",
                    bagSize = "40 kg",
                    standardPrice = 380,
                    subsidizedPrice = 320,
                    inStock = true,
                    composition = "Rich Humus + Beneficial Microbes",
                    usageNotes = "Apply 250 kg per acre during land preparation"
                ),
                FertilizerProduct(
                    id = "p6",
                    name = "Pure Neem Cake (வேப்பம் புண்ணாக்கு)",
                    tamilName = "அரக்கு இல்லாத சுத்தமான வேப்பம் புண்ணாக்கு",
                    category = FertilizerType.ORGANIC,
                    brand = "AgriBio TN",
                    bagSize = "50 kg",
                    standardPrice = 1450,
                    subsidizedPrice = null,
                    inStock = true,
                    composition = "Natural Azadirachtin + Nitrogen slow release",
                    usageNotes = "Kills nematode pests and enriches soil carbon"
                )
            )
        ),
        // KONGU REGION: Coimbatore
        AgriStore(
            id = "s3",
            name = "TNAU Farmers Agro Clinic & Bio-Input Depot",
            tamilName = "தமிழ்நாடு வேளாண் பல்கலைக்கழக பயிர் மருத்துவகம் & விதை மையம்",
            storeType = "TNAU University Center",
            address = "Marudhamalai Road, Lawley, Coimbatore",
            taluk = "Coimbatore North",
            district = "Coimbatore",
            phone = "0422-6611261",
            rating = 5.0,
            reviewCount = 310,
            distanceKm = 12.0,
            isGovtAided = true,
            mapOffsetX = 0.22f,
            mapOffsetY = 0.52f,
            products = listOf(
                FertilizerProduct(
                    id = "p7",
                    name = "Trichoderma Viride Bio-Fungicide (உயிரி பூஞ்சானக்கொல்லி)",
                    tamilName = "டிரைக்கோடெர்மா விரிடி (உயிரி உரம்)",
                    category = FertilizerType.BIO_PESTICIDE,
                    brand = "TNAU Bio-Lab",
                    bagSize = "1 kg",
                    standardPrice = 140,
                    subsidizedPrice = 100,
                    inStock = true,
                    composition = "Antagonistic fungal spores 2x10^6 cfu/g",
                    usageNotes = "Seed treatment for root rot & wilt"
                ),
                FertilizerProduct(
                    id = "p8",
                    name = "TNAU Coconut Tonic (தென்னை டானிக்)",
                    tamilName = "TNAU தென்னை ஊட்டச்சத்து டானிக்",
                    category = FertilizerType.ORGANIC,
                    brand = "TNAU Research",
                    bagSize = "200 ml Bottle",
                    standardPrice = 65,
                    subsidizedPrice = 50,
                    inStock = true,
                    composition = "Essential micro & secondary nutrients for root feeding",
                    usageNotes = "Feed once in 6 months via active root feeding"
                ),
                FertilizerProduct(
                    id = "p9",
                    name = "Azospirillum & Phosphobacteria Bio-Fertilizer",
                    tamilName = "அசோஸ்பைரில்லம் & பாஸ்போபாக்டீரியா",
                    category = FertilizerType.ORGANIC,
                    brand = "TNAU",
                    bagSize = "2 kg Packet",
                    standardPrice = 180,
                    subsidizedPrice = 130,
                    inStock = true,
                    composition = "Atmospheric nitrogen-fixing bacteria",
                    usageNotes = "Soil enrichment saves 25% chemical urea"
                )
            )
        ),
        // KONGU REGION: Erode
        AgriStore(
            id = "s4",
            name = "Erode District Agro Farmers Co-op Depot",
            tamilName = "ஈரோடு மாவட்ட கூட்டுறவு விவசாய விற்பனை மையம்",
            storeType = "PACCS Govt Co-operative",
            address = "Perundurai Road, Erode",
            taluk = "Erode",
            district = "Erode",
            phone = "0424-2258900",
            rating = 4.7,
            reviewCount = 189,
            distanceKm = 18.5,
            isGovtAided = true,
            mapOffsetX = 0.35f,
            mapOffsetY = 0.45f,
            products = listOf(
                FertilizerProduct(
                    id = "p10",
                    name = "Gromor Complex 20:20:00:13 (குரோமோர் காம்ப்ளக்ஸ்)",
                    tamilName = "காம்ப்ளக்ஸ் 20:20:00:13 (சல்பர் அடங்கியது)",
                    category = FertilizerType.CHEMICAL,
                    brand = "Coromandel",
                    bagSize = "50 kg",
                    standardPrice = 1260,
                    subsidizedPrice = 1260,
                    inStock = true,
                    composition = "20% N, 20% P, 13% Sulphur",
                    usageNotes = "Bumper yield for Turmeric & Vegetables"
                ),
                FertilizerProduct(
                    id = "p11",
                    name = "BSR-2 Certified Turmeric Seed Rhizomes",
                    tamilName = "BSR-2 சான்றளிக்கப்பட்ட விதை மஞ்சள் கிழங்கு",
                    category = FertilizerType.SEEDS,
                    brand = "TN State Seeds",
                    bagSize = "50 kg Bag",
                    standardPrice = 4200,
                    subsidizedPrice = 3400,
                    inStock = true,
                    composition = "High Curcumin > 4.2% certified rhizomes",
                    usageNotes = "Plant on raised ridges with drip line"
                )
            )
        ),
        // SOUTHERN REGION: Madurai
        AgriStore(
            id = "s5",
            name = "Meenakshi Bio-Agri Inputs & Jasmine Seed Store",
            tamilName = "மீனாட்சி இயற்கை விவசாய இடுபொருட்கள் மையம்",
            storeType = "Authorized Private Dealer",
            address = "Mattuthavani Agri Yard, Madurai",
            taluk = "Madurai East",
            district = "Madurai",
            phone = "9443312988",
            rating = 4.8,
            reviewCount = 176,
            distanceKm = 24.0,
            isGovtAided = false,
            mapOffsetX = 0.45f,
            mapOffsetY = 0.68f,
            products = listOf(
                FertilizerProduct(
                    id = "p12",
                    name = "Concentrated Fish Amino Acid (மீன் அமினோ அமிலம்)",
                    tamilName = "செறிவூட்டப்பட்ட மீன் அமினோ அமிலம்",
                    category = FertilizerType.ORGANIC,
                    brand = "Pasumai Madurai",
                    bagSize = "1 Liter",
                    standardPrice = 280,
                    subsidizedPrice = 240,
                    inStock = true,
                    composition = "Fermented Sea Fish + Country Jaggery",
                    usageNotes = "Abundant flowering in Jasmine and Cotton"
                ),
                FertilizerProduct(
                    id = "p13",
                    name = "Pure Organic Bone Meal (எலும்புத்தூள் உரம்)",
                    tamilName = "இயற்கை எலும்புத்தூள் பாஸ்பரஸ் உரம்",
                    category = FertilizerType.ORGANIC,
                    brand = "Organic India Bio",
                    bagSize = "25 kg",
                    standardPrice = 750,
                    subsidizedPrice = null,
                    inStock = true,
                    composition = "Slow release natural Phosphorus & Calcium",
                    usageNotes = "Prevents flower dropping in chillies & jasmine"
                )
            )
        ),
        // SOUTHERN REGION: Dindigul
        AgriStore(
            id = "s6",
            name = "Oddanchatram Vegetable & Fertilizer Co-operative",
            tamilName = "ஒட்டன்சத்திரம் காய்கறி உற்பத்தியாளர்கள் உர சங்கம்",
            storeType = "PACCS Govt Co-operative",
            address = "Dharapuram Road, Oddanchatram, Dindigul",
            taluk = "Oddanchatram",
            district = "Dindigul",
            phone = "04553-241088",
            rating = 4.9,
            reviewCount = 240,
            distanceKm = 28.0,
            isGovtAided = true,
            mapOffsetX = 0.38f,
            mapOffsetY = 0.60f,
            products = listOf(
                FertilizerProduct(
                    id = "p14",
                    name = "Water Soluble 19:19:19 (டிரிப் உரங்கள்)",
                    tamilName = "19:19:19 நீரில் கரையும் உரம் (சொட்டு நீர்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "IFFCO / Yara",
                    bagSize = "25 kg",
                    standardPrice = 2400,
                    subsidizedPrice = 2100,
                    inStock = true,
                    composition = "Balanced 100% water-soluble NPK",
                    usageNotes = "Drip fertigation for tomato & shallots"
                ),
                FertilizerProduct(
                    id = "p15",
                    name = "Co-5 Hybrid Small Onion True Seeds",
                    tamilName = "Co-5 வீரிய ஒட்டு சின்ன வெங்காய விதை",
                    category = FertilizerType.SEEDS,
                    brand = "TNAU Seeds",
                    bagSize = "1 kg Tin",
                    standardPrice = 1800,
                    subsidizedPrice = 1450,
                    inStock = true,
                    composition = "Purity 99%, High pungent shallot seed",
                    usageNotes = "Direct sowing or nursery transplanting"
                )
            )
        ),
        // NORTHERN REGION: Villupuram
        AgriStore(
            id = "s7",
            name = "Villupuram Farmers Producer Co. (Groundnut Special)",
            tamilName = "விழுப்புரம் விவசாயிகள் உற்பத்தியாளர் நிறுவனம் (FPO)",
            storeType = "FPO Organic Depot",
            address = "Trichy Main Road, Villupuram",
            taluk = "Villupuram",
            district = "Villupuram",
            phone = "04146-224411",
            rating = 4.7,
            reviewCount = 135,
            distanceKm = 42.0,
            isGovtAided = true,
            mapOffsetX = 0.65f,
            mapOffsetY = 0.32f,
            products = listOf(
                FertilizerProduct(
                    id = "p16",
                    name = "Gypsum for Groundnut (ஜிப்சம் உரம்)",
                    tamilName = "நிலக்கடலை சாகுபடிக்கான ஜிப்சம்",
                    category = FertilizerType.CHEMICAL,
                    brand = "SPIC Gypsum",
                    bagSize = "50 kg",
                    standardPrice = 280,
                    subsidizedPrice = 180,
                    inStock = true,
                    composition = "Calcium 20%, Sulphur 16%",
                    usageNotes = "Ensures solid oil pods without empty shells"
                ),
                FertilizerProduct(
                    id = "p17",
                    name = "Cold Pressed Neem Oil Spray 10,000 ppm",
                    tamilName = "இயற்கை வேப்பெண்ணெய் பூச்சிவிரட்டி",
                    category = FertilizerType.BIO_PESTICIDE,
                    brand = "Kisan Pasumai",
                    bagSize = "1 Liter",
                    standardPrice = 450,
                    subsidizedPrice = 380,
                    inStock = true,
                    composition = "High Azadirachtin organic repellent",
                    usageNotes = "Broad spectrum sucking pest deterrent"
                )
            ),
            latitude = 11.9401,
            longitude = 79.4861
        ),
        // DELTA REGION: Kumbakonam PACCS, Thanjavur (Proximity ~28 km from Orathanadu)
        AgriStore(
            id = "s8",
            name = "Kumbakonam Central Co-op Agri Bank & Depot",
            tamilName = "கும்பகோணம் மத்திய கூட்டுறவு வேளாண் வங்கி & உரக்கிடங்கு",
            storeType = "PACCS Govt Co-operative",
            address = "Court Road, Kumbakonam, Thanjavur",
            taluk = "Kumbakonam",
            district = "Thanjavur",
            phone = "0435-2430155",
            rating = 4.8,
            reviewCount = 164,
            distanceKm = 28.4,
            isGovtAided = true,
            mapOffsetX = 0.61f,
            mapOffsetY = 0.44f,
            latitude = 10.9602,
            longitude = 79.3845,
            products = listOf(
                FertilizerProduct(
                    id = "p18",
                    name = "Neem Coated Urea (வேப்பம்பூசப்பட்ட யூரியா)",
                    tamilName = "யூரியா (நீம் கோட்டட்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "KRIBHCO",
                    bagSize = "45 kg",
                    standardPrice = 266,
                    subsidizedPrice = 266,
                    inStock = true,
                    composition = "46% Nitrogen",
                    usageNotes = "Subsidized standard quota available"
                ),
                FertilizerProduct(
                    id = "p19",
                    name = "Zinc Sulphate Heptahydrate 21% (துத்தநாக சல்பேட்)",
                    tamilName = "துத்தநாக சல்பேட் நுண்ணூட்ட உரம்",
                    category = FertilizerType.CHEMICAL,
                    brand = "IFFCO Micro",
                    bagSize = "10 kg Bag",
                    standardPrice = 480,
                    subsidizedPrice = 360,
                    inStock = true,
                    composition = "21% Zinc, 10% Sulphur",
                    usageNotes = "Cures Khaira disease & yellowing in paddy"
                ),
                FertilizerProduct(
                    id = "p20",
                    name = "DAP 18:46:00 (டி.ஏ.பி உரம்)",
                    tamilName = "டை அம்மோனியம் பாஸ்பேட் (DAP)",
                    category = FertilizerType.CHEMICAL,
                    brand = "SPIC",
                    bagSize = "50 kg",
                    standardPrice = 1350,
                    subsidizedPrice = 1350,
                    inStock = true,
                    composition = "18% N, 46% P2O5",
                    usageNotes = "Basal root application"
                )
            )
        ),
        // DELTA REGION: Mannargudi Delta Depot, Tiruvarur (Proximity ~22 km from Orathanadu)
        AgriStore(
            id = "s9",
            name = "Mannargudi Delta Farmers Fertilizer Society",
            tamilName = "மன்னார்குடி டெல்டா விவசாயிகள் உர விநியோக சங்கம்",
            storeType = "PACCS Govt Co-operative",
            address = "Pandaladi Street, Mannargudi, Tiruvarur",
            taluk = "Mannargudi",
            district = "Tiruvarur",
            phone = "04367-252119",
            rating = 4.9,
            reviewCount = 205,
            distanceKm = 22.1,
            isGovtAided = true,
            mapOffsetX = 0.63f,
            mapOffsetY = 0.48f,
            latitude = 10.6667,
            longitude = 79.4500,
            products = listOf(
                FertilizerProduct(
                    id = "p21",
                    name = "Neem Coated Urea (வேப்பம்பூசப்பட்ட யூரியா)",
                    tamilName = "யூரியா (நீம் கோட்டட்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "SPIC Urea",
                    bagSize = "45 kg",
                    standardPrice = 266,
                    subsidizedPrice = 266,
                    inStock = true,
                    composition = "46% Nitrogen",
                    usageNotes = "Subsidized government stock"
                ),
                FertilizerProduct(
                    id = "p22",
                    name = "MOP Potash 00:00:60 (பொட்டாஷ் உரம்)",
                    tamilName = "பொட்டாஷ் (MOP 60%)",
                    category = FertilizerType.CHEMICAL,
                    brand = "IPL",
                    bagSize = "50 kg",
                    standardPrice = 1680,
                    subsidizedPrice = 1680,
                    inStock = true,
                    composition = "60% Potash (K2O)",
                    usageNotes = "Delta paddy panicle initiation"
                ),
                FertilizerProduct(
                    id = "p23",
                    name = "Phosphobacteria Bio-Fertilizer (பாஸ்போபாக்டீரியா)",
                    tamilName = "பாஸ்போபாக்டீரியா உயிரி உரம்",
                    category = FertilizerType.ORGANIC,
                    brand = "TNAU Bio",
                    bagSize = "2 kg",
                    standardPrice = 150,
                    subsidizedPrice = 110,
                    inStock = true,
                    composition = "Phosphate solubilizing bacteria",
                    usageNotes = "Releases fixed phosphorus in soil"
                )
            )
        ),
        // CENTRAL REGION: Tiruchirappalli Srirangam (Proximity ~55 km from Orathanadu)
        AgriStore(
            id = "s10",
            name = "Srirangam Agricultural Producers Co-operative Marketing Society",
            tamilName = "ஸ்ரீரங்கம் வேளாண் உற்பத்தியாளர்கள் கூட்டுறவு விற்பனை சங்கம்",
            storeType = "PACCS Govt Co-operative",
            address = "Gandhi Market Complex, Tiruchirappalli",
            taluk = "Srirangam",
            district = "Tiruchirappalli",
            phone = "0431-2704100",
            rating = 4.7,
            reviewCount = 188,
            distanceKm = 56.0,
            isGovtAided = true,
            mapOffsetX = 0.49f,
            mapOffsetY = 0.47f,
            latitude = 10.8600,
            longitude = 78.6900,
            products = listOf(
                FertilizerProduct(
                    id = "p24",
                    name = "Gromor Complex 20:20:00:13 (குரோமோர் காம்ப்ளக்ஸ்)",
                    tamilName = "காம்ப்ளக்ஸ் 20:20:00:13",
                    category = FertilizerType.CHEMICAL,
                    brand = "Coromandel",
                    bagSize = "50 kg",
                    standardPrice = 1260,
                    subsidizedPrice = 1260,
                    inStock = true,
                    composition = "20% N, 20% P, 13% S",
                    usageNotes = "Banana and sugarcane basal application"
                ),
                FertilizerProduct(
                    id = "p25",
                    name = "Neem Coated Urea (யூரியா)",
                    tamilName = "யூரியா (நீம் கோட்டட்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "IFFCO",
                    bagSize = "45 kg",
                    standardPrice = 266,
                    subsidizedPrice = 266,
                    inStock = true,
                    composition = "46% Nitrogen",
                    usageNotes = "Active government subsidized quota"
                ),
                FertilizerProduct(
                    id = "p26",
                    name = "Pseudomonas Fluorescens Bio-Fungicide",
                    tamilName = "சூடோமோனாஸ் புளோரசன்ஸ் உயிரி மருந்து",
                    category = FertilizerType.BIO_PESTICIDE,
                    brand = "TNAU Research",
                    bagSize = "1 kg",
                    standardPrice = 150,
                    subsidizedPrice = 110,
                    inStock = true,
                    composition = "Fluorescent Pseudomonas bacterial culture",
                    usageNotes = "Protects against bacterial leaf blight in rice"
                )
            )
        ),
        // SALEM REGION: Attur Organic Farmers Producer Depot (~115 km)
        AgriStore(
            id = "s11",
            name = "Attur Farmers Producer Co. & Sago Agro Mart",
            tamilName = "ஆத்தூர் உழவர் உற்பத்தியாளர் நிறுவனம் & இயற்கை அங்காடி",
            storeType = "FPO Organic Depot",
            address = "Salem Cuddalore Main Road, Attur, Salem",
            taluk = "Attur",
            district = "Salem",
            phone = "04282-241550",
            rating = 4.8,
            reviewCount = 142,
            distanceKm = 115.0,
            isGovtAided = true,
            mapOffsetX = 0.45f,
            mapOffsetY = 0.38f,
            latitude = 11.5975,
            longitude = 78.6012,
            products = listOf(
                FertilizerProduct(
                    id = "p27",
                    name = "Enriched Humic Acid Granules 15% (ஹியூமிக் அமிலம்)",
                    tamilName = "செறிவூட்டப்பட்ட ஹியூமிக் அமில குருணைகள்",
                    category = FertilizerType.ORGANIC,
                    brand = "Pasumai Salem",
                    bagSize = "10 kg",
                    standardPrice = 620,
                    subsidizedPrice = 480,
                    inStock = true,
                    composition = "Humic Acid 15% + Fulvic Acid",
                    usageNotes = "Soil root booster for tapioca & turmeric"
                ),
                FertilizerProduct(
                    id = "p28",
                    name = "TNAU Tapioca & Vegetable Micro-Nutrient Mix",
                    tamilName = "TNAU மரவள்ளி மற்றும் காய்கறி நுண்ணூட்ட கலவை",
                    category = FertilizerType.CHEMICAL,
                    brand = "TNAU",
                    bagSize = "5 kg",
                    standardPrice = 320,
                    subsidizedPrice = 240,
                    inStock = true,
                    composition = "Zn, Fe, Mn, Cu, B, Mo specialized mix",
                    usageNotes = "Foliar spray at 30 & 60 days"
                )
            )
        ),
        // SOUTHERN REGION: Tirunelveli Sankarankovil Agro Mart (~225 km)
        AgriStore(
            id = "s12",
            name = "Sankarankovil Farmers Service Agro Mart",
            tamilName = "சங்கரன்கோவில் உழவர் சேவை வேளாண் அங்காடி",
            storeType = "Authorized Private Dealer",
            address = "Rajapalayam Road, Sankarankovil, Tenkasi / Tirunelveli",
            taluk = "Sankarankovil",
            district = "Tirunelveli",
            phone = "04636-222880",
            rating = 4.7,
            reviewCount = 112,
            distanceKm = 228.0,
            isGovtAided = false,
            mapOffsetX = 0.32f,
            mapOffsetY = 0.82f,
            latitude = 9.1725,
            longitude = 77.5325,
            products = listOf(
                FertilizerProduct(
                    id = "p29",
                    name = "Coromandel Gromor 14:35:14 (காம்ப்ளக்ஸ்)",
                    tamilName = "குரோமோர் 14:35:14 (உயர் பாஸ்பரஸ்)",
                    category = FertilizerType.CHEMICAL,
                    brand = "Coromandel",
                    bagSize = "50 kg",
                    standardPrice = 1480,
                    subsidizedPrice = 1480,
                    inStock = true,
                    composition = "14% N, 35% P, 14% K",
                    usageNotes = "Cotton and maize root growth"
                ),
                FertilizerProduct(
                    id = "p30",
                    name = "Certified MCU-5 Hybrid Cotton Seeds",
                    tamilName = "சான்றளிக்கப்பட்ட MCU-5 பருத்தி விதைகள்",
                    category = FertilizerType.SEEDS,
                    brand = "TN State Seeds",
                    bagSize = "1 kg Pkt",
                    standardPrice = 720,
                    subsidizedPrice = 580,
                    inStock = true,
                    composition = "98% Germination guarantee certified seed",
                    usageNotes = "Black cotton soil sowing"
                )
            )
        )
    )

    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Earth radius in km
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2)
        val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
        return Math.round(r * c * 10.0) / 10.0
    }

    fun getDistrictCoordinates(district: String): Pair<Double, Double> = when (district.trim().lowercase()) {
        "thanjavur" -> 10.7870 to 79.1378
        "tiruvarur" -> 10.7712 to 79.6368
        "nagapattinam" -> 10.7656 to 79.8424
        "coimbatore" -> 11.0168 to 76.9558
        "erode" -> 11.3410 to 77.7172
        "madurai" -> 9.9252 to 78.1198
        "dindigul" -> 10.3673 to 77.9803
        "salem" -> 11.6643 to 78.1460
        "tiruchirappalli", "trichy" -> 10.8600 to 78.6900
        "villupuram" -> 11.9401 to 79.4861
        "theni" -> 10.0104 to 77.4768
        "tirunelveli" -> 8.7139 to 77.7567
        "cuddalore" -> 11.7480 to 79.7714
        else -> 10.7870 to 79.1378 // default Thanjavur
    }

    val sampleSchemes = listOf(
        GovtScheme(
            id = "sch1",
            schemeCode = "TN-2026-MIS-DRIP",
            yearValidity = "2026-2027 Active",
            title = "Tamil Nadu Micro Irrigation Scheme (Drip & Sprinkler 100% Subsidy)",
            tamilTitle = "பிரதம மந்திரி நுண்ணீர் பாசன திட்டம் (சொட்டு நீர் 100% மானியம்)",
            department = "Department of Horticulture & Plantation Crops, Govt of TN",
            category = SchemeCategory.IRRIGATION_SUBSIDY,
            subsidyPercentage = "100% for Small & Marginal Farmers (< 5 Acres), 75% for Other Farmers",
            maxFinancialBenefit = "Up to ₹1,35,000 per hectare completely free",
            summaryEnglish = "2026 Enhanced guidelines: Small and marginal farmers receive 100% full financial subsidy for setting up automated inline drip irrigation and micro-sprinklers in coconut, sugarcane, banana, turmeric, and vegetables with 7-year replacement warranty.",
            summaryTamil = "2026 புதிய விதிமுறைகள்: 5 ஏக்கருக்கு குறைவான நிலமுள்ள சிறு, குறு விவசாயிகளுக்கு 100% முழு அரசு மானியத்தில் சொட்டு நீர் பாசன கருவிகள், பில்டர்கள் மற்றும் உரத்தொட்டிகள் இலவசமாக அமைத்து தரப்படுகின்றன.",
            eligibility = listOf(
                "Valid agricultural land ownership (Patta / Chitta in Tamil Nadu)",
                "Functional borewell or open well water source",
                "Small / Marginal farmer certificate from VAO / Tahsildar guarantees 100% free setup"
            ),
            requiredDocuments = listOf(
                "Current year Patta & Chitta (நடப்பு பசலி பட்டா / சிட்டா)",
                "FMB Village Map sketch (புல வரைபடம்)",
                "Aadhaar Card copy & 2 passport size photos",
                "Bank Passbook front page (with IFSC code)",
                "Quotation from empanelled drip manufacturer (Jain / Netafim)"
            ),
            howToApplySteps = listOf(
                "Step 1: Apply online on TN Horticulture portal (tnhorticulture.tn.gov.in) or via Uzhav X",
                "Step 2: Assistant Director of Horticulture (ADH) performs on-site field verification",
                "Step 3: Work order issued and authorized agency completes installation",
                "Step 4: Geo-tagged photo inspection and 100% subsidy settled"
            ),
            officialPortalUrl = "https://tnhorticulture.tn.gov.in",
            helplineNumber = "1800-425-4444 (Toll Free)",
            isPanchayatScheme = true,
            isNewFor2026 = false
        ),
        GovtScheme(
            id = "sch2",
            schemeCode = "TN-2026-KAVIADP-PH4",
            yearValidity = "2026-2027 Phase IV",
            title = "Kalaignar's All Village Integrated Agriculture Development Programme 2026",
            tamilTitle = "கலைஞரின் அனைத்து கிராம ஒருங்கிணைந்த வேளாண் வளர்ச்சி திட்டம் (கட்டம் IV)",
            department = "Department of Agriculture & Farmers Welfare, Govt of TN",
            category = SchemeCategory.DRYLAND_DEVELOPMENT,
            subsidyPercentage = "50% to 100% Free Inputs across Panchayat Wards",
            maxFinancialBenefit = "Free coconut saplings (2 per family) + Soil Health Card + Desi seed kit",
            summaryEnglish = "New 2026 Gram Panchayat initiative: Converts fallow lands into productive farms, provides free high-yielding Tall x Dwarf coconut saplings, battery sprayers at 50% discount, and free farm pond digging.",
            summaryTamil = "2026 புதிய கட்டம்: அனைத்து கிராம ஊராட்சிகளிலும் தரிசு நிலங்களை விளைநிலங்களாக மாற்றி, குடும்பத்திற்கு 2 இலவச வீரிய ஒட்டு தென்னங்கன்றுகள், 50% மானியத்தில் பேட்டரி தெளிப்பான் மற்றும் இலவச பண்ணைக்குட்டை அமைக்கும் திட்டம்.",
            eligibility = listOf(
                "Resident farmer of notified 2026 Gram Panchayat village",
                "All landholding and tenant farmers eligible without land size restriction"
            ),
            requiredDocuments = listOf(
                "Aadhaar Card",
                "Panchayat residence certificate",
                "Land patta copy or Village VAO tenancy certificate",
                "Bank account details"
            ),
            howToApplySteps = listOf(
                "Enroll directly through your Village Agri Extension Officer (வேளாண் விரிவாக்க அலுவலர்)",
                "Attend Panchayat Gram Sabha meeting held on Republic Day / Gandhi Jayanti",
                "Collect approved input coupons for immediate redemption"
            ),
            officialPortalUrl = "https://tnagrisnet.tn.gov.in",
            helplineNumber = "1551 (Kisan Call Centre)",
            isPanchayatScheme = true,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch3",
            schemeCode = "PM-2026-KUSUM-SOLAR",
            yearValidity = "2026-2027 Active",
            title = "Stand-alone Solar Powered Agriculture Pump Scheme (PM-KUSUM 2026)",
            tamilTitle = "சூரிய மின்சக்தியால் இயங்கும் தனித்த பாசன பம்பு செட் திட்டம் (70% மானியம்)",
            department = "Agricultural Engineering Department (AED), Tamil Nadu",
            category = SchemeCategory.SOLAR_POWER,
            subsidyPercentage = "70% Government Subsidy (Central 30% + TN State 40%)",
            maxFinancialBenefit = "Up to ₹2,80,000 subsidy on 5 HP / 7.5 HP / 10 HP Solar Pumps",
            summaryEnglish = "New 2026 allocation: 15,000 solar pumps approved for Tamil Nadu farmers without existing free EB connections. Farmers contribute only 30% and receive 25 years free daytime uninterrupted solar irrigation.",
            summaryTamil = "2026 புதிய ஒதுக்கீடு: மின் இணைப்புக்காக காத்திருக்கும் விவசாயிகளுக்கு 70% அரசு மானியத்தில் 5, 7.5 மற்றும் 10 HP சோலார் பம்பு செட்டுகள் நிறுவப்பட்டு 25 ஆண்டுகளுக்கு இலவச சூரிய ஒளி பாசனம் வழங்கப்படுகிறது.",
            eligibility = listOf(
                "Farmers who do not have an existing free TANGEDCO agricultural electricity connection",
                "Adequate water discharge in borewell, open well, or farm pond"
            ),
            requiredDocuments = listOf(
                "Patta, Chitta, Adangal copy for current fasli year",
                "Certificate from TANGEDCO stating EB service not yet availed",
                "Aadhaar card and cancelled bank cheque leaf"
            ),
            howToApplySteps = listOf(
                "Register on AED online portal (aed.tn.gov.in) or via Uzhav X",
                "Assistant Executive Engineer conducts ground water yield check",
                "Deposit 30% beneficiary share; installation done in 30 days"
            ),
            officialPortalUrl = "https://aed.tn.gov.in",
            helplineNumber = "044-24352258",
            isPanchayatScheme = false,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch4",
            schemeCode = "TN-2026-KCC-ZERO-INT",
            yearValidity = "2026-2027 Active",
            title = "Kisan Credit Card (KCC) 0% Interest Crop & Dairy Loan Scheme 2026",
            tamilTitle = "தொடக்க வேளாண் கூட்டுறவு சங்கம் மூலம் வட்டி இல்லா பயிர் கடன் (KCC)",
            department = "Tamil Nadu Co-operative Apex Bank & District Central Co-op Banks",
            category = SchemeCategory.CROP_LOANS,
            subsidyPercentage = "0% Zero Interest up to ₹3,00,000 on prompt repayment within 1 year",
            maxFinancialBenefit = "Scale of finance: Up to ₹52,000/acre for Paddy, ₹85,000/acre for Turmeric/Banana",
            summaryEnglish = "2026 Revision: Zero-interest production credit limit extended to include dairy farming (₹1,60,000 for 2 milch cows) and poultry alongside paddy, sugarcane, and cotton cultivation through local PACCS.",
            summaryTamil = "2026 புதிய உச்சவரம்பு: 12 மாதங்களுக்குள் முறையாக திருப்பி செலுத்தும் விவசாயிகளுக்கு ₹3,00,000 வரை முற்றிலும் வட்டி இல்லா கடன். பயிர் சாகுபடி மட்டுமின்றி கறவை மாடு வளர்ப்பிற்கும் இக்கடன் விரிவுபடுத்தப்பட்டுள்ளது.",
            eligibility = listOf(
                "Member of Primary Agricultural Co-operative Credit Society (PACCS)",
                "Owner cultivators, tenant farmers, and Joint Liability Groups (JLGs)"
            ),
            requiredDocuments = listOf(
                "Latest Adangal issued by Village Administrative Officer (VAO)",
                "Patta passbook",
                "No Objection Certificate (NOC) from nearby commercial banks",
                "Aadhaar card & 2 passport size photographs"
            ),
            howToApplySteps = listOf(
                "Submit KCC loan application at your village PACCS co-operative office",
                "Field supervisor verifies crop cultivation standing on your field",
                "Disbursement made to bank account + fertilizer voucher issued"
            ),
            officialPortalUrl = "https://tncoop.gov.in",
            helplineNumber = "1800-425-1155 (Toll Free)",
            isPanchayatScheme = true,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch5",
            schemeCode = "SMAM-2026-TRACTOR-DRONE",
            yearValidity = "2026-2027 Active",
            title = "Sub-Mission on Agricultural Mechanization (SMAM 2026 - Kisan Drone & Tractor)",
            tamilTitle = "வேளாண் இயந்திரமயமாக்கல் - 50% டிராக்டர் & 80% கிசான் ட்ரோன் மானிய திட்டம்",
            department = "Agricultural Engineering Department (AED), Govt of TN",
            category = SchemeCategory.MACHINERY_RENTAL,
            subsidyPercentage = "50% for individual machinery, 80% (up to ₹10 Lakhs) for FPO Custom Hiring",
            maxFinancialBenefit = "Up to ₹1,25,000 subsidy on Tractors; Up to ₹8,00,000 on Agri Spraying Drones",
            summaryEnglish = "New 2026 Scheme: Subsidies on rotary tillers, paddy transplanters, power weeders, and modern Kisan Drones for automated pesticide and fertilizer spraying run by village youth and FPOs.",
            summaryTamil = "2026 புதிய திட்டம்: டிராக்டர், ரோட்டவேட்டர், நாற்று நடும் இயந்திரங்களுக்கு 50% மானியமும், பூச்சி மருந்து தெளிக்கும் நவீன கிசான் ட்ரோன்களுக்கு 80% வரை அரசு மானியமும் வழங்கப்படுகிறது.",
            eligibility = listOf(
                "Individual farmer, SHG women groups, or registered FPO",
                "Valid tractor driving license or drone pilot certificate"
            ),
            requiredDocuments = listOf(
                "Patta / Chitta or FPO registration certificate",
                "Quotation from authorized machinery dealer",
                "Bank loan sanction letter or proof of beneficiary contribution"
            ),
            howToApplySteps = listOf(
                "Apply online on Agricoop portal (agrimachinery.nic.in)",
                "Computerized random selection under district quota",
                "Receive sanction order and inspect physical delivery"
            ),
            officialPortalUrl = "https://agrimachinery.nic.in",
            helplineNumber = "044-24352258",
            isPanchayatScheme = true,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch6",
            schemeCode = "TN-2026-MILLET-MISSION",
            yearValidity = "2026-2027 Active",
            title = "Tamil Nadu Millet Mission 2026 (சிறுதானிய இயக்கம் - ₹8,000/ha Incentive)",
            tamilTitle = "தமிழ்நாடு சிறுதானிய இயக்கம் 2026 (ஏக்கருக்கு சிறப்பு ஊக்கத்தொகை)",
            department = "Directorate of Agriculture, Tamil Nadu",
            category = SchemeCategory.MILLET_MISSION,
            subsidyPercentage = "₹8,000 per hectare direct DBT + 50% subsidy on mini-millet processing mills",
            maxFinancialBenefit = "₹8,000/ha incentive + free certified seed mini-kits + bio-fertilizers",
            summaryEnglish = "New 2026 priority mission: Promotes climate-resilient millets (Ragi, Kuthiraivali, Samai, Varagu, Thinai). Provides direct income support and guaranteed procurement through PDS fair price shops.",
            summaryTamil = "2026 புதிய இயக்கம்: கேழ்வரகு, குதிரைவாலி, சாமை, வரகு, தினை போன்ற சிறுதானியங்கள் பயிரிடும் விவசாயிகளுக்கு ஹெக்டேருக்கு ₹8,000 நேரடி ஊக்கத்தொகையும் இலவச விதை பாக்கெட்டுகளும் வழங்கப்படுகின்றன.",
            eligibility = listOf(
                "Farmers cultivating minor millets in Dharmapuri, Krishnagiri, Salem, Dindigul, Virudhunagar, etc.",
                "Minimum 0.5 acre cultivation"
            ),
            requiredDocuments = listOf(
                "Millet cultivation entry in Adangal",
                "Aadhaar card",
                "Bank passbook"
            ),
            howToApplySteps = listOf(
                "Register crop area with Assistant Agricultural Officer (AAO)",
                "Collect certified seed mini-kits from Block Agri Depot",
                "Incentive credited directly to bank account upon field verification"
            ),
            officialPortalUrl = "https://tnagrisnet.tn.gov.in",
            helplineNumber = "1800-425-4444",
            isPanchayatScheme = true,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch7",
            schemeCode = "PMFBY-2026-TN-INSURANCE",
            yearValidity = "2026-2027 Active",
            title = "Pradhan Mantri Fasal Bima Yojana (PMFBY 2026 Tamil Nadu Crop Insurance)",
            tamilTitle = "பிரதம மந்திரி பயிர் காப்பீட்டு திட்டம் 2026 (95% அரசு ஏற்கும் காப்பீடு கட்டணம்)",
            department = "Directorate of Agriculture & Insurance Companies",
            category = SchemeCategory.INSURANCE_RELIEF,
            subsidyPercentage = "95% of actuarial premium borne by TN Govt & Central Govt",
            maxFinancialBenefit = "Full sum insured payout (₹38,000 to ₹58,000 / acre) for flood/drought/cyclone loss",
            summaryEnglish = "2026 Comprehensive Risk Coverage: Covers unseasonal rains, drought, pests, and cyclone damage. Farmers pay only nominal 1.5% premium for Samba Paddy and 2% for Kharif crops; state pays the remaining 95%.",
            summaryTamil = "2026 பயிர் காப்பீடு: புயல், வெள்ளம், வறட்சி மற்றும் நோய் தாக்குதலால் ஏற்படும் பயிர் இழப்பிற்கு முழு இழப்பீடு. விவசாயி சம்பா நெல்லுக்கு 1.5% பிரீமியம் மட்டும் செலுத்தினால் போதும்; மீதி 95% அரசு செலுத்துகிறது.",
            eligibility = listOf(
                "All farmers cultivating notified crops in notified revenue village firkas",
                "Both loanee and non-loanee farmers eligible"
            ),
            requiredDocuments = listOf(
                "Adangal certificate from VAO with sown crop name and sowing date",
                "Aadhaar card and land patta",
                "Bank passbook front page"
            ),
            howToApplySteps = listOf(
                "Enroll at Common Service Center (e-Sevai / CSC), PACCS, or nationalized bank",
                "Pay nominal farmer share premium before cutoff date (Nov 15 for Samba paddy)",
                "Loss assessment conducted automatically via satellite and Crop Cutting Experiments (CCE)"
            ),
            officialPortalUrl = "https://pmfby.gov.in",
            helplineNumber = "14447 (PMFBY Helpline)",
            isPanchayatScheme = true,
            isNewFor2026 = true
        ),
        GovtScheme(
            id = "sch8",
            schemeCode = "TN-2026-ORGANIC-FARMING",
            yearValidity = "2026-2027 Active",
            title = "Tamil Nadu Organic Agriculture Development Scheme 2026 (TN-OFDS)",
            tamilTitle = "தமிழ்நாடு இயற்கை வேளாண்மை வளர்ச்சி திட்டம் 2026 (₹10,000 மானியம்)",
            department = "Tamil Nadu Organic Certification Department (TNOCD)",
            category = SchemeCategory.ORGANIC_FARMING,
            subsidyPercentage = "100% Free PGS Certification + ₹10,000/ha Input Support",
            maxFinancialBenefit = "₹10,000 per hectare for vermicompost units, bio-inputs & certification",
            summaryEnglish = "New 2026 Green Initiative: Supports transformation of farms to 100% chemical-free organic farming. Provides free organic certification, subsidies for building permanent vermicompost pits, and Panchakavya units.",
            summaryTamil = "2026 புதிய திட்டம்: ரசாயனமில்லா இயற்கை விவசாயத்திற்கு மாறும் விவசாயிகளுக்கு இலவச இயற்கை சான்றிதழ், மண்புழு உரத்தொட்டி கட்ட ₹10,000 மானியம் மற்றும் பயோ-இன்புட் தயாரிப்பு பயிற்சி.",
            eligibility = listOf(
                "Farmers willing to practice chemical-free organic agriculture for min 3 years",
                "Individual farmers or organic clusters (50 acres)"
            ),
            requiredDocuments = listOf(
                "Land patta copy",
                "Self-declaration of non-use of synthetic chemical pesticides",
                "Aadhaar card & bank passbook"
            ),
            howToApplySteps = listOf(
                "Register through TNOCD or local Assistant Director of Horticulture",
                "Participatory Guarantee System (PGS) group inspection",
                "Collect organic certification and DBT subsidy"
            ),
            officialPortalUrl = "https://tnocd.tn.gov.in",
            helplineNumber = "0422-2435080",
            isPanchayatScheme = true,
            isNewFor2026 = true
        )
    )

    val sampleEquipments = listOf(
        AgriEquipment(
            id = "eq1",
            name = "Mahindra 575 DI SP Plus Tractor (47 HP)",
            tamilName = "மகிந்திரா 575 DI டிராக்டர் (47 HP)",
            category = EquipmentCategory.TRACTOR_ATTACHMENTS,
            brand = "Mahindra Tractors",
            modelNumber = "575 DI SP Plus",
            originalPrice = 720000,
            subsidyApplicable = true,
            subsidyPercentText = "Govt Subsidy up to ₹1,25,000 under SMAM",
            effectivePrice = 595000,
            specifications = listOf("4 Cylinder 47 HP Engine", "Heavy Duty Hydraulic Lift 1500 kg", "Dual Clutch with Reverse PTO"),
            suitableForCrops = "Paddy, Sugarcane, Cotton, Maize, Horticulture",
            dealerName = "Cauvery Tractors & Agro Sales",
            dealerPhone = "04362-278900",
            dealerLocation = "Pudukkottai Main Road, Thanjavur",
            warranty = "6 Years Manufacturer Warranty",
            emiStartsFrom = "₹12,400 / month (KCC Agri Loan linked)"
        ),
        AgriEquipment(
            id = "eq2",
            name = "Battery & Manual Dual Power Knapsack Sprayer (16L)",
            tamilName = "பேட்டரி & கையேடு இரட்டை பயன்பாட்டு தெளிப்பான் (16 லிட்டர்)",
            category = EquipmentCategory.SPRAYERS,
            brand = "Aspee / Neptune",
            modelNumber = "VN-16 Dual",
            originalPrice = 3800,
            subsidyApplicable = true,
            subsidyPercentText = "50% Dept of Agri Subsidy (₹1,900 off)",
            effectivePrice = 1900,
            specifications = listOf("12V 12Ah Lithium-ion Battery", "4 Hours Continuous Spray", "Brass Telescopic Lance + 4 Nozzles"),
            suitableForCrops = "All crops, organic panchakavya and bio-pesticide spraying",
            dealerName = "TNAU Agri Engineering Depot",
            dealerPhone = "9842145566",
            dealerLocation = "Near Railway Station, Orathanadu",
            warranty = "1 Year Replacement Warranty",
            emiStartsFrom = "Direct Cash / UPI"
        ),
        AgriEquipment(
            id = "eq3",
            name = "7 HP Petrol Rotary Power Weeder / Tiller",
            tamilName = "7 HP பவர் வீடர் (களை எடுக்கும் கருவி)",
            category = EquipmentCategory.WEEDERS_HARVESTERS,
            brand = "KisanKraft",
            modelNumber = "KK-IC-350P",
            originalPrice = 48000,
            subsidyApplicable = true,
            subsidyPercentText = "40% Subsidy available (₹19,200 off)",
            effectivePrice = 28800,
            specifications = listOf("212 cc 4-Stroke Engine", "Adjustable tilling width 24-36 inches", "Cultivator blades included"),
            suitableForCrops = "Vegetables, Banana inter-crop, Sugarcane earthing up",
            dealerName = "Sakthi Farm Equipments",
            dealerPhone = "9443219088",
            dealerLocation = "Trichy Road, Thanjavur",
            warranty = "2 Years Engine Warranty",
            emiStartsFrom = "₹2,600 / month"
        ),
        AgriEquipment(
            id = "eq4",
            name = "Automatic Inline Drip Irrigation Kit (1 Acre Complete)",
            tamilName = "முழு தானியங்கி சொட்டு நீர் பாசன கிட் (1 ஏக்கர்)",
            category = EquipmentCategory.SOLAR_IRRIGATION,
            brand = "Jain Irrigation / Netafim",
            modelNumber = "Hydro-Drip One",
            originalPrice = 32000,
            subsidyApplicable = true,
            subsidyPercentText = "100% Free under TN Micro-Irrigation for Small Farmers",
            effectivePrice = 0,
            specifications = listOf("16mm UV resistant lateral tubes", "Sand & Screen filter assembly", "Venturi fertilizer injector included"),
            suitableForCrops = "Coconut, Banana, Guava, Chillies, Vegetables",
            dealerName = "Jain Agro Dealer Network",
            dealerPhone = "1800-599-5555",
            dealerLocation = "Old Bus Stand, Thanjavur",
            warranty = "5 Years Field Warranty",
            emiStartsFrom = "100% Govt Subsidy Free"
        ),
        AgriEquipment(
            id = "eq5",
            name = "Digital 4-in-1 Soil pH & NPK Fertility Tester",
            tamilName = "டிஜிட்டல் மண் பரிசோதனை கருவி (pH, ஈரப்பதம், சத்துக்கள்)",
            category = EquipmentCategory.SOIL_TEST_TOOLS,
            brand = "Kisan SmartTech",
            modelNumber = "AgriSense Pro",
            originalPrice = 2499,
            subsidyApplicable = false,
            subsidyPercentText = "Special Farmer Discount 35%",
            effectivePrice = 1625,
            specifications = listOf("Dual high sensitivity metal probe", "Instant pH (3.5 - 9.0) and Moisture reading", "No battery required"),
            suitableForCrops = "All soils across Tamil Nadu",
            dealerName = "Uzhav X Smart Agri Store",
            dealerPhone = "1800-425-9988",
            dealerLocation = "Doorstep Express Delivery",
            warranty = "1 Year Free Replacement",
            emiStartsFrom = "Cash on Delivery Available"
        )
    )

    val sampleWeatherReport = AgriWeatherReport(
        district = "Thanjavur",
        taluka = "Orathanadu",
        currentTempC = 30,
        condition = "Partly Cloudy with High Humidity",
        tamilCondition = "பகுதி மேகமூட்டம் & அதிக ஈரப்பதம்",
        humidityPercent = 76,
        rainProbabilityPercent = 25,
        windSpeedKmh = 14,
        soilMoistureLevel = "Optimal (68%)",
        solarRadiation = "Good for Photosynthesis",
        currentAdvisoryTamil = "சம்பா நெல் பயிருக்கு தூர் கட்டும் பருவம்: தற்போது நிலவும் 76% ஈரப்பதத்தால் இலை சுருட்டு புழு தாக்குதல் ஏற்பட வாய்ப்புள்ளது. மாலை நேரத்தில் 3% வேப்ப எண்ணெய் தெளிக்கவும்.",
        currentAdvisoryEnglish = "Samba paddy tillering stage: Prevailing 76% humidity creates conducive condition for leaf folder caterpillar. Spray 3% cold pressed neem oil extract during evening hours.",
        forecast7Days = listOf(
            DailyForecast("Today", "இன்று", 31, 24, "Partly Cloudy", "பகுதி மேகமூட்டம்", 20, 14, "Ideal for foliar spray and weeding", "இலை வழி உரம் தெளிக்க உகந்த நாள்"),
            DailyForecast("Tomorrow", "நாளை", 30, 23, "Scattered Showers", "சிதறிய மழை", 65, 18, "Hold fertilizer top dressing until rain stops", "மழை முடியும் வரை யூரியா இடுவதை தவிர்க்கவும்"),
            DailyForecast("Wed", "புதன்", 29, 23, "Light Rain", "மிதமான மழை", 75, 16, "Ensure water drainage channels in low-lying fields", "தாழ்வான நெல் வயல்களில் வடிகால் வசதி செய்க"),
            DailyForecast("Thu", "வியாழன்", 31, 24, "Mostly Sunny", "சூரிய வெளிச்சம்", 15, 12, "Good day for paddy panicle inspection", "நெல் கதிர் ஆய்வு செய்ய ஏற்ற நாள்"),
            DailyForecast("Fri", "வெள்ளி", 32, 25, "Clear Sky", "தெளிவான வானம்", 10, 10, "Schedule regular canal irrigation", "முறையான பாசன நீர் பாய்ச்சலாம்"),
            DailyForecast("Sat", "சனி", 32, 24, "Partly Cloudy", "பகுதி மேகமூட்டம்", 20, 12, "Favorable for vegetable harvesting", "காய்கறி அறுவடைக்கு உகந்த நாள்"),
            DailyForecast("Sun", "ஞாயிறு", 31, 24, "Cloudy", "மேகமூட்டம்", 35, 14, "Monitor for sucking pests", "சாறு உறிஞ்சும் பூச்சிகளை கண்காணிக்கவும்")
        )
    )

    val sampleForumPosts = listOf(
        ForumPost(
            id = "f1",
            authorName = "Ramasamy Gounder",
            authorDistrict = "Pollachi, Coimbatore",
            authorRole = "Progressive Farmer (முன்னோடி விவசாயி)",
            isVerifiedExpert = false,
            timeAgo = "2 hours ago",
            title = "Coconut Rugose Spiralling Whitefly infestation control with organic methods",
            body = "தென்னை மரங்களில் சுருள் வெள்ளை ஈ தாக்குதல் அதிகமாக உள்ளது. ரசாயன மருந்து அடிக்காமல் இயற்கை முறையில் கட்டுப்படுத்த எளிய வழி உள்ளதா? அனுபவம் உள்ள விவசாயிகள் ஆலோசனை கூறவும்.",
            cropTag = "Coconut (தென்னை)",
            upvotes = 34,
            hasUserUpvoted = false,
            postType = ForumPostType.QUESTION,
            likesCount = 34,
            hasLiked = false,
            comments = listOf(
                ForumComment(
                    id = "c1",
                    authorName = "Dr. K. Senthil Nathan",
                    authorRole = "KVK Agronomist & Entomologist (வேளாண் விஞ்ஞானி)",
                    timeAgo = "1 hour ago",
                    content = "சுருள் வெள்ளை ஈயை கட்டுப்படுத்த 20 கிராம் மைதா மாவை 1 லிட்டர் நீரில் காய்ச்சி ஒட்டும் திரவமாக தெளிக்கவும். மேலும் மஞ்சள் நிற ஒட்டும் பொறிகளை ஏக்கருக்கு 8 இடங்களில் 6 அடி உயரத்தில் கட்டவும். நன்மை செய்யும் என்கார்சியா குளவிகளை அழிக்காமல் பாதுகாக்கவும்.",
                    isExpertAnswer = true,
                    upvotes = 12,
                    hasLiked = true
                ),
                ForumComment(
                    id = "c2",
                    authorName = "Velusamy",
                    authorRole = "Farmer, Pollachi",
                    timeAgo = "45 mins ago",
                    content = "விஞ்ஞானி சொன்னது உண்மை. நான் மஞ்சள் பொறி வைத்து 80% கட்டுப்படுத்தி விட்டேன்.",
                    isExpertAnswer = false,
                    upvotes = 5,
                    hasLiked = false
                )
            )
        ),
        ForumPost(
            id = "f2",
            authorName = "Meenakshi Sundaram",
            authorDistrict = "Madurai",
            authorRole = "Organic Jasmine Farmer (இயற்கை மல்லிகை விவசாயி)",
            isVerifiedExpert = true,
            timeAgo = "5 hours ago",
            title = "Panchakavya + Fish Amino Acid schedule for bumper Jasmine harvest",
            body = "மதுரை குண்டு மல்லிக்கு 10 நாட்களுக்கு ஒருமுறை பஞ்சகாவ்யா 300 மிலி + மீன் அமினோ அமிலம் 200 மிலி கைத்தெளிப்பான் மூலம் கொடுத்து வருகிறேன். மொட்டுகள் திரட்சியாகவும் நல்ல வாசனையுடனும் பூக்கின்றன. சந்தையில் கூடுதல் விலை கிடைக்கிறது.",
            cropTag = "Horticulture (மல்லிகை)",
            upvotes = 58,
            hasUserUpvoted = true,
            postType = ForumPostType.TIP,
            likesCount = 58,
            hasLiked = true,
            comments = listOf(
                ForumComment(
                    id = "c3",
                    authorName = "Annamalai",
                    authorRole = "Farmer, Usilampatti",
                    timeAgo = "3 hours ago",
                    content = "மீன் அமினோ அமிலம் தயாரிக்கும் முறை விவரம் தர முடியுமா அம்மா?",
                    isExpertAnswer = false,
                    upvotes = 3,
                    hasLiked = false
                )
            )
        ),
        ForumPost(
            id = "f3",
            authorName = "Senguttuvan",
            authorDistrict = "Erode",
            authorRole = "Turmeric Cultivator (மஞ்சள் விவசாயி)",
            isVerifiedExpert = false,
            timeAgo = "Yesterday",
            title = "Raised bed + Drip Irrigation secret for 26 Quintals/Acre Turmeric yield",
            body = "ஈரோடு விரலி மஞ்சள் சாகுபடியில் மேட்டுப்பாத்தி அமைத்து 16 மிமீ சொட்டு நீர் குழாய் அமைத்தேன். கிழங்கு அழுகல் நோய் 0% ஆக குறைந்தது. யூரியா, பொட்டாஷ் உரங்களை சொட்டு நீரில் கரைத்து கொடுத்ததால் உரம் 30% மிச்சமானது. BSR-2 ரகத்தில் ஏக்கருக்கு 26 குவிண்டால் விளைச்சல் கிடைத்தது.",
            cropTag = "Turmeric (மஞ்சள்)",
            upvotes = 76,
            hasUserUpvoted = false,
            postType = ForumPostType.EXPERIENCE,
            likesCount = 76,
            hasLiked = false,
            comments = listOf(
                ForumComment(
                    id = "c4",
                    authorName = "Kandasamy",
                    authorRole = "Farmer, Bhavani",
                    timeAgo = "20 hours ago",
                    content = "மேட்டுப்பாத்தி அகலம் மற்றும் இடைவெளி எவ்வளவு வைத்தீர்கள் அண்ணா?",
                    isExpertAnswer = false,
                    upvotes = 4,
                    hasLiked = false
                ),
                ForumComment(
                    id = "c5",
                    authorName = "Senguttuvan",
                    authorRole = "Author",
                    timeAgo = "18 hours ago",
                    content = "பாத்தி அகலம் 4 அடி, உயரம் 1 அடி, பாத்திகளுக்கு இடையே 1.5 அடி வாய்க்கால் இடைவெளி.",
                    isExpertAnswer = false,
                    upvotes = 8,
                    hasLiked = true
                )
            )
        ),
        ForumPost(
            id = "f4",
            authorName = "Karuppasamy",
            authorDistrict = "Thanjavur",
            authorRole = "Paddy Farmer (நெல் விவசாயி)",
            isVerifiedExpert = false,
            timeAgo = "Yesterday",
            title = "Kuruvai harvest price in DPC vs Private Traders",
            body = "தஞ்சாவூர் நேரடி நெல் கொள்முதல் நிலையத்தில் ஈரப்பதம் 17% க்குள் இருந்தால் குவிண்டால் ₹2,450 அரசு விலை கொடுக்கிறார்கள். தனியார் வியாபாரிகள் வயலுக்கே வந்து ₹2,200 கொடுக்கிறார்கள். கொள்முதல் நிலையத்தில் சுணக்கம் இல்லாமல் உடனே எடுக்க ஏற்பாடு செய்ய வேண்டும்.",
            cropTag = "Paddy (நெல்)",
            upvotes = 42,
            hasUserUpvoted = false,
            postType = ForumPostType.QUESTION,
            likesCount = 42,
            hasLiked = false,
            comments = emptyList()
        ),
        ForumPost(
            id = "f5",
            authorName = "Muthukumar",
            authorDistrict = "Tenkasi",
            authorRole = "Pulses Grower (பயறு விவசாயி)",
            isVerifiedExpert = false,
            timeAgo = "2 days ago",
            title = "Pest Alert: Pod Borer caterpillar in black gram - 3% Neem spray advice",
            body = "நெல் தரிசு உளுந்து பயிரில் காய்ப்புழு தாக்குதல் தென்படுகிறது. பூக்கும் தருணத்தில் வேப்ப எண்ணெய் 3% அல்லது பேசில்லஸ் துரிஞ்சியென்சிஸ் (Bt) 2 கிராம்/லிட்டர் மாலை வேளையில் தெளித்தால் காய்ப்புழு முற்றிலும் அழியும்.",
            cropTag = "Pulses (உளுந்து)",
            upvotes = 29,
            hasUserUpvoted = true,
            postType = ForumPostType.ALERT,
            likesCount = 29,
            hasLiked = true,
            comments = emptyList()
        )
    )

    val sampleDiseases = listOf(
        PlantDiseaseDiagnosis(
            id = "dis1",
            cropName = "Paddy / Rice (நெல்)",
            tamilCropName = "நெல்",
            diseaseName = "Rice Blast Disease (இலைக்கருகல் நோய்)",
            tamilDiseaseName = "நெல் இலைக்கருகல் & குலை நோய்",
            scientificName = "Magnaporthe oryzae (Pyricularia oryzae)",
            confidencePercent = 97,
            severityLevel = "Moderate to High",
            symptomsEnglish = listOf(
                "Spindle-shaped eye spots with brown or reddish borders and grey centres on leaves",
                "Neck rot causing panicles to droop and turn whitish empty chaffs",
                "Rapid spread during cloudy weather with high relative humidity (> 85%)"
            ),
            symptomsTamil = listOf(
                "இலைகளில் இரு முனைகளும் கூம்பான கண் வடிவ அல்லது கதிர் வடிவ பழுப்பு புள்ளிகள்",
                "புள்ளியின் மையம் சாம்பல் நிறமாகவும் ஓரங்கள் பழுப்பு நிறமாகவும் மாறுதல்",
                "கதிர் கழுத்து அழுகி தானியங்கள் பதராக மாறுதல்"
            ),
            organicRemedies = listOf(
                "Spray 10% Desi Cow Urine extract (1 Liter cow urine in 10 Liters water) mixed with 100g asafoetida",
                "Foliar spray with Pseudomonas fluorescens bio-agent @ 5 grams / Liter of water",
                "Apply Neem cake @ 100 kg / acre to activate soil resistance"
            ),
            chemicalTreatments = listOf(
                "Tricyclazole 75% WP @ 0.6 grams per Liter of water (120g / acre in 200L water)",
                "Or Isoprothiolane 40% EC @ 1.5 ml per Liter of water",
                "Or Azoxystrobin 18.2% + Difenoconazole 11.4% SC @ 1 ml per Liter"
            ),
            preventiveCare = listOf(
                "Avoid excessive application of chemical Nitrogen / Urea fertilizer",
                "Split urea application into 3-4 doses rather than single heavy application",
                "Maintain thin water layer in field during active tillering"
            ),
            tnauexpertNote = "TNAU Advisory: Apply Tricyclazole during early morning before sun gets hot. Repeat spray after 12 days if rainy conditions persist.",
            stepByStepCureProcess = listOf(
                com.example.model.TreatmentStep(
                    stepNumber = 1,
                    titleEnglish = "Step 1: Immediate Field Drainage & Leaf Pruning",
                    titleTamil = "படி 1: வயல் நீர் வடிகால் & கருகிய இலை அகற்றம்",
                    descriptionEnglish = "Drain standing excess water in paddy field. Prune heavily infected bottom leaves showing spindle-shaped blast lesions and destroy them outside the field.",
                    descriptionTamil = "வயலில் தேங்கியுள்ள அதிகப்படியான நீரை உடனடியாக வடித்து விடவும். பாதிக்கப்பட்ட கீழ் இலைகளை அகற்றி வயலுக்கு வெளியே அழித்து விடுங்கள்.",
                    timeframe = "Day 1 (Immediate)",
                    iconCategory = "sanitation"
                ),
                com.example.model.TreatmentStep(
                    stepNumber = 2,
                    titleEnglish = "Step 2: Pseudomonas Bio-Control Spray",
                    titleTamil = "படி 2: சூடோமோனாஸ் உயிரி மருந்து தெளிப்பு",
                    descriptionEnglish = "Spray Pseudomonas fluorescens @ 5g / Liter of water (1 kg / acre in 200L water) during early morning hours.",
                    descriptionTamil = "சூடோமோனாஸ் புளோரசன்ஸ் 5 கிராம்/லிட்டர் நீர் வீதம் (ஏக்கருக்கு 1 கிலோ) அதிகாலை வேளையில் பயிர் முழுவதும் நன்கு நனையுமாறு தெளிக்கவும்.",
                    timeframe = "Day 2 (Morning Spray)",
                    iconCategory = "organic"
                ),
                com.example.model.TreatmentStep(
                    stepNumber = 3,
                    titleEnglish = "Step 3: Tricyclazole 75% WP Spray",
                    titleTamil = "படி 3: டிரைசைக்ளசோல் 75% WP பூஞ்சாணக்கொல்லி தெளிப்பு",
                    descriptionEnglish = "Spray Tricyclazole 75% WP @ 0.6g per Liter of water (120g per acre in 200 Liters water) to arrest fungus growth.",
                    descriptionTamil = "டிரைசைக்ளசோல் 75% WP பூஞ்சாணக்கொல்லி 0.6 கிராம்/லிட்டர் நீர் வீதம் (ஏக்கருக்கு 120 கிராம்) கலந்து கைத்தெளிப்பான் மூலம் தெளிக்கவும்.",
                    timeframe = "Day 3 to 4",
                    iconCategory = "chemical"
                ),
                com.example.model.TreatmentStep(
                    stepNumber = 4,
                    titleEnglish = "Step 4: Potash Top Dressing & Nitrogen Split",
                    titleTamil = "படி 4: பொட்டாஷ் மேலுரம் & தழைச்சத்து மேலாண்மை",
                    descriptionEnglish = "Stop top dressing Urea temporarily. Apply Murate of Potash (MOP) @ 25 kg / acre to strengthen paddy tiller walls against neck blast.",
                    descriptionTamil = "யூரியா இடுவதை தற்காலிகமாக நிறுத்துங்கள். ஏக்கருக்கு 25 கிலோ பொட்டாஷ் உரம் மேலுரமாக இட்டு நெல் பயிரின் எதிர்ப்பு திறனை உயர்த்தவும்.",
                    timeframe = "Day 7 to 10",
                    iconCategory = "prevention"
                )
            )
        ),
        PlantDiseaseDiagnosis(
            id = "dis2",
            cropName = "Tomato (தக்காளி)",
            tamilCropName = "தக்காளி",
            diseaseName = "Early Blight & Leaf Spot (இலைப்புள்ளி நோய்)",
            tamilDiseaseName = "தக்காளி ஆரம்பக்கால கருகல் & இலைப்புள்ளி",
            scientificName = "Alternaria solani",
            confidencePercent = 94,
            severityLevel = "Moderate",
            symptomsEnglish = listOf(
                "Concentric target-board rings on older lower leaves",
                "Yellowing surrounding dark brown patches leading to defoliation",
                "Stem collar rot in young seedlings"
            ),
            symptomsTamil = listOf(
                "கீழ் இலைகளில் வளைய வடிவிலான வட்ட வடிவ பழுப்பு கருகல் புள்ளிகள்",
                "புள்ளிகளை சுற்றி இலை மஞ்சள் நிறமாக மாறி உதிர்ந்து விடுதல்",
                "தண்டு பகுதியில் கரும்பழுப்பு நிற புண்கள் ஏற்படுதல்"
            ),
            organicRemedies = listOf(
                "Spray Panchakavya 3% (30 ml / Liter) mixed with sour buttermilk 50 ml / Liter",
                "Trichoderma viride soil application enriched in vermicompost"
            ),
            chemicalTreatments = listOf(
                "Mancozeb 75% WP @ 2 grams per Liter of water",
                "Or Copper Oxychloride 50% WP @ 2.5 grams per Liter of water"
            ),
            preventiveCare = listOf(
                "Provide bamboo staking to prevent leaves touching moist soil",
                "Avoid overhead sprinkler irrigation; prefer drip irrigation"
            ),
            tnauexpertNote = "Remove and safely burn lower infected leaves before spraying fungicide."
        ),
        PlantDiseaseDiagnosis(
            id = "dis3",
            cropName = "Chilli (மிளகாய்)",
            tamilCropName = "மிளகாய்",
            diseaseName = "Leaf Curl Virus & Thrips Damage (இலைச்சுருட்டு நோய்)",
            tamilDiseaseName = "மிளகாய் இலை சுருட்டு & இலை பேன் தாக்குதல்",
            scientificName = "Chilli leaf curl virus transmitted by Bemisia tabaci / Scirtothrips dorsalis",
            confidencePercent = 96,
            severityLevel = "High",
            symptomsEnglish = listOf(
                "Upward cupping of leaves with puckering and stunted growth",
                "Downward curling caused by mite infestation",
                "Severe reduction in flowering and fruit set"
            ),
            symptomsTamil = listOf(
                "இலைகள் படகு வடிவில் மேல்நோக்கி சுருண்டு சிறுத்து போதல்",
                "செடிகள் வளர்ச்சி குன்றி பூக்கள் உதிர்ந்து காய் பிடிக்காமல் போதல்",
                "வெள்ளை ஈ மற்றும் இலைப்பேன் மூலம் வைரஸ் பரவுதல்"
            ),
            organicRemedies = listOf(
                "Spray 5-Leaf Herbal Pest Repellent (ஐந்திலை கரைசல் - வேம்பு, எருக்கு, ஆடாதோடை, நொச்சி, சோற்றுக்கற்றாழை)",
                "Install yellow and blue sticky traps (10 traps / acre) to trap vector insects"
            ),
            chemicalTreatments = listOf(
                "Diafenthiuron 50% WP @ 1.25 grams per Liter of water",
                "Or Fipronil 5% SC @ 2 ml per Liter of water"
            ),
            preventiveCare = listOf(
                "Raise 2 border rows of maize or bajra around chilli field as windbreak barrier",
                "Spray systemic insecticide at first sign of whitefly population"
            ),
            tnauexpertNote = "Viral infected plants cannot be cured; remove severely stunted plants and destroy vector insects immediately."
        ),
        PlantDiseaseDiagnosis(
            id = "dis4",
            cropName = "Cotton (பருத்தி)",
            tamilCropName = "பருத்தி",
            diseaseName = "Bacterial Blight & Angular Leaf Spot (கோண இலைப்புள்ளி)",
            tamilDiseaseName = "பருத்தி கோண இலைப்புள்ளி & கருங்காய்ப்புழு",
            scientificName = "Xanthomonas citri pv. malvacearum",
            confidencePercent = 95,
            severityLevel = "Severe",
            symptomsEnglish = listOf(
                "Angular dark brown water-soaked lesions bounded by leaf veins",
                "Black arm symptoms on stem causing branch breakage",
                "Boll lesions leading to premature drying and fiber staining"
            ),
            symptomsTamil = listOf(
                "இலை நரம்புகளுக்கு இடையில் கோண வடிவ கரும்பழுப்பு நிற நீர் கசிந்த புள்ளிகள்",
                "தண்டு பகுதியில் கருப்பு நிற பட்டை புண்கள் தோன்றி கிளைகள் முறிந்து போதல்",
                "பஞ்சு காய்களில் புள்ளி தோன்றி காய் அழுகி பஞ்சின் தரம் பாதித்தல்"
            ),
            organicRemedies = listOf(
                "Seed treatment with Pseudomonas fluorescens @ 10g / kg seed before sowing",
                "Spray fresh cow dung filtrate extract 5% (50g / L water) as bio-bactericide"
            ),
            chemicalTreatments = listOf(
                "Streptomycin sulphate + Tetracycline hydrochloride (Streptocycline) @ 100 mg + Copper Oxychloride @ 2.5g per Liter of water",
                "Spray volume: 200 Liters per acre"
            ),
            preventiveCare = listOf(
                "Delint seeds with concentrated Sulphuric acid (100 ml / kg seed) before sowing",
                "Avoid late nitrogen application in rainfed black soils"
            ),
            tnauexpertNote = "Spray during morning hours when leaf pores are open. Repeat after 15 days if continuous drizzles occur."
        ),
        PlantDiseaseDiagnosis(
            id = "dis5",
            cropName = "Turmeric (மஞ்சள்)",
            tamilCropName = "மஞ்சள்",
            diseaseName = "Leaf Spot & Rhizome Rot (மஞ்சள் இலைப்புள்ளி & கிழங்கு அழுகல்)",
            tamilDiseaseName = "மஞ்சள் இலைப்புள்ளி நோய் & வேரழுகல்",
            scientificName = "Colletotrichum capsici & Pythium aphanidermatum",
            confidencePercent = 98,
            severityLevel = "Moderate",
            symptomsEnglish = listOf(
                "Elliptical spots on both surfaces of leaves turning brown with yellow halo",
                "Rhizome soft rot leading to foul odor and plant collapse",
                "Drying of leaf tips from lower leaves moving upwards"
            ),
            symptomsTamil = listOf(
                "இலைகளில் நீள்வட்ட வடிவ பழுப்பு புள்ளிகள் தோன்றி நடுப்பகுதி வெளிர்ப்படுதல்",
                "மண்ணில் உள்ள கிழங்கு அழுகி துர்நாற்றம் வீசி செடிகள் சாய்ந்து போதல்",
                "முதிர்ந்த இலைகளின் ஓரங்கள் மற்றும் முனைகள் மஞ்சள் நிறமாகி காய்ந்து போதல்"
            ),
            organicRemedies = listOf(
                "Soil application of Trichoderma harzianum @ 2.5 kg mixed in 250 kg FYM per acre",
                "Drench root zone with Jeevamrutham (200 Liters per acre)"
            ),
            chemicalTreatments = listOf(
                "Carbendazim 12% + Mancozeb 63% WP (Saaf) @ 2 grams per Liter of water",
                "For Rhizome rot: Metalaxyl + Mancozeb (Ridomil MZ) @ 2.5 g / Liter soil drench"
            ),
            preventiveCare = listOf(
                "Provide deep drainage channels to prevent stagnant moisture around turmeric beds",
                "Solarize turmeric seed rhizomes before planting in ridges"
            ),
            tnauexpertNote = "Erode & Salem TNAU Advisory: Ensure raised-bed planting to avoid rhizome rot during heavy Northeast monsoon rains."
        ),
        PlantDiseaseDiagnosis(
            id = "dis6",
            cropName = "Sugarcane (கரும்பு)",
            tamilCropName = "கரும்பு",
            diseaseName = "Red Rot Disease (செவ்வழுகல் நோய்)",
            tamilDiseaseName = "கரும்பு செவ்வழுகல் நோய்",
            scientificName = "Colletotrichum falcatum",
            confidencePercent = 93,
            severityLevel = "Severe",
            symptomsEnglish = listOf(
                "Third and fourth leaves from top show yellowing and drying along margins",
                "Red internal pith with distinct white horizontal patches when cane is split longitudinally",
                "Alcoholic fermentation sour odor emitted from split diseased stalks"
            ),
            symptomsTamil = listOf(
                "மேலிருந்து 3 மற்றும் 4வது இலைகள் மஞ்சள் நிறமாகி ஓரங்கள் காய்ந்து போதல்",
                "கரும்பை நீளவாக்கில் பிளந்து பார்த்தால் உட்பகுதி சிவப்பாகவும் குறுக்கு வெட்டு வெள்ளை திட்டுக்களுடனும் காணப்படுதல்",
                "பிளந்த கரும்பிலிருந்து புளித்த சாராய வாசனை வருதல்"
            ),
            organicRemedies = listOf(
                "Set-treatment with bio-agent Trichoderma viride @ 5g / Liter water for 15 minutes before planting",
                "Green manuring with Sunnhemp / Daincha in alternate rows"
            ),
            chemicalTreatments = listOf(
                "Dip cane sets in Carbendazim 50% WP @ 1g / Liter for 15 minutes before planting",
                "Remove and burn diseased clumps immediately to prevent field spread"
            ),
            preventiveCare = listOf(
                "Use healthy seed canes from TNAU / ICAR-SBI certified disease-free nurseries",
                "Avoid ratoon crop if plant crop is infected with red rot"
            ),
            tnauexpertNote = "Red rot is non-curable in standing crop. Rogue out affected clumps with root mass and drench spot with 0.1% Carbendazim."
        )
    )

    val sampleFeedbackReports = listOf(
        FeedbackReport(
            id = "rep1",
            farmerName = "Sundaram Murugan",
            district = "Thanjavur",
            taluka = "Orathanadu",
            village = "Pudur",
            phone = "9842109876",
            category = ReportCategory.CANAL_WATER_ISSUE,
            description = "Grand Anicut canal branch water discharge is uneven. Tail-end farmers in Pudur village are not receiving water for samba paddy.",
            timestamp = "2026-10-29",
            status = ReportStatus.INVESTIGATING
        )
    )

    fun recommendCrops(request: CropRecommendationRequest): RecommendationResult {
        val soilLower = request.soilType.lowercase()
        val waterLower = request.waterSource.lowercase()
        val seasonLower = request.season.lowercase()
        val districtLower = request.district.lowercase()
        val acres = request.landSizeAcres.coerceAtLeast(0.5)

        val isRainfed = waterLower.contains("rainfed") || waterLower.contains("மானாவாரி")
        val isDrip = waterLower.contains("drip") || waterLower.contains("சொட்டு")
        val isCanal = waterLower.contains("canal") || waterLower.contains("கால்வாய்")
        val isBorewell = waterLower.contains("borewell") || waterLower.contains("ஆழ்துளை") || waterLower.contains("well")

        val isSamba = seasonLower.contains("samba") || seasonLower.contains("சம்பா")
        val isKuruvai = seasonLower.contains("kuruvai") || seasonLower.contains("குறுவை")
        val isSummer = seasonLower.contains("summer") || seasonLower.contains("navarai") || seasonLower.contains("நவரை") || seasonLower.contains("கோடை")

        val isAlluvial = soilLower.contains("alluvial") || soilLower.contains("வண்டல்")
        val isRedLoam = soilLower.contains("red") || soilLower.contains("செம்மண்")
        val isBlackSoil = soilLower.contains("black") || soilLower.contains("கரிசல்")
        val isClay = soilLower.contains("clay") || soilLower.contains("களிமண்")
        val isSandy = soilLower.contains("sandy") || soilLower.contains("மணல்")
        val isLaterite = soilLower.contains("laterite") || soilLower.contains("சரளை")

        val candidates = mutableListOf<RecommendedCrop>()

        // 1. Samba Paddy (CR 1009 Sub 1 / ADT 53 / BPT 5204)
        run {
            var soilScore = when {
                isAlluvial || isClay -> 35
                isRedLoam -> 25
                isBlackSoil -> 22
                else -> 15
            }
            var waterScore = when {
                isCanal -> 35
                isBorewell -> 32
                isDrip -> 18 // Drip not standard for flooded paddy
                else -> 5 // Rainfed paddy has high crop failure risk
            }
            var seasonScore = when {
                isSamba -> 30
                isKuruvai -> 15
                else -> 10
            }
            if (districtLower.contains("thanjavur") || districtLower.contains("tiruvarur") || districtLower.contains("nagapattinam") || districtLower.contains("cuddalore")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(40, 98)
            if (!isRainfed) {
                val yield = if (isCanal || isBorewell) 27.0 else 19.0
                val price = 2450 // MSP + TN State Incentive ₹100
                val cost = 24500
                val gross = (yield * price).toInt()
                val net = gross - cost
                candidates.add(
                    RecommendedCrop(
                        cropName = "Samba Paddy (CR 1009 Sub-1 / ADT 53)",
                        tamilName = "சம்பா நெல் (CR 1009 சப்-1 / ADT 53)",
                        category = "Cereals / Grains",
                        matchScorePercent = match,
                        estimatedYieldQuintalPerAcre = yield,
                        costOfCultivationPerAcre = cost,
                        marketPricePerQuintal = price,
                        estimatedGrossRevenue = gross,
                        estimatedNetProfitPerAcre = net,
                        durationDays = 140,
                        waterNeed = "High (Standing Water 5cm)",
                        bestSowingWindow = "September - November",
                        suitableSoilSummary = "Alluvial Clay & Heavy Loam with high moisture retention",
                        climateSuitability = "Ideal for North-East monsoon rainfed delta & canal command areas",
                        marketTrendReasoning = "Assured procurement at Govt DPC centers with TN bonus (₹2,450/Q). Zero price risk.",
                        tnauexpertTips = listOf(
                            "CR 1009 Sub-1 tolerates up to 14 days of complete water submersion during delta floods",
                            "Adopt SRI (System of Rice Intensification) 25x25cm square planting to save 30% seed & water",
                            "Apply Azospirillum bio-fertilizer @ 2 kg/acre mixed with 25 kg FYM before transplanting"
                        ),
                        riskFactor = "Low (Govt Guaranteed MSP)",
                        recommendedVarieties = "CR 1009 Sub-1, ADT 53, BPT 5204 (Samba Mahsuri)",
                        fertilizerSchedule = "Basal: 50kg DAP + 25kg Potash | Tillering: 35kg Urea | Panicle: 30kg Urea + 15kg Potash"
                    )
                )
            }
        }

        // 2. Kuruvai Paddy (CO 51 / ADT 43)
        if (isKuruvai && !isRainfed) {
            val match = when {
                isAlluvial || isClay -> 95
                isRedLoam -> 88
                else -> 80
            }
            val yield = 25.5
            val price = 2450
            val cost = 23000
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Kuruvai Paddy (CO 51 / ADT 43)",
                    tamilName = "குறுவை நெல் (CO 51 / ADT 43)",
                    category = "Cereals / Grains",
                    matchScorePercent = match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 110,
                    waterNeed = "High (Assured Canal / Tube well)",
                    bestSowingWindow = "June - July",
                    suitableSoilSummary = "Alluvial Loam & Clay Loam",
                    climateSuitability = "Sunny dry conditions with reliable canal discharge from Mettur",
                    marketTrendReasoning = "Quick 110-day crop allows farmers to take a profitable second Samba or Thaladi crop.",
                    tnauexpertTips = listOf(
                        "CO 51 provides high blast disease resistance and fine grain quality",
                        "Use 14-day young single seedlings for SRI planting"
                    ),
                    riskFactor = "Low (Govt MSP)",
                    recommendedVarieties = "CO 51, ADT 43, ASD 16",
                    fertilizerSchedule = "100:50:50 kg N:P:K per hectare"
                )
            )
        }

        // 3. Turmeric (Erode Local / BSR-2 / IISR Pragati)
        run {
            var soilScore = when {
                isRedLoam -> 35
                isAlluvial -> 32
                isClay -> 22
                else -> 18
            }
            var waterScore = when {
                isDrip -> 35 // Drip gives exceptional results in turmeric
                isBorewell -> 32
                isCanal -> 26
                else -> 5 // Cannot survive rainfed drought
            }
            var seasonScore = when {
                isKuruvai -> 30 // Sowing in May-July
                isSamba -> 22
                else -> 12
            }
            if (districtLower.contains("erode") || districtLower.contains("salem") || districtLower.contains("dharmapuri") || districtLower.contains("coimbatore")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(40, 97)
            if (!isRainfed) {
                val yield = if (isDrip) 26.5 else 23.0
                val price = 15900 // Erode Mandi premium market rate
                val cost = if (isDrip) 88000 else 82000
                val gross = (yield * price).toInt()
                val net = gross - cost
                candidates.add(
                    RecommendedCrop(
                        cropName = "Turmeric (BSR-2 / IISR Pragati)",
                        tamilName = "ஈரோடு விரலி மஞ்சள் (BSR-2 / பிரகதி)",
                        category = "Spices & High-Value Cash Crop",
                        matchScorePercent = match,
                        estimatedYieldQuintalPerAcre = yield,
                        costOfCultivationPerAcre = cost,
                        marketPricePerQuintal = price,
                        estimatedGrossRevenue = gross,
                        estimatedNetProfitPerAcre = net,
                        durationDays = 270,
                        waterNeed = "Moderate with Drip (Cannot stand waterlogging)",
                        bestSowingWindow = "May - July (Vaigasi - Aani)",
                        suitableSoilSummary = "Well-drained Red Sandy Loam or rich Alluvial Loam with pH 6.5 - 7.5",
                        climateSuitability = "Warm humid tropical climate with partial monsoon shade",
                        marketTrendReasoning = "Export demand & domestic pharma/wellness demand have propelled turmeric to historic highs (₹15,900/Q). Top profit generator.",
                        tnauexpertTips = listOf(
                            "Adopt broad bed and furrow (BBF) or raised beds with inline drip system to avoid rhizome rot",
                            "Treat seed rhizomes with Trichoderma viride @ 4g/kg or Mancozeb @ 2g/litre water",
                            "Apply 10 tonnes of FYM + 2 kg Trichoderma + 2 kg Azospirillum per acre during land preparation"
                        ),
                        riskFactor = "Medium (Long 9-month gestation period)",
                        recommendedVarieties = "BSR-2 (High Curcumin 4.2%), IISR Pragati, Erode Local",
                        fertilizerSchedule = "Basal: 25t FYM + 50kg P2O5 | Top Dressing via fertigation: 120kg N + 120kg K2O split in 10 doses"
                    )
                )
            }
        }

        // 4. Small Onion / Shallots (Co-5 / Dindigul Local)
        run {
            var soilScore = when {
                isRedLoam -> 35
                isSandy -> 32
                isAlluvial -> 28
                isClay -> 20
                else -> 22
            }
            var waterScore = when {
                isDrip -> 35
                isBorewell -> 32
                isCanal -> 26
                else -> 10
            }
            var seasonScore = when {
                isSamba || isSummer -> 28
                isKuruvai -> 24
                else -> 20
            }
            if (districtLower.contains("dindigul") || districtLower.contains("perambalur") || districtLower.contains("tiruchirappalli") || districtLower.contains("tiruppur")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(40, 96)
            if (!isRainfed) {
                val yield = if (isDrip) 72.0 else 64.0
                val price = 5200 // Mandi market price
                val cost = 56000
                val gross = (yield * price).toInt()
                val net = gross - cost
                candidates.add(
                    RecommendedCrop(
                        cropName = "Small Onion / Shallots (Co-5 / Dindigul Local)",
                        tamilName = "சின்ன வெங்காயம் (Co-5 / திண்டுக்கல் ரகம்)",
                        category = "Vegetables & Commercial",
                        matchScorePercent = match,
                        estimatedYieldQuintalPerAcre = yield,
                        costOfCultivationPerAcre = cost,
                        marketPricePerQuintal = price,
                        estimatedGrossRevenue = gross,
                        estimatedNetProfitPerAcre = net,
                        durationDays = 75,
                        waterNeed = "Moderate (Drip irrigation delivers 30% higher bulb size)",
                        bestSowingWindow = "October - December or April - May",
                        suitableSoilSummary = "Friable Red Sandy Loam with high organic carbon and excellent drainage",
                        climateSuitability = "Cool nights during vegetative phase and warm sunny days during bulb development",
                        marketTrendReasoning = "Constant household demand across Tamil Nadu; very short 75-day turnaround delivers fast liquidity.",
                        tnauexpertTips = listOf(
                            "Select medium-sized seed bulbs (10-12g); dip in Carbendazim 2g/kg before sowing",
                            "Spray TNAU Vegetable Special @ 5g/litre at 30 and 45 days after planting for uniform bulb expansion",
                            "Stop irrigation 7 days prior to harvest to prevent post-harvest neck rotting"
                        ),
                        riskFactor = "Medium (Mandi market price volatility)",
                        recommendedVarieties = "Co-5, Co (On) 5, Dindigul Local, Arka Ujjwal",
                        fertilizerSchedule = "Basal: 15t FYM + 60kg DAP + 30kg Potash | Top dress: 30kg Urea at 30 days"
                    )
                )
            }
        }

        // 5. Banana / வாழை (G9 Grand Naine / Robusta / Poovan)
        run {
            var soilScore = when {
                isAlluvial || isClay -> 35
                isRedLoam -> 32
                else -> 20
            }
            var waterScore = when {
                isDrip || isCanal -> 35
                isBorewell -> 32
                else -> 5 // Requires high, uninterrupted water
            }
            var seasonScore = 26 // Can be planted year-round if water is assured
            if (districtLower.contains("tiruchirappalli") || districtLower.contains("thanjavur") || districtLower.contains("theni") || districtLower.contains("erode")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(40, 95)
            if (!isRainfed) {
                val yield = 350.0 // 35 tonnes per acre (~1200 plants)
                val price = 1800 // Mandi average per quintal (₹18/kg)
                val cost = 175000 // High input cost (plants, drip, propping, fertilizers)
                val gross = (yield * price).toInt()
                val net = gross - cost
                candidates.add(
                    RecommendedCrop(
                        cropName = "Banana (Grand Naine G-9 / Robusta)",
                        tamilName = "வாழை (கிராண்ட் நைன் G-9 / பூவன்)",
                        category = "Horticulture Cash Crop",
                        matchScorePercent = match,
                        estimatedYieldQuintalPerAcre = yield,
                        costOfCultivationPerAcre = cost,
                        marketPricePerQuintal = price,
                        estimatedGrossRevenue = gross,
                        estimatedNetProfitPerAcre = net,
                        durationDays = 330,
                        waterNeed = "High (Continuous moisture; Drip with fertigation recommended)",
                        bestSowingWindow = "February - April or August - October",
                        suitableSoilSummary = "Deep Alluvial Clay Loam with minimum 1 meter depth and good aeration",
                        climateSuitability = "Humid tropical climate protected from heavy wind storms",
                        marketTrendReasoning = "Steady daily consumption, temple ceremonies, export markets, and leaf by-product yield huge returns.",
                        tnauexpertTips = listOf(
                            "Plant virus-indexed tissue culture plants for uniform bunch weight (>30 kg per bunch)",
                            "Use Casuarina or Bamboo poles for propping to protect heavy bunches from lodging during winds",
                            "De-sucker regularly keeping only one follower sucker after flowering"
                        ),
                        riskFactor = "Medium (Storm lodging risk)",
                        recommendedVarieties = "Grand Naine (G9), Robusta, Poovan, Rasthali, Ney Poovan",
                        fertilizerSchedule = "Apply 200g Urea, 100g Single Super Phosphate, 300g MOP per plant in 4 splits"
                    )
                )
            }
        }

        // 6. Bt Cotton (RCH 659 / Suraj / MCU 5)
        run {
            var soilScore = when {
                isBlackSoil -> 35
                isRedLoam -> 30
                isAlluvial -> 24
                else -> 18
            }
            var waterScore = when {
                isBorewell || isCanal -> 30
                isDrip -> 34
                isRainfed -> 28 // Cotton handles dryland rainfed well!
                else -> 25
            }
            var seasonScore = when {
                isSamba -> 30 // Sowing in Aug-Oct
                isSummer -> 15
                else -> 20
            }
            if (districtLower.contains("virudhunagar") || districtLower.contains("tirunelveli") || districtLower.contains("thoothukudi") || districtLower.contains("salem")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(45, 94)
            val yield = if (isRainfed) 7.5 else (if (isDrip) 13.0 else 11.0)
            val price = 7700 // Cotton Corporation of India / Mandi price
            val cost = if (isRainfed) 24000 else 32000
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Bt Cotton (RCH 659 / Suraj / MCU-5)",
                    tamilName = "பருத்தி (RCH 659 / சூராஜ் / MCU-5)",
                    category = "Fiber Cash Crop",
                    matchScorePercent = match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 155,
                    waterNeed = "Low to Moderate (Deep taproot handles dry spells)",
                    bestSowingWindow = "August - October",
                    suitableSoilSummary = "Deep Black Cotton Soil (Regur) or fine textured Red Sandy Loam",
                    climateSuitability = "Warm sunny days with dry clear weather during boll bursting",
                    marketTrendReasoning = "Continuous demand from Tamil Nadu spinning mills (Coimbatore, Tiruppur, Rajapalayam) guarantees spot cash sales.",
                    tnauexpertTips = listOf(
                        "Nip terminal shoots at 80-90 days to stimulate sympodial (fruiting) branch growth",
                        "Install pheromone traps @ 5/acre for early detection of Pink Bollworm",
                        "Foliar spray with 1% DAP + 0.5% Potash during boll development reduces boll shedding"
                    ),
                    riskFactor = "Low (Assured Mill Demand)",
                    recommendedVarieties = "RCH 659 Bt, Suraj, MCU 5, Mallika",
                    fertilizerSchedule = "Basal: 10t FYM + 40kg P2O5 | Top Dressing: 80kg N + 40kg K2O in 3 split doses"
                )
            )
        }

        // 7. Groundnut / நிலக்கடலை (TMV 14 / Kadiri 6 / VRI 8)
        run {
            var soilScore = when {
                isRedLoam -> 35
                isSandy -> 34
                isLaterite -> 30
                isAlluvial -> 26
                else -> 18 // Avoid heavy waterlogging clay
            }
            var waterScore = when {
                isDrip || isBorewell -> 32
                isRainfed -> 28 // Excellent rainfed performer
                else -> 26
            }
            var seasonScore = when {
                isSummer || isKuruvai -> 29
                isSamba -> 22
                else -> 25
            }
            if (districtLower.contains("tiruvannamalai") || districtLower.contains("villupuram") || districtLower.contains("cuddalore") || districtLower.contains("namakkal")) {
                soilScore += 3
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(45, 94)
            val yield = if (isRainfed) 10.5 else 16.5
            val price = 7500 // Mandi market price per quintal
            val cost = if (isRainfed) 20000 else 25000
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Groundnut (TMV 14 / Kadiri 6 / VRI 8)",
                    tamilName = "மணிலா / நிலக்கடலை (TMV 14 / VRI 8)",
                    category = "Oilseeds & Soil Enricher",
                    matchScorePercent = match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 105,
                    waterNeed = "Moderate (Critical water at flowering and pegging stages)",
                    bestSowingWindow = "June - July (Rainfed) or December - February (Irrigated)",
                    suitableSoilSummary = "Light Red Sandy Loam or alluvial sand with porous texture for easy peg penetration",
                    climateSuitability = "Tropical warm climate with ample sunshine; light showers during early vegetative stage",
                    marketTrendReasoning = "High edible oil mill off-take and seed demand guarantee solid rates (₹7,500/Q). Fixes atmospheric nitrogen for free.",
                    tnauexpertTips = listOf(
                        "Apply Gypsum @ 200 kg/acre at 40-45 days (pegging stage) to ensure full pod kernel filling and prevent blind pods",
                        "Treat seeds with Rhizobium culture @ 600g/acre seed to maximize root nodulation",
                        "Sprinkle water during early morning hours to encourage vigorous peg entry"
                    ),
                    riskFactor = "Low (High Market Liquidity)",
                    recommendedVarieties = "TMV 14, VRI 8, Kadiri 6, Dharani",
                    fertilizerSchedule = "Basal: 10:20:30 kg NPK per acre + 200 kg Gypsum at 45th day"
                )
            )
        }

        // 8. Black Gram / உளுந்து (VBN 8 / ADT 6)
        run {
            var soilScore = when {
                isAlluvial || isRedLoam || isClay -> 34
                isBlackSoil -> 32
                else -> 26
            }
            var waterScore = when {
                isRainfed -> 34 // King of residual moisture / rainfed!
                else -> 32
            }
            var seasonScore = when {
                isSummer -> 30 // Famous rice-fallow pulse
                isSamba -> 25
                else -> 20
            }
            val match = (soilScore + waterScore + seasonScore).coerceIn(50, 93)
            val yield = 5.2
            val price = 8400 // Mandi pulse rate
            val cost = 11000 // Ultra-low investment
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Black Gram (VBN 8 / ADT 6)",
                    tamilName = "உளுந்து (VBN 8 / ADT 6)",
                    category = "Pulses & Legumes",
                    matchScorePercent = match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 68,
                    waterNeed = "Very Low (Survives on residual moisture in rice fallows)",
                    bestSowingWindow = "January - February (Rice Fallow) or June - July",
                    suitableSoilSummary = "Delta alluvial clay loam, red sandy loam, or moisture-retentive black soil",
                    climateSuitability = "Warm sunny days after monsoon with minimal rain during pod maturation",
                    marketTrendReasoning = "High protein pulse with minimal cost; leaves up to 35 kg soil nitrogen/acre for succeeding crop.",
                    tnauexpertTips = listOf(
                        "Broadcast seeds 4-6 days before paddy harvest into wet standing soil to catch optimum moisture",
                        "Foliar spray with TNAU Pulse Wonder @ 2 kg/acre at 50% flowering to boost pod setting by 20%",
                        "VBN 8 is resistant to Yellow Mosaic Virus (YMV)"
                    ),
                    riskFactor = "Very Low (Negligible Capex)",
                    recommendedVarieties = "VBN 8, VBN 6, ADT 6, MDU 1",
                    fertilizerSchedule = "Seed treatment with Rhizobium + Foliar 2% DAP or TNAU Pulse Wonder"
                )
            )
        }

        // 9. Barnyard Millet / குதிரைவாலி & Ragi (CO 15 / MDU 1) - Exceptional for Rainfed
        run {
            var soilScore = when {
                isSandy || isRedLoam || isBlackSoil || isLaterite -> 35
                else -> 28
            }
            var waterScore = when {
                isRainfed -> 35 // Number 1 recommendation when rainfed!
                else -> 28
            }
            var seasonScore = 28
            val match = (soilScore + waterScore + seasonScore).coerceIn(55, 96)
            val yield = 12.0
            val price = 4300 // Health food craze premium
            val cost = 11500
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Barnyard Millet / Ragi (CO 15 / MDU 1)",
                    tamilName = "குதிரைவாலி / கேழ்வரகு (CO 15 / MDU 1)",
                    category = "Nutri-Cereals / Millets",
                    matchScorePercent = if (isRainfed) 96 else match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 85,
                    waterNeed = "Extremely Low (Requires only 250-350mm total rainfall)",
                    bestSowingWindow = "July - September or January - March",
                    suitableSoilSummary = "Marginal dryland soils, red sandy loam, gravelly laterite, or shallow black soils",
                    climateSuitability = "Tolerates high temperatures, heat waves, and moisture deficit without wilting",
                    marketTrendReasoning = "Surging urban demand for diabetic-friendly superfoods and millets has doubled market prices to ₹4,300/Q.",
                    tnauexpertTips = listOf(
                        "Treat seeds with Azospirillum @ 2 packets per acre for drought resilience",
                        "Requires minimal or zero chemical sprays; ideal for natural organic farming certifications",
                        "Intercrop with red gram in 4:1 ratio for risk insurance"
                    ),
                    riskFactor = "Very Low (Climate Resilient)",
                    recommendedVarieties = "Kuthiraivali MDU 1, Ragi CO 15, Samai CO 4",
                    fertilizerSchedule = "Basal: 5t FYM + 20kg Urea + 20kg DAP"
                )
            )
        }

        // 10. Sesame / Gingelly / நல்லெண்ணெய் எள் (TMV 7 / VRI 3)
        if (isSummer || isKuruvai) {
            val match = when {
                isSandy || isRedLoam || isAlluvial -> 91
                else -> 82
            }
            val yield = 4.8
            val price = 12500 // High value gingelly oil extraction demand
            val cost = 13500
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Sesame / Gingelly (TMV 7 / VRI 3)",
                    tamilName = "எள் (TMV 7 / VRI 3 / பழுப்பு எள்)",
                    category = "High-Value Oilseed",
                    matchScorePercent = match,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 80,
                    waterNeed = "Low (Thrives in hot summer sunshine)",
                    bestSowingWindow = "February - March (Summer) or June - July",
                    suitableSoilSummary = "Well-drained Sandy Loam or Alluvial Sandy soil",
                    climateSuitability = "Hot sunny weather during flowering and pod development",
                    marketTrendReasoning = "Cold-pressed virgin gingelly oil fetches huge consumer premiums (₹12,500/Q for raw seed).",
                    tnauexpertTips = listOf(
                        "Mix seeds with 4 parts dry sand for even broadcasting",
                        "Thin seedlings at 15-20 days leaving 30x30cm spacing for prolific capsule branching",
                        "Harvest when 75% leaves turn yellow and lower capsules begin to crack"
                    ),
                    riskFactor = "Low",
                    recommendedVarieties = "TMV 7 (Brown seed, 52% oil), VRI 3, SVPR 1",
                    fertilizerSchedule = "Basal: 5t FYM + 15:10:10 kg NPK per acre"
                )
            )
        }

        // 11. Tomato / Chilli (PKM 1 / K1)
        if (!isRainfed) {
            val isTomatoMatch = (if (isDrip) 94 else 88)
            val yield = if (isDrip) 160.0 else 135.0
            val price = 2200 // Mandi price per quintal
            val cost = 62000
            val gross = (yield * price).toInt()
            val net = gross - cost
            candidates.add(
                RecommendedCrop(
                    cropName = "Hybrid Tomato (PKM 1 / Shivam F1)",
                    tamilName = "நாட்டு / வீரிய தக்காளி (PKM 1 / சிவம்)",
                    category = "Vegetables & Commercial",
                    matchScorePercent = isTomatoMatch,
                    estimatedYieldQuintalPerAcre = yield,
                    costOfCultivationPerAcre = cost,
                    marketPricePerQuintal = price,
                    estimatedGrossRevenue = gross,
                    estimatedNetProfitPerAcre = net,
                    durationDays = 120,
                    waterNeed = "Moderate with Drip",
                    bestSowingWindow = "September - November or May - July",
                    suitableSoilSummary = "Rich Red Loam with pH 6.0 - 7.0 and high compost humus",
                    climateSuitability = "Mild warm season with low humidity to avoid fungal blight",
                    marketTrendReasoning = "High productivity of 160 Quintals/Acre delivers rapid gross revenue with regular weekly harvests.",
                    tnauexpertTips = listOf(
                        "Use silver-black plastic mulch to reduce weed growth and conserve 40% soil moisture",
                        "Trellis plants with GI wire and twine to keep fruits off soil and avoid fruit borer damage",
                        "Apply calcium nitrate foliar spray @ 5g/litre to prevent Blossom End Rot"
                    ),
                    riskFactor = "Medium (Perishable produce)",
                    recommendedVarieties = "PKM 1, Shivam F1, Abhinav F1",
                    fertilizerSchedule = "Drip fertigation: 150:100:150 kg NPK split weekly"
                )
            )
        }

        // Sort candidates based on Net Profit per acre
        val sortedCandidates = candidates.sortedByDescending { it.estimatedNetProfitPerAcre }
        val topCrop = sortedCandidates.firstOrNull()

        val topNet = topCrop?.estimatedNetProfitPerAcre ?: 0
        val totalFarmProfit = (topNet * acres).toInt()
        val totalFarmProfitLakhs = String.format("%.2f", totalFarmProfit / 100000.0)

        val adviceTamil = buildString {
            append("உங்கள் ${request.district} நிலத்தில் உள்ள ${request.soilType} மற்றும் ${request.waterSource} வசதியை ஆய்வு செய்ததில், ")
            if (topCrop != null) {
                append("${topCrop.tamilName} சாகுபடி செய்வது மிகவும் உகந்தது என AI கணிக்கிறது. ")
                append("ஏக்கருக்கு சுமார் ₹${topCrop.estimatedNetProfitPerAcre} நிகர லாபம் ஈட்ட முடியும். ")
                append("உங்கள் மொத்த ${request.landSizeAcres} ஏக்கர் நிலத்திற்கு எதிர்பார்க்கப்படும் மொத்த பண்ணை லாபம் ₹$totalFarmProfitLakhs லட்சம் ஆகும்.")
            } else {
                append("வழக்கமான நெல் மற்றும் சிறுதானியங்களை சுழற்சி முறையில் பயிரிட பரிந்துரைக்கப்படுகிறது.")
            }
        }

        val adviceEnglish = buildString {
            append("Agronomic AI analysis for ${request.district} with ${request.soilType} and ${request.waterSource} indicates ")
            if (topCrop != null) {
                append("${topCrop.cropName} is the most profitable choice with an estimated net return of ₹${topCrop.estimatedNetProfitPerAcre}/acre. ")
                append("Projected total net profit for your ${request.landSizeAcres} acres farm is approximately ₹$totalFarmProfitLakhs Lakhs.")
            } else {
                append("Crop rotation with pulses and millets is recommended.")
            }
        }

        val climateZone = when {
            districtLower.contains("thanjavur") || districtLower.contains("tiruvarur") || districtLower.contains("nagapattinam") -> "Cauvery Delta Agro-Climatic Zone (Avg Rainfall 1,020mm)"
            districtLower.contains("coimbatore") || districtLower.contains("tiruppur") || districtLower.contains("erode") -> "Western Agro-Climatic Zone (Avg Rainfall 680mm)"
            districtLower.contains("madurai") || districtLower.contains("virudhunagar") || districtLower.contains("thoothukudi") -> "Southern Dry Zone (Avg Rainfall 740mm)"
            districtLower.contains("cuddalore") || districtLower.contains("villupuram") || districtLower.contains("kanchipuram") -> "North Eastern Coastal Zone (Avg Rainfall 1,150mm)"
            else -> "Tamil Nadu Semi-Arid Agricultural Zone"
        }

        return RecommendationResult(
            request = request,
            topCrops = sortedCandidates,
            climateSummary = "$climateZone | Season: ${request.season} | Farm Size: ${request.landSizeAcres} Acres",
            generalAdviceTamil = adviceTamil,
            generalAdviceEnglish = adviceEnglish,
            topProfitableCrop = topCrop,
            totalEstimatedFarmProfit = totalFarmProfit
        )
    }
}
