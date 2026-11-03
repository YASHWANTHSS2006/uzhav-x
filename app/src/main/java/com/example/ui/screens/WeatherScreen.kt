package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.viewmodel.UzhavViewModel

@Composable
fun WeatherScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val weather = state.weatherReport
    val primaryColor = MaterialTheme.colorScheme.primary

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("weather_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudQueue,
                        contentDescription = "Weather",
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "வேளாண் வானிலை & மழை முன்னறிவிப்பு" else "Agricultural Weather & Monsoon Advisory",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = "${weather.district} District (${weather.taluka})",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Main Weather Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF2E7D32), Color(0xFF1B5E20))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${weather.currentTempC}°C",
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) weather.tamilCondition else weather.condition,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.WbSunny,
                                contentDescription = "Sun",
                                tint = Color(0xFFFFD54F),
                                modifier = Modifier.size(64.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Stats Row
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            WeatherStatItem(label = if (lang == AppLanguage.TAMIL) "ஈரப்பதம்" else "Humidity", value = "${weather.humidityPercent}%", icon = Icons.Default.WaterDrop)
                            WeatherStatItem(label = if (lang == AppLanguage.TAMIL) "மழை வாய்ப்பு" else "Rain Chance", value = "${weather.rainProbabilityPercent}%", icon = Icons.Default.Umbrella)
                            WeatherStatItem(label = if (lang == AppLanguage.TAMIL) "காற்று வேகம்" else "Wind", value = "${weather.windSpeedKmh} km/h", icon = Icons.Default.Air)
                            WeatherStatItem(label = if (lang == AppLanguage.TAMIL) "மண் ஈரப்பதம்" else "Soil Moisture", value = weather.soilMoistureLevel, icon = Icons.Default.Grass)
                        }
                    }
                }
            }
        }

        // TNAU Scientific Advisory
        item {
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFFFF3E0),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
            ) {
                Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Advisory",
                        tint = Color(0xFFE65100),
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பருவநிலை சார்ந்த வேளாண் ஆலோசனை:" else "Weather-based Farm Advisory:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFE65100)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) weather.currentAdvisoryTamil else weather.currentAdvisoryEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF3E2723)
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = if (lang == AppLanguage.TAMIL) "7 நாட்கள் வானிலை & சாகுபடி முன்னறிவிப்பு" else "7-Day Farm Forecast & Spraying Advice",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // 7-day forecast items
        items(weather.forecast7Days) { day ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1.2f)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) day.tamilDay else day.dayName,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) day.tamilCondition else day.condition,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) day.tamilAdvisoryNote else day.advisoryNote,
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFFE1F5FE)
                        ) {
                            Text(
                                text = "🌧 ${day.rainProbPercent}%",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF0277BD),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "${day.tempMax}° / ${day.tempMin}°",
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeatherStatItem(
    label: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(imageVector = icon, contentDescription = label, tint = Color.White.copy(alpha = 0.85f), modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.height(2.dp))
        Text(text = value, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold, color = Color.White)
        Text(text = label, style = MaterialTheme.typography.labelSmall, fontSize = 9.sp, color = Color.White.copy(alpha = 0.7f))
    }
}
