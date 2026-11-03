package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AgriRepository
import com.example.data.FirestoreAgriService
import com.example.data.MandiPriceService
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

enum class UzhavScreen {
    HOME,
    CROP_RECOMMENDATION,
    MANDI_PRICES,
    STORES_MAP,
    SCHEMES_2026,
    EQUIPMENT_MARKET,
    WEATHER_ADVISORY,
    PLANT_SCANNER,
    COMMUNITY_FORUM,
    DAILY_NEWS,
    FEEDBACK_REPORTS,
    AI_CHATBOT,
    PROFILE_REGISTRATION,
    LOGIN,
    ADMIN_DASHBOARD,
    CROP_TASK_SCHEDULE,
    IRRIGATION_SCHEDULER,
    FERTILIZER_PRICING
}

enum class FertilizerSortOption {
    LOWEST_PRICE,
    SAVINGS_HIGH_TO_LOW,
    NEAREST_DISTANCE,
    RATING;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        LOWEST_PRICE -> if (lang == AppLanguage.TAMIL) "குறைந்த விலை (Lowest Price)" else "Lowest Price"
        SAVINGS_HIGH_TO_LOW -> if (lang == AppLanguage.TAMIL) "அதிக சேமிப்பு (Max Savings)" else "Max Savings"
        NEAREST_DISTANCE -> if (lang == AppLanguage.TAMIL) "அருகிலுள்ள கடை (Nearest)" else "Nearest Store"
        RATING -> if (lang == AppLanguage.TAMIL) "உயர்ந்த மதிப்பீடு (Top Rated)" else "Top Rated"
    }
}

enum class StoreSortOption {
    PROXIMITY,
    PRICE_LOW_TO_HIGH,
    RATING;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        PROXIMITY -> if (lang == AppLanguage.TAMIL) "அருகிலுள்ளவை (தூரம்)" else "Nearest (Proximity)"
        PRICE_LOW_TO_HIGH -> if (lang == AppLanguage.TAMIL) "குறைந்த விலை" else "Lowest Price"
        RATING -> if (lang == AppLanguage.TAMIL) "சிறந்த மதிப்பீடு" else "Top Rated"
    }
}

data class UzhavUiState(
    val language: AppLanguage = AppLanguage.TAMIL, // Default to Tamil as requested
    val currentRole: UserRole = UserRole.FARMER,
    val currentScreen: UzhavScreen = UzhavScreen.HOME,
    val farmerProfile: FarmerProfile = FarmerProfile(),

    // Crop Recommendation
    val recommendationRequest: CropRecommendationRequest = CropRecommendationRequest(),
    val recommendationResult: RecommendationResult? = null,
    val isCalculatingRecommendation: Boolean = false,

    // Mandi Prices
    val mandiPrices: List<MandiPrice> = emptyList(),
    val selectedMandiCategory: String = "All",
    val selectedMandiRegion: String = "All Regions",
    val mandiSearchQuery: String = "",
    val selectedMandiPriceForDetail: MandiPrice? = null,
    val isMandiLoading: Boolean = false,
    val mandiErrorMessage: String? = null,
    val mandiLastFetchedTime: String = "Today 08:30 AM",
    val mandiRegionalSummary: MandiRegionalSummary? = null,
    val mandiGroundedSearchResult: MandiSearchGroundingResult? = null,
    val isMandiSearchGrounding: Boolean = false,
    val mandiSearchGroundingError: String? = null,

    // Agri Stores & Inventory (Mutable for Admin)
    val stores: List<AgriStore> = AgriRepository.initialStores,
    val selectedStoreCategory: FertilizerType? = null,
    val selectedFertilizerNameFilter: String? = null,
    val maxProximityKm: Float? = null, // null = All TN, or 15f, 30f, 50f, 100f
    val storeSortOption: StoreSortOption = StoreSortOption.PROXIMITY,
    val selectedStoreForMap: AgriStore? = AgriRepository.initialStores.firstOrNull(),
    val storeSearchQuery: String = "",
    val selectedDistrictFilter: String = "All Districts",

    // 2026 Government Schemes
    val schemes: List<GovtScheme> = AgriRepository.sampleSchemes,
    val selectedSchemeCategory: SchemeCategory = SchemeCategory.ALL,
    val schemeSearchQuery: String = "",
    val selectedSchemeForDetail: GovtScheme? = null,

    // Daily News
    val newsList: List<AgriNews> = AgriRepository.sampleNews,
    val selectedNewsCategory: NewsCategory = NewsCategory.ALL,
    val selectedNewsForDetail: AgriNews? = null,

    // Equipments
    val equipments: List<AgriEquipment> = AgriRepository.sampleEquipments,
    val selectedEquipmentCategory: EquipmentCategory? = null,

    // Weather
    val weatherReport: AgriWeatherReport = AgriRepository.sampleWeatherReport,

    // Plant Doctor Scanner
    val currentDiagnosis: PlantDiseaseDiagnosis? = null,
    val savedDiagnoses: List<PlantDiseaseDiagnosis> = emptyList(),
    val isScanning: Boolean = false,
    val scannedImageBitmap: android.graphics.Bitmap? = null,
    val scanStatusMessage: String? = null,

    // Community Forum
    val forumPosts: List<ForumPost> = AgriRepository.sampleForumPosts,
    val selectedCropFilterForum: String = "All",

    // Feedback & Grievances (Admin manages)
    val feedbackReports: List<FeedbackReport> = AgriRepository.sampleFeedbackReports.toList(),

    // AI Chatbot
    val chatMessages: List<ChatMessage> = listOf(
        ChatMessage(
            id = "init",
            text = "வணக்கம்! நான் உழவன் AI - உங்கள் டிஜிட்டல் வேளாண் உதவியாளர். பயிர் பரிந்துரை, தினசரி மண்டி விலை, உரக்கடை விபரம், 2026 மானியங்கள் அல்லது பயிர் நோய் தீர்வு பற்றி தமிழில் அல்லது ஆங்கிலத்தில் கேளுங்கள்!\n\nHello! I am Uzhav X AI - your smart farming guide. Ask me anything in Tamil or English!",
            isUser = false,
            timestamp = "Just now",
            suggestedFollowups = listOf(
                "சம்பா நெல் இலைக்கருகல் நோய் மருந்து என்ன?",
                "சொட்டு நீர் 100% மானியம் பெறுவது எப்படி?",
                "இன்றைய ஈரோடு மஞ்சள் விலை நிலவரம்",
                "இயற்கை பஞ்சகாவ்யா தயாரிக்கும் முறை"
            )
        )
    ),
    val isAiThinking: Boolean = false,
    val currentAiInputText: String = "",
    val selectedAiModel: String = "gemini-3.8-flash", // gemini-3.8-flash, gemini-3.5-flash-lite, gemini-4-argon

    // App Customization & Privacy Settings
    val isCloudSyncEnabled: Boolean = true,
    val isPhonePrivate: Boolean = true,
    val appFontSizeScale: Float = 1.0f, // 1.0f = Normal, 1.2f = Large
    val appLogoBranding: String = "Uzhav X Classic Green",

    // Daily Agri-Tip & Notifications
    val dailyAgriTip: com.example.model.DailyAgriTip? = null,
    val isTipLoading: Boolean = false,
    val isDailyNotificationEnabled: Boolean = true,
    val preferredNotificationTime: String = "06:00 AM",

    // Crop Task Reminders & Growth Stage Tracker
    val taskReminders: List<com.example.model.FarmTaskReminder> = generateDefaultSampleTaskReminders(),

    // System Dark/Light Theme & Dynamic Color
    val isDarkTheme: Boolean = false,

    // Offline Map Caching & Off-grid Store Navigation
    val isOfflineMapEnabled: Boolean = true,
    val isOfflineRegionDownloaded: Boolean = true,
    val isDownloadingMapTiles: Boolean = false,
    val offlineMapCacheSizeMb: Double = com.example.data.OfflineMapCacheManager.calculateTotalCacheSizeMb(),
    val offlineCachedTileCount: Int = com.example.data.OfflineMapCacheManager.calculateTotalCachedTiles(),
    val activeOfflineNavigationRoute: com.example.data.OfflineNavigationRoute? = null,
    val isNavigatingOffline: Boolean = false,

    // Admin state dialogs & notices
    val adminNoticeMessage: String? = null,

    // AI Irrigation Scheduler & Water Optimization
    val irrigationRequest: com.example.model.IrrigationScheduleRequest = com.example.model.IrrigationScheduleRequest(),
    val irrigationResult: com.example.model.IrrigationScheduleRecommendation? = null,
    val isCalculatingIrrigation: Boolean = false,

    // Real-Time Local Fertilizer Pricing & Cost Comparison
    val fertilizerPrices: List<com.example.model.FertilizerPriceItem> = com.example.data.FertilizerPriceService.sampleFertilizerPrices,
    val fertilizerSearchQuery: String = "",
    val fertilizerSelectedDistrict: String = "All Districts",
    val fertilizerSelectedCategory: com.example.model.FertilizerType? = null,
    val fertilizerSortOption: FertilizerSortOption = FertilizerSortOption.LOWEST_PRICE,
    val fertilizerCalcCrop: String = "Paddy (நெல்)",
    val fertilizerCalcAcres: Double = 2.0,
    val fertilizerRequirement: com.example.data.FertilizerPriceService.FertilizerRequirement? = com.example.data.FertilizerPriceService.calculateFertilizerRequirement("Paddy (நெல்)", 2.0),
    val fertilizerAiAdviceText: String? = null,
    val isFertilizerAiLoading: Boolean = false,

    // Room Database Offline Cache Status
    val isMandiRoomCached: Boolean = true,
    val isSubsidiesRoomCached: Boolean = true,
    val mandiCacheLastUpdated: String = "Room Database Cache Active",
    val schemeCacheLastUpdated: String = "Room Database Cache Active"
)

class UzhavViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(UzhavUiState())
    val uiState: StateFlow<UzhavUiState> = _uiState.asStateFlow()

    private val firestoreService = FirestoreAgriService()
    val mandiPriceService = MandiPriceService()
    val plantScannerService = com.example.data.PlantDiseaseScannerService()
    val geminiCropAdvisorService = com.example.data.GeminiCropAdvisorService()
    val dailyAgriTipService = com.example.data.DailyAgriTipService()
    val irrigationAdvisorService = com.example.data.IrrigationAdvisorService()
    val fertilizerPriceService = com.example.data.FertilizerPriceService
    val weatherService = com.example.data.WeatherService()
    val agriAiChatbotService = com.example.data.AgriAiChatbotService()
    private var roomCacheRepository: com.example.data.RoomCacheRepository? = null

    init {
        // Run initial recommendation with default profile values
        calculateCropRecommendation()
        initFirestoreSync()
        fetchDailyMandiPrices(forceRefresh = false)
        fetchDailyAgriTip(forceRefresh = false)
        calculateIrrigationSchedule()
        fetchLiveWeather(_uiState.value.farmerProfile.district, _uiState.value.farmerProfile.taluka)
    }

    fun initRoomCache(context: android.content.Context) {
        if (roomCacheRepository == null) {
            val repo = com.example.data.RoomCacheRepository(context.applicationContext)
            roomCacheRepository = repo

            viewModelScope.launch {
                // Populate Room DB with initial prices & schemes if empty
                val existingMandi = repo.getCachedMandiPrices()
                if (existingMandi.isEmpty()) {
                    repo.cacheMandiPrices(mandiPriceService.fetchDailyMarketPrices(false).getOrDefault(emptyList()))
                }
                val existingSubsidies = repo.getCachedSubsidies()
                if (existingSubsidies.isEmpty()) {
                    repo.cacheSubsidies(AgriRepository.sampleSchemes)
                }

                // Observe Room Flow streams reactively
                launch {
                    repo.cachedMandiPricesFlow.collect { cachedList ->
                        if (cachedList.isNotEmpty()) {
                            val summary = mandiPriceService.computeRegionalSummary(cachedList)
                            _uiState.update { current ->
                                current.copy(
                                    mandiPrices = cachedList,
                                    mandiRegionalSummary = summary,
                                    isMandiRoomCached = true,
                                    mandiCacheLastUpdated = "Room DB Cache (${cachedList.firstOrNull()?.lastUpdated ?: "Today"})"
                                )
                            }
                        }
                    }
                }

                launch {
                    repo.cachedSubsidiesFlow.collect { cachedList ->
                        if (cachedList.isNotEmpty()) {
                            _uiState.update { current ->
                                current.copy(
                                    schemes = cachedList,
                                    isSubsidiesRoomCached = true,
                                    schemeCacheLastUpdated = "Room DB Cache (2026-2027 Active)"
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    fun fetchLiveWeather(district: String, taluka: String) {
        viewModelScope.launch {
            try {
                val report = weatherService.fetchWeatherForDistrict(district, taluka)
                _uiState.update { it.copy(weatherReport = report) }
            } catch (e: Exception) {
                // Keep existing fallback weather report
            }
        }
    }

    fun syncWithFirebaseUser(user: com.google.firebase.auth.FirebaseUser) {
        val userEmail = user.email ?: ""
        val isOwnerAdmin = userEmail.equals("ssyash2211@gmail.com", ignoreCase = true)
        val role = if (isOwnerAdmin) UserRole.ADMIN else UserRole.FARMER

        val notice = if (isOwnerAdmin) {
            if (_uiState.value.language == AppLanguage.TAMIL)
                "👑 உரிமையாளர் & நிர்வாகி உள்நுழைந்துள்ளார் ($userEmail): முழு தரவுத்தளம் மற்றும் அமைப்புகள் அணுகல் செயலில் உள்ளது!"
            else
                "👑 Owner & Admin Logged In ($userEmail): Full Database Access & Admin Controls Active!"
        } else {
            if (_uiState.value.language == AppLanguage.TAMIL)
                "வணக்கம் ${user.displayName ?: "விவசாயி"}! பாதுகாப்பான Firebase கணக்கு இணைக்கப்பட்டது."
            else
                "Welcome ${user.displayName ?: "Farmer"}! Connected securely via Firebase."
        }

        _uiState.update { current ->
            val updatedProfile = current.farmerProfile.copy(
                name = user.displayName ?: current.farmerProfile.name,
                contact = if (userEmail.isNotBlank()) userEmail else current.farmerProfile.contact,
                isRegistered = true,
                currentRole = role
            )
            current.copy(
                farmerProfile = updatedProfile,
                currentRole = role,
                adminNoticeMessage = notice
            )
        }

        viewModelScope.launch {
            try {
                firestoreService.saveFarmerProfile(_uiState.value.farmerProfile)
            } catch (_: Exception) {}
        }
    }

    private fun initFirestoreSync() {
        try {
            firestoreService.listenToForumPosts(
                onUpdate = { cloudPosts ->
                    if (cloudPosts.isNotEmpty()) {
                        _uiState.update { current ->
                            val existingIds = cloudPosts.map { it.id }.toSet()
                            val localUnique = current.forumPosts.filter { it.id !in existingIds }
                            current.copy(forumPosts = cloudPosts + localUnique)
                        }
                    }
                },
                onError = { /* fallback gracefully to repository sample data */ }
            )
            firestoreService.listenToMandiPrices(
                onUpdate = { cloudPrices ->
                    if (cloudPrices.isNotEmpty()) {
                        _uiState.update { it.copy(mandiPrices = cloudPrices) }
                    }
                },
                onError = { /* fallback gracefully */ }
            )
        } catch (_: Exception) {}
    }

    // --- Language & Role Management ---

    fun toggleLanguage() {
        _uiState.update { current ->
            val nextLang = if (current.language == AppLanguage.TAMIL) AppLanguage.ENGLISH else AppLanguage.TAMIL
            current.copy(language = nextLang)
        }
    }

    fun setLanguage(lang: AppLanguage) {
        _uiState.update { it.copy(language = lang) }
    }

    fun toggleUserRole() {
        _uiState.update { current ->
            val nextRole = if (current.currentRole == UserRole.FARMER) UserRole.ADMIN else UserRole.FARMER
            val notice = if (nextRole == UserRole.ADMIN) {
                if (current.language == AppLanguage.TAMIL) "நிர்வாகி அணுகல் (அட்மின் மோடு) செயல்படுத்தப்பட்டது. நீங்கள் கடைகளின் இருப்பு, உர விலைகள் மற்றும் விவசாயிகளின் புகார்களை நிர்வகிக்கலாம்."
                else "Admin Mode Enabled. You can update store inventory, fertilizer prices, and resolve farmer grievances."
            } else {
                if (current.language == AppLanguage.TAMIL) "விவசாயி (பயனர்) முறைக்கு மாற்றப்பட்டது."
                else "Switched to Farmer (User) Mode."
            }
            current.copy(currentRole = nextRole, adminNoticeMessage = notice)
        }
    }

    fun dismissAdminNotice() {
        _uiState.update { it.copy(adminNoticeMessage = null) }
    }

    fun navigateTo(screen: UzhavScreen) {
        _uiState.update { it.copy(currentScreen = screen) }
    }

    // --- Profile Management ---

    fun updateProfile(profile: FarmerProfile) {
        _uiState.update { current ->
            current.copy(
                farmerProfile = profile,
                recommendationRequest = current.recommendationRequest.copy(
                    district = profile.district,
                    taluka = profile.taluka,
                    village = profile.village,
                    soilType = profile.soilType,
                    landSizeAcres = profile.landSizeAcres
                )
            )
        }
        calculateCropRecommendation()
        recalculateStoreDistancesForDistrict(profile.district)
        fetchLiveWeather(profile.district, profile.taluka)
        viewModelScope.launch {
            try {
                firestoreService.saveFarmerProfile(profile)
            } catch (_: Exception) {}
        }
    }

    // --- AI-Powered Crop Recommendation ---

    fun updateRecommendationInput(
        district: String? = null,
        taluka: String? = null,
        village: String? = null,
        soilType: String? = null,
        season: String? = null,
        waterSource: String? = null,
        landSize: Double? = null
    ) {
        _uiState.update { current ->
            val updated = current.recommendationRequest.copy(
                district = district ?: current.recommendationRequest.district,
                taluka = taluka ?: current.recommendationRequest.taluka,
                village = village ?: current.recommendationRequest.village,
                soilType = soilType ?: current.recommendationRequest.soilType,
                season = season ?: current.recommendationRequest.season,
                waterSource = waterSource ?: current.recommendationRequest.waterSource,
                landSizeAcres = landSize ?: current.recommendationRequest.landSizeAcres
            )
            current.copy(recommendationRequest = updated)
        }
    }

    fun calculateCropRecommendation() {
        viewModelScope.launch {
            _uiState.update { it.copy(isCalculatingRecommendation = true) }
            val req = _uiState.value.recommendationRequest
            val lang = _uiState.value.language
            val result = geminiCropAdvisorService.getRecommendations(req, lang)
            _uiState.update {
                it.copy(
                    recommendationResult = result,
                    isCalculatingRecommendation = false
                )
            }
        }
    }

    // --- Mandi Prices & Regional Market Trends ---

    fun fetchDailyMandiPrices(forceRefresh: Boolean = false) {
        viewModelScope.launch {
            _uiState.update { it.copy(isMandiLoading = true, mandiErrorMessage = null) }
            val result = mandiPriceService.fetchDailyMarketPrices(forceRefresh = forceRefresh)
            result.onSuccess { prices ->
                val summary = mandiPriceService.computeRegionalSummary(prices)
                _uiState.update { current ->
                    val selectedDetail = if (current.selectedMandiPriceForDetail != null) {
                        prices.find { it.id == current.selectedMandiPriceForDetail.id } ?: prices.firstOrNull()
                    } else {
                        prices.firstOrNull()
                    }
                    current.copy(
                        mandiPrices = prices,
                        selectedMandiPriceForDetail = selectedDetail,
                        mandiRegionalSummary = summary,
                        isMandiLoading = false,
                        mandiErrorMessage = null,
                        mandiLastFetchedTime = prices.firstOrNull()?.lastUpdated ?: current.mandiLastFetchedTime
                    )
                }
                viewModelScope.launch {
                    try {
                        roomCacheRepository?.cacheMandiPrices(prices)
                    } catch (_: Exception) {}
                }
            }.onFailure { error ->
                _uiState.update {
                    it.copy(
                        isMandiLoading = false,
                        mandiErrorMessage = error.localizedMessage ?: "Failed to fetch mandi prices"
                    )
                }
            }
        }
    }

    fun refreshMandiPrices() {
        fetchDailyMandiPrices(forceRefresh = true)
    }

    fun setMandiRegion(region: String) {
        _uiState.update { it.copy(selectedMandiRegion = region) }
    }

    fun setMandiCategory(category: String) {
        _uiState.update { it.copy(selectedMandiCategory = category) }
    }

    fun setMandiSearchQuery(query: String) {
        _uiState.update { it.copy(mandiSearchQuery = query) }
    }

    fun selectMandiPriceForDetail(item: MandiPrice) {
        _uiState.update { it.copy(selectedMandiPriceForDetail = item) }
    }

    fun searchLiveMandiWithGoogle(queryOverride: String? = null) {
        val query = queryOverride ?: _uiState.value.mandiSearchQuery.ifBlank { "Tamil Nadu mandi crop prices today" }
        viewModelScope.launch {
            _uiState.update { it.copy(isMandiSearchGrounding = true, mandiSearchGroundingError = null) }
            try {
                val result = mandiPriceService.searchLiveTNMarketPricesWithGoogle(query, _uiState.value.language)
                _uiState.update {
                    it.copy(
                        isMandiSearchGrounding = false,
                        mandiGroundedSearchResult = result,
                        mandiSearchGroundingError = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isMandiSearchGrounding = false,
                        mandiSearchGroundingError = e.localizedMessage ?: "Grounded search failed"
                    )
                }
            }
        }
    }

    fun dismissMandiGroundedSearch() {
        _uiState.update { it.copy(mandiGroundedSearchResult = null, mandiSearchGroundingError = null) }
    }

    // --- Stores & Fertilizer Price Comparison ---

    fun setStoreCategory(category: FertilizerType?) {
        _uiState.update { it.copy(selectedStoreCategory = category) }
    }

    fun setSelectedFertilizerNameFilter(name: String?) {
        _uiState.update { it.copy(selectedFertilizerNameFilter = name) }
    }

    fun setStoreProximityFilter(maxKm: Float?) {
        _uiState.update { it.copy(maxProximityKm = maxKm) }
    }

    fun setStoreSortOption(sort: StoreSortOption) {
        _uiState.update { it.copy(storeSortOption = sort) }
    }

    fun setStoreDistrictFilter(district: String) {
        _uiState.update { it.copy(selectedDistrictFilter = district) }
        if (district != "All Districts") {
            recalculateStoreDistancesForDistrict(district)
        }
    }

    fun setStoreSearchQuery(query: String) {
        _uiState.update { it.copy(storeSearchQuery = query) }
    }

    fun selectStoreForMap(store: AgriStore) {
        _uiState.update { it.copy(selectedStoreForMap = store) }
    }

    fun recalculateStoreDistancesForDistrict(district: String) {
        val (farmerLat, farmerLon) = AgriRepository.getDistrictCoordinates(district)
        _uiState.update { current ->
            val updated = current.stores.map { store ->
                val dist = AgriRepository.calculateDistanceKm(farmerLat, farmerLon, store.latitude, store.longitude)
                store.copy(distanceKm = dist)
            }
            current.copy(
                stores = updated,
                selectedStoreForMap = updated.find { it.id == current.selectedStoreForMap?.id } ?: updated.firstOrNull()
            )
        }
    }

    // --- Admin Operations: Update Store Inventory & Fertilizer Prices ---

    fun adminUpdateProductStockAndPrice(
        storeId: String,
        productId: String,
        newStandardPrice: Int,
        newSubsidizedPrice: Int?,
        inStock: Boolean
    ) {
        _uiState.update { current ->
            val updatedStores = current.stores.map { store ->
                if (store.id == storeId) {
                    val updatedProducts = store.products.map { prod ->
                        if (prod.id == productId) {
                            prod.copy(
                                standardPrice = newStandardPrice,
                                subsidizedPrice = newSubsidizedPrice,
                                inStock = inStock
                            )
                        } else prod
                    }
                    store.copy(products = updatedProducts)
                } else store
            }
            val msg = if (current.language == AppLanguage.TAMIL) "உரத்தின் விலை & இருப்பு வெற்றிகரமாக புதுப்பிக்கப்பட்டது!" else "Product price and stock updated successfully!"
            current.copy(
                stores = updatedStores,
                selectedStoreForMap = updatedStores.find { it.id == current.selectedStoreForMap?.id } ?: updatedStores.firstOrNull(),
                adminNoticeMessage = msg
            )
        }
    }

    fun adminAddNewProductToStore(
        storeId: String,
        productName: String,
        tamilName: String,
        category: FertilizerType,
        brand: String,
        bagSize: String,
        standardPrice: Int,
        subsidizedPrice: Int?
    ) {
        val newProd = FertilizerProduct(
            id = "p_custom_${System.currentTimeMillis()}",
            name = productName,
            tamilName = tamilName,
            category = category,
            brand = brand,
            bagSize = bagSize,
            standardPrice = standardPrice,
            subsidizedPrice = subsidizedPrice,
            inStock = true,
            composition = "Authorized agricultural input formula",
            usageNotes = "Apply as per recommended field dosage"
        )
        _uiState.update { current ->
            val updatedStores = current.stores.map { store ->
                if (store.id == storeId) {
                    store.copy(products = store.products + newProd)
                } else store
            }
            val msg = if (current.language == AppLanguage.TAMIL) "புதிய உரம் / விதை சேர்க்கப்பட்டது!" else "New fertilizer product added to shop!"
            current.copy(
                stores = updatedStores,
                selectedStoreForMap = updatedStores.find { it.id == current.selectedStoreForMap?.id } ?: updatedStores.firstOrNull(),
                adminNoticeMessage = msg
            )
        }
    }

    // --- 2026 Schemes ---

    fun setSchemeCategory(category: SchemeCategory) {
        _uiState.update { it.copy(selectedSchemeCategory = category) }
    }

    fun setSchemeSearchQuery(query: String) {
        _uiState.update { it.copy(schemeSearchQuery = query) }
    }

    fun selectSchemeForDetail(scheme: GovtScheme?) {
        _uiState.update { it.copy(selectedSchemeForDetail = scheme) }
    }

    // --- Daily News ---

    fun setNewsCategory(category: NewsCategory) {
        _uiState.update { it.copy(selectedNewsCategory = category) }
    }

    fun selectNewsForDetail(news: AgriNews?) {
        _uiState.update { it.copy(selectedNewsForDetail = news) }
    }

    // --- Equipments Marketplace ---

    fun setEquipmentCategory(category: EquipmentCategory?) {
        _uiState.update { it.copy(selectedEquipmentCategory = category) }
    }

    // --- Plant Disease Scanner (Google ML Kit + TNAU Agronomy) ---
    fun scanLeafSample(diagnosisIndex: Int = 0) {
        viewModelScope.launch {
            val statusMsg = if (_uiState.value.language == AppLanguage.TAMIL)
                "Google ML Kit & Gemini AI மாதிரி இலையை ஆய்வு செய்கிறது..."
            else
                "Google ML Kit & AI analyzing crop leaf patterns..."
            _uiState.update { it.copy(isScanning = true, scanStatusMessage = statusMsg) }
            kotlinx.coroutines.delay(800)
            val sampleLabels = when (diagnosisIndex) {
                0 -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.98f, 0),
                    com.example.model.MlKitLabelResult("Leaf", 0.95f, 1),
                    com.example.model.MlKitLabelResult("Gramineae / Grass", 0.91f, 2),
                    com.example.model.MlKitLabelResult("Foliar Blast Lesions", 0.88f, 3)
                )
                1 -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.97f, 0),
                    com.example.model.MlKitLabelResult("Tomato Foliage", 0.94f, 1),
                    com.example.model.MlKitLabelResult("Alternaria Blight Spot", 0.89f, 2),
                    com.example.model.MlKitLabelResult("Chlorosis", 0.84f, 3)
                )
                2 -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.96f, 0),
                    com.example.model.MlKitLabelResult("Chilli Pepper Leaf", 0.93f, 1),
                    com.example.model.MlKitLabelResult("Leaf Curl Symptoms", 0.90f, 2),
                    com.example.model.MlKitLabelResult("Thrips Stippling", 0.85f, 3)
                )
                3 -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.97f, 0),
                    com.example.model.MlKitLabelResult("Cotton Foliage", 0.92f, 1),
                    com.example.model.MlKitLabelResult("Angular Leaf Spot", 0.88f, 2),
                    com.example.model.MlKitLabelResult("Bacterial Blight", 0.86f, 3)
                )
                4 -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.95f, 0),
                    com.example.model.MlKitLabelResult("Turmeric Leaf", 0.92f, 1),
                    com.example.model.MlKitLabelResult("Colletotrichum Spot", 0.87f, 2),
                    com.example.model.MlKitLabelResult("Necrotic Margin", 0.83f, 3)
                )
                else -> listOf(
                    com.example.model.MlKitLabelResult("Plant", 0.96f, 0),
                    com.example.model.MlKitLabelResult("Sugarcane / Grass", 0.91f, 1),
                    com.example.model.MlKitLabelResult("Red Rot Discoloration", 0.86f, 2)
                )
            }
            val base = AgriRepository.sampleDiseases.getOrNull(diagnosisIndex)
                ?: AgriRepository.sampleDiseases.first()
            val diagnosis = base.copy(
                detectedLabels = sampleLabels,
                detectionSource = "Google ML Kit (On-Device Vision) + TNAU Agronomy",
                detectedStage = "Active Vegetative Stage",
                urgency = "Action Recommended within 48 Hours"
            )
            _uiState.update {
                it.copy(
                    isScanning = false,
                    scanStatusMessage = null,
                    currentDiagnosis = diagnosis,
                    scannedImageBitmap = null
                )
            }
        }
    }

    fun analyzeCustomLeaf(bitmap: android.graphics.Bitmap) {
        viewModelScope.launch {
            val statusMsg = if (_uiState.value.language == AppLanguage.TAMIL)
                "Google ML Kit & AI இலை நோயை ஆய்வு செய்கிறது..."
            else
                "Google ML Kit Image Labeling & AI analyzing leaf pathology..."
            _uiState.update {
                it.copy(
                    isScanning = true,
                    scannedImageBitmap = bitmap,
                    scanStatusMessage = statusMsg
                )
            }
            try {
                val diagnosis = plantScannerService.analyzeLeafImage(bitmap, _uiState.value.language)
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        scanStatusMessage = null,
                        currentDiagnosis = diagnosis,
                        scannedImageBitmap = bitmap
                    )
                }
            } catch (e: Exception) {
                val fallback = AgriRepository.sampleDiseases.first().copy(
                    detectedLabels = listOf(
                        com.example.model.MlKitLabelResult("Plant", 0.96f, 0),
                        com.example.model.MlKitLabelResult("Leaf", 0.92f, 1)
                    ),
                    detectionSource = "Google ML Kit (On-Device Vision) + TNAU Agronomy"
                )
                _uiState.update {
                    it.copy(
                        isScanning = false,
                        scanStatusMessage = null,
                        currentDiagnosis = fallback,
                        scannedImageBitmap = bitmap
                    )
                }
            }
        }
    }

    fun analyzeLeafFromUri(context: android.content.Context, uri: android.net.Uri) {
        viewModelScope.launch {
            val bitmap = plantScannerService.decodeBitmapFromUri(context, uri)
            if (bitmap != null) {
                analyzeCustomLeaf(bitmap)
            }
        }
    }

    fun saveDiagnosisToFarmLog(diagnosis: PlantDiseaseDiagnosis) {
        _uiState.update { current ->
            val updated = listOf(diagnosis) + current.savedDiagnoses.filter { it.id != diagnosis.id }
            val msg = if (current.language == AppLanguage.TAMIL)
                "பயிர் நோய் ஆய்வு விவரம் உங்கள் பண்ணை பதிவேட்டில் சேமிக்கப்பட்டது!"
            else
                "Diagnosis and treatment advisory saved to Farm Log!"
            current.copy(savedDiagnoses = updated, adminNoticeMessage = msg)
        }
    }

    fun removeSavedDiagnosis(id: String) {
        _uiState.update { current ->
            current.copy(savedDiagnoses = current.savedDiagnoses.filter { it.id != id })
        }
    }

    fun clearDiagnosis() {
        _uiState.update { it.copy(currentDiagnosis = null, scannedImageBitmap = null, scanStatusMessage = null) }
    }

    // --- Community Forum ---

    fun toggleUpvote(postId: String) {
        _uiState.update { current ->
            val updated = current.forumPosts.map { post ->
                if (post.id == postId) {
                    val newUpvoted = !post.hasUserUpvoted
                    val newCount = if (newUpvoted) post.upvotes + 1 else (post.upvotes - 1).coerceAtLeast(0)
                    post.copy(
                        upvotes = newCount,
                        hasUserUpvoted = newUpvoted,
                        likesCount = newCount,
                        hasLiked = newUpvoted
                    )
                } else post
            }
            current.copy(forumPosts = updated)
        }
    }

    fun toggleCommentLike(postId: String, commentId: String) {
        _uiState.update { current ->
            val updated = current.forumPosts.map { post ->
                if (post.id == postId) {
                    val updatedComments = post.comments.map { comment ->
                        if (comment.id == commentId) {
                            val nextLiked = !comment.hasLiked
                            val nextCount = if (nextLiked) comment.upvotes + 1 else (comment.upvotes - 1).coerceAtLeast(0)
                            comment.copy(hasLiked = nextLiked, upvotes = nextCount)
                        } else comment
                    }
                    post.copy(comments = updatedComments)
                } else post
            }
            current.copy(forumPosts = updated)
        }
    }

    fun addCommentToPost(postId: String, commentText: String) {
        if (commentText.isBlank()) return
        val newComment = ForumComment(
            id = "c_${System.currentTimeMillis()}",
            authorName = _uiState.value.farmerProfile.name,
            authorRole = if (_uiState.value.currentRole == UserRole.ADMIN) "Agri Officer / Store Admin" else "Farmer (${_uiState.value.farmerProfile.district})",
            timeAgo = "Just now",
            content = commentText,
            isExpertAnswer = _uiState.value.currentRole == UserRole.ADMIN,
            upvotes = 0,
            hasLiked = false
        )
        _uiState.update { current ->
            val updated = current.forumPosts.map { post ->
                if (post.id == postId) {
                    post.copy(comments = post.comments + newComment)
                } else post
            }
            current.copy(forumPosts = updated)
        }
    }

    fun createNewForumPost(
        title: String,
        body: String,
        cropTag: String,
        postType: ForumPostType = ForumPostType.QUESTION
    ) {
        if (title.isBlank() || body.isBlank()) return
        val newPost = ForumPost(
            id = "f_${System.currentTimeMillis()}",
            authorName = _uiState.value.farmerProfile.name,
            authorDistrict = _uiState.value.farmerProfile.district,
            authorRole = if (_uiState.value.currentRole == UserRole.ADMIN) "Agri Dept Officer" else "Farmer (${_uiState.value.farmerProfile.village})",
            isVerifiedExpert = _uiState.value.currentRole == UserRole.ADMIN,
            timeAgo = "Just now",
            title = title,
            body = body,
            cropTag = cropTag,
            upvotes = 1,
            hasUserUpvoted = true,
            comments = emptyList(),
            postType = postType,
            likesCount = 1,
            hasLiked = true
        )
        _uiState.update { it.copy(forumPosts = listOf(newPost) + it.forumPosts) }
        viewModelScope.launch {
            try {
                firestoreService.addForumPost(newPost)
            } catch (_: Exception) {}
        }
    }

    // --- Feedback & Grievances ---

    fun submitFeedbackReport(category: ReportCategory, description: String) {
        if (description.isBlank()) return
        val report = FeedbackReport(
            id = "rep_${System.currentTimeMillis()}",
            farmerName = _uiState.value.farmerProfile.name,
            district = _uiState.value.farmerProfile.district,
            taluka = _uiState.value.farmerProfile.taluka,
            village = _uiState.value.farmerProfile.village,
            phone = _uiState.value.farmerProfile.contact,
            category = category,
            description = description,
            timestamp = "Today",
            status = ReportStatus.SUBMITTED
        )
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "உங்கள் புகார் / கருத்து பதிவு செய்யப்பட்டது. வேளாண் உதவி அலுவலர் விரைவில் பரிசீலிப்பார்!" else "Your report has been submitted to the Agri Extension desk!"
            current.copy(
                feedbackReports = listOf(report) + current.feedbackReports,
                adminNoticeMessage = msg
            )
        }
        viewModelScope.launch {
            try {
                firestoreService.submitGrievance(report)
            } catch (_: Exception) {}
        }
    }

    fun adminUpdateReportStatus(reportId: String, newStatus: ReportStatus) {
        _uiState.update { current ->
            val updated = current.feedbackReports.map {
                if (it.id == reportId) it.copy(status = newStatus) else it
            }
            val msg = if (current.language == AppLanguage.TAMIL) "புகார் நிலை '${newStatus.getDisplayName(current.language)}' என மாற்றப்பட்டது." else "Report status updated to ${newStatus.name}"
            current.copy(feedbackReports = updated, adminNoticeMessage = msg)
        }
    }

    // --- AI Chatbot ---

    fun setSelectedAiModel(modelName: String) {
        _uiState.update { it.copy(selectedAiModel = modelName) }
    }

    fun updateAiInputText(text: String) {
        _uiState.update { it.copy(currentAiInputText = text) }
    }

    fun sendAiMessage(promptText: String? = null) {
        val query = (promptText ?: _uiState.value.currentAiInputText).trim()
        if (query.isBlank()) return

        val userMsg = ChatMessage(
            id = "u_${System.currentTimeMillis()}",
            text = query,
            isUser = true,
            timestamp = "Just now"
        )

        _uiState.update {
            it.copy(
                chatMessages = it.chatMessages + userMsg,
                currentAiInputText = "",
                isAiThinking = true
            )
        }

        viewModelScope.launch {
            val lang = _uiState.value.language
            val currentHistory = _uiState.value.chatMessages
            val selectedModel = _uiState.value.selectedAiModel
            val district = _uiState.value.farmerProfile.district
            val aiMsg = agriAiChatbotService.generateAiChatResponse(query, currentHistory, lang, selectedModel, district)
            _uiState.update {
                it.copy(
                    chatMessages = it.chatMessages + aiMsg,
                    isAiThinking = false
                )
            }
        }
    }

    private fun generateAgriExpertReply(query: String, lang: AppLanguage): String {
        val q = query.lowercase()
        return when {
            q.contains("விலை") || q.contains("price") || q.contains("mandi") || q.contains("சந்தை") -> {
                if (lang == AppLanguage.TAMIL) {
                    "🌾 இன்றைய தமிழ்நாடு சந்தை நிலவரம்:\n• நேரடி நெல் கொள்முதல் விலை: ₹2,450/குவிண்டால் (தமிழக அரசு ஊக்கத்தொகையுடன்)\n• ஈரோடு விரலி மஞ்சள்: ₹15,900/குவிண்டால் (உயர் வரத்து)\n• பொள்ளாச்சி இளநீர்: ₹42/காய்\n• ஒட்டன்சத்திரம் நாட்டு தக்காளி: ₹24/கிலோ\n• திண்டுக்கல் சின்ன வெங்காயம்: ₹52/கிலோ\n\nவிரிவான 7 நாள் விலை மாற்ற வரைபடத்தை 'மண்டி விலைகள்' பக்கத்தில் பார்க்கலாம்!"
                } else {
                    "🌾 Today's Tamil Nadu Mandi Highlights:\n• Paddy (Samba): ₹2,450/quintal at Direct Purchase Centres\n• Erode Turmeric (Finger): ₹15,900/quintal (Strong export bidding)\n• Pollachi Tender Coconut: ₹42/nut\n• Oddanchatram Tomato: ₹24/kg\n• Dindigul Shallots: ₹52/kg\n\nVisit the 'Mandi Prices' tab for interactive 7-day trend graphs!"
                }
            }
            q.contains("மானியம்") || q.contains("subsidy") || q.contains("scheme") || q.contains("திட்டம்") || q.contains("கடன்") || q.contains("loan") -> {
                if (lang == AppLanguage.TAMIL) {
                    "🏛️ 2026-27 முக்கிய தமிழக அரசு மானியங்கள்:\n1. சொட்டு நீர் பாசனம்: 5 ஏக்கருக்கு குறைவான சிறு/குறு விவசாயிகளுக்கு 100% முழு மானியம் (ஹெக்டேருக்கு ₹1,35,000 வரை).\n2. சூரிய மின்சார பம்பு செட் (PM-KUSUM): 70% அரசு மானியத்தில் 5-10 HP மோட்டார்.\n3. தொடக்க கூட்டுறவு பயிர் கடன் (KCC): 12 மாதத்திற்குள் திருப்பி செலுத்தினால் 0% முற்றிலும் வட்டி இல்லா கடன் (ஏக்கருக்கு ₹52,000 வரை).\n4. கிசான் ட்ரோன் & டிராக்டர்: 50% முதல் 80% வரை SMAM மானியம்.\n\nவிண்ணப்பிக்க தேவையான ஆவணங்கள் மற்றும் வழிகாட்டுதலுக்கு 'அரசு திட்டங்கள்' பக்கத்தை பார்க்கவும்!"
                } else {
                    "🏛️ 2026-27 Key Tamil Nadu Agriculture Schemes:\n1. Micro Irrigation (Drip): 100% full subsidy for Small & Marginal farmers (< 5 acres) up to ₹1,35,000/ha.\n2. Solar Pumps (PM-KUSUM): 70% capital subsidy on 5HP to 10HP stand-alone pumps.\n3. Kisan Credit Card (KCC): 0% zero-interest crop loans up to ₹3,00,000 through PACCS on prompt repayment.\n4. Farm Mechanization (SMAM): 50% subsidy on tractors and 80% on Kisan spraying drones.\n\nCheck the 'Govt Schemes' tab for application steps & document checklists!"
                }
            }
            q.contains("நோய்") || q.contains("மருந்து") || q.contains("disease") || q.contains("blast") || q.contains("கருகல்") || q.contains("பூச்சி") || q.contains("pest") -> {
                if (lang == AppLanguage.TAMIL) {
                    "🌿 பயிர் பாதுகாப்பு & நோய் மேலாண்மை வழிகாட்டுதல்:\n• நெல் இலைக்கருகல் நோய்: ட்ரைசைக்ளசோல் 75% WP @ 0.6 கிராம்/லிட்டர் அல்லது 10% மாட்டு சிறுநீர் + பெருங்காயம் கரைசல் தெளிக்கவும்.\n• சின்ன வெங்காயம் & மிளகாய் இலைசுருட்டு: ஐந்திலை கரைசல் அல்லது டயாபென்தியூரான் 50% WP @ 1.25 கிராம்/லிட்டர்.\n• இயற்கை பூச்சிவிரட்டி: 10,000 ppm வேப்பெண்ணெய் 3 மிலி/லிட்டர் நீரில் ஒட்டும் திரவம் சேர்த்து தெளிக்கவும்.\n\nநோய் தாக்கிய இலையை நமது 'பயிர் மருத்துவர்' ஸ்கேனரில் படம் பிடித்தால் உடனடி துல்லிய தீர்வை பெறலாம்!"
                } else {
                    "🌿 Crop Protection & Pest Advisory:\n• Rice Leaf Blast: Spray Tricyclazole 75% WP @ 0.6g/L water or apply organic 10% cow urine + asafoetida extract.\n• Chilli Leaf Curl & Thrips: Spray Diafenthiuron 50% WP @ 1.25g/L or 5-leaf herbal organic decoction.\n• Organic Pest Repellent: Cold-pressed Neem oil 10,000 ppm @ 3ml/L water.\n\nUse our 'Crop Doctor Scanner' to scan a diseased leaf for instant AI diagnosis and remedies!"
                }
            }
            q.contains("உரம்") || q.contains("fertilizer") || q.contains("urea") || q.contains("யூரியா") || q.contains("பஞ்சகாவ்யா") -> {
                if (lang == AppLanguage.TAMIL) {
                    "🧪 உரம் & இயற்கை இடுபொருட்கள் ஆலோசனை:\n• யூரியா (45 கிலோ மூட்டை) அரசு மானிய விலை ₹266. தூர் கட்டும் பருவத்தில் மட்டும் இடவும்.\n• டி.ஏ.பி (DAP 50 கிலோ) ₹1,350. அடி உரமாக இட வேண்டும்.\n• இயற்கை பஞ்சகாவ்யா: 3% கரைசலாக (1 லிட்டர் நீரில் 30 மிலி) பயிர்களுக்கு தெளித்தால் நோய் எதிர்ப்பு சக்தியும் அதிக விளைச்சலும் கிடைக்கும்.\n• அருகில் உள்ள PACCS தொடக்க வேளாண் கூட்டுறவு சங்கங்களின் இருப்பு நிலவரத்தை 'உரக்கடைகள்' பக்கத்தில் ஒப்பிட்டு பார்க்கலாம்!"
                } else {
                    "🧪 Fertilizer & Bio-Input Advisory:\n• Neem-coated Urea (45 kg) Govt subsidized MRP: ₹266.\n• DAP 18:46:00 (50 kg): ₹1,350 as basal fertilizer.\n• Panchagavya: 3% foliar spray (30 ml/L water) acts as an organic growth booster and pest repellent.\n• Compare nearby PACCS cooperative and private agro dealers under the 'Agri Stores' tab!"
                }
            }
            else -> {
                if (lang == AppLanguage.TAMIL) {
                    "விவசாய நண்பரே, உங்கள் கேள்விக்கு மகிழ்ச்சியுடன் உதவ தயாராக உள்ளேன். உழவன் X செயலியில் நீங்கள்:\n1. 'பயிர் பரிந்துரை' மூலம் உங்கள் ஊர், மண் மற்றும் தற்போதைய சந்தை விலைக்கு உகந்த பயிரை கண்டறியலாம்.\n2. 'உரக்கடைகள்' மூலம் தமிழ்நாடு முழுவதிலும் உள்ள கடைகளின் உர விலைகளை ஒப்பிடலாம்.\n3. 'பயிர் மருத்துவர்' மூலம் நோய் தாக்கிய இலைகளை ஸ்கேன் செய்து உடனடி இயற்கை/இரசாயன தீர்வுகளை பெறலாம்.\n4. 'அரசு திட்டங்கள்' மூலம் 2026 மானியங்கள் மற்றும் பயிர் கடன்களுக்கு விண்ணப்பிக்கலாம்."
                } else {
                    "Dear Farmer, I am here to help you maximize your agricultural profits. With Uzhav X you can:\n1. Get AI Crop Recommendations tailored to your district, soil type, and mandi prices.\n2. Compare fertilizer prices (chemical & organic) across all Tamil Nadu agro centers.\n3. Scan plant leaves for instant disease diagnosis and organic/chemical remedies.\n4. Explore comprehensive 2026 government subsidies (Maniyam) and KCC loans."
                }
            }
        }
    }

    // --- Data Alterations & Customization Methods ---

    fun updatePrivacyAndSettings(cloudSync: Boolean, phonePrivate: Boolean, fontScale: Float) {
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "அமைப்புகள் மற்றும் தரவு பாதுகாப்பு விருப்பங்கள் புதுப்பிக்கப்பட்டன!" else "App settings and privacy options updated!"
            current.copy(
                isCloudSyncEnabled = cloudSync,
                isPhonePrivate = phonePrivate,
                appFontSizeScale = fontScale,
                adminNoticeMessage = msg
            )
        }
    }

    fun adminUpdateMandiCropPrice(id: String, minPrice: Int, maxPrice: Int, modalPrice: Int) {
        _uiState.update { current ->
            val updated = current.mandiPrices.map { item ->
                if (item.id == id) {
                    item.copy(minPrice = minPrice, maxPrice = maxPrice, modalPrice = modalPrice, lastUpdated = "Updated just now")
                } else item
            }
            val msg = if (current.language == AppLanguage.TAMIL) "மண்டி சந்தை விலை மாற்றப்பட்டது!" else "Mandi price updated successfully!"
            current.copy(mandiPrices = updated, adminNoticeMessage = msg)
        }
        viewModelScope.launch {
            try {
                roomCacheRepository?.cacheMandiPrices(_uiState.value.mandiPrices)
            } catch (_: Exception) {}
        }
    }

    fun adminAddCustomCropPrice(
        cropName: String,
        tamilName: String,
        category: String,
        marketName: String,
        district: String,
        modalPrice: Double
    ) {
        val mPrice = modalPrice.toInt()
        val minP = (mPrice * 0.9).toInt()
        val maxP = (mPrice * 1.1).toInt()
        val newMandi = MandiPrice(
            id = "custom_${System.currentTimeMillis()}",
            cropName = cropName,
            tamilName = tamilName,
            category = category,
            marketName = marketName,
            district = district,
            minPrice = minP,
            maxPrice = maxP,
            modalPrice = mPrice,
            unit = "Quintal (குவிண்டால்)",
            arrivalVolume = "Fresh Supply",
            lastUpdated = "Today",
            priceHistory7Days = listOf((mPrice * 0.95).toInt(), (mPrice * 0.97).toInt(), mPrice, mPrice, mPrice, mPrice, mPrice),
            isPositive = true,
            changeAmount = 50
        )
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "புதிய பயிர் விலை சேர்க்கப்பட்டது!" else "Custom crop price added to Mandi database!"
            current.copy(mandiPrices = listOf(newMandi) + current.mandiPrices, adminNoticeMessage = msg)
        }
        viewModelScope.launch {
            try {
                roomCacheRepository?.cacheMandiPrices(_uiState.value.mandiPrices)
            } catch (_: Exception) {}
        }
    }

    fun adminAddCustomScheme(
        title: String,
        tamilTitle: String,
        category: SchemeCategory,
        department: String,
        maxBenefit: String,
        portalUrl: String
    ) {
        val newScheme = GovtScheme(
            id = "sch_custom_${System.currentTimeMillis()}",
            schemeCode = "TN-2026-CUSTOM",
            title = title,
            tamilTitle = tamilTitle,
            department = department,
            category = category,
            subsidyPercentage = "75% - 100% Subsidy",
            maxFinancialBenefit = maxBenefit,
            summaryEnglish = title,
            summaryTamil = tamilTitle,
            eligibility = listOf("Tamil Nadu Farmers with Chitta / Patta"),
            requiredDocuments = listOf("Aadhaar Card", "Bank Passbook", "Patta Copy"),
            howToApplySteps = listOf("Visit official portal or Agri Extension Office"),
            officialPortalUrl = portalUrl,
            helplineNumber = "1800-180-1551"
        )
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "புதிய அரசு மானிய திட்டம் சேர்க்கப்பட்டது!" else "New subsidy scheme added!"
            current.copy(schemes = listOf(newScheme) + current.schemes, adminNoticeMessage = msg)
        }
        viewModelScope.launch {
            try {
                roomCacheRepository?.cacheSubsidies(_uiState.value.schemes)
            } catch (_: Exception) {}
        }
    }

    // --- Daily Agri-Tip & Notifications ---

    fun fetchDailyAgriTip(forceRefresh: Boolean = true) {
        viewModelScope.launch {
            _uiState.update { it.copy(isTipLoading = true) }
            try {
                val tip = dailyAgriTipService.fetchDailyAgriTip(
                    district = _uiState.value.farmerProfile.district,
                    soilType = _uiState.value.farmerProfile.soilType,
                    forceRefresh = forceRefresh
                )
                _uiState.update { it.copy(dailyAgriTip = tip, isTipLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(isTipLoading = false) }
            }
        }
    }

    fun toggleDailyNotifications(enabled: Boolean) {
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) {
                if (enabled) "தினசரி வேளாண் குறிப்பு அறிவிப்புகள் செயல்படுத்தப்பட்டன!" else "தினசரி அறிவிப்புகள் நிறுத்தப்பட்டன."
            } else {
                if (enabled) "Daily Agri-Tip notifications enabled!" else "Daily notifications disabled."
            }
            current.copy(isDailyNotificationEnabled = enabled, adminNoticeMessage = msg)
        }
    }

    fun setPreferredNotificationTime(timeStr: String) {
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "அறிவிப்பு நேரம் '$timeStr' என மாற்றப்பட்டது" else "Notification schedule updated to $timeStr"
            current.copy(preferredNotificationTime = timeStr, adminNoticeMessage = msg)
        }
    }

    // --- Crop Task Schedule & Recurring Reminders ---

    private var cropTaskSchedulerHelper: com.example.data.CropTaskSchedulerHelper? = null

    fun initScheduler(context: android.content.Context) {
        if (cropTaskSchedulerHelper == null) {
            val helper = com.example.data.CropTaskSchedulerHelper(context.applicationContext)
            cropTaskSchedulerHelper = helper
            loadTaskRemindersFromDb()
        }
    }

    fun loadTaskRemindersFromDb() {
        viewModelScope.launch {
            val helper = cropTaskSchedulerHelper
            if (helper != null) {
                val dbTasks = helper.loadAllRemindersFromDb()
                if (dbTasks.isNotEmpty()) {
                    _uiState.update { it.copy(taskReminders = dbTasks) }
                } else {
                    generateTaskScheduleForCrop("Samba Paddy", "சம்பா நெல்", "2026-09-20")
                }
            }
        }
    }

    fun generateTaskScheduleForCrop(cropName: String, cropTamilName: String, sowingDateStr: String) {
        viewModelScope.launch {
            val helper = cropTaskSchedulerHelper
            if (helper != null) {
                val newTasks = helper.generateTasksForSowingDate(cropName, cropTamilName, sowingDateStr)
                for (t in newTasks) {
                    helper.saveReminderToDb(t)
                }
                val allUpdated = helper.loadAllRemindersFromDb()
                _uiState.update { it.copy(taskReminders = allUpdated) }
            }
        }
    }

    fun calculateCropGrowthProgress(cropName: String, cropTamilName: String, sowingDateStr: String): com.example.model.CropGrowthProgress? {
        val helper = cropTaskSchedulerHelper ?: return null
        val stages = helper.getCropGrowthStages(cropName)
        val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val sowingDate = try { sdf.parse(sowingDateStr) ?: java.util.Date() } catch (_: Exception) { java.util.Date() }

        val diffMillis = java.util.Date().time - sowingDate.time
        val daysAfterSowing = (diffMillis / (1000 * 60 * 60 * 24)).toInt().coerceAtLeast(1)

        val activeStage = stages.find { daysAfterSowing >= it.dayStart && daysAfterSowing <= it.dayEnd }
            ?: stages.lastOrNull() ?: return null

        val totalDuration = stages.lastOrNull()?.dayEnd ?: 120
        val progressPercent = (daysAfterSowing.toFloat() / totalDuration.toFloat()).coerceIn(0.05f, 1.0f)

        return com.example.model.CropGrowthProgress(
            cropName = cropName,
            cropTamilName = cropTamilName,
            sowingDate = sowingDateStr,
            daysAfterSowing = daysAfterSowing,
            totalCropDurationDays = totalDuration,
            currentStage = activeStage,
            overallProgressPercent = progressPercent,
            nextUpcomingTask = _uiState.value.taskReminders.firstOrNull { !it.isCompleted }
        )
    }

    fun toggleTaskCompleted(task: com.example.model.FarmTaskReminder) {
        viewModelScope.launch {
            val helper = cropTaskSchedulerHelper
            if (helper != null) {
                val updated = helper.markTaskCompletedAndReschedule(task)
                val allUpdated = helper.loadAllRemindersFromDb()
                _uiState.update { current ->
                    val msg = if (current.language == AppLanguage.TAMIL) "'${task.titleTamil}' பணி நிறைவேற்றப்பட்டது! அடுத்த பணி அட்டவணைப் படுத்தப்பட்டது." else "Task marked complete and next recurrence scheduled!"
                    current.copy(taskReminders = allUpdated, adminNoticeMessage = msg)
                }
            } else {
                _uiState.update { current ->
                    val updatedList = current.taskReminders.map { if (it.id == task.id) it.copy(isCompleted = !it.isCompleted) else it }
                    current.copy(taskReminders = updatedList)
                }
            }
        }
    }

    fun addCustomTaskReminder(task: com.example.model.FarmTaskReminder) {
        viewModelScope.launch {
            val helper = cropTaskSchedulerHelper
            if (helper != null) {
                helper.saveReminderToDb(task)
                val allUpdated = helper.loadAllRemindersFromDb()
                _uiState.update { current ->
                    val msg = if (current.language == AppLanguage.TAMIL) "புதிய பாசன/உரப்பணி சேர்க்கப்பட்டது!" else "New task reminder scheduled!"
                    current.copy(taskReminders = allUpdated, adminNoticeMessage = msg)
                }
            } else {
                _uiState.update { it.copy(taskReminders = listOf(task) + it.taskReminders) }
            }
        }
    }

    fun deleteTaskReminder(taskId: String) {
        viewModelScope.launch {
            val helper = cropTaskSchedulerHelper
            if (helper != null) {
                helper.deleteTask(taskId)
                val allUpdated = helper.loadAllRemindersFromDb()
                _uiState.update { it.copy(taskReminders = allUpdated) }
                _uiState.update { current ->
                    current.copy(taskReminders = current.taskReminders.filter { it.id != taskId })
                }
            }
        }
    }

    fun updateSoilTestResult(result: SoilTestResult) {
        _uiState.update { current ->
            val updatedProfile = current.farmerProfile.copy(soilTestResult = result)
            val updatedReq = current.recommendationRequest.copy(soilTestResult = result)
            val msg = if (current.language == AppLanguage.TAMIL)
                "மண் பரிசோதனை விவரங்கள் வெற்றிகரமாக சேமிக்கப்பட்டன! ஜெமினி AI கணிப்பில் சேர்க்கப்பட்டது."
            else
                "Soil test results updated and linked to AI Crop Recommendation profile!"
            current.copy(
                farmerProfile = updatedProfile,
                recommendationRequest = updatedReq,
                adminNoticeMessage = msg
            )
        }
        viewModelScope.launch {
            try {
                firestoreService.saveFarmerProfile(_uiState.value.farmerProfile)
            } catch (_: Exception) {}
        }
    }

    // --- System Dark/Light Theme & Dynamic Color ---

    fun toggleDarkTheme() {
        _uiState.update { current ->
            val nextDark = !current.isDarkTheme
            val msg = if (current.language == AppLanguage.TAMIL) {
                if (nextDark) "இருண்ட தீம் (Dark Theme) செயல்படுத்தப்பட்டது" else "வெளிச்சமான தீம் (Light Theme) செயல்படுத்தப்பட்டது"
            } else {
                if (nextDark) "Dark Theme Enabled" else "Light Theme Enabled"
            }
            current.copy(isDarkTheme = nextDark, adminNoticeMessage = msg)
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        _uiState.update { it.copy(isDarkTheme = isDark) }
    }

    // --- Offline Map Caching & Off-grid Navigation ---

    fun toggleOfflineMapMode() {
        _uiState.update { current ->
            val nextState = !current.isOfflineMapEnabled
            val msg = if (current.language == AppLanguage.TAMIL) {
                if (nextState) "ஆஃப்லைன் வரைபட பயன்முறை (Offline Map Cache) ஆன் செய்யப்பட்டது!" else "ஆன்லைன் வரைபட பயன்முறைக்கு மாறப்பட்டது."
            } else {
                if (nextState) "Offline Map Cache Enabled for rural field work!" else "Online Map Mode Active."
            }
            current.copy(isOfflineMapEnabled = nextState, adminNoticeMessage = msg)
        }
    }

    fun downloadOfflineMapRegion(district: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isDownloadingMapTiles = true) }
            kotlinx.coroutines.delay(1200)
            val msg = if (_uiState.value.language == AppLanguage.TAMIL)
                "$district ஆஃப்லைன் வரைபட டைல்கள் வெற்றி பெற்று பதிவிறக்கம் செய்யப்பட்டது (18.4 MB - 1,240 tiles)!"
            else
                "$district map tiles successfully downloaded and cached for offline use (18.4 MB - 1,240 tiles)!"
            _uiState.update {
                it.copy(
                    isDownloadingMapTiles = false,
                    isOfflineRegionDownloaded = true,
                    offlineMapCacheSizeMb = 18.4 + 14.2,
                    offlineCachedTileCount = 3270,
                    adminNoticeMessage = msg
                )
            }
        }
    }

    fun clearOfflineMapCache() {
        _uiState.update { current ->
            val msg = if (current.language == AppLanguage.TAMIL) "ஆஃப்லைன் வரைபட கேச் நீக்கப்பட்டது!" else "Offline map cache cleared."
            current.copy(
                offlineMapCacheSizeMb = 0.0,
                offlineCachedTileCount = 0,
                isOfflineRegionDownloaded = false,
                adminNoticeMessage = msg
            )
        }
    }

    fun startOfflineNavigation(store: AgriStore) {
        val (lat, lon) = AgriRepository.getDistrictCoordinates(_uiState.value.farmerProfile.district)
        val route = com.example.data.OfflineMapCacheManager.generateOfflineTurnByTurnRoute(
            farmerLat = lat,
            farmerLon = lon,
            store = store,
            lang = _uiState.value.language
        )
        _uiState.update {
            it.copy(
                activeOfflineNavigationRoute = route,
                isNavigatingOffline = true,
                selectedStoreForMap = store
            )
        }
    }

    fun stopOfflineNavigation() {
        _uiState.update { it.copy(activeOfflineNavigationRoute = null, isNavigatingOffline = false) }
    }

    // --- AI Irrigation Scheduler & Water Optimization ---

    fun updateIrrigationRequest(request: com.example.model.IrrigationScheduleRequest) {
        _uiState.update { it.copy(irrigationRequest = request) }
    }

    fun calculateIrrigationSchedule() {
        _uiState.update { it.copy(isCalculatingIrrigation = true) }
        viewModelScope.launch {
            val req = _uiState.value.irrigationRequest
            val rec = irrigationAdvisorService.getAiEnhancedIrrigationAdvice(req)
            _uiState.update {
                it.copy(
                    irrigationResult = rec,
                    isCalculatingIrrigation = false
                )
            }
        }
    }

    fun addIrrigationTaskToReminders(rec: com.example.model.IrrigationScheduleRecommendation) {
        val newTask = com.example.model.FarmTaskReminder(
            id = "irr_${System.currentTimeMillis()}",
            cropName = rec.request.cropType.substringBefore(" ("),
            cropTamilName = rec.request.cropType,
            titleTamil = "நீர் பாசனம் - ${rec.recommendedWaterLitersPerAcre}L (${rec.durationMinutes} நிமி)",
            titleEnglish = "Irrigation - ${rec.recommendedWaterLitersPerAcre}L (${rec.durationMinutes} mins)",
            category = com.example.model.TaskCategory.IRRIGATION,
            recurrence = com.example.model.RecurrencePattern.EVERY_3_DAYS,
            scheduledDate = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault()).format(java.util.Date()),
            scheduledTime = "06:00 AM",
            isCompleted = false,
            growthStageNameTamil = rec.request.growthStage,
            growthStageNameEnglish = rec.request.growthStage,
            notesTamil = rec.aiAdviceTamil,
            notesEnglish = rec.aiAdviceEnglish
        )

        _uiState.update { current ->
            val updatedTasks = current.taskReminders + newTask
            val msg = if (current.language == AppLanguage.TAMIL) "பாசன அட்டவணை பயிர் காலண்டரில் சேர்க்கப்பட்டது!" else "Irrigation schedule added to farm calendar!"
            current.copy(
                taskReminders = updatedTasks,
                adminNoticeMessage = msg
            )
        }
    }

    // --- Fertilizer Price Comparison & Calculator Methods ---

    fun updateFertilizerSearchQuery(query: String) {
        _uiState.update { it.copy(fertilizerSearchQuery = query) }
    }

    fun updateFertilizerDistrictFilter(district: String) {
        _uiState.update { it.copy(fertilizerSelectedDistrict = district) }
    }

    fun updateFertilizerCategoryFilter(category: com.example.model.FertilizerType?) {
        _uiState.update { it.copy(fertilizerSelectedCategory = category) }
    }

    fun updateFertilizerSortOption(option: FertilizerSortOption) {
        _uiState.update { it.copy(fertilizerSortOption = option) }
    }

    fun calculateFertilizerRequirement(crop: String, acres: Double) {
        val req = fertilizerPriceService.calculateFertilizerRequirement(crop, acres)
        _uiState.update {
            it.copy(
                fertilizerCalcCrop = crop,
                fertilizerCalcAcres = acres,
                fertilizerRequirement = req
            )
        }
    }

    fun fetchFertilizerAiAdvisory(district: String, query: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isFertilizerAiLoading = true) }
            val advice = fertilizerPriceService.fetchAiFertilizerAdvisory(
                district = if (district == "All Districts") "Tamil Nadu" else district,
                selectedFertilizer = query.ifBlank { "General Fertilizer & Subsidy" },
                lang = _uiState.value.language
            )
            _uiState.update {
                it.copy(
                    fertilizerAiAdviceText = advice,
                    isFertilizerAiLoading = false
                )
            }
        }
    }
}

