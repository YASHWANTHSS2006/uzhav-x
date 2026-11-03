package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.DataExportHelper
import com.example.model.AppLanguage
import com.example.model.GovtScheme
import com.example.model.MandiPrice

enum class ExportType {
    MANDI_PRICES,
    SUBSIDIES,
    ALL
}

enum class ExportFormat {
    PDF,
    CSV
}

@Composable
fun ExportDialog(
    language: AppLanguage,
    exportType: ExportType = ExportType.ALL,
    mandiPrices: List<MandiPrice> = emptyList(),
    schemes: List<GovtScheme> = emptyList(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var selectedFormat by remember { mutableStateOf(ExportFormat.PDF) }
    var selectedTarget by remember { mutableStateOf(exportType) }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Download,
                contentDescription = "Export Data",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(28.dp)
            )
        },
        title = {
            Text(
                text = if (language == AppLanguage.TAMIL) "ஆஃப்லைன் அறிக்கை பதிவிறக்கம்" else "Export Offline Report",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                Text(
                    text = if (language == AppLanguage.TAMIL)
                        "மண்டி விலைகள் மற்றும் அரசு மானிய தகவல்களை PDF அல்லது CSV கோப்பாக உங்கள் கைபேசியில் சேமிக்கலாம்."
                    else
                        "Save crop prices and government subsidies directly to your phone as PDF or CSV for offline reference.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Target Selector if ALL is available
                Text(
                    text = if (language == AppLanguage.TAMIL) "அறிக்கை வகை:" else "Select Report Type:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedTarget == ExportType.MANDI_PRICES,
                        onClick = { selectedTarget = ExportType.MANDI_PRICES },
                        label = { Text(if (language == AppLanguage.TAMIL) "மண்டி விலை" else "Mandi Prices", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedTarget == ExportType.SUBSIDIES,
                        onClick = { selectedTarget = ExportType.SUBSIDIES },
                        label = { Text(if (language == AppLanguage.TAMIL) "அரசு மானியம்" else "Subsidies", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedTarget == ExportType.ALL,
                        onClick = { selectedTarget = ExportType.ALL },
                        label = { Text(if (language == AppLanguage.TAMIL) "அனைத்தும்" else "Both", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                // Format Selector (PDF vs CSV)
                Text(
                    text = if (language == AppLanguage.TAMIL) "கோப்பு வடிவம் (Format):" else "Select File Format:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFormat = ExportFormat.PDF },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedFormat == ExportFormat.PDF) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (selectedFormat == ExportFormat.PDF) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.PictureAsPdf,
                                contentDescription = "PDF",
                                tint = if (selectedFormat == ExportFormat.PDF) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "PDF Document",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.TAMIL) "படிக்க & அச்சிட" else "Print & Read",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }

                    Card(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedFormat = ExportFormat.CSV },
                        colors = CardDefaults.cardColors(
                            containerColor = if (selectedFormat == ExportFormat.CSV) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = if (selectedFormat == ExportFormat.CSV) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.TableChart,
                                contentDescription = "CSV",
                                tint = if (selectedFormat == ExportFormat.CSV) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "CSV Spreadsheet",
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (language == AppLanguage.TAMIL) "Excel அட்டவணை" else "Excel / Sheets",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    performExport(context, language, selectedTarget, selectedFormat, mandiPrices, schemes)
                    onDismiss()
                },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_export_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(if (language == AppLanguage.TAMIL) "பதிவிறக்கு & பகிர்" else "Generate & Export")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.TAMIL) "ரத்து" else "Cancel")
            }
        }
    )
}

private fun performExport(
    context: Context,
    language: AppLanguage,
    target: ExportType,
    format: ExportFormat,
    mandiPrices: List<MandiPrice>,
    schemes: List<GovtScheme>
) {
    var filesCount = 0

    if (target == ExportType.MANDI_PRICES || target == ExportType.ALL) {
        val pricesToExport = mandiPrices.ifEmpty { com.example.data.AgriRepository.sampleMandiPrices }
        if (format == ExportFormat.PDF) {
            val file = DataExportHelper.exportMandiPricesPdf(context, pricesToExport, language)
            file?.let {
                filesCount++
                DataExportHelper.openOrShareFile(context, it, "application/pdf", "Mandi Prices PDF")
            }
        } else {
            val file = DataExportHelper.exportMandiPricesCsv(context, pricesToExport)
            file?.let {
                filesCount++
                DataExportHelper.openOrShareFile(context, it, "text/csv", "Mandi Prices CSV")
            }
        }
    }

    if (target == ExportType.SUBSIDIES || target == ExportType.ALL) {
        val schemesToExport = schemes.ifEmpty { com.example.data.AgriRepository.sampleSchemes }
        if (format == ExportFormat.PDF) {
            val file = DataExportHelper.exportSubsidiesPdf(context, schemesToExport, language)
            file?.let {
                filesCount++
                if (target == ExportType.SUBSIDIES) {
                    DataExportHelper.openOrShareFile(context, it, "application/pdf", "Subsidies PDF")
                }
            }
        } else {
            val file = DataExportHelper.exportSubsidiesCsv(context, schemesToExport)
            file?.let {
                filesCount++
                if (target == ExportType.SUBSIDIES) {
                    DataExportHelper.openOrShareFile(context, it, "text/csv", "Subsidies CSV")
                }
            }
        }
    }

    if (filesCount > 0) {
        val msg = if (language == AppLanguage.TAMIL) "அறிக்கை வெற்றியுடன் உருவாக்கம் பெற்றது!" else "Offline report generated successfully!"
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }
}
