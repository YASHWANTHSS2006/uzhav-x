package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
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
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.IrrigationScheduleRequest
import com.example.viewmodel.UzhavUiState
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IrrigationSchedulerScreen(
    viewModel: UzhavViewModel,
    state: UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary
    val req = state.irrigationRequest
    val result = state.irrigationResult

    val cropOptions = listOf(
        "Paddy (நெல்)",
        "Sugarcane (கரும்பு)",
        "Banana (வாழை)",
        "Groundnut (நிலக்கடலை)",
        "Cotton (பருத்தி)",
        "Vegetables (காய்கறிகள்)",
        "Coconut (தென்னை)",
        "Maize (சோளம்)"
    )

    val soilTypeOptions = listOf(
        "Alluvial Loam (வண்டல் மண்)",
        "Black Clay (களிமண்)",
        "Red Soil (செம்மண்)",
        "Sandy Soil (மணல் மண்)"
    )

    val growthStageOptions = listOf(
        "Vegetative (வளர்ச்சி பருவம்)",
        "Flowering & Grain Filling (பூக்கும் & காய் பருவம்)",
        "Maturation / Pre-harvest (அறுவடை முன் பருவம்)"
    )

    val irrigationMethodOptions = listOf(
        "Drip Irrigation (சொட்டு நீர் பாசனம்)",
        "Sprinkler System (தெளிப்பு நீர்)",
        "Traditional Flood (வாய்க்கால் பாசனம்)"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("irrigation_scheduler_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Header Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("irrigation_hero_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF0277BD),
                                    Color(0xFF00838F),
                                    Color(0xFF00695C)
                                )
                            )
                        )
                        .padding(18.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(48.dp)
                                    .background(Color.White.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.WaterDrop,
                                    contentDescription = "Water Saver",
                                    tint = Color.White,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.White.copy(alpha = 0.25f)
                                ) {
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "AI நீர் மேலாண்மை கருவி" else "AI Smart Irrigation Tool",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                    )
                                }
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "துல்லிய பாசன திட்டமிடுபவர்" else "Precision Water Scheduler",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL)
                                "உங்கள் பயிர் வகை மற்றும் மண் ஈரப்பதத்தை செலுத்தி AI மூலம் துல்லியமான பாசன அளவு, நேரம் மற்றும் நீர் சேமிப்பு ஆலோசனையைப் பெறுங்கள்."
                            else
                                "Input crop type and local soil moisture level to calculate exact water needs, optimal watering windows, and water savings.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }
            }
        }

        // 2. Input Form Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("irrigation_input_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "1. பயிர் & மண் விபரம்" else "1. Crop & Field Details",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    // Crop Selection Chips
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பயிர் வகை (Crop Type):" else "Select Crop Type:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.testTag("irrigation_crop_selector")
                        ) {
                            items(cropOptions) { crop ->
                                val selected = req.cropType == crop
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateIrrigationRequest(req.copy(cropType = crop)) },
                                    label = { Text(crop, fontSize = 12.sp) },
                                    leadingIcon = if (selected) {
                                        { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp)) }
                                    } else null,
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primaryContainer
                                    )
                                )
                            }
                        }
                    }

                    Divider()

                    // Soil Moisture Slider & Gauge
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "மண் ஈரப்பதம் (Soil Moisture):" else "Soil Moisture Level:",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(req.getMoistureGaugeColor()).copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "${req.soilMoisturePercent.toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(req.getMoistureGaugeColor()),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = req.getMoistureCategoryLabel(lang),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = Color(req.getMoistureGaugeColor())
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Moisture Meter Progress Indicator
                        LinearProgressIndicator(
                            progress = req.soilMoisturePercent / 100f,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(10.dp)
                                .clip(CircleShape),
                            color = Color(req.getMoistureGaugeColor()),
                            trackColor = MaterialTheme.colorScheme.outlineVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Interactive Slider
                        Slider(
                            value = req.soilMoisturePercent,
                            onValueChange = { viewModel.updateIrrigationRequest(req.copy(soilMoisturePercent = it)) },
                            valueRange = 5f..90f,
                            steps = 16,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("irrigation_moisture_slider")
                        )

                        // Quick Preset Moisture Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            AssistChip(
                                onClick = { viewModel.updateIrrigationRequest(req.copy(soilMoisturePercent = 15f)) },
                                label = { Text(if (lang == AppLanguage.TAMIL) "வறண்டது 15%" else "Dry 15%", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preset_dry_button")
                            )
                            AssistChip(
                                onClick = { viewModel.updateIrrigationRequest(req.copy(soilMoisturePercent = 32f)) },
                                label = { Text(if (lang == AppLanguage.TAMIL) "மிதமான 32%" else "Moderate 32%", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preset_moderate_button")
                            )
                            AssistChip(
                                onClick = { viewModel.updateIrrigationRequest(req.copy(soilMoisturePercent = 52f)) },
                                label = { Text(if (lang == AppLanguage.TAMIL) "உகந்த 52%" else "Optimal 52%", fontSize = 11.sp) },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("preset_optimal_button")
                            )
                        }
                    }

                    Divider()

                    // Soil Type & Method Selection
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மண் வகை (Soil Type):" else "Soil Type:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.testTag("irrigation_soil_type_chips")
                        ) {
                            items(soilTypeOptions) { soil ->
                                val selected = req.soilType == soil
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateIrrigationRequest(req.copy(soilType = soil)) },
                                    label = { Text(soil, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    Column {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பாசன முறை (Irrigation System):" else "Irrigation Method:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.testTag("irrigation_method_chips")
                        ) {
                            items(irrigationMethodOptions) { method ->
                                val selected = req.irrigationMethod == method
                                FilterChip(
                                    selected = selected,
                                    onClick = { viewModel.updateIrrigationRequest(req.copy(irrigationMethod = method)) },
                                    label = { Text(method, fontSize = 12.sp) }
                                )
                            }
                        }
                    }

                    // Land Area & Temp Input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = req.landAreaAcres.toString(),
                            onValueChange = { input ->
                                val acres = input.toDoubleOrNull() ?: 1.0
                                viewModel.updateIrrigationRequest(req.copy(landAreaAcres = acres))
                            },
                            label = { Text(if (lang == AppLanguage.TAMIL) "நில அளவு (ஏக்கர்)" else "Land Area (Acres)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("irrigation_acres_input")
                        )
                        OutlinedTextField(
                            value = "${req.ambientTempC}°C",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == AppLanguage.TAMIL) "வெப்பநிலை" else "Temp") },
                            modifier = Modifier.weight(0.8f)
                        )
                    }

                    // Action Button
                    Button(
                        onClick = { viewModel.calculateIrrigationSchedule() },
                        enabled = !state.isCalculatingIrrigation,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("calculate_irrigation_button"),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        if (state.isCalculatingIrrigation) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = Color.White,
                                strokeWidth = 2.5.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = if (lang == AppLanguage.TAMIL) "AI கணக்கிடுகிறது..." else "Calculating AI Schedule...")
                        } else {
                            Icon(Icons.Default.Calculate, contentDescription = null)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பாசன அட்டவணை கணக்கிடுக (Calculate Schedule)" else "Calculate Water Schedule",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // 3. AI Recommendation Results Display
        result?.let { rec ->
            item {
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn() + slideInVertically()
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("irrigation_result_card"),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                        border = androidx.compose.foundation.BorderStroke(1.5.dp, MaterialTheme.colorScheme.primary)
                    ) {
                        Column(
                            modifier = Modifier.padding(18.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            // Water Savings Hero Badge
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color(0xFF1B5E20)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Savings,
                                        contentDescription = "Water Saved",
                                        tint = Color(0xFFFFD54F),
                                        modifier = Modifier.size(28.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL)
                                                "💧 ${rec.waterSavingsPercentage}% நீர் சேமிப்பு திட்டம்"
                                            else
                                                "💧 ${rec.waterSavingsPercentage}% Water Savings Efficiency",
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) "பாரம்பரிய பாசனத்தை விட சேமிக்கப்படும் நீர் அளவு" else "Saved compared to traditional flood irrigation",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.White.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }

                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பரிந்துரைக்கப்பட்ட பாசன அட்டவணை" else "Recommended Irrigation Schedule",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )

                            // Key Metrics Grid
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = if (lang == AppLanguage.TAMIL) "அடுத்த நேரம்" else "Next Window",
                                    value = rec.nextIrrigationDate,
                                    icon = Icons.Default.Schedule,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = if (lang == AppLanguage.TAMIL) "தேவையான நீர்" else "Water Volume",
                                    value = "${rec.totalWaterLiters} Liters",
                                    icon = Icons.Default.Water,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                MetricCard(
                                    title = if (lang == AppLanguage.TAMIL) "பாசன நேரம்" else "Duration",
                                    value = "${rec.durationMinutes} Mins",
                                    icon = Icons.Default.Timer,
                                    modifier = Modifier.weight(1f)
                                )
                                MetricCard(
                                    title = if (lang == AppLanguage.TAMIL) "உகந்த நேரம்" else "Best Hours",
                                    value = rec.optimalTimeOfDay.substringBefore(" ("),
                                    icon = Icons.Default.WbSunny,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Divider()

                            // AI Advice Text
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "உழவன் AI வேளாண்மை ஆலோசனை:" else "Uzhav AI Irrigation Advisory:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) rec.aiAdviceTamil else rec.aiAdviceEnglish,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(12.dp)
                                )
                            }

                            // Steps List
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "வயல் சார்ந்த முக்கிய வழிமுறைகள்:" else "Field Action Steps:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )

                            val steps = if (lang == AppLanguage.TAMIL) rec.actionStepsTamil else rec.actionStepsEnglish
                            steps.forEach { step ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier
                                            .size(18.dp)
                                            .padding(top = 2.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = step,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))

                            // Action Button to Add to Task Schedule
                            OutlinedButton(
                                onClick = {
                                    viewModel.addIrrigationTaskToReminders(rec)
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("add_irrigation_to_schedule_button"),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Event, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "பயிர் அட்டவணையில் பாசனப் பணி சேர்க்க" else "Add Water Schedule to Farm Calendar",
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Text(
                    text = value,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