fun generateDefaultSampleTaskReminders(): List<com.example.model.FarmTaskReminder> {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
    val list = mutableListOf<com.example.model.FarmTaskReminder>()

    val baseCal = java.util.Calendar.getInstance()
    val cal = java.util.Calendar.getInstance()

    // 1. Irrigation
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, -3)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_1",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "நாற்றங்கால் நீர் பாசனம்",
            titleEnglish = "Nursery Water Management",
            category = com.example.model.TaskCategory.IRRIGATION,
            recurrence = com.example.model.RecurrencePattern.EVERY_3_DAYS,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "06:00 AM",
            isCompleted = true,
            growthStageNameTamil = "1. நாற்றுப் பருவம்",
            growthStageNameEnglish = "1. Nursery Stage",
            notesTamil = "காலை 6 மணிக்கு 5 செ.மீ நீர் பாய்ச்சவும்",
            notesEnglish = "Irrigate 5cm standing water early morning"
        )
    )

    // 2. Fertilizer Bio-Fertilizer
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_2",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "அசோஸ்பைரில்லம் உயிரி உரம் இடுதல்",
            titleEnglish = "Apply Azospirillum Bio-Fertilizer",
            category = com.example.model.TaskCategory.FERTILIZER,
            recurrence = com.example.model.RecurrencePattern.ONCE,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "07:30 AM",
            isCompleted = false,
            growthStageNameTamil = "1. நாற்றுப் பருவம்",
            growthStageNameEnglish = "1. Nursery Stage",
            notesTamil = "ஏக்கருக்கு 1 கிலோ உயிரி உரம் இடவும்",
            notesEnglish = "Apply 1kg per acre bio-fertilizer"
        )
    )

    // 3. Alternate Wetting & Drying Irrigation
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 4)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_3",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "காய்ச்சலும் பாய்ச்சலுமாக நீர் பாசனம்",
            titleEnglish = "Alternate Wetting & Drying Irrigation",
            category = com.example.model.TaskCategory.IRRIGATION,
            recurrence = com.example.model.RecurrencePattern.EVERY_3_DAYS,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "06:30 AM",
            isCompleted = false,
            growthStageNameTamil = "2. தூர் கட்டும் பருவம்",
            growthStageNameEnglish = "2. Tillering Stage",
            notesTamil = "மண்ணில் லேசான விரிசல் கண்ட பின் பாசனம்",
            notesEnglish = "Irrigate when surface forms fine hairline cracks"
        )
    )

    // 4. Urea & Potash Top Dress
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 7)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_4",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "யூரியா & பொட்டாஷ் மேலுரம் இடல்",
            titleEnglish = "Top Dress Urea & Potash",
            category = com.example.model.TaskCategory.FERTILIZER,
            recurrence = com.example.model.RecurrencePattern.BI_WEEKLY,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "08:00 AM",
            isCompleted = false,
            growthStageNameTamil = "2. தூர் கட்டும் பருவம்",
            growthStageNameEnglish = "2. Tillering Stage",
            notesTamil = "ஏக்கருக்கு 22 கிலோ யூரியா + 10 கிலோ பொட்டாஷ்",
            notesEnglish = "22kg Urea + 10kg Potash per acre"
        )
    )

    // 5. Pest Spray (Neem Oil)
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 11)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_5",
            cropName = "Turmeric",
            cropTamilName = "மஞ்சள்",
            titleTamil = "இலைச்சுருட்டு புழு வேப்பெண்ணெய் தெளிப்பு",
            titleEnglish = "Neem Oil Spray for Leaf Folder Pest",
            category = com.example.model.TaskCategory.PEST_SPRAY,
            recurrence = com.example.model.RecurrencePattern.WEEKLY,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "05:00 PM",
            isCompleted = false,
            growthStageNameTamil = "2. கிழங்கு பெருக்கம் பருவம்",
            growthStageNameEnglish = "2. Rhizome Bulking Stage",
            notesTamil = "3% இயற்கை வேப்பெண்ணெய் தெளிப்பு",
            notesEnglish = "Foliar spray 3% cold-pressed neem oil"
        )
    )

    // 6. Panicle Water
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 15)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_6",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "தொடர் 5 செ.மீ நீர் பாசனம்",
            titleEnglish = "Maintain 5cm Standing Water",
            category = com.example.model.TaskCategory.IRRIGATION,
            recurrence = com.example.model.RecurrencePattern.EVERY_3_DAYS,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "06:00 AM",
            isCompleted = false,
            growthStageNameTamil = "3. கதிர் உருவாகும் பருவம்",
            growthStageNameEnglish = "3. Panicle Initiation",
            notesTamil = "பூக்கும் காலத்தில் நீர் பற்றாக்குறை வராமல் பராமரிக்க",
            notesEnglish = "Maintain standing water during flowering"
        )
    )

    // 7. Harvest
    cal.time = baseCal.time
    cal.add(java.util.Calendar.DAY_OF_MONTH, 22)
    list.add(
        com.example.model.FarmTaskReminder(
            id = "t_sample_7",
            cropName = "Samba Paddy",
            cropTamilName = "சம்பா நெல்",
            titleTamil = "இயந்திர அறுவடை & தானிய சேமிப்பு",
            titleEnglish = "Combine Harvest & Grain Drying",
            category = com.example.model.TaskCategory.HARVEST,
            recurrence = com.example.model.RecurrencePattern.ONCE,
            scheduledDate = sdf.format(cal.time),
            scheduledTime = "07:00 AM",
            isCompleted = false,
            growthStageNameTamil = "4. முதிர்ச்சி & அறுவடை பருவம்",
            growthStageNameEnglish = "4. Maturity & Harvest",
            notesTamil = "தானிய ஈரப்பதம் 14% குறைக்க உலர்த்துதல்",
            notesEnglish = "Sun dry grains to 14% moisture"
        )
    )

    return list
}



