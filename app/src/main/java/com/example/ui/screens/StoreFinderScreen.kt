package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import android.content.Intent
import android.net.Uri
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.StoreMapCanvas
import com.example.viewmodel.StoreSortOption
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreFinderScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = state.language
    val isAdmin = state.currentRole == UserRole.ADMIN
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    var showMapSection by remember { mutableStateOf(true) }
    var showAdminEditDialog by remember { mutableStateOf<Pair<String, FertilizerProduct>?>(null) } // storeId to product
    var showAddProductDialog by remember { mutableStateOf<String?>(null) } // storeId

    val districtOptions = listOf(
        "All Districts", "Ariyalur", "Chengalpattu", "Chennai", "Coimbatore", "Cuddalore", "Dharmapuri",
        "Dindigul", "Erode", "Kallakurichi", "Kanchipuram", "Kanyakumari", "Karur", "Krishnagiri", "Madurai",
        "Mayiladuthurai", "Nagapattinam", "Namakkal", "Nilgiris", "Perambalur", "Pudukkottai", "Ramanathapuram",
        "Ranipet", "Salem", "Sivagangai", "Tenkasi", "Thanjavur", "Theni", "Thoothukudi", "Tiruchirappalli",
        "Tirunelveli", "Tirupattur", "Tiruppur", "Tiruvallur", "Tiruvannamalai", "Tiruvarur", "Vellore",
        "Villupuram", "Virudhunagar"
    )

    // Specific popular fertilizers for 1-tap quick price comparison
    val popularFertilizers = listOf(
        null to if (lang == AppLanguage.TAMIL) "அனைத்து பொருட்கள்" else "All Products",
        "Urea" to if (lang == AppLanguage.TAMIL) "யூரியா (₹266)" else "Urea (₹266)",
        "DAP" to if (lang == AppLanguage.TAMIL) "DAP (₹1350)" else "DAP (₹1350)",
        "Potash" to if (lang == AppLanguage.TAMIL) "பொட்டாஷ் (₹1680)" else "Potash (₹1680)",
        "Complex" to if (lang == AppLanguage.TAMIL) "காம்ப்ளக்ஸ் 20:20" else "Complex 20:20",
        "Panchakavya" to if (lang == AppLanguage.TAMIL) "பஞ்சகாவ்யா (₹520)" else "Panchagavya",
        "Vermicompost" to if (lang == AppLanguage.TAMIL) "மண்புழு உரம்" else "Vermicompost",
        "Gypsum" to if (lang == AppLanguage.TAMIL) "ஜிப்சம் (₹180)" else "Gypsum"
    )

    // Filter Stores based on Proximity, Category, Specific Fertilizer, District, and Search Query
    val filteredStores = remember(
        state.stores,
        state.selectedDistrictFilter,
        state.selectedStoreCategory,
        state.selectedFertilizerNameFilter,
        state.maxProximityKm,
        state.storeSearchQuery,
        state.storeSortOption
    ) {
        val list = state.stores.filter { store ->
            // Proximity Filter
            val matchesProximity = state.maxProximityKm == null || store.distanceKm <= state.maxProximityKm

            // District Filter
            val matchesDistrict = state.selectedDistrictFilter == "All Districts" ||
                    store.district.equals(state.selectedDistrictFilter, ignoreCase = true)

            // Category Filter (Chemical, Organic, Bio-Pesticide, Seeds)
            val matchesCategory = state.selectedStoreCategory == null ||
                    store.products.any { it.category == state.selectedStoreCategory }

            // Specific Fertilizer Focus (e.g. Urea, DAP, Potash)
            val matchesSpecificFertilizer = state.selectedFertilizerNameFilter.isNullOrBlank() ||
                    store.products.any {
                        it.name.contains(state.selectedFertilizerNameFilter, ignoreCase = true) ||
                                it.tamilName.contains(state.selectedFertilizerNameFilter, ignoreCase = true)
                    }

            // Search Query Filter
            val query = state.storeSearchQuery.trim()
            val matchesSearch = query.isBlank() ||
                    store.name.contains(query, ignoreCase = true) ||
                    store.tamilName.contains(query, ignoreCase = true) ||
                    store.address.contains(query, ignoreCase = true) ||
                    store.district.contains(query, ignoreCase = true) ||
                    store.taluk.contains(query, ignoreCase = true) ||
                    store.products.any {
                        it.name.contains(query, ignoreCase = true) ||
                                it.tamilName.contains(query, ignoreCase = true) ||
                                it.composition.contains(query, ignoreCase = true)
                    }

            matchesProximity && matchesDistrict && matchesCategory && matchesSpecificFertilizer && matchesSearch
        }

        // Apply Sorting
        when (state.storeSortOption) {
            StoreSortOption.PROXIMITY -> list.sortedBy { it.distanceKm }
            StoreSortOption.PRICE_LOW_TO_HIGH -> list.sortedBy { store ->
                store.getDisplayPrice(state.selectedStoreCategory, state.selectedFertilizerNameFilter)?.first ?: Int.MAX_VALUE
            }
            StoreSortOption.RATING -> list.sortedByDescending { it.rating }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("store_finder_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Title & Access Badge
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Store,
                            contentDescription = "Agri Stores",
                            tint = primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "உரக்கடைகள் & விலை வரைபடம்" else "Fertilizer Stores & Price Map",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }

                    // Admin / Farmer Badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAdmin) secondaryColor.copy(alpha = 0.2f) else primaryColor.copy(alpha = 0.15f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = if (isAdmin) Icons.Default.AdminPanelSettings else Icons.Default.CheckCircle,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = if (isAdmin) secondaryColor else primaryColor
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isAdmin) "Admin Access" else "Farmer Mode",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = if (isAdmin) secondaryColor else primaryColor
                            )
                        }
                    }
                }

                Text(
                    text = if (lang == AppLanguage.TAMIL)
                        "தமிழ்நாடு முழுவதும் தொடக்க வேளாண் கூட்டுறவு சங்கம் (PACCS) மற்றும் தனியார் கடைகளில் யூரியா, DAP, பொட்டாஷ், இயற்கை உரங்களின் இருப்பு மற்றும் விலையை வரைபடத்தில் காணுங்கள்."
                    else
                        "View real-time fertilizer store locations, live stock, and compare subsidized government pricing against private dealer rates across Tamil Nadu.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // 1b. Offline Map Cache Control Panel
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("offline_map_cache_panel"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = if (state.isOfflineMapEnabled) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant),
                border = BorderStroke(1.dp, if (state.isOfflineMapEnabled) Color(0xFFA5D6A7) else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CloudOff,
                                contentDescription = "Offline Cache",
                                tint = if (state.isOfflineMapEnabled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "ஆஃப்லைன் வரைபட கேச் (Offline Map Cache)" else "Offline Map Cache Mode",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = if (state.isOfflineMapEnabled) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.isOfflineMapEnabled)
                                        (if (lang == AppLanguage.TAMIL) "செயலில் உள்ளது • ${String.format("%.1f", state.offlineMapCacheSizeMb)} MB (${state.offlineCachedTileCount} டைல்கள்)" else "Active • ${String.format("%.1f", state.offlineMapCacheSizeMb)} MB (${state.offlineCachedTileCount} tiles cached)")
                                    else
                                        (if (lang == AppLanguage.TAMIL) "முடக்கப்பட்டன • ஆன்லைன் மேப்" else "Disabled • Online Mode"),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (state.isOfflineMapEnabled) Color(0xFF2E7D32) else MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Switch(
                            checked = state.isOfflineMapEnabled,
                            onCheckedChange = { viewModel.toggleOfflineMapMode() },
                            modifier = Modifier.testTag("offline_map_mode_toggle")
                        )
                    }

                    if (state.isOfflineMapEnabled) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { viewModel.downloadOfflineMapRegion(state.farmerProfile.district) },
                                enabled = !state.isDownloadingMapTiles,
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("download_offline_tiles_button"),
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                            ) {
                                if (state.isDownloadingMapTiles) {
                                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (lang == AppLanguage.TAMIL) "பதிவிறக்குகிறது..." else "Downloading...", fontSize = 11.sp)
                                } else {
                                    Icon(Icons.Default.Download, contentDescription = "Download", modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(if (lang == AppLanguage.TAMIL) "மாவட்ட வரைபடம் தரவிறக்கு" else "Cache District Map", fontSize = 11.sp)
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.clearOfflineMapCache() },
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("clear_map_cache_button")
                            ) {
                                Icon(Icons.Default.DeleteOutline, contentDescription = "Clear Cache", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (lang == AppLanguage.TAMIL) "கேச் நீக்கு" else "Clear", fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        }

        // 2. Govt Subsidy & Near You Quick Highlights Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Subsidized Urea stat
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = BorderStroke(1.dp, Color(0xFFA5D6A7))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மானிய யூரியா" else "Subsidized Urea",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                        Text(
                            text = "₹266 / 45kg",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "அரசு நிர்ணய விலை" else "Govt Rate (PACCS)",
                            fontSize = 9.sp,
                            color = Color(0xFF388E3C)
                        )
                    }
                }

                // Subsidized DAP stat
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE3F2FD),
                    border = BorderStroke(1.dp, Color(0xFF90CAF9))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மானிய DAP" else "Subsidized DAP",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1565C0)
                        )
                        Text(
                            text = "₹1,350 / 50kg",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF0D47A1)
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "அரசு மானிய விலை" else "Govt Rate (SPIC)",
                            fontSize = 9.sp,
                            color = Color(0xFF1976D2)
                        )
                    }
                }

                // Stores Near You stat
                Surface(
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFCC80))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "அருகிலுள்ளவை" else "Near You",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Text(
                            text = "${filteredStores.size} Stores",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFBF360C)
                        )
                        Text(
                            text = if (state.maxProximityKm != null) "≤ ${state.maxProximityKm.toInt()} km" else "Tamil Nadu",
                            fontSize = 9.sp,
                            color = Color(0xFFF57C00)
                        )
                    }
                }
            }
        }

        // 3. Admin Notice
        if (isAdmin) {
            item {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFFFF3E0),
                    border = BorderStroke(1.dp, Color(0xFFFFB74D))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.EditNote,
                            contentDescription = "Admin Notice",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "நிர்வாகி முறை (Admin Mode) செயலில் உள்ளது" else "Store Admin Mode Active",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100)
                            )
                            Text(
                                text = if (lang == AppLanguage.TAMIL)
                                    "நீங்கள் கடைகளின் உர விலைகள், மானிய விலை மற்றும் இருப்பு நிலையை உடனுக்குடன் மாற்றலாம்."
                                else
                                    "You can update fertilizer prices, stock availability, and add new products to stores.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF5D4037)
                            )
                        }
                    }
                }
            }
        }

        // 4. Search Bar & Map Toggle
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.storeSearchQuery,
                    onValueChange = { viewModel.setStoreSearchQuery(it) },
                    placeholder = {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "உரக்கடை, உரம் தேடுக (யூரியா, DAP, SPIC)..." else "Search store, urea, DAP, SPIC...",
                            fontSize = 13.sp
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = {
                        if (state.storeSearchQuery.isNotBlank()) {
                            IconButton(onClick = { viewModel.setStoreSearchQuery("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("store_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                IconButton(
                    onClick = { showMapSection = !showMapSection },
                    modifier = Modifier
                        .background(
                            if (showMapSection) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(12.dp)
                        )
                        .testTag("toggle_map_view_button")
                ) {
                    Icon(
                        imageVector = if (showMapSection) Icons.Default.Map else Icons.Default.FormatListBulleted,
                        contentDescription = "Toggle Map",
                        tint = primaryColor
                    )
                }
            }
        }

        // 5. Fertilizer Category Filter (Chemical, Organic, Bio-Pesticide, Seeds)
        item {
            Column {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "உர வகை வடிகட்டி:" else "Filter by Fertilizer Type:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("fertilizer_type_filter_row")
                ) {
                    item {
                        FilterChip(
                            selected = state.selectedStoreCategory == null,
                            onClick = { viewModel.setStoreCategory(null) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "அனைத்து உரங்கள்" else "All Fertilizers") },
                            modifier = Modifier.testTag("filter_all_fertilizers")
                        )
                    }
                    items(FertilizerType.values()) { fType ->
                        FilterChip(
                            selected = state.selectedStoreCategory == fType,
                            onClick = { viewModel.setStoreCategory(fType) },
                            label = { Text(fType.getDisplayName(lang)) },
                            modifier = Modifier.testTag("filter_type_${fType.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // 6. Popular Fertilizer 1-Tap Price Focus Chips
        item {
            Column {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "நேரடி உர விலை ஒப்பீடு (வரைபடத்தில் காண்க):" else "Quick Product Price Focus (Shows on Map):",
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("quick_fertilizer_chips_row")
                ) {
                    items(popularFertilizers) { (nameFilter, label) ->
                        val isSelected = state.selectedFertilizerNameFilter == nameFilter
                        SuggestionChip(
                            onClick = { viewModel.setSelectedFertilizerNameFilter(nameFilter) },
                            label = { Text(label, fontSize = 11.sp) },
                            border = BorderStroke(1.dp, if (isSelected) primaryColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = if (isSelected) primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surface
                            ),
                            modifier = Modifier.testTag("popular_fert_${nameFilter ?: "all"}")
                        )
                    }
                }
            }
        }

        // 7. Interactive Map-Based UI Component
        if (showMapSection) {
            item {
                StoreMapCanvas(
                    stores = filteredStores,
                    selectedStore = state.selectedStoreForMap,
                    onStoreSelected = { viewModel.selectStoreForMap(it) },
                    farmerDistrict = state.farmerProfile.district,
                    language = lang,
                    categoryFilter = state.selectedStoreCategory,
                    specificFertilizerFilter = state.selectedFertilizerNameFilter,
                    maxProximityKm = state.maxProximityKm,
                    onProximitySelected = { viewModel.setStoreProximityFilter(it) },
                    onNavigateDirections = { store -> viewModel.startOfflineNavigation(store) }
                )
            }
        }

        // 8. Filter Bar: District & Sort Option
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // District selector chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("district_filter_row")
                ) {
                    items(districtOptions) { dist ->
                        val isSelected = state.selectedDistrictFilter == dist
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setStoreDistrictFilter(dist) },
                            label = { Text(dist, fontSize = 11.sp) },
                            modifier = Modifier.height(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Sort Dropdown Menu
                var sortMenuExpanded by remember { mutableStateOf(false) }
                Box {
                    IconButton(
                        onClick = { sortMenuExpanded = true },
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                            .size(32.dp)
                            .testTag("sort_stores_button")
                    ) {
                        Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp))
                    }

                    DropdownMenu(
                        expanded = sortMenuExpanded,
                        onDismissRequest = { sortMenuExpanded = false }
                    ) {
                        StoreSortOption.values().forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.getDisplayName(lang)) },
                                onClick = {
                                    viewModel.setStoreSortOption(option)
                                    sortMenuExpanded = false
                                },
                                leadingIcon = {
                                    if (state.storeSortOption == option) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = primaryColor)
                                    }
                                }
                            )
                        }
                    }
                }
            }
        }

        // 9. Store Count Summary & Sort Label
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TAMIL)
                        "கடைகள் மற்றும் உர விலை பட்டியல் (${filteredStores.size})"
                    else
                        "Stores & Price Comparison (${filteredStores.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = state.storeSortOption.getDisplayName(lang),
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // 10. List of Store Cards with Products & Price Comparison
        if (filteredStores.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 20.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = "No stores",
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL)
                                "தேர்ந்தெடுக்கப்பட்ட தூரம் அல்லது உர வகைக்கு கடைகள் இல்லை"
                            else
                                "No stores found matching current proximity or fertilizer filter",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        TextButton(
                            onClick = {
                                viewModel.setStoreProximityFilter(null)
                                viewModel.setStoreCategory(null)
                                viewModel.setSelectedFertilizerNameFilter(null)
                                viewModel.setStoreDistrictFilter("All Districts")
                                viewModel.setStoreSearchQuery("")
                            }
                        ) {
                            Text(if (lang == AppLanguage.TAMIL) "அனைத்து வடிகட்டிகளையும் நீக்குக" else "Reset All Filters")
                        }
                    }
                }
            }
        }

        items(filteredStores) { store ->
            val isSelectedOnMap = store.id == state.selectedStoreForMap?.id

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.selectStoreForMap(store) }
                    .testTag("store_card_${store.id}"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelectedOnMap) 4.dp else 1.dp),
                border = if (isSelectedOnMap) BorderStroke(2.dp, primaryColor) else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Store Title, Type & Proximity Badge
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) store.tamilName else store.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (store.isGovtAided) Color(0xFFE8F5E9) else Color(0xFFFFF3E0)
                                ) {
                                    Text(
                                        text = store.storeType,
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (store.isGovtAided) Color(0xFF1B5E20) else Color(0xFFE65100),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "★ ${store.rating} (${store.reviewCount})",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFFF57F17),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.NearMe,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(13.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${store.distanceKm} km away",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor
                                )
                                Text(
                                    text = " • ${store.taluk}, ${store.district}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Text(
                                text = "Ph: ${store.phone} • ${store.address}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }

                        if (isAdmin) {
                            FilledTonalButton(
                                onClick = { showAddProductDialog = store.id },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                                modifier = Modifier.testTag("admin_add_product_to_${store.id}")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (lang == AppLanguage.TAMIL) "பொருள் சேர்" else "Add Item", fontSize = 11.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (lang == AppLanguage.TAMIL) "உரங்களின் நேரடி விலை & இருப்பு:" else "Fertilizer Stock & Pricing:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Products in this store
                    val displayedProducts = store.products.filter { prod ->
                        val matchCat = state.selectedStoreCategory == null || prod.category == state.selectedStoreCategory
                        val matchName = state.selectedFertilizerNameFilter.isNullOrBlank() ||
                                prod.name.contains(state.selectedFertilizerNameFilter, true) ||
                                prod.tamilName.contains(state.selectedFertilizerNameFilter, true)
                        matchCat && matchName
                    }.ifEmpty { store.products }

                    displayedProducts.forEach { product ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(10.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) product.tamilName else product.name,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Surface(
                                            shape = RoundedCornerShape(4.dp),
                                            color = when (product.category) {
                                                FertilizerType.ORGANIC -> Color(0xFFE8F5E9)
                                                FertilizerType.BIO_PESTICIDE -> Color(0xFFE0F2F1)
                                                FertilizerType.SEEDS -> Color(0xFFFFF3E0)
                                                else -> Color(0xFFE3F2FD)
                                            }
                                        ) {
                                            Text(
                                                text = when (product.category) {
                                                    FertilizerType.ORGANIC -> if (lang == AppLanguage.TAMIL) "இயற்கை" else "Organic"
                                                    FertilizerType.BIO_PESTICIDE -> if (lang == AppLanguage.TAMIL) "உயிரி உரம்" else "Bio-Input"
                                                    FertilizerType.SEEDS -> if (lang == AppLanguage.TAMIL) "விதைகள்" else "Seeds"
                                                    else -> if (lang == AppLanguage.TAMIL) "இரசாயனம்" else "Chemical"
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (product.category) {
                                                    FertilizerType.ORGANIC -> Color(0xFF2E7D32)
                                                    FertilizerType.BIO_PESTICIDE -> Color(0xFF00796B)
                                                    FertilizerType.SEEDS -> Color(0xFFE65100)
                                                    else -> Color(0xFF1565C0)
                                                },
                                                modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${product.brand} • ${product.bagSize} (${product.composition})",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                    Text(
                                        text = if (product.inStock) (if (lang == AppLanguage.TAMIL) "✔ இருப்பில் உள்ளது" else "✔ In Stock")
                                        else (if (lang == AppLanguage.TAMIL) "✖ இருப்பு இல்லை" else "✖ Out of Stock"),
                                        style = MaterialTheme.typography.labelSmall,
                                        color = if (product.inStock) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Column(horizontalAlignment = Alignment.End) {
                                        if (product.subsidizedPrice != null) {
                                            Text(
                                                text = "₹${product.subsidizedPrice}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = primaryColor
                                            )
                                            Text(
                                                text = if (lang == AppLanguage.TAMIL) "அரசு மானிய விலை" else "Govt Rate",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = primaryColor,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        } else {
                                            Text(
                                                text = "₹${product.standardPrice}",
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = if (lang == AppLanguage.TAMIL) "விற்பனை விலை" else "Dealer MRP",
                                                style = MaterialTheme.typography.labelSmall,
                                                color = MaterialTheme.colorScheme.outline,
                                                fontSize = 9.sp
                                            )
                                        }
                                    }

                                    // Admin Edit Button
                                    if (isAdmin) {
                                        Spacer(modifier = Modifier.width(8.dp))
                                        IconButton(
                                            onClick = { showAdminEditDialog = Pair(store.id, product) },
                                            modifier = Modifier.testTag("admin_edit_${store.id}_${product.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Edit,
                                                contentDescription = "Edit Price",
                                                tint = secondaryColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Store Actions: View On Map, Call Store, & Directions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.selectStoreForMap(store)
                                showMapSection = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("view_on_map_${store.id}"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.PinDrop, contentDescription = "Map", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lang == AppLanguage.TAMIL) "வரைபடம்" else "Map", fontSize = 11.sp)
                        }

                        OutlinedButton(
                            onClick = {
                                val phoneNumber = store.phone.replace("-", "").replace(" ", "")
                                val dialIntent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phoneNumber"))
                                try {
                                    context.startActivity(dialIntent)
                                } catch (e: Exception) {
                                    // Handle exception
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("call_store_${store.id}"),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = "Call", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lang == AppLanguage.TAMIL) "அழைக்க" else "Call", fontSize = 11.sp)
                        }

                        Button(
                            onClick = {
                                val lat = store.latitude ?: 10.7870
                                val lng = store.longitude ?: 79.1378
                                val label = if (lang == AppLanguage.TAMIL) store.tamilName else store.name
                                val mapUri = Uri.parse("geo:$lat,$lng?q=${Uri.encode(label)}")
                                val mapIntent = Intent(Intent.ACTION_VIEW, mapUri)
                                try {
                                    context.startActivity(mapIntent)
                                } catch (e: Exception) {
                                    viewModel.startOfflineNavigation(store)
                                }
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("start_offline_nav_${store.id}"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.Default.Navigation, contentDescription = "Route", modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (lang == AppLanguage.TAMIL) "வழித்தடம்" else "Route", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Admin Dialog: Edit Stock & Price
    showAdminEditDialog?.let { (storeId, prod) ->
        var editStandardPrice by remember { mutableStateOf(prod.standardPrice.toString()) }
        var editSubsidizedPrice by remember { mutableStateOf(prod.subsidizedPrice?.toString() ?: "") }
        var editInStock by remember { mutableStateOf(prod.inStock) }

        AlertDialog(
            onDismissRequest = { showAdminEditDialog = null },
            title = { Text(if (lang == AppLanguage.TAMIL) "உரத்தின் விலை & இருப்பு மாற்றம் (Admin)" else "Edit Price & Stock (Admin)") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) prod.tamilName else prod.name,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = editStandardPrice,
                        onValueChange = { editStandardPrice = it },
                        label = { Text("Standard Price (₹)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_input_standard_price")
                    )
                    OutlinedTextField(
                        value = editSubsidizedPrice,
                        onValueChange = { editSubsidizedPrice = it },
                        label = { Text("Subsidized Price (₹, Optional)") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("admin_input_subsidized_price")
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("In Stock Availability")
                        Switch(
                            checked = editInStock,
                            onCheckedChange = { editInStock = it },
                            modifier = Modifier.testTag("admin_toggle_instock")
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val stdPrice = editStandardPrice.toIntOrNull() ?: prod.standardPrice
                        val subPrice = editSubsidizedPrice.toIntOrNull()
                        viewModel.adminUpdateProductStockAndPrice(storeId, prod.id, stdPrice, subPrice, editInStock)
                        showAdminEditDialog = null
                    },
                    modifier = Modifier.testTag("admin_confirm_save_button")
                ) {
                    Text(if (lang == AppLanguage.TAMIL) "சேமி" else "Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAdminEditDialog = null }) {
                    Text(if (lang == AppLanguage.TAMIL) "ரத்து" else "Cancel")
                }
            }
        )
    }

    // Admin Dialog: Add New Product to Store
    showAddProductDialog?.let { storeId ->
        var newName by remember { mutableStateOf("") }
        var newTamilName by remember { mutableStateOf("") }
        var newBrand by remember { mutableStateOf("TNAU Certified") }
        var newBagSize by remember { mutableStateOf("50 kg") }
        var newPrice by remember { mutableStateOf("1200") }
        var newSubPrice by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddProductDialog = null },
            title = { Text(if (lang == AppLanguage.TAMIL) "புதிய உரம் சேர்க்கவும்" else "Add New Fertilizer Product") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = newName,
                        onValueChange = { newName = it },
                        label = { Text("Product Name (English)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newTamilName,
                        onValueChange = { newTamilName = it },
                        label = { Text("பெயர் (தமிழ்)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = newBrand,
                            onValueChange = { newBrand = it },
                            label = { Text("Brand") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newBagSize,
                            onValueChange = { newBagSize = it },
                            label = { Text("Size") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedTextField(
                            value = newPrice,
                            onValueChange = { newPrice = it },
                            label = { Text("Price (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = newSubPrice,
                            onValueChange = { newSubPrice = it },
                            label = { Text("Subsidy (₹)") },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newName.isNotBlank()) {
                            val stdP = newPrice.toIntOrNull() ?: 500
                            val subP = newSubPrice.toIntOrNull()
                            viewModel.adminAddNewProductToStore(
                                storeId, newName, if (newTamilName.isBlank()) newName else newTamilName,
                                FertilizerType.CHEMICAL, newBrand, newBagSize, stdP, subP
                            )
                            showAddProductDialog = null
                        }
                    }
                ) {
                    Text(if (lang == AppLanguage.TAMIL) "சேர்" else "Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddProductDialog = null }) {
                    Text(if (lang == AppLanguage.TAMIL) "ரத்து" else "Cancel")
                }
            }
        )
    }

    // Turn-by-Turn Offline Navigation Dialog
    if (state.isNavigatingOffline && state.activeOfflineNavigationRoute != null) {
        val route = state.activeOfflineNavigationRoute!!
        AlertDialog(
            onDismissRequest = { viewModel.stopOfflineNavigation() },
            modifier = Modifier.testTag("offline_navigation_dialog"),
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Navigation,
                        contentDescription = null,
                        tint = primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "ஆஃப்லைன் வழித்தடம் (Off-grid GPS)" else "Offline Turn-by-Turn Navigation",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${if (lang == AppLanguage.TAMIL) route.storeTamilName else route.storeName} • ${String.format("%.1f", route.totalDistanceKm)} km (${route.estimatedTimeMinutes} mins)",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor
                        )
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFE8F5E9),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.CloudOff, contentDescription = null, tint = Color(0xFF2E7D32), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "இணைய இணைப்பு இல்லாமல் ஆஃப்லைன் வழிகாட்டல் இயங்குகிறது" else "Navigating off-grid using local cached map tiles & GPS points",
                                fontSize = 11.sp,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    route.instructions.forEach { step ->
                        Row(
                            verticalAlignment = Alignment.Top,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = primaryColor.copy(alpha = 0.15f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "${step.stepNumber}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 12.sp,
                                        color = primaryColor
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) step.instructionTamil else step.instructionEnglish,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${step.distanceKm} km",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.stopOfflineNavigation() },
                    modifier = Modifier.testTag("close_offline_nav_button")
                ) {
                    Text(if (lang == AppLanguage.TAMIL) "வழித்தடத்தை மூடு" else "Close Route")
                }
            }
        )
    }
}
