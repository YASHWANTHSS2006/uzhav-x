package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.FertilizerPriceItem
import com.example.model.FertilizerType
import com.example.model.StockStatus
import com.example.viewmodel.FertilizerSortOption
import com.example.viewmodel.UzhavUiState
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FertilizerPriceScreen(
    uiState: UzhavUiState,
    viewModel: UzhavViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }
    val context = LocalContext.current
    val lang = uiState.language
    val isTamil = lang == AppLanguage.TAMIL

    var activeTab by remember { mutableStateOf(0) } // 0 = Retailer Price Comparison, 1 = AI Fertilizer Calculator
    var showSortMenu by remember { mutableStateOf(false) }

    // Filter & Sort Logic
    val filteredItems = remember(
        uiState.fertilizerPrices,
        uiState.fertilizerSearchQuery,
        uiState.fertilizerSelectedDistrict,
        uiState.fertilizerSelectedCategory,
        uiState.fertilizerSortOption
    ) {
        var list = uiState.fertilizerPrices.filter { item ->
            val matchQuery = uiState.fertilizerSearchQuery.isBlank() ||
                    item.productName.contains(uiState.fertilizerSearchQuery, ignoreCase = true) ||
                    item.productTamilName.contains(uiState.fertilizerSearchQuery, ignoreCase = true) ||
                    item.storeName.contains(uiState.fertilizerSearchQuery, ignoreCase = true) ||
                    item.storeTamilName.contains(uiState.fertilizerSearchQuery, ignoreCase = true) ||
                    item.brand.contains(uiState.fertilizerSearchQuery, ignoreCase = true)

            val matchDistrict = uiState.fertilizerSelectedDistrict == "All Districts" ||
                    item.district.equals(uiState.fertilizerSelectedDistrict, ignoreCase = true)

            val matchCategory = uiState.fertilizerSelectedCategory == null ||
                    item.category == uiState.fertilizerSelectedCategory

            matchQuery && matchDistrict && matchCategory
        }

        list = when (uiState.fertilizerSortOption) {
            FertilizerSortOption.LOWEST_PRICE -> list.sortedBy { it.retailerPrice }
            FertilizerSortOption.SAVINGS_HIGH_TO_LOW -> list.sortedByDescending { it.savingsVsRetail }
            FertilizerSortOption.NEAREST_DISTANCE -> list.sortedBy { it.distanceKm }
            FertilizerSortOption.RATING -> list.sortedByDescending { it.rating }
        }

        list
    }

    val availableDistricts = remember {
        listOf("All Districts", "Thanjavur", "Madurai", "Salem", "Tiruchirappalli", "Coimbatore", "Tiruvarur", "Erode", "Dindigul")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = if (isTamil) "உரம் விலை & சில்லறை விற்பனை" else "Fertilizer Prices & Retailers",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Text(
                            text = if (isTamil) "அரசு மானிய விலை & கடைகளின் ஒப்பீடு" else "Govt Subsidized Rates & Store Comparison",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("fertilizer_price_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            viewModel.fetchFertilizerAiAdvisory(
                                district = uiState.fertilizerSelectedDistrict,
                                query = uiState.fertilizerSearchQuery
                            )
                        },
                        modifier = Modifier.testTag("fertilizer_ai_advice_icon_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI Advisory",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Tab Selector Row
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary
            ) {
                Tab(
                    selected = activeTab == 0,
                    onClick = { activeTab = 0 },
                    text = {
                        Text(
                            text = if (isTamil) "உரம் விலைப் பட்டியல் (Price Compare)" else "Retailer Prices",
                            fontWeight = if (activeTab == 0) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("fertilizer_tab_prices")
                )
                Tab(
                    selected = activeTab == 1,
                    onClick = { activeTab = 1 },
                    text = {
                        Text(
                            text = if (isTamil) "உரக் கணக்கீடு (Bag Calculator)" else "Fertilizer Calculator",
                            fontWeight = if (activeTab == 1) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    modifier = Modifier.testTag("fertilizer_tab_calc")
                )
            }

            if (activeTab == 0) {
                // RETAILER PRICE COMPARISON TAB
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Search Bar
                    item {
                        OutlinedTextField(
                            value = uiState.fertilizerSearchQuery,
                            onValueChange = { viewModel.updateFertilizerSearchQuery(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("fertilizer_search_bar"),
                            placeholder = {
                                Text(if (isTamil) "உரம், பிராண்ட் அல்லது கடை பெயர்..." else "Search urea, DAP, store name, brand...")
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Search, contentDescription = "Search")
                            },
                            trailingIcon = {
                                if (uiState.fertilizerSearchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.updateFertilizerSearchQuery("") }) {
                                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                                    }
                                }
                            },
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )
                    }

                    // Govt MRP Price Benchmark Card
                    item {
                        GovtPriceBenchmarkBanner(isTamil = isTamil)
                    }

                    // AI Advice Card if active
                    if (uiState.isFertilizerAiLoading || uiState.fertilizerAiAdviceText != null) {
                        item {
                            AiAdvisoryCard(
                                isLoading = uiState.isFertilizerAiLoading,
                                adviceText = uiState.fertilizerAiAdviceText,
                                isTamil = isTamil
                            )
                        }
                    }

                    // District & Category Filter Chips
                    item {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // District Selector Row
                            Text(
                                text = if (isTamil) "மாவட்டம் தேர்ந்தெடு:" else "Select District:",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                items(availableDistricts) { district ->
                                    val selected = uiState.fertilizerSelectedDistrict == district
                                    FilterChip(
                                        selected = selected,
                                        onClick = { viewModel.updateFertilizerDistrictFilter(district) },
                                        label = {
                                            Text(
                                                text = if (district == "All Districts") {
                                                    if (isTamil) "அனைத்து மாவட்டங்கள்" else "All Districts"
                                                } else district
                                            )
                                        },
                                        leadingIcon = if (selected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        modifier = Modifier.testTag("fertilizer_district_chip_${district.lowercase().replace(" ", "_")}")
                                    )
                                }
                            }

                            // Category Chips & Sort Selector Header Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isTamil) "உர வகை:" else "Category:",
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )

                                // Sort Dropdown trigger
                                Box {
                                    Row(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                            .clickable { showSortMenu = true }
                                            .padding(horizontal = 10.dp, vertical = 6.dp)
                                            .testTag("fertilizer_sort_dropdown"),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(16.dp))
                                        Text(
                                            text = uiState.fertilizerSortOption.getDisplayName(lang),
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, modifier = Modifier.size(16.dp))
                                    }

                                    DropdownMenu(
                                        expanded = showSortMenu,
                                        onDismissRequest = { showSortMenu = false }
                                    ) {
                                        FertilizerSortOption.values().forEach { option ->
                                            DropdownMenuItem(
                                                text = { Text(option.getDisplayName(lang)) },
                                                onClick = {
                                                    viewModel.updateFertilizerSortOption(option)
                                                    showSortMenu = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // Category Filter Chips
                            LazyRow(
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                item {
                                    FilterChip(
                                        selected = uiState.fertilizerSelectedCategory == null,
                                        onClick = { viewModel.updateFertilizerCategoryFilter(null) },
                                        label = { Text(if (isTamil) "அனைத்தும்" else "All Types") },
                                        modifier = Modifier.testTag("fertilizer_category_all")
                                    )
                                }
                                items(FertilizerType.values()) { category ->
                                    val selected = uiState.fertilizerSelectedCategory == category
                                    FilterChip(
                                        selected = selected,
                                        onClick = { viewModel.updateFertilizerCategoryFilter(category) },
                                        label = { Text(category.getDisplayName(lang)) },
                                        leadingIcon = if (selected) {
                                            { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                        } else null,
                                        modifier = Modifier.testTag("fertilizer_category_${category.name.lowercase()}")
                                    )
                                }
                            }
                        }
                    }

                    // Count Summary & Subsidy Info
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isTamil) "கண்டறியப்பட்ட சில்லறை விலைகள்: ${filteredItems.size}" else "Retail Options Found: ${filteredItems.size}",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.tertiaryContainer
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(Icons.Default.Verified, contentDescription = null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onTertiaryContainer)
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isTamil) "அரசு நிர்ணய விலை" else "Govt Subsidized MRP",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.onTertiaryContainer
                                    )
                                }
                            }
                        }
                    }

                    // Price Items List
                    if (filteredItems.isEmpty()) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(Icons.Default.SearchOff, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.outline)
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = if (isTamil) "தேடப்பட்ட உரம் அல்லது கடைகள் ஏதும் கிடைக்கவில்லை." else "No matching fertilizer retailers found.",
                                        style = MaterialTheme.typography.bodyMedium,
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    OutlinedButton(
                                        onClick = {
                                            viewModel.updateFertilizerSearchQuery("")
                                            viewModel.updateFertilizerDistrictFilter("All Districts")
                                            viewModel.updateFertilizerCategoryFilter(null)
                                        }
                                    ) {
                                        Text(if (isTamil) "அனைத்து ஃபில்டர்களையும் நீக்கு" else "Reset All Filters")
                                    }
                                }
                            }
                        }
                    } else {
                        items(filteredItems, key = { it.id }) { item ->
                            FertilizerPriceCard(
                                item = item,
                                isTamil = isTamil,
                                onCallStore = { phone ->
                                    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))
                                    try {
                                        context.startActivity(intent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, "Contact: $phone", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onNavigateStore = { storeName ->
                                    val uri = Uri.parse("geo:0,0?q=${Uri.encode(storeName + " " + item.district)}")
                                    val mapIntent = Intent(Intent.ACTION_VIEW, uri)
                                    try {
                                        context.startActivity(mapIntent)
                                    } catch (_: Exception) {
                                        Toast.makeText(context, storeName, Toast.LENGTH_SHORT).show()
                                    }
                                }
                            )
                        }
                    }
                }
            } else {
                // FERTILIZER CALCULATOR TAB
                FertilizerCalculatorScreen(
                    uiState = uiState,
                    viewModel = viewModel,
                    isTamil = isTamil
                )
            }
        }
    }
}

