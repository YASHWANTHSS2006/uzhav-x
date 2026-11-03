package com.example.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Science
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.SoilTestResult

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SoilTestDialog(
    currentResult: SoilTestResult,
    language: AppLanguage,
    onDismiss: () -> Unit,
    onSave: (SoilTestResult) -> Unit
) {
    var phText by remember { mutableStateOf(currentResult.phLevel.toString()) }
    var nText by remember { mutableStateOf(currentResult.nitrogenKgPerHa.toInt().toString()) }
    var pText by remember { mutableStateOf(currentResult.phosphorusKgPerHa.toInt().toString()) }
    var kText by remember { mutableStateOf(currentResult.potassiumKgPerHa.toInt().toString()) }
    var ocText by remember { mutableStateOf(currentResult.organicCarbonPercent.toString()) }
    var labName by remember { mutableStateOf(currentResult.labName.ifBlank { "TNAU KVK Lab, Thanjavur" }) }
    var testDate by remember { mutableStateOf(currentResult.testDate.ifBlank { "2026-10-15" }) }
    var microNotes by remember { mutableStateOf(currentResult.micronutrientsNotes) }

    val phVal = phText.toDoubleOrNull() ?: 6.8
    val tempResult = SoilTestResult(
        isLogged = true,
        testDate = testDate,
        labName = labName,
        phLevel = phVal,
        nitrogenKgPerHa = nText.toDoubleOrNull() ?: 280.0,
        phosphorusKgPerHa = pText.toDoubleOrNull() ?: 18.5,
        potassiumKgPerHa = kText.toDoubleOrNull() ?: 220.0,
        organicCarbonPercent = ocText.toDoubleOrNull() ?: 0.65,
        micronutrientsNotes = microNotes
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Science,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.TAMIL) "மண் பரிசோதனை முடிவு பதிவு (Log Soil Test)" else "Log / Update Soil Test Results",
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .testTag("soil_test_dialog_content"),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = if (language == AppLanguage.TAMIL) "உங்கள் மண் அட்டை (Soil Health Card) விவரங்களை பதிவு செய்யவும்:"
                    else "Enter exact soil laboratory values for precise AI crop and fertilizer recommendations:",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )

                // Lab & Date
                OutlinedTextField(
                    value = labName,
                    onValueChange = { labName = it },
                    label = { Text(if (language == AppLanguage.TAMIL) "ஆய்வகம் (Testing Lab)" else "Testing Laboratory") },
                    modifier = Modifier.fillMaxWidth().testTag("soil_lab_input")
                )

                OutlinedTextField(
                    value = testDate,
                    onValueChange = { testDate = it },
                    label = { Text(if (language == AppLanguage.TAMIL) "பரிசோதனை தேதி (YYYY-MM-DD)" else "Test Date (YYYY-MM-DD)") },
                    modifier = Modifier.fillMaxWidth().testTag("soil_date_input")
                )

                Divider()

                // pH Level Slider / Input
                Text(
                    text = "${if (language == AppLanguage.TAMIL) "கார அமிலத்தன்மை (pH Level):" else "pH Level:"} $phVal (${tempResult.getPhCategory(language)})",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Slider(
                    value = phVal.toFloat(),
                    onValueChange = { phText = String.format("%.1f", it) },
                    valueRange = 4.0f..10.0f,
                    steps = 60,
                    modifier = Modifier.fillMaxWidth().testTag("soil_ph_slider")
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phText,
                        onValueChange = { phText = it },
                        label = { Text("pH Value") },
                        modifier = Modifier.weight(1f).testTag("soil_ph_input")
                    )
                    OutlinedTextField(
                        value = ocText,
                        onValueChange = { ocText = it },
                        label = { Text("OC (%)") },
                        modifier = Modifier.weight(1f).testTag("soil_oc_input")
                    )
                }

                Divider()

                // NPK Values
                Text(
                    text = if (language == AppLanguage.TAMIL) "முதன்மை சத்துக்கள் (kg/ha):" else "Primary Nutrients (Available kg/ha):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedTextField(
                        value = nText,
                        onValueChange = { nText = it },
                        label = { Text("Nitrogen (N)") },
                        modifier = Modifier.weight(1f).testTag("soil_n_input")
                    )
                    OutlinedTextField(
                        value = pText,
                        onValueChange = { pText = it },
                        label = { Text("Phosphorus (P)") },
                        modifier = Modifier.weight(1f).testTag("soil_p_input")
                    )
                    OutlinedTextField(
                        value = kText,
                        onValueChange = { kText = it },
                        label = { Text("Potassium (K)") },
                        modifier = Modifier.weight(1f).testTag("soil_k_input")
                    )
                }

                // Micronutrients Notes
                OutlinedTextField(
                    value = microNotes,
                    onValueChange = { microNotes = it },
                    label = { Text(if (language == AppLanguage.TAMIL) "நுண்ணூட்டசத்து / குறிப்புகள்" else "Micronutrient Notes (Zinc, Iron, Boron)") },
                    modifier = Modifier.fillMaxWidth().testTag("soil_micro_input")
                )

                // Live Preview TNAU Tip
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                ) {
                    Text(
                        text = tempResult.getTnauAmendmentAdvice(language),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(8.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(tempResult) },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("save_soil_test_btn")
            ) {
                Text(if (language == AppLanguage.TAMIL) "சேமிக்க" else "Save Soil Test")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.TAMIL) "ரத்து" else "Cancel")
            }
        }
    )
}
