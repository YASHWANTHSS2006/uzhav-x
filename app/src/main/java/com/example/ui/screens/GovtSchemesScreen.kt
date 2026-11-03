package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
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
import com.example.model.AppLanguage
import com.example.model.GovtScheme
import com.example.model.SchemeCategory
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GovtSchemesScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    var showSubsidyCalculator by remember { mutableStateOf(false) }
    var showExportDialog by remember { mutableStateOf(false) }
    var calcLandAcres by remember { mutableStateOf("3") }
    var selectedCropType by remember { mutableStateOf("Horticulture / Coconut") }

    val filteredSchemes = state.schemes.filter { scheme ->
        val matchesCategory = state.selectedSchemeCategory == SchemeCategory.ALL || scheme.category == state.selectedSchemeCategory
        val matchesSearch = state.schemeSearchQuery.isBlank() ||
                scheme.title.contains(state.schemeSearchQuery, ignoreCase = true) ||
                scheme.tamilTitle.contains(state.schemeSearchQuery, ignoreCase = true) ||
                scheme.department.contains(state.schemeSearchQuery, ignoreCase = true) ||
                scheme.summaryEnglish.contains(state.schemeSearchQuery, ignoreCase = true) ||
                scheme.summaryTamil.contains(state.schemeSearchQuery, ignoreCase = true)
        matchesCategory && matchesSearch
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("govt_schemes_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = "Schemes",
                            tint = primaryColor,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "2026 அரசு திட்டங்கள் & மானியங்கள்" else "2026-27 Govt Schemes & Subsidies",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            if (state.isSubsidiesRoomCached) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(top = 2.dp)
                                ) {
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
                                                text = if (lang == AppLanguage.TAMIL) "Room ஆஃப்லைன் மானிய தரவுத்தளம்" else "Room DB Offline Cached",
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

                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        FilledTonalIconButton(
                            onClick = { showExportDialog = true },
                            modifier = Modifier
                                .testTag("subsidies_export_btn")
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = "Export Subsidies PDF/CSV",
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = secondaryColor.copy(alpha = 0.2f)
                        ) {
                            Text(
                                text = "2026 New Schemes",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = secondaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Text(
                    text = if (lang == AppLanguage.TAMIL) "தமிழக அரசு மற்றும் மத்திய அரசின் அனைத்து வேளாண் மானியங்கள் (மானியம்), பஞ்சாயத்து திட்டங்கள் மற்றும் வட்டி இல்லா வங்கி பயிர் கடன்கள்."
                    else "Complete repository of Tamil Nadu & Central Govt agricultural subsidies (Maniyam), panchayat development schemes, and zero-interest bank loans.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Subsidy Calculator Tool Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showSubsidyCalculator = !showSubsidyCalculator },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Calculate, contentDescription = "Calculator", tint = primaryColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "மானியம் & கடன் தொகைக் கணிப்பான்" else "Subsidy & Loan Benefit Calculator",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                        Icon(
                            imageVector = if (showSubsidyCalculator) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle",
                            tint = primaryColor
                        )
                    }

                    AnimatedVisibility(visible = showSubsidyCalculator) {
                        Column(modifier = Modifier.padding(top = 12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = calcLandAcres,
                                onValueChange = { calcLandAcres = it },
                                label = { Text(if (lang == AppLanguage.TAMIL) "நில அளவு (ஏக்கரில்)" else "Land Size (Acres)") },
                                modifier = Modifier.fillMaxWidth()
                            )

                            val acres = calcLandAcres.toDoubleOrNull() ?: 1.0
                            val isSmallFarmer = acres <= 5.0
                            val dripSubsidy = if (isSmallFarmer) acres * 48000.0 else acres * 36000.0
                            val kccLimit = acres * 52000.0

                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface
                            ) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = if (isSmallFarmer) {
                                            if (lang == AppLanguage.TAMIL) "✔ சிறு/குறு விவசாயி தகுதி: 100% முழு மானியம் கிடைக்கும்!" else "✔ Small/Marginal Farmer: 100% Full Free Subsidy Eligible!"
                                        } else {
                                            if (lang == AppLanguage.TAMIL) "✔ பெரிய விவசாயி தகுதி: 75% அரசு மானியம் கிடைக்கும்!" else "✔ Other Farmer: 75% Govt Subsidy Eligible!"
                                        },
                                        fontWeight = FontWeight.Bold,
                                        color = primaryColor
                                    )
                                    Text(
                                        text = "${if (lang == AppLanguage.TAMIL) "சொட்டு நீர் பாசன மானிய மதிப்பு: " else "Est. Drip Irrigation Subsidy: "} ₹${dripSubsidy.toInt()}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        text = "${if (lang == AppLanguage.TAMIL) "0% வட்டி பயிர் கடன் உச்சவரம்பு (KCC): " else "0% Interest KCC Crop Loan Limit: "} ₹${kccLimit.toInt()}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = state.schemeSearchQuery,
                onValueChange = { viewModel.setSchemeSearchQuery(it) },
                placeholder = { Text(if (lang == AppLanguage.TAMIL) "திட்டம் அல்லது மானியம் தேடுக (எ.கா: சொட்டு நீர், சோலார்)" else "Search schemes (e.g. Drip, Solar, KCC)...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                trailingIcon = {
                    if (state.schemeSearchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.setSchemeSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
        }

        // Category Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SchemeCategory.values()) { cat ->
                    FilterChip(
                        selected = state.selectedSchemeCategory == cat,
                        onClick = { viewModel.setSchemeCategory(cat) },
                        label = { Text(cat.getDisplayName(lang)) }
                    )
                }
            }
        }

        item {
            Text(
                text = if (lang == AppLanguage.TAMIL) "2026-27 திட்டங்கள் மற்றும் கடன்கள் பட்டியல் (${filteredSchemes.size})" else "Active Government Schemes (${filteredSchemes.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        // Schemes List
        items(filteredSchemes) { scheme ->
            SchemeItemCard(
                scheme = scheme,
                language = lang,
                onSelectDetail = { viewModel.selectSchemeForDetail(it) }
            )
        }
    }

    // Detail Dialog
    state.selectedSchemeForDetail?.let { detail ->
        AlertDialog(
            onDismissRequest = { viewModel.selectSchemeForDetail(null) },
            title = {
                Text(
                    text = if (lang == AppLanguage.TAMIL) detail.tamilTitle else detail.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = primaryColor.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${detail.subsidyPercentage} • ${detail.yearValidity}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    item {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) detail.summaryTamil else detail.summaryEnglish,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    item {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "தகுதி நிபந்தனைகள்:" else "Eligibility Criteria:",
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        detail.eligibility.forEach { el ->
                            Text("• $el", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "தேவையான ஆவணங்கள்:" else "Required Documents:",
                            fontWeight = FontWeight.Bold,
                            color = secondaryColor
                        )
                        detail.requiredDocuments.forEach { doc ->
                            Text("✔ $doc", style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "விண்ணப்பிக்கும் முறை:" else "How to Apply:",
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                        detail.howToApplySteps.forEach { step ->
                            Text(step, style = MaterialTheme.typography.bodySmall)
                        }
                    }

                    item {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = MaterialTheme.colorScheme.surfaceVariant
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Text(
                                    text = "${if (lang == AppLanguage.TAMIL) "அலுவலக உதவி எண்: " else "Helpline: "} ${detail.helplineNumber}",
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${if (lang == AppLanguage.TAMIL) "இணையதளம்: " else "Portal: "} ${detail.officialPortalUrl}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.selectSchemeForDetail(null) }) {
                    Text(if (lang == AppLanguage.TAMIL) "முடிந்தது" else "Close")
                }
            }
        )
    }

    if (showExportDialog) {
        com.example.ui.components.ExportDialog(
            language = lang,
            exportType = com.example.ui.components.ExportType.SUBSIDIES,
            schemes = filteredSchemes,
            onDismiss = { showExportDialog = false }
        )
    }
}

@Composable
fun SchemeItemCard(
    scheme: GovtScheme,
    language: AppLanguage,
    onSelectDetail: (GovtScheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelectDetail(scheme) },
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
                        text = if (language == AppLanguage.TAMIL) scheme.tamilTitle else scheme.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = scheme.department,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = primaryColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = scheme.yearValidity,
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryColor,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Subsidy Benefit Pill
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFE8F5E9)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "Subsidy",
                        tint = Color(0xFF1B5E20),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = scheme.subsidyPercentage,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == AppLanguage.TAMIL) scheme.summaryTamil else scheme.summaryEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Helpline: ${scheme.helplineNumber.substringBefore(" /")}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )

                Button(
                    onClick = { onSelectDetail(scheme) },
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) "முழு விவரம் & ஆவணங்கள்" else "View Details & Apply",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
