package com.example.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgriEquipment
import com.example.model.AppLanguage
import com.example.model.EquipmentCategory
import com.example.viewmodel.UzhavViewModel

@Composable
fun EquipmentMarketScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    var selectedEquipmentForInquiry by remember { mutableStateOf<AgriEquipment?>(null) }
    var inquirySuccessNotice by remember { mutableStateOf<String?>(null) }

    val filteredEquipments = state.equipments.filter { eq ->
        state.selectedEquipmentCategory == null || eq.category == state.selectedEquipmentCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("equipment_market_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Agriculture,
                        contentDescription = "Equipments",
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "வேளாண் இயந்திரங்கள் & கருவிகள் அங்காடி" else "Farm Equipment & Machinery Hub",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = if (lang == AppLanguage.TAMIL) "டிராக்டர், பவர் வீடர், பேட்டரி ஸ்ப்ரேயர்கள் மற்றும் சொட்டு நீர் கருவிகளை அரசு மானிய விலையில் வாங்கலாம்."
                    else "Purchase certified agricultural machinery, power weeders, sprayers, and drip kits with direct SMAM govt subsidies.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Category Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = state.selectedEquipmentCategory == null,
                        onClick = { viewModel.setEquipmentCategory(null) },
                        label = { Text(if (lang == AppLanguage.TAMIL) "அனைத்து கருவிகள்" else "All Equipment") }
                    )
                }
                items(EquipmentCategory.values()) { cat ->
                    FilterChip(
                        selected = state.selectedEquipmentCategory == cat,
                        onClick = { viewModel.setEquipmentCategory(cat) },
                        label = { Text(cat.getDisplayName(lang)) }
                    )
                }
            }
        }

        // Equipment Cards
        items(filteredEquipments) { equip ->
            EquipmentCard(
                equipment = equip,
                language = lang,
                onInquire = { selectedEquipmentForInquiry = it }
            )
        }
    }

    // Inquiry Dialog
    selectedEquipmentForInquiry?.let { eq ->
        var farmerNote by remember { mutableStateOf("") }
        AlertDialog(
            onDismissRequest = { selectedEquipmentForInquiry = null },
            title = {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "இயந்திரம் வாங்குவதற்கான முன்பதிவு" else "Equipment Purchase Inquiry",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) eq.tamilName else eq.name,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                    Text(
                        text = "${if (lang == AppLanguage.TAMIL) "அங்கீகரிக்கப்பட்ட விற்பனையாளர்: " else "Authorized Dealer: "} ${eq.dealerName} (${eq.dealerLocation})",
                        style = MaterialTheme.typography.bodySmall
                    )
                    Text(
                        text = "Phone: ${eq.dealerPhone}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedTextField(
                        value = farmerNote,
                        onValueChange = { farmerNote = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "உங்கள் குறிப்பு அல்லது கேள்வி" else "Your inquiry message") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        inquirySuccessNotice = if (lang == AppLanguage.TAMIL) "விற்பனையாளருக்கு உங்கள் தகவல் அனுப்பப்பட்டது. விரைவில் தொடர்பு கொள்வார்கள்!" else "Inquiry sent to dealer. They will contact you shortly!"
                        selectedEquipmentForInquiry = null
                    }
                ) {
                    Text(if (lang == AppLanguage.TAMIL) "தொடர்பு கொள்க" else "Submit Inquiry")
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEquipmentForInquiry = null }) {
                    Text(if (lang == AppLanguage.TAMIL) "ரத்து" else "Cancel")
                }
            }
        )
    }

    // Notice SnackBar
    inquirySuccessNotice?.let { notice ->
        Snackbar(
            modifier = Modifier.padding(16.dp),
            action = {
                TextButton(onClick = { inquirySuccessNotice = null }) {
                    Text("OK", color = Color.White)
                }
            }
        ) {
            Text(notice)
        }
    }
}

@Composable
fun EquipmentCard(
    equipment: AgriEquipment,
    language: AppLanguage,
    onInquire: (AgriEquipment) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) equipment.tamilName else equipment.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${equipment.brand} • Model: ${equipment.modelNumber}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
                if (equipment.subsidyApplicable) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = primaryColor.copy(alpha = 0.15f)
                    ) {
                        Text(
                            text = "Subsidy Approved",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Pricing Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Column {
                    if (equipment.subsidyApplicable) {
                        Text(
                            text = equipment.subsidyPercentText,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "₹${equipment.effectivePrice}",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.ExtraBold,
                            color = primaryColor
                        )
                        if (equipment.originalPrice > equipment.effectivePrice) {
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "₹${equipment.originalPrice}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                textDecoration = androidx.compose.ui.text.style.TextDecoration.LineThrough
                            )
                        }
                    }
                }

                Text(
                    text = equipment.emiStartsFrom,
                    style = MaterialTheme.typography.labelSmall,
                    color = secondaryColor,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Specifications
            equipment.specifications.forEach { spec ->
                Row(modifier = Modifier.padding(vertical = 1.dp)) {
                    Text("✔ ", color = primaryColor, fontSize = 11.sp)
                    Text(spec, style = MaterialTheme.typography.bodySmall)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = equipment.dealerName,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${equipment.dealerLocation} • Ph: ${equipment.dealerPhone}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Button(
                    onClick = { onInquire(equipment) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp)
                ) {
                    Text(if (language == AppLanguage.TAMIL) "முன்பதிவு / தகவல்" else "Inquire", fontSize = 11.sp)
                }
            }
        }
    }
}