@Composable
private fun GovtPriceBenchmarkBanner(isTamil: Boolean) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fertilizer_mrp_banner"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isTamil) "அரசு நிர்ணயித்த அதிகபட்ச சில்லறை விலை (Govt Fixed MRP)" else "Govt Subsidized MRP Rates (Statutory)",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            Text(
                text = if (isTamil)
                    "மத்திய வேளாண் அமைச்சகத்தால் அறிவிக்கப்பட்ட முக்கிய உரங்களின் அதிகபட்ச மானிய விலை பட்டியல்:"
                else
                    "Statutory ceiling rates fixed by Union & TN State Agri Dept for 2026:",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                BenchmarkPriceBadge(name = "Neem Urea (45kg)", price = "₹266", isGovt = true)
                BenchmarkPriceBadge(name = "DAP (50kg)", price = "₹1,350", isGovt = true)
                BenchmarkPriceBadge(name = "MOP Potash (50kg)", price = "₹1,700", isGovt = true)
            }
        }
    }
}

@Composable
private fun BenchmarkPriceBadge(name: String, price: String, isGovt: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = name,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = price,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
private fun AiAdvisoryCard(
    isLoading: Boolean,
    adviceText: String?,
    isTamil: Boolean
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fertilizer_ai_advice_card"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                Text(
                    text = if (isTamil) "AI உர ஆலோசகர் (Gemini Smart Advice)" else "AI Fertilizer Advisory",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }

            if (isLoading) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = if (isTamil) "Gemini உர ஆலோசனை தயார் செய்கிறது..." else "Fetching AI fertilizer guidance...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            } else if (adviceText != null) {
                Text(
                    text = adviceText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }
    }
}

