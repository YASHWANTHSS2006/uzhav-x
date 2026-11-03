package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MandiPriceService
import com.example.model.AppLanguage
import com.example.model.MandiPrice
import com.example.model.RegionalMandiComparison
import com.example.ui.components.PriceChartCanvas
import com.example.viewmodel.UzhavViewModel
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.data.SpeechRecognitionHelper

private enum class DetailViewTab {
    TREND_CHART,
    REGIONAL_COMPARISON
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MandiPricesScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    val context = LocalContext.current
    var isListeningVoice by remember { mutableStateOf(false) }
    val speechHelper = remember(lang) {
        SpeechRecognitionHelper(
            context = context,
            languageCode = if (lang == AppLanguage.TAMIL) "ta-IN" else "en-IN",
            onResult = { spokenText ->
                viewModel.setMandiSearchQuery(spokenText)
            },
            onError = { err -> },
            onListeningChanged = { listening ->
                isListeningVoice = listening
            }
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            speechHelper.startListening()
        }
    }

    var selectedDetailTab by remember { mutableStateOf(DetailViewTab.TREND_CHART) }
    var showExportDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "Grains", "Vegetables", "Spices", "Fruits", "Cash Crops", "Oilseeds", "Pulses", "Plantation")
    val regions = MandiPriceService.ALL_REGIONS

    val filteredPrices = state.mandiPrices.filter { price ->
        val matchesCategory = state.selectedMandiCategory == "All" || price.category.equals(state.selectedMandiCategory, ignoreCase = true)
        val matchesRegion = state.selectedMandiRegion == "All Regions" || price.district.equals(state.selectedMandiRegion, ignoreCase = true)
        val matchesSearch = state.mandiSearchQuery.isBlank() ||
                price.cropName.contains(state.mandiSearchQuery, ignoreCase = true) ||
                price.tamilName.contains(state.mandiSearchQuery, ignoreCase = true) ||
                price.marketName.contains(state.mandiSearchQuery, ignoreCase = true) ||
                price.district.contains(state.mandiSearchQuery, ignoreCase = true)
        matchesCategory && matchesRegion && matchesSearch
    }

    val rotationTransition = rememberInfiniteTransition(label = "refresh_spin")
    val spinAngle by rotationTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "spin_angle"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("mandi_prices_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & Refresh Bar
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.weight(1f)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = primaryColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.TrendingUp,
                                    contentDescription = "Mandi Prices",
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "தினசரி மண்டி விலை நிலவரம்" else "Daily Mandi Market Prices",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(7.dp)
                                        .clip(CircleShape)
                                        .background(if (state.isMandiLoading) Color(0xFFFFA000) else Color(0xFF2E7D32))
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Text(
                                    text = if (state.isMandiLoading)
                                        (if (lang == AppLanguage.TAMIL) "விலைகள் புதுப்பிக்கப்படுகின்றன..." else "Updating live prices...")
                                    else
                                        "${if (lang == AppLanguage.TAMIL) "இறுதி புதுப்பிப்பு: " else "Last updated: "}${state.mandiLastFetchedTime}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                                if (state.isMandiRoomCached) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Surface(
                                        color = Color(0xFFE8F5E9),
                                        shape = RoundedCornerShape(6.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Storage,
                                                contentDescription = "Room Cache",
                                                tint = Color(0xFF2E7D32),
                                                modifier = Modifier.size(11.dp)
                                            )
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Text(
                                                text = if (lang == AppLanguage.TAMIL) "Room ஆஃப்லைன் கேச்" else "Room DB Cached",
                                                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                                                color = Color(0xFF1B5E20),
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Action Buttons (Export + Refresh)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .testTag("mandi_export_btn")
                                .size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export Prices PDF/CSV"
                            )
                        }

                        FilledTonalIconButton(
                            onClick = { viewModel.refreshMandiPrices() },
                            enabled = !state.isMandiLoading,
                            modifier = Modifier
                                .testTag("mandi_refresh_btn")
                                .size(42.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Mandi Prices",
                                modifier = Modifier.rotate(if (state.isMandiLoading) spinAngle else 0f)
                            )
                        }
                    }
                }
            }
        }

        // Animated Loading Progress Bar
        if (state.isMandiLoading) {
            item {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .testTag("mandi_loading_indicator"),
                    color = primaryColor
                )
            }
        }

        // Regional Market Overview & Today's Highlights Banner
        state.mandiRegionalSummary?.let { summary ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_regional_summary_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                    ),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Analytics,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "தமிழக சந்தை ஆய்வு & நிலவரம்" else "TN Regional Market Overview",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${summary.totalCommodities} ${if (lang == AppLanguage.TAMIL) "பயிர்கள்" else "Crops"}",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = primaryColor,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Top Gainer Pill
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "அதிக ஏற்றம்" else "Top Gainer",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF2E7D32),
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = summary.topGainerCrop,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1B5E20)
                                    )
                                    Text(
                                        text = "${summary.topGainerGain} • ${summary.topGainerMarket.substringBefore(" ")}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF2E7D32)
                                    )
                                }
                            }

                            // High Arrival Pill
                            Surface(
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "அதிக வரத்து மண்டி" else "Peak Arrival Hub",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = MaterialTheme.colorScheme.outline,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Text(
                                        text = summary.topArrivalMarket.substringBefore("Market").substringBefore("Yard").trim(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = summary.topArrivalVolume,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) summary.stateMarketStatusTamil else summary.stateMarketStatusEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }

        // Search Bar with Google Search Grounding
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = state.mandiSearchQuery,
                    onValueChange = { viewModel.setMandiSearchQuery(it) },
                    placeholder = {
                        Text(
                            if (lang == AppLanguage.TAMIL)
                                "பயிர் அல்லது மண்டி தேடுக (எ.கா: நெல், தக்காளி, ஈரோடு)"
                            else
                                "Search crop or mandi (e.g. Paddy, Tomato, Erode)"
                        )
                    },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search Mandis") },
                    trailingIcon = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(
                                onClick = {
                                    permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                },
                                modifier = Modifier.testTag("mandi_voice_search_btn")
                            ) {
                                Icon(
                                    imageVector = if (isListeningVoice) Icons.Default.Mic else Icons.Default.MicNone,
                                    contentDescription = "Voice Search",
                                    tint = if (isListeningVoice) Color(0xFFD32F2F) else primaryColor
                                )
                            }
                            if (state.mandiSearchQuery.isNotBlank()) {
                                IconButton(onClick = { viewModel.setMandiSearchQuery("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        }
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_search_input"),
                    shape = RoundedCornerShape(12.dp)
                )

                if (isListeningVoice) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color(0xFF2E7D32))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பேசுங்கள்... பயிர் அல்லது மண்டி பெயரைச் சொல்லவும்" else "Listening... Speak crop or market name",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }
                }

                // Google Search Grounding Button Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    FilledTonalButton(
                        onClick = { viewModel.searchLiveMandiWithGoogle() },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("google_search_grounding_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.TravelExplore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = primaryColor
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "Google நேரடி சந்தை தேடல்" else "Google Search Grounding (Live)",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = primaryColor.copy(alpha = 0.1f)
                    ) {
                        Text(
                            text = "gemini-3.8-flash",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                // Quick Search Suggestion Pills
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.testTag("mandi_quick_search_pills")
                ) {
                    val quickPills = listOf(
                        "🌾 நெல்" to "Paddy",
                        "🟡 மஞ்சள்" to "Turmeric",
                        "🍅 தக்காளி" to "Tomato",
                        "🧅 வெங்காயம்" to "Small Onion",
                        "🌿 ஏலக்காய்" to "Cardamom",
                        "🥥 கொப்பரை" to "Copra",
                        "⚪ பருத்தி" to "Cotton",
                        "🍌 வாழை" to "Banana"
                    )
                    items(quickPills) { (tamilLabel, englishQuery) ->
                        AssistChip(
                            onClick = {
                                viewModel.setMandiSearchQuery(englishQuery)
                                viewModel.searchLiveMandiWithGoogle(englishQuery)
                            },
                            label = { Text(if (lang == AppLanguage.TAMIL) tamilLabel else englishQuery, fontSize = 11.sp) },
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("mandi_pill_${englishQuery.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Active Google Search Grounding Report / Loading State
        if (state.isMandiSearchGrounding || state.mandiGroundedSearchResult != null) {
            item {
                MandiGoogleGroundedCard(
                    isLoading = state.isMandiSearchGrounding,
                    result = state.mandiGroundedSearchResult,
                    language = lang,
                    onDismiss = { viewModel.dismissMandiGroundedSearch() }
                )
            }
        }

        // Regional District Filter Chips (Kongu, Delta, South, etc.)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "மாவட்ட மண்டிகள் (வட்டாரம்)" else "Regional Districts",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("mandi_region_chips_row")
                ) {
                    items(regions) { reg ->
                        val isSelected = state.selectedMandiRegion == reg
                        val labelText = if (reg == "All Regions") {
                            if (lang == AppLanguage.TAMIL) "அனைத்து மண்டிகள்" else "All Regions"
                        } else {
                            reg
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setMandiRegion(reg) },
                            label = { Text(labelText) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) }
                            } else null,
                            modifier = Modifier.testTag("mandi_region_chip_${reg.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Crop Category Filter Chips
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "பயிர் வகைகள்" else "Crop Category",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.outline
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.testTag("mandi_category_chips_row")
                ) {
                    items(categories) { cat ->
                        val isSelected = state.selectedMandiCategory == cat
                        val displayLabel = when (cat) {
                            "All" -> if (lang == AppLanguage.TAMIL) "அனைத்தும்" else "All"
                            "Grains" -> if (lang == AppLanguage.TAMIL) "தானியங்கள்" else "Grains"
                            "Vegetables" -> if (lang == AppLanguage.TAMIL) "காய்கறிகள்" else "Vegetables"
                            "Spices" -> if (lang == AppLanguage.TAMIL) "மசாலா / மஞ்சள்" else "Spices"
                            "Fruits" -> if (lang == AppLanguage.TAMIL) "பழங்கள்" else "Fruits"
                            "Cash Crops" -> if (lang == AppLanguage.TAMIL) "பணப்பயிர்கள்" else "Cash Crops"
                            "Oilseeds" -> if (lang == AppLanguage.TAMIL) "எண்ணெய் வித்துக்கள்" else "Oilseeds"
                            "Pulses" -> if (lang == AppLanguage.TAMIL) "பருப்பு வகைகள்" else "Pulses"
                            "Plantation" -> if (lang == AppLanguage.TAMIL) "தோட்டப்பயிர்கள்" else "Plantation"
                            else -> cat
                        }
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setMandiCategory(cat) },
                            label = { Text(displayLabel) },
                            modifier = Modifier.testTag("mandi_cat_chip_${cat.lowercase().replace(" ", "_")}")
                        )
                    }
                }
            }
        }

        // Selected Crop Trend & Regional Comparison Section
        state.selectedMandiPriceForDetail?.let { detail ->
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("mandi_detail_section_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        // Header with selected crop name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) detail.tamilName else detail.cropName,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = "${detail.marketName} • ${detail.district}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "₹${detail.modalPrice}",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                                Text(
                                    text = detail.unit,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Segmented Tab for Trend vs Regional Comparison
                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selectedDetailTab == DetailViewTab.TREND_CHART) primaryColor else Color.Transparent)
                                        .clickable { selectedDetailTab = DetailViewTab.TREND_CHART }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.ShowChart,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (selectedDetailTab == DetailViewTab.TREND_CHART) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) "விலை வரைபடம்" else "7-Day Trend",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedDetailTab == DetailViewTab.TREND_CHART) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(16.dp))
                                        .background(if (selectedDetailTab == DetailViewTab.REGIONAL_COMPARISON) primaryColor else Color.Transparent)
                                        .clickable { selectedDetailTab = DetailViewTab.REGIONAL_COMPARISON }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.CompareArrows,
                                            contentDescription = null,
                                            modifier = Modifier.size(16.dp),
                                            tint = if (selectedDetailTab == DetailViewTab.REGIONAL_COMPARISON) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) "மண்டிகள் ஒப்பீடு" else "Compare Mandis",
                                            style = MaterialTheme.typography.labelMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (selectedDetailTab == DetailViewTab.REGIONAL_COMPARISON) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Tab 1: 7-Day Trend Chart & Stats
                        if (selectedDetailTab == DetailViewTab.TREND_CHART) {
                            PriceChartCanvas(
                                priceHistory = detail.priceHistory7Days,
                                cropName = if (lang == AppLanguage.TAMIL) detail.tamilName else detail.cropName,
                                unit = detail.unit,
                                language = lang
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            // Market Metric pills
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                MetricBox(
                                    title = if (lang == AppLanguage.TAMIL) "குறைந்தபட்சம்" else "Min Price",
                                    value = "₹${detail.minPrice}",
                                    color = MaterialTheme.colorScheme.outline
                                )
                                MetricBox(
                                    title = if (lang == AppLanguage.TAMIL) "சராசரி விலை" else "Modal Price",
                                    value = "₹${detail.modalPrice}",
                                    color = primaryColor,
                                    isBold = true
                                )
                                MetricBox(
                                    title = if (lang == AppLanguage.TAMIL) "அதிகபட்சம்" else "Max Price",
                                    value = "₹${detail.maxPrice}",
                                    color = MaterialTheme.colorScheme.outline
                                )
                                MetricBox(
                                    title = if (lang == AppLanguage.TAMIL) "வரத்து அளவு" else "Arrival Volume",
                                    value = detail.arrivalVolume,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                            }
                        } else {
                            // Tab 2: Regional Mandi Comparison
                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL)
                                        "தமிழகத்தின் பிற மண்டிகளில் இந்த பயிரின் தற்போதைய விலை ஒப்பீடு:"
                                    else
                                        "Current price comparison across other Tamil Nadu mandis:",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )

                                if (detail.regionalComparisons.isNotEmpty()) {
                                    detail.regionalComparisons.forEach { item ->
                                        RegionalComparisonRow(item = item, lang = lang)
                                    }
                                } else {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "இந்த பயிருக்கு பிற மண்டிகள் தரவு தயாராகிறது." else "Regional mandi comparison being compiled for this crop.",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.outline
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section Title: Commodities Feed
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "அனைத்து பயிர்கள் & விலை விவரங்கள்" else "Market Arrivals & Prices",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${filteredPrices.size} ${if (lang == AppLanguage.TAMIL) "பொருட்கள்" else "items"}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Empty state when search or filter returns zero results
        if (filteredPrices.isEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
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
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பொருட்கள் எதுவும் கிடைக்கவில்லை" else "No commodities found",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "உங்கள் தேடலை மாற்றவும் அல்லது வேறு மாவட்டத்தை தேர்ந்தெடுக்கவும்." else "Try clearing your search query or choosing another district.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(top = 4.dp)
                        ) {
                            if (state.mandiSearchQuery.isNotBlank()) {
                                FilledTonalButton(
                                    onClick = { viewModel.searchLiveMandiWithGoogle(state.mandiSearchQuery) },
                                    modifier = Modifier.testTag("empty_state_google_search_btn")
                                ) {
                                    Icon(Icons.Default.TravelExplore, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        if (lang == AppLanguage.TAMIL) "Google-ல் தேடு (Live)" else "Google Search (Live)"
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    viewModel.setMandiSearchQuery("")
                                    viewModel.setMandiCategory("All")
                                    viewModel.setMandiRegion("All Regions")
                                }
                            ) {
                                Text(if (lang == AppLanguage.TAMIL) "அனைத்தையும் காட்டு" else "Reset Filters")
                            }
                        }
                    }
                }
            }
        }

        // List of Mandi Commodity Cards
        items(filteredPrices, key = { it.id }) { price ->
            val isSelected = price.id == state.selectedMandiPriceForDetail?.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mandi_card_${price.id}")
                    .clickable { viewModel.selectMandiPriceForDetail(price) },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) primaryColor.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface
                ),
                border = if (isSelected) BorderStroke(1.5.dp, primaryColor) else BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.4f)),
                elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 3.dp else 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) price.tamilName else price.cropName,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Surface(
                                    shape = CircleShape,
                                    color = primaryColor.copy(alpha = 0.15f)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Visibility,
                                        contentDescription = "Active",
                                        tint = primaryColor,
                                        modifier = Modifier
                                            .size(16.dp)
                                            .padding(2.dp)
                                    )
                                }
                            }
                        }
                        Text(
                            text = "${price.marketName} • ${price.district}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${price.lastUpdated} • ${if (lang == AppLanguage.TAMIL) "வரத்து" else "Arrivals"}: ${price.arrivalVolume}",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.85f)
                            )
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "₹${price.modalPrice}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                        Text(
                            text = price.unit,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (price.isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                        ) {
                            Text(
                                text = "${if (price.isPositive) "▲ +" else "▼ "}${price.changeAmount}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (price.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showExportDialog) {
        com.example.ui.components.ExportDialog(
            language = lang,
            exportType = com.example.ui.components.ExportType.MANDI_PRICES,
            mandiPrices = filteredPrices,
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
private fun MetricBox(
    title: String,
    value: String,
    color: Color,
    isBold: Boolean = false
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outline
        )
        Text(
            text = value,
            style = if (isBold) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            fontWeight = if (isBold) FontWeight.ExtraBold else FontWeight.Bold,
            color = color
        )
    }
}

@Composable
private fun RegionalComparisonRow(
    item: RegionalMandiComparison,
    lang: AppLanguage
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        color = if (item.isHighestPrice) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f),
        border = if (item.isHighestPrice) BorderStroke(1.dp, Color(0xFF81C784)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = item.marketName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (item.isHighestPrice) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                    )
                    if (item.isHighestPrice) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color(0xFF2E7D32)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "அதிக விலை" else "Best Price",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
                Text(
                    text = "${item.district} • ${if (lang == AppLanguage.TAMIL) "வரத்து" else "Arrivals"}: ${item.arrivalVolume}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (item.isHighestPrice) Color(0xFF388E3C) else MaterialTheme.colorScheme.outline
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₹${item.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = if (item.isHighestPrice) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${if (item.isPositive) "▲ +" else "▼ "}${item.changeAmount}",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = if (item.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
            }
        }
    }
}

