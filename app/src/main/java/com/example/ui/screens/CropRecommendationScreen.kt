package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.SoilHealthCard
import com.example.ui.components.SoilTestDialog
import com.example.viewmodel.UzhavViewModel

enum class CropSortFilter {
    ALL,
    HIGHEST_PROFIT,
    SHORT_DURATION,
    LOW_WATER,
    LOW_RISK
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CropRecommendationScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val req = state.recommendationRequest
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    var districtExpanded by remember { mutableStateOf(false) }
    var soilExpanded by remember { mutableStateOf(false) }
    var seasonExpanded by remember { mutableStateOf(false) }
    var waterExpanded by remember { mutableStateOf(false) }

    var selectedFilter by remember { mutableStateOf(CropSortFilter.ALL) }
    var isInputExpanded by remember { mutableStateOf(true) }
    var showSoilTestDialog by remember { mutableStateOf(false) }

    val recResult = state.recommendationResult

    // Compute filtered list safely at the top level
    val displayedCrops = remember(recResult, selectedFilter) {
        val crops = recResult?.topCrops.orEmpty()
        when (selectedFilter) {
            CropSortFilter.ALL -> crops
            CropSortFilter.HIGHEST_PROFIT -> crops.sortedByDescending { it.estimatedNetProfitPerAcre }
            CropSortFilter.SHORT_DURATION -> crops.filter { it.durationDays <= 90 }
            CropSortFilter.LOW_WATER -> crops.filter { it.waterNeed.contains("Low", ignoreCase = true) }
            CropSortFilter.LOW_RISK -> crops.filter { it.riskFactor.contains("Low", ignoreCase = true) }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("crop_recommendation_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Screen Header
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.45f)
                ),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "Gemini AI Crop Advisor",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "AI பயிர் பரிந்துரை & லாபக் கணிப்பான்" else "AI Crop Recommendation Engine",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "Gemini 3.8 Flash AI & TNAU வழிகாட்டலில் மண், நீர், பருவம் மற்றும் நேரடி மண்டி விலைகள் பகுப்பாய்வு"
                            else "Powered by Gemini 3.8 Flash AI & TNAU Agrometeorological Engine",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }

        // Soil Health Card Item
        item {
            SoilHealthCard(
                soilTestResult = state.farmerProfile.soilTestResult,
                language = lang,
                onEditClicked = { showSoilTestDialog = true }
            )
        }