@Composable
private fun FertilizerPriceCard(
    item: FertilizerPriceItem,
    isTamil: Boolean,
    onCallStore: (String) -> Unit,
    onNavigateStore: (String) -> Unit
) {
    val isGovtOutlet = item.storeType.contains("PACCS", ignoreCase = true) || item.storeType.contains("TNAU", ignoreCase = true)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("fertilizer_item_card_${item.id}"),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header Row: Product Name & Stock Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isTamil) item.productTamilName else item.productName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Text(
                                text = item.brand,
                                style = MaterialTheme.typography.labelSmall,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                        Text(
                            text = "• ${item.bagSize}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Stock status badge
                StockBadge(status = item.stockStatus, isTamil = isTamil)
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Pricing Comparison Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isTamil) "விற்பனை விலை:" else "Retailer Price:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${item.retailerPrice}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (item.isGovtPrice) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                        )
                        Text(
                            text = " / ${item.bagSize}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = if (isTamil) "அரசு நிர்ணய MRP:" else "Govt Fixed MRP:",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "₹${item.standardGovtPrice}",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    if (item.savingsVsRetail > 0) {
                        Text(
                            text = if (isTamil) "+₹${item.savingsVsRetail} கூடுதல்" else "+₹${item.savingsVsRetail} vs MRP",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                    } else {
                        Text(
                            text = if (isTamil) "✓ நியாய விலை" else "✓ Fair Govt Rate",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Store Info & Distance
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                color = if (isGovtOutlet) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Icon(
                                imageVector = if (isGovtOutlet) Icons.Default.VerifiedUser else Icons.Default.Storefront,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp),
                                tint = if (isGovtOutlet) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = if (isTamil) item.storeTamilName else item.storeName,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = "${item.district} (${item.taluk}) • ${item.distanceKm} km away",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB800), modifier = Modifier.size(16.dp))
                        Text(
                            text = "${item.rating}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = { onCallStore(item.phone) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fertilizer_call_button_${item.id}"),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTamil) "கடை அழைப்பு" else "Call Store")
                }

                Button(
                    onClick = { onNavigateStore(item.storeName) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fertilizer_navigate_button_${item.id}"),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    Icon(Icons.Default.Navigation, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(if (isTamil) "வழிப்பாதை (Map)" else "Directions")
                }
            }
        }
    }
}

