package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.SoilNutrientStatus
import com.example.model.SoilTestResult

@Composable
fun SoilHealthCard(
    soilTestResult: SoilTestResult,
    language: AppLanguage,
    onEditClicked: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isLogged = soilTestResult.isLogged
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("soil_health_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isLogged) Color(0xFFF1F8E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Science,
                        contentDescription = "Soil Test Health Card",
                        tint = if (isLogged) Color(0xFF2E7D32) else primaryColor,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "மண் பரிசோதனை சுகாதார அட்டை (Soil Health)" else "Soil Test Result & Health Card",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = if (isLogged) Color(0xFF1B5E20) else MaterialTheme.colorScheme.onSurface
                        )
                        if (isLogged) {
                            Text(
                                text = "${soilTestResult.labName} | ${soilTestResult.testDate}",
                                fontSize = 11.sp,
                                color = Color(0xFF388E3C)
                            )
                        } else {
                            Text(
                                text = if (language == AppLanguage.TAMIL) "பரிசோதனை முடிவுகளை பதிவு செய்து துல்லிய பரிந்துரை பெறுக" else "Log NPK & pH to receive precise fertilizer dosages",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }

                Button(
                    onClick = onEditClicked,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isLogged) Color(0xFF2E7D32) else primaryColor
                    ),
                    modifier = Modifier.testTag("log_soil_test_btn"),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(
                        imageVector = if (isLogged) Icons.Default.Edit else Icons.Default.Add,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isLogged) (if (language == AppLanguage.TAMIL) "மாற்றுக" else "Edit")
                        else (if (language == AppLanguage.TAMIL) "பதிவு செய்" else "Log Test"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Divider(color = if (isLogged) Color(0xFFA5D6A7) else MaterialTheme.colorScheme.outlineVariant)

            // pH Level Gauge
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) "மண் கார அமிலத்தன்மை (pH Level):" else "Soil pH Level:",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "pH ${soilTestResult.phLevel} (${soilTestResult.getPhCategory(language)})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = getPhColor(soilTestResult.phLevel)
                    )
                }

                // pH Bar Progress Visual (0 to 14)
                val phRatio = (soilTestResult.phLevel / 14.0).toFloat().coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(phRatio)
                            .clip(RoundedCornerShape(5.dp))
                            .background(getPhColor(soilTestResult.phLevel))
                    )
                }
            }

            // NPK Primary Nutrients Meters
            Text(
                text = if (language == AppLanguage.TAMIL) "முதன்மை சத்துக்கள் (NPK Availability):" else "Primary Nutrients (NPK Availability):",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                NutrientMeterItem(
                    label = "Nitrogen (N)",
                    valueText = "${soilTestResult.nitrogenKgPerHa.toInt()} kg/ha",
                    status = soilTestResult.getNitrogenStatus(),
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                NutrientMeterItem(
                    label = "Phosphorus (P)",
                    valueText = "${soilTestResult.phosphorusKgPerHa.toInt()} kg/ha",
                    status = soilTestResult.getPhosphorusStatus(),
                    language = language,
                    modifier = Modifier.weight(1f)
                )
                NutrientMeterItem(
                    label = "Potassium (K)",
                    valueText = "${soilTestResult.potassiumKgPerHa.toInt()} kg/ha",
                    status = soilTestResult.getPotassiumStatus(),
                    language = language,
                    modifier = Modifier.weight(1f)
                )
            }

            // Organic Carbon & Micronutrients
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == AppLanguage.TAMIL) "கரிம கரிமப்பொருள் (Organic Carbon %):" else "Organic Carbon (OC %):",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = "${soilTestResult.organicCarbonPercent}% (${soilTestResult.getOrganicCarbonStatus().getDisplayName(language)})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = getNutrientColor(soilTestResult.getOrganicCarbonStatus())
                )
            }

            if (soilTestResult.micronutrientsNotes.isNotBlank()) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = Color(0xFFD84315),
                        modifier = Modifier.size(16.dp)
                    )
                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "நுண்ணூட்டசத்து:" else "Micronutrients:"} ${soilTestResult.micronutrientsNotes}",
                        fontSize = 11.sp,
                        color = Color(0xFFBF360C),
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // TNAU Amendment Advice Banner
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFFFF8E1)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Lightbulb,
                        contentDescription = "Advice",
                        tint = Color(0xFFF57F17),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = soilTestResult.getTnauAmendmentAdvice(language),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE65100)
                    )
                }
            }
        }
    }
}

@Composable
private fun NutrientMeterItem(
    label: String,
    valueText: String,
    status: SoilNutrientStatus,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(label, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(valueText, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = getNutrientColor(status).copy(alpha = 0.15f)
            ) {
                Text(
                    text = status.getDisplayName(language),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = getNutrientColor(status),
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                )
            }
        }
    }
}

private fun getPhColor(ph: Double): Color = when {
    ph < 5.5 -> Color(0xFFD32F2F) // Acidic Red
    ph < 6.5 -> Color(0xFFF57C00) // Slightly Acidic Orange
    ph <= 7.5 -> Color(0xFF2E7D32) // Optimal Neutral Green
    ph <= 8.5 -> Color(0xFF0288D1) // Slightly Alkaline Blue
    else -> Color(0xFF7B1FA2) // Alkaline Purple
}

private fun getNutrientColor(status: SoilNutrientStatus): Color = when (status) {
    SoilNutrientStatus.LOW -> Color(0xFFC62828) // Red
    SoilNutrientStatus.MEDIUM -> Color(0xFF2E7D32) // Green
    SoilNutrientStatus.HIGH -> Color(0xFF1565C0) // Blue
}