@Composable
fun MandiGoogleGroundedCard(
    isLoading: Boolean,
    result: com.example.model.MandiSearchGroundingResult?,
    language: AppLanguage,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("mandi_grounded_search_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, primaryColor.copy(alpha = 0.5f)),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Google Brand Strip Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.5.dp)
                    .clip(RoundedCornerShape(2.dp))
            ) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF4285F4))) // Google Blue
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFEA4335))) // Google Red
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFFFBBC05))) // Google Yellow
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF34A853))) // Google Green
            }

            // Header: Title, Model Badge, and Close Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TravelExplore,
                        contentDescription = "Google Search Grounding",
                        tint = Color(0xFF1A73E8),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "Google நேரடி சந்தை ஆய்வு" else "Google Grounded Market Report",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "gemini-3.8-flash (googleSearch tool)",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                if (!isLoading) {
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("dismiss_grounded_search_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }

            if (isLoading) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 14.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(30.dp),
                        strokeWidth = 3.dp,
                        color = Color(0xFF1A73E8)
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL)
                            "தமிழக சந்தைகளின் நேரடி விலைகளை Google Search மூலம் சேகரிக்கிறது..."
                        else
                            "Grounding real-time Tamil Nadu mandi prices with Google Search...",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        fontWeight = FontWeight.Medium
                    )
                }
            } else if (result != null) {
                // Key metrics row: Price & Trend
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Estimated Price Range
                    Surface(
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFE8F5E9)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (language == AppLanguage.TAMIL) "நேரடி சந்தை விலை" else "Estimated Live Rate",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = result.estimatedPriceRange,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                    }

                    // Market Trend
                    Surface(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        color = if (result.marketTrend.contains("Bullish") || result.marketTrend.contains("ஏற்றம்"))
                            Color(0xFFE8F5E9)
                        else if (result.marketTrend.contains("Bearish") || result.marketTrend.contains("இறக்கம்"))
                            Color(0xFFFFEBEE)
                        else
                            Color(0xFFFFF3E0)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (language == AppLanguage.TAMIL) "சந்தை போக்கு" else "Market Trend",
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = result.marketTrend,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = if (result.marketTrend.contains("Bullish") || result.marketTrend.contains("ஏற்றம்"))
                                    Color(0xFF1B5E20)
                                else if (result.marketTrend.contains("Bearish") || result.marketTrend.contains("இறக்கம்"))
                                    Color(0xFFC62828)
                                else
                                    Color(0xFFE65100)
                            )
                        }
                    }
                }

                // Top Trading Mandis in TN
                if (result.topMarkets.isNotEmpty()) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "முக்கிய வர்த்தக மண்டிகள்:" else "Leading Tamil Nadu Mandis:",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            result.topMarkets.take(3).forEach { market ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = market,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Summary content in farmer's active language
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) result.summaryTamil else result.summaryEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                // Grounding Citations & Sources Footer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Verified Grounding",
                            tint = Color(0xFF34A853),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Grounded via Google Search • ${result.timestamp}",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}