        // Form Inputs Card (Collapsible for easy viewing after calculation)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("crop_input_form_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header of Card with Collapse toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isInputExpanded = !isInputExpanded },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Landscape,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "நிலம், மண், நீர் & பருவம் உள்ளிடவும்" else "Farm, Soil, Water & Season Inputs",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(
                            onClick = { isInputExpanded = !isInputExpanded },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = if (isInputExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                contentDescription = "Toggle Input Section",
                                tint = primaryColor
                            )
                        }
                    }

                    AnimatedVisibility(visible = isInputExpanded) {
                        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                            // 1. District & State (Region input)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = "Tamil Nadu",
                                    onValueChange = {},
                                    readOnly = true,
                                    label = { Text(if (lang == AppLanguage.TAMIL) "மாநிலம்" else "State") },
                                    modifier = Modifier.weight(0.9f)
                                )

                                ExposedDropdownMenuBox(
                                    expanded = districtExpanded,
                                    onExpandedChange = { districtExpanded = it },
                                    modifier = Modifier.weight(1.3f)
                                ) {
                                    OutlinedTextField(
                                        value = req.district,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text(if (lang == AppLanguage.TAMIL) "மாவட்டம் (Region)" else "District (Region)") },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .testTag("district_dropdown_trigger")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = districtExpanded,
                                        onDismissRequest = { districtExpanded = false }
                                    ) {
                                        TAMIL_NADU_DISTRICTS.forEach { dist ->
                                            DropdownMenuItem(
                                                text = { Text(dist) },
                                                onClick = {
                                                    viewModel.updateRecommendationInput(district = dist)
                                                    districtExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // 2. Taluka & Village
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = req.taluka,
                                    onValueChange = { viewModel.updateRecommendationInput(taluka = it) },
                                    label = { Text(if (lang == AppLanguage.TAMIL) "தாலுகா / வட்டம்" else "Taluka / Block") },
                                    modifier = Modifier.weight(1f)
                                )
                                OutlinedTextField(
                                    value = req.village,
                                    onValueChange = { viewModel.updateRecommendationInput(village = it) },
                                    label = { Text(if (lang == AppLanguage.TAMIL) "கிராமம்" else "Village") },
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            // 3. Soil Type (Mandatory Parameter from User Request)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "1. நிலத்தின் மண் வகை (Soil Type):" else "1. Soil Type:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = soilExpanded,
                                    onExpandedChange = { soilExpanded = it },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = req.soilType,
                                        onValueChange = {},
                                        readOnly = true,
                                        leadingIcon = {
                                            Icon(Icons.Default.Grain, contentDescription = null, tint = primaryColor)
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                            .testTag("soil_type_dropdown")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = soilExpanded,
                                        onDismissRequest = { soilExpanded = false }
                                    ) {
                                        SOIL_TYPES.forEach { soil ->
                                            DropdownMenuItem(
                                                text = { Text(soil) },
                                                onClick = {
                                                    viewModel.updateRecommendationInput(soilType = soil)
                                                    soilExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // 4. Water Availability / Source (Mandatory Parameter)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "2. நீர் பாசன வசதி (Water Availability):" else "2. Water Availability / Irrigation:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = waterExpanded,
                                    onExpandedChange = { waterExpanded = it },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = req.waterSource,
                                        onValueChange = {},
                                        readOnly = true,
                                        leadingIcon = {
                                            Icon(Icons.Default.WaterDrop, contentDescription = null, tint = Color(0xFF0288D1))
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = waterExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                            .testTag("water_source_dropdown")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = waterExpanded,
                                        onDismissRequest = { waterExpanded = false }
                                    ) {
                                        WATER_SOURCES.forEach { water ->
                                            DropdownMenuItem(
                                                text = { Text(water) },
                                                onClick = {
                                                    viewModel.updateRecommendationInput(waterSource = water)
                                                    waterExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // 5. Season (Mandatory Parameter)
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "3. பயிர் சாகுபடி பருவம் (Season):" else "3. Sowing Season:",
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                ExposedDropdownMenuBox(
                                    expanded = seasonExpanded,
                                    onExpandedChange = { seasonExpanded = it },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    OutlinedTextField(
                                        value = req.season,
                                        onValueChange = {},
                                        readOnly = true,
                                        leadingIcon = {
                                            Icon(Icons.Default.WbSunny, contentDescription = null, tint = Color(0xFFE65100))
                                        },
                                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = seasonExpanded) },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .menuAnchor()
                                            .testTag("season_dropdown")
                                    )
                                    ExposedDropdownMenu(
                                        expanded = seasonExpanded,
                                        onDismissRequest = { seasonExpanded = false }
                                    ) {
                                        TAMIL_NADU_SEASONS.forEach { season ->
                                            DropdownMenuItem(
                                                text = { Text(season) },
                                                onClick = {
                                                    viewModel.updateRecommendationInput(season = season)
                                                    seasonExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            // 6. Farm Land Size in Acres (Interactive Slider & Buttons)
                            Column {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "4. நிலத்தின் பரப்பளவு (Land Size):" else "4. Farm Land Size:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = primaryColor.copy(alpha = 0.12f)
                                    ) {
                                        Text(
                                            text = "${req.landSizeAcres} ${if (lang == AppLanguage.TAMIL) "ஏக்கர்" else "Acres"}",
                                            style = MaterialTheme.typography.labelLarge,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = primaryColor,
                                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                        )
                                    }
                                }

                                Slider(
                                    value = req.landSizeAcres.toFloat(),
                                    onValueChange = {
                                        val rounded = (Math.round(it * 2) / 2.0).coerceIn(0.5, 25.0)
                                        viewModel.updateRecommendationInput(landSize = rounded)
                                    },
                                    valueRange = 0.5f..20.0f,
                                    steps = 38,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("land_size_slider")
                                )

                                // Quick acreage presets
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    listOf(1.0, 2.0, 3.0, 5.0, 10.0).forEach { acresVal ->
                                        val isSelected = req.landSizeAcres == acresVal
                                        FilterChip(
                                            selected = isSelected,
                                            onClick = { viewModel.updateRecommendationInput(landSize = acresVal) },
                                            label = { Text("${acresVal.toInt()} Ac") },
                                            modifier = Modifier.height(32.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // CTA: Run AI Recommendation
                    Button(
                        onClick = {
                            viewModel.calculateCropRecommendation()
                            isInputExpanded = false
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("run_crop_recommendation_btn"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                    ) {
                        if (state.isCalculatingRecommendation) {
                            CircularProgressIndicator(
                                color = Color.White,
                                modifier = Modifier.size(22.dp),
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "Gemini AI வேளாண் பகுப்பாய்வு நடக்கிறது..." else "Gemini AI Analyzing Profit Models...",
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(imageVector = Icons.Default.AutoGraph, contentDescription = "Run")
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "Gemini AI பயிர் பரிந்துரையை பெறுக" else "Get Gemini AI Crop Suggestions",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Recommendation Results Section
        recResult?.let { result ->
            // 1. Executive Summary & Top Farm Profit Banner
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("recommendation_summary_card"),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B5E20)),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.AutoAwesome,
                                    contentDescription = "Gemini AI",
                                    tint = Color(0xFFFFD54F),
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "Gemini AI முதன்மை தேர்வு" else "Gemini AI Top Recommendation",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFFFD54F)
                                )
                            }

                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFF2E7D32)
                            ) {
                                Text(
                                    text = "${result.topCrops.firstOrNull()?.matchScorePercent ?: 95}% Match",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }

                        Text(
                            text = if (lang == AppLanguage.TAMIL)
                                (result.topCrops.firstOrNull()?.tamilName ?: "மஞ்சள் / நெல்")
                            else
                                (result.topCrops.firstOrNull()?.cropName ?: "Turmeric / Paddy"),
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White
                        )

                        // Big Farm Total Projection
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF0F3D13)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL)
                                            "மொத்த பண்ணை நிகர லாபம் (${req.landSizeAcres} ஏக்):"
                                        else
                                            "Projected Total Farm Profit (${req.landSizeAcres} Ac):",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB9F6CA)
                                    )
                                    Text(
                                        text = "₹${String.format("%,d", result.totalEstimatedFarmProfit)}",
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color(0xFFFFD54F)
                                    )
                                }

                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "ஏக்கருக்கு:" else "Per Acre:",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = Color(0xFFB9F6CA)
                                    )
                                    Text(
                                        text = "₹${String.format("%,d", result.topCrops.firstOrNull()?.estimatedNetProfitPerAcre ?: 0)}",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }

                        Text(
                            text = if (lang == AppLanguage.TAMIL) result.generalAdviceTamil else result.generalAdviceEnglish,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color(0xFFE8F5E9)
                        )

                        Text(
                            text = result.climateSummary,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFA5D6A7)
                        )
                    }
                }
            }

            // 2. Visual Top Profit Comparison Bar
            if (result.topCrops.size >= 2) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.BarChart,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "பயிர்களின் நிகர லாப ஒப்பீடு (ஏக்கருக்கு)" else "Profitability Comparison (Net / Acre)",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val maxProfit = (result.topCrops.maxOfOrNull { it.estimatedNetProfitPerAcre } ?: 100000).toFloat()

                            result.topCrops.take(4).forEach { c ->
                                val ratio = (c.estimatedNetProfitPerAcre.toFloat() / maxProfit).coerceIn(0.15f, 1f)
                                Column(modifier = Modifier.padding(vertical = 4.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) c.tamilName.substringBefore("(") else c.cropName.substringBefore("("),
                                            style = MaterialTheme.typography.bodySmall,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "₹${c.estimatedNetProfitPerAcre}",
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color(0xFF1B5E20)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(MaterialTheme.colorScheme.surfaceVariant)
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth(ratio)
                                                .fillMaxHeight()
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    if (c == result.topCrops.firstOrNull()) Color(0xFF2E7D32)
                                                    else primaryColor
                                                )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. Filter & Sort Chips (Horizontal scroll row)
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பரிந்துரைக்கப்பட்ட பயிர்கள் (${displayedCrops.size})" else "Recommended Crops (${displayedCrops.size})",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == CropSortFilter.ALL,
                            onClick = { selectedFilter = CropSortFilter.ALL },
                            label = { Text(if (lang == AppLanguage.TAMIL) "அனைத்தும்" else "All") }
                        )
                        FilterChip(
                            selected = selectedFilter == CropSortFilter.HIGHEST_PROFIT,
                            onClick = { selectedFilter = CropSortFilter.HIGHEST_PROFIT },
                            label = { Text(if (lang == AppLanguage.TAMIL) "அதிக லாபம்" else "Highest Profit") }
                        )
                        FilterChip(
                            selected = selectedFilter == CropSortFilter.SHORT_DURATION,
                            onClick = { selectedFilter = CropSortFilter.SHORT_DURATION },
                            label = { Text(if (lang == AppLanguage.TAMIL) "குறுகிய காலம் (<90 நாட்கள்)" else "Short Duration") }
                        )
                        FilterChip(
                            selected = selectedFilter == CropSortFilter.LOW_WATER,
                            onClick = { selectedFilter = CropSortFilter.LOW_WATER },
                            label = { Text(if (lang == AppLanguage.TAMIL) "குறைந்த நீர் தேவை" else "Low Water Need") }
                        )
                        FilterChip(
                            selected = selectedFilter == CropSortFilter.LOW_RISK,
                            onClick = { selectedFilter = CropSortFilter.LOW_RISK },
                            label = { Text(if (lang == AppLanguage.TAMIL) "குறைந்த இடர்" else "Low Risk") }
                        )
                    }
                }
            }

            // Filtered Crops List
            if (displayedCrops.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "இந்த வடிகட்டலில் பயிர்கள் இல்லை" else "No crops match this filter",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            } else {
                items(displayedCrops) { crop ->
                    CropRecommendationCard(
                        crop = crop,
                        landSizeAcres = req.landSizeAcres,
                        language = lang
                    )
                }
            }
        }
    }

    if (showSoilTestDialog) {
        SoilTestDialog(
            currentResult = state.farmerProfile.soilTestResult,
            language = lang,
            onDismiss = { showSoilTestDialog = false },
            onSave = { updatedResult ->
                viewModel.updateSoilTestResult(updatedResult)
                showSoilTestDialog = false
            }
        )
    }
}

@Composable
fun CropRecommendationCard(
    crop: RecommendedCrop,
    landSizeAcres: Double,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    var expandedDetails by remember { mutableStateOf(false) }

    val totalFarmNet = crop.calculateTotalNetProfit(landSizeAcres)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { expandedDetails = !expandedDetails }
            .testTag("crop_card_${crop.cropName.replace(" ", "_").replace("/", "_")}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Crop name & Match Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) crop.tamilName else crop.cropName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Text(
                                text = crop.category,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSecondaryContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Text(
                            text = "• ${crop.durationDays} ${if (language == AppLanguage.TAMIL) "நாட்கள்" else "Days"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = when {
                        crop.matchScorePercent >= 92 -> Color(0xFFE8F5E9)
                        crop.matchScorePercent >= 80 -> Color(0xFFFFF8E1)
                        else -> MaterialTheme.colorScheme.surfaceVariant
                    }
                ) {
                    Text(
                        text = "${crop.matchScorePercent}% Match",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = when {
                            crop.matchScorePercent >= 92 -> Color(0xFF1B5E20)
                            crop.matchScorePercent >= 80 -> Color(0xFFE65100)
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Economics Highlights: Net Profit / Acre & Total Farm Profit
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Net Profit per acre
                Surface(
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFE8F5E9)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "நிகர லாபம் / ஏக்" else "Net Profit / Acre",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFF1B5E20),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${crop.estimatedNetProfitPerAcre}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }

                // Total Farm Net for farmer's acres
                Surface(
                    modifier = Modifier.weight(1.1f),
                    shape = RoundedCornerShape(10.dp),
                    color = Color(0xFFFFF3E0)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "${landSizeAcres} ஏக் பண்ணை லாபம்" else "Total ${landSizeAcres} Ac Profit",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color(0xFFE65100),
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₹${String.format("%,d", totalFarmNet)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFFE65100)
                        )
                    }
                }

                // ROI %
                Surface(
                    modifier = Modifier.weight(0.8f),
                    shape = RoundedCornerShape(10.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Text(
                            text = "ROI",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = "+${crop.roiPercent}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Gross Revenue & Cultivation Cost Sub-metrics
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "மொத்த வருவாய்: " else "Gross Rev: "} ₹${crop.estimatedGrossRevenue}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "சாகுபடி செலவு: " else "Cost: "} ₹${crop.costOfCultivationPerAcre}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Yield & Mandi Price
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "எதிர்பார்க்கப்படும் விளைச்சல்: " else "Est. Yield: "} ${crop.estimatedYieldQuintalPerAcre} Q/Acre",
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "மண்டி விலை: " else "Mandi Price: "} ₹${crop.marketPricePerQuintal}/Q",
                    style = MaterialTheme.typography.bodySmall,
                    color = secondaryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            // Expandable Technical Agronomy & TNAU Details
            AnimatedVisibility(visible = expandedDetails) {
                Column(
                    modifier = Modifier.padding(top = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

                    if (crop.recommendedVarieties.isNotBlank()) {
                        Text(
                            text = "${if (language == AppLanguage.TAMIL) "பரிந்துரைக்கப்பட்ட ரகங்கள்: " else "Recommended Varieties: "} ${crop.recommendedVarieties}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    if (crop.fertilizerSchedule.isNotBlank()) {
                        Text(
                            text = "${if (language == AppLanguage.TAMIL) "உர அட்டவணை: " else "Fertilizer Schedule: "} ${crop.fertilizerSchedule}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "நீர் தேவை: " else "Water Need: "} ${crop.waterNeed}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "சிறந்த விதைப்பு காலம்: " else "Best Sowing Window: "} ${crop.bestSowingWindow}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "மண் உகந்த தன்மை: " else "Soil Suitability: "} ${crop.suitableSoilSummary}",
                        style = MaterialTheme.typography.bodySmall
                    )

                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "சந்தை போக்கு & விலை காரணம்: " else "Market Driver: "} ${crop.marketTrendReasoning}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Text(
                        text = if (language == AppLanguage.TAMIL) "TNAU வேளாண் பல்கலைக்கழக வழிகாட்டுதல்கள்:" else "TNAU Agronomy Guidance:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    crop.tnauexpertTips.forEach { tip ->
                        Row(modifier = Modifier.padding(start = 4.dp)) {
                            Text("• ", color = primaryColor, fontWeight = FontWeight.Bold)
                            Text(text = tip, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${if (language == AppLanguage.TAMIL) "இடர் நிலை: " else "Risk Rating: "} ${crop.riskFactor}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (crop.riskFactor.contains("Low", ignoreCase = true)) Color(0xFF2E7D32) else Color(0xFFE65100)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (expandedDetails) (if (language == AppLanguage.TAMIL) "விவரங்களை சுருக்குக ▲" else "Collapse Details ▲")
                    else (if (language == AppLanguage.TAMIL) "TNAU வழிகாட்டல் & விவரங்கள் ▼" else "TNAU Tips & Details ▼"),
                    style = MaterialTheme.typography.labelSmall,
                    color = primaryColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