@Composable
private fun StockBadge(status: StockStatus, isTamil: Boolean) {
    val (bgColor, textColor) = when (status) {
        StockStatus.IN_STOCK -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        StockStatus.LIMITED_STOCK -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        StockStatus.OUT_OF_STOCK -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = bgColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Text(
                text = status.getDisplayName(if (isTamil) AppLanguage.TAMIL else AppLanguage.ENGLISH),
                style = MaterialTheme.typography.labelSmall,
                color = textColor,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun FertilizerCalculatorScreen(
    uiState: UzhavUiState,
    viewModel: UzhavViewModel,
    isTamil: Boolean
) {
    val cropsList = listOf("Paddy (நெல்)", "Sugarcane (கரும்பு)", "Cotton (பருத்தி)", "Maize (மக்காச்சோளம்)", "Groundnut (நிலக்கடலை)")
    var selectedCrop by remember { mutableStateOf(uiState.fertilizerCalcCrop) }
    var acresInput by remember { mutableStateOf(uiState.fertilizerCalcAcres.toString()) }
    var showCropDropdown by remember { mutableStateOf(false) }

    val requirement = uiState.fertilizerRequirement

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .testTag("fertilizer_calculator_container"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = if (isTamil) "நிலத்தின் பரப்பு & பயிர் தேவையை உள்ளிடவும்" else "Enter Farm Area & Crop",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    // Crop Selection Dropdown
                    Box {
                        OutlinedTextField(
                            value = selectedCrop,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (isTamil) "பயிர் வகை (Select Crop)" else "Crop Type") },
                            trailingIcon = {
                                IconButton(onClick = { showCropDropdown = true }) {
                                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { showCropDropdown = true }
                                .testTag("fertilizer_calc_crop_dropdown"),
                            shape = RoundedCornerShape(12.dp)
                        )

                        DropdownMenu(
                            expanded = showCropDropdown,
                            onDismissRequest = { showCropDropdown = false }
                        ) {
                            cropsList.forEach { crop ->
                                DropdownMenuItem(
                                    text = { Text(crop) },
                                    onClick = {
                                        selectedCrop = crop
                                        showCropDropdown = false
                                        val acres = acresInput.toDoubleOrNull() ?: 1.0
                                        viewModel.calculateFertilizerRequirement(crop, acres)
                                    }
                                )
                            }
                        }
                    }

                    // Acres Input
                    OutlinedTextField(
                        value = acresInput,
                        onValueChange = { input ->
                            acresInput = input
                            val acres = input.toDoubleOrNull() ?: 1.0
                            viewModel.calculateFertilizerRequirement(selectedCrop, acres)
                        },
                        label = { Text(if (isTamil) "நிலத்தின் அளவு (ஏக்கர் / Acres)" else "Land Size in Acres") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fertilizer_calc_acres_input"),
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.Landscape, contentDescription = null)
                        }
                    )

                    Button(
                        onClick = {
                            val acres = acresInput.toDoubleOrNull() ?: 1.0
                            viewModel.calculateFertilizerRequirement(selectedCrop, acres)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("fertilizer_calc_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Calculate, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isTamil) "உர மூட்டை & செலவு கணக்கிடு" else "Calculate Required Fertilizer")
                    }
                }
            }
        }

        // Calculation Results Card
        if (requirement != null) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("fertilizer_calc_result_card"),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Text(
                            text = if (isTamil)
                                "🎯 ${requirement.acres} ஏக்கர் ${requirement.cropName} பயிருக்கான உத்தேச தேவைகள்:"
                            else
                                "🎯 Estimated Fertilizer Needs for ${requirement.acres} Acres of ${requirement.cropName}:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )

                        // Requirement Bags Breakdown Grid
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            BagCountCard(
                                title = "Neem Urea (45kg)",
                                count = "${requirement.ureaBagsCount} Bags",
                                modifier = Modifier.weight(1f)
                            )
                            BagCountCard(
                                title = "DAP (50kg)",
                                count = "${requirement.dapBagsCount} Bags",
                                modifier = Modifier.weight(1f)
                            )
                            if (requirement.mopBagsCount > 0) {
                                BagCountCard(
                                    title = "MOP Potash",
                                    count = "${requirement.mopBagsCount} Bags",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                            if (requirement.sspBagsCount > 0) {
                                BagCountCard(
                                    title = "SSP Super",
                                    count = "${requirement.sspBagsCount} Bags",
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        HorizontalDivider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))

                        // Estimated Expenditure Summary
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isTamil) "அரசு மானிய மொத்த விலை:" else "Total Cost at Govt Rates:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "₹${requirement.totalEstimatedCostGovt}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (isTamil) "தனியார் சில்லறை உத்தேச விலை:" else "Est. Retail Market Cost:",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                                )
                                Text(
                                    text = "₹${requirement.totalEstimatedCostRetail}",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.9f)
                                )
                            }
                        }

                        // Application Schedule Tip
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = MaterialTheme.colorScheme.surface.copy(alpha = 0.85f)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.Top,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Lightbulb,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = if (isTamil) "உரமிடுதல் வழிகாட்டுதல் (Application Guidance):" else "Application Tip:",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                    Text(
                                        text = if (isTamil) requirement.applicationAdviceTamil else requirement.applicationAdviceEnglish,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BagCountCard(
    title: String,
    count: String,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(10.dp),
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = count,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
