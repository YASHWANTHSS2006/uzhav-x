package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.*
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedbackReportScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val isAdmin = state.currentRole == UserRole.ADMIN
    val primaryColor = MaterialTheme.colorScheme.primary

    var selectedCategory by remember { mutableStateOf(ReportCategory.FERTILIZER_SHORTAGE) }
    var descriptionText by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("feedback_report_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.ReportProblem,
                        contentDescription = "Feedback & Reports",
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "விவசாயிகள் கருத்து & புகார் மையம்" else "Farmer Grievance & Feedback Desk",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = if (lang == AppLanguage.TAMIL) "வாய்க்கால் நீர் வரத்து பிரச்சனை, கூட்டுறவு சங்கத்தில் உரத்தட்டுப்பாடு அல்லது தீவிர பூச்சி தாக்குதலை வேளாண் துறைக்கு உடனுக்குடன் புகாரளிக்கவும்."
                    else "Report canal irrigation blockages, PACCS fertilizer shortages, or pest alerts directly to agriculture extension authorities.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // New Report Form (Available for Farmers)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "புதிய புகார் அல்லது கருத்தை பதிவு செய்க" else "Submit New Grievance / Report",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )

                    // Category Selector Dropdown
                    ExposedDropdownMenuBox(
                        expanded = categoryExpanded,
                        onExpandedChange = { categoryExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = selectedCategory.getDisplayName(lang),
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == AppLanguage.TAMIL) "புகார் வகை" else "Grievance Category") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = categoryExpanded,
                            onDismissRequest = { categoryExpanded = false }
                        ) {
                            ReportCategory.values().forEach { cat ->
                                DropdownMenuItem(
                                    text = { Text(cat.getDisplayName(lang)) },
                                    onClick = {
                                        selectedCategory = cat
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = descriptionText,
                        onValueChange = { descriptionText = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "புகாரின் விவரம் (ஊர், பிரச்சனை, தேவை)" else "Issue details (location, description)") },
                        placeholder = { Text(if (lang == AppLanguage.TAMIL) "எ.கா: எங்கள் கிராம கூட்டுறவு சங்கத்தில் யூரியா உரம் வரத்து இல்லை..." else "e.g. Urea shortage at our PACCS society...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Button(
                        onClick = {
                            if (descriptionText.isNotBlank()) {
                                viewModel.submitFeedbackReport(selectedCategory, descriptionText)
                                descriptionText = ""
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Send, contentDescription = "Submit")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == AppLanguage.TAMIL) "புகாரை பதிவு செய்க" else "Submit Grievance")
                    }
                }
            }
        }

        // Section: List of Grievances & Status
        item {
            Text(
                text = if (isAdmin) {
                    if (lang == AppLanguage.TAMIL) "நிர்வாகி பார்வை: அனைத்து புகார்கள் (${state.feedbackReports.size})" else "Admin Review: Registered Grievances (${state.feedbackReports.size})"
                } else {
                    if (lang == AppLanguage.TAMIL) "பதிவு செய்யப்பட்ட புகார்களின் நிலை" else "Registered Grievance Status"
                },
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(state.feedbackReports) { report ->
            FeedbackReportCard(
                report = report,
                language = lang,
                isAdmin = isAdmin,
                onUpdateStatus = { viewModel.adminUpdateReportStatus(report.id, it) }
            )
        }
    }
}

@Composable
fun FeedbackReportCard(
    report: FeedbackReport,
    language: AppLanguage,
    isAdmin: Boolean,
    onUpdateStatus: (ReportStatus) -> Unit,
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
                        text = report.category.getDisplayName(language),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${report.farmerName} • ${report.village}, ${report.district} (${report.timestamp})",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                val statusColor = when (report.status) {
                    ReportStatus.SUBMITTED -> Color(0xFFF57F17)
                    ReportStatus.INVESTIGATING -> Color(0xFF1976D2)
                    ReportStatus.RESOLVED -> Color(0xFF2E7D32)
                }

                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = statusColor.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = report.status.getDisplayName(language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = statusColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = report.description,
                style = MaterialTheme.typography.bodyMedium
            )

            // Admin Action Buttons: Change Status
            if (isAdmin) {
                Spacer(modifier = Modifier.height(10.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) "நிர்வாகி நடவடிக்கை:" else "Admin Action:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = secondaryColor
                    )
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        OutlinedButton(
                            onClick = { onUpdateStatus(ReportStatus.INVESTIGATING) },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (language == AppLanguage.TAMIL) "பரிசீலனை" else "Review", fontSize = 10.sp)
                        }
                        Button(
                            onClick = { onUpdateStatus(ReportStatus.RESOLVED) },
                            shape = RoundedCornerShape(6.dp),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(if (language == AppLanguage.TAMIL) "தீர்வு காண்" else "Resolve", fontSize = 10.sp)
                        }
                    }
                }
            }
        }
    }
}
