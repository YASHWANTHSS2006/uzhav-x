package com.example.ui.screens

import android.content.Intent
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.MlKitLabelResult
import com.example.model.PlantDiseaseDiagnosis
import com.example.model.TreatmentStep
import com.example.viewmodel.UzhavScreen
import com.example.viewmodel.UzhavViewModel

@Composable
fun PlantScannerScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    var flashActive by remember { mutableStateOf(false) }
    var showDosageCalculator by remember { mutableStateOf(false) }
    var acreageInput by remember { mutableStateOf("1.0") }

    // Direct Camera Photo Capture Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap: Bitmap? ->
        if (capturedBitmap != null) {
            viewModel.analyzeCustomLeaf(capturedBitmap)
        }
    }

    // Android Photo Picker Launcher for Gallery
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.analyzeLeafFromUri(context, uri)
        }
    }

    val sampleLeaves = listOf(
        Triple("Paddy Blast (நெல் கருகல்)", "நெல்", Icons.Default.Grass),
        Triple("Tomato Blight (தக்காளி புள்ளி)", "தக்காளி", Icons.Default.LocalFlorist),
        Triple("Chilli Curl (மிளகாய் சுருட்டு)", "மிளகாய்", Icons.Default.Eco),
        Triple("Cotton Blight (பருத்தி புள்ளி)", "பருத்தி", Icons.Default.Yard),
        Triple("Turmeric Rot (மஞ்சள் அழுகல்)", "மஞ்சள்", Icons.Default.Spa),
        Triple("Sugarcane Rot (கரும்பு அழுகல்)", "கரும்பு", Icons.Default.Forest)
    )

    // Animated scanning laser line
    val infiniteTransition = rememberInfiniteTransition(label = "scanner_laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laser_pos"
    )

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("plant_scanner_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title & ML Kit Image Labeling Badge
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = CircleShape,
                            color = primaryColor.copy(alpha = 0.12f),
                            modifier = Modifier.size(42.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Plant Doctor",
                                    tint = primaryColor,
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பயிர் மருத்துவர் AI ஸ்கேனர்" else "Crop Doctor & AI Plant Scanner",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color(0xFFE8F5E9)
                                ) {
                                    Text(
                                        text = "⚡ Google ML Kit Image Labeling (On-Device)",
                                        style = MaterialTheme.typography.labelSmall,
                                        color = Color(0xFF1B5E20),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Reset / Clear button if diagnosis is open
                    if (state.currentDiagnosis != null) {
                        IconButton(
                            onClick = { viewModel.clearDiagnosis() },
                            modifier = Modifier.testTag("clear_diagnosis_button")
                        ) {
                            Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = primaryColor)
                        }
                    }
                }

                Text(
                    text = if (lang == AppLanguage.TAMIL)
                        "நோய் தாக்கிய இலை அல்லது பயிரை படம் பிடியுங்கள். Google ML Kit பார்வை நுண்ணறிவு மற்றும் TNAU வழிகாட்டல் மூலம் நோய் கண்டறிந்து படிப்படியான சிகிச்சை பெறுக."
                    else
                        "Photograph or upload crop leaves. Powered by on-device Google ML Kit Image Labeling and TNAU pathology advisory to identify issues and give step-by-step cure steps.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Camera Viewfinder / Preview Box
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF141C15)),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    // Scanned Image Preview if available
                    if (state.scannedImageBitmap != null) {
                        Image(
                            bitmap = state.scannedImageBitmap.asImageBitmap(),
                            contentDescription = "Scanned Leaf",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        // Viewfinder background pattern
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(Color(0xFF2E3D30), Color(0xFF0F1510))
                                    )
                                )
                        )
                    }

                    // Viewfinder Framing Reticle & Laser Scan Line
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp)
                    ) {
                        // Flash Button in Top Right
                        IconButton(
                            onClick = { flashActive = !flashActive },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .background(Color.Black.copy(alpha = 0.55f), CircleShape)
                                .size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (flashActive) Icons.Default.FlashOn else Icons.Default.FlashOff,
                                contentDescription = "Flash",
                                tint = if (flashActive) Color(0xFFFFD54F) else Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        // Target Box in Center
                        Box(
                            modifier = Modifier
                                .size(185.dp)
                                .align(Alignment.Center)
                                .border(
                                    width = 2.dp,
                                    color = if (state.isScanning) Color(0xFF00E676) else Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(18.dp)
                                )
                                .background(
                                    if (state.isScanning) Color(0x3300E676) else Color.Transparent
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (state.isScanning) {
                                // Moving laser line inside reticle
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(3.dp)
                                        .align(Alignment.TopCenter)
                                        .offset(y = (laserPosition * 175).dp)
                                        .background(
                                            Brush.horizontalGradient(
                                                listOf(Color.Transparent, Color(0xFF00E676), Color.White, Color(0xFF00E676), Color.Transparent)
                                            )
                                        )
                                )
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.padding(8.dp)
                                ) {
                                    CircularProgressIndicator(color = Color(0xFF00E676), strokeWidth = 3.dp, modifier = Modifier.size(32.dp))
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = state.scanStatusMessage ?: if (lang == AppLanguage.TAMIL) "ML Kit இலை அம்சங்களை ஆய்வு செய்கிறது..." else "ML Kit Image Labeling running...",
                                        color = Color.White,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else if (state.scannedImageBitmap == null) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.CropFree,
                                        contentDescription = "Scanner Target",
                                        tint = Color.White.copy(alpha = 0.75f),
                                        modifier = Modifier.size(56.dp)
                                    )
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Text(
                                        text = if (lang == AppLanguage.TAMIL) "இலையை கட்டத்திற்குள் வைக்கவும்" else "Align leaf inside target box",
                                        color = Color.White.copy(alpha = 0.85f),
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        // Bottom Action Controls (Camera, Gallery, Sample)
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 6.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Gallery Photo Picker Button
                            FilledTonalButton(
                                onClick = {
                                    galleryLauncher.launch(
                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                    )
                                },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.Black.copy(alpha = 0.65f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("gallery_upload_button")
                            ) {
                                Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "படங்கள்" else "Gallery",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }

                            // Big Camera Shutter Button
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { cameraLauncher.launch(null) }
                                    .padding(4.dp)
                                    .testTag("camera_capture_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .background(primaryColor, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.CameraAlt,
                                        contentDescription = "Take Photo",
                                        tint = Color.White,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                            }

                            // Instant Sample Button
                            FilledTonalButton(
                                onClick = { viewModel.scanLeafSample(0) },
                                colors = ButtonDefaults.filledTonalButtonColors(
                                    containerColor = Color.Black.copy(alpha = 0.65f),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                                modifier = Modifier.testTag("sample_scan_button")
                            ) {
                                Icon(Icons.Default.AutoFixHigh, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "மாதிரி" else "Sample",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Fast Sample Leaf Selector (Instant Demonstration for 6 common crops)
        item {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "மாதிரி இலைகளை தேர்வு செய்து சோதிக்கவும்:" else "Test with Common Tamil Nadu Crops:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "TNAU Pathology Presets",
                        style = MaterialTheme.typography.labelSmall,
                        color = primaryColor
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 2.dp)
                ) {
                    itemsIndexed(sampleLeaves) { index, (label, _, icon) ->
                        OutlinedButton(
                            onClick = { viewModel.scanLeafSample(index) },
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("sample_leaf_button_$index")
                        ) {
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp), tint = primaryColor)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(label, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }
        }

        // Comprehensive Diagnosis Results Card
        state.currentDiagnosis?.let { diag ->
            item {
                DiseaseDiagnosisCard(
                    diagnosis = diag,
                    language = lang,
                    showDosageCalculator = showDosageCalculator,
                    onToggleDosageCalculator = { showDosageCalculator = !showDosageCalculator },
                    acreageInput = acreageInput,
                    onAcreageChange = { acreageInput = it },
                    onNavigateToStores = { viewModel.navigateTo(UzhavScreen.FERTILIZER_PRICING) },
                    onShare = {
                        val shareText = buildString {
                            append("🌿 Uzhav X Crop Doctor - Plant Disease Diagnosis\n")
                            append("Crop: ${diag.cropName} (${diag.tamilCropName})\n")
                            append("Disease: ${diag.diseaseName} (${diag.tamilDiseaseName})\n")
                            append("Pathogen: ${diag.scientificName}\n")
                            append("Confidence: ${diag.confidencePercent}%\n")
                            append("Severity: ${diag.severityLevel}\n")
                            append("Urgency: ${diag.urgency}\n\n")
                            if (diag.detectedLabels.isNotEmpty()) {
                                append("ML Kit Labels: ${diag.detectedLabels.joinToString { "${it.text} (${(it.confidence * 100).toInt()}%)" }}\n\n")
                            }
                            append("📋 Step-by-Step Cure Process:\n")
                            diag.stepByStepCureProcess.forEach { step ->
                                append("${step.timeframe} - ${step.titleEnglish}\n${step.descriptionEnglish}\n\n")
                            }
                            append("🏛️ TNAU Advisory: ${diag.tnauexpertNote}")
                        }
                        val intent = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, "Uzhav X Plant Disease Diagnosis")
                            putExtra(Intent.EXTRA_TEXT, shareText)
                        }
                        context.startActivity(Intent.createChooser(intent, "Share with Agri Extension Officer"))
                    },
                    onSaveRecord = {
                        viewModel.saveDiagnosisToFarmLog(diag)
                        Toast.makeText(
                            context,
                            if (lang == AppLanguage.TAMIL) "நோய் விபரம் உங்கள் பண்ணை பதிவேட்டில் சேமிக்கப்பட்டது!" else "Diagnosis saved to Farm Records!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
            }
        }

        // Saved Diagnoses / Farm Records History
        if (state.savedDiagnoses.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("saved_diagnoses_card"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Bookmark,
                                    contentDescription = null,
                                    tint = primaryColor,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "சேமிக்கப்பட்ட பயிர் நோய் பதிவேடு (${state.savedDiagnoses.size})" else "Saved Crop Disease History (${state.savedDiagnoses.size})",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        state.savedDiagnoses.forEach { savedItem ->
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = if (lang == AppLanguage.TAMIL) savedItem.tamilDiseaseName else savedItem.diseaseName,
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${savedItem.cropName} • ${savedItem.severityLevel}",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.outline
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.removeSavedDiagnosis(savedItem.id) }
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = Color(0xFFC62828),
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DiseaseDiagnosisCard(
    diagnosis: PlantDiseaseDiagnosis,
    language: AppLanguage,
    showDosageCalculator: Boolean,
    onToggleDosageCalculator: () -> Unit,
    acreageInput: String,
    onAcreageChange: (String) -> Unit,
    onNavigateToStores: () -> Unit,
    onShare: () -> Unit,
    onSaveRecord: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    // Severity color calculation
    val (severityBg, severityText) = when {
        diagnosis.severityLevel.contains("Severe", ignoreCase = true) || diagnosis.severityLevel.contains("High", ignoreCase = true) ->
            Pair(Color(0xFFFFEBEE), Color(0xFFC62828))
        diagnosis.severityLevel.contains("Moderate", ignoreCase = true) ->
            Pair(Color(0xFFFFF3E0), Color(0xFFE65100))
        else ->
            Pair(Color(0xFFE8F5E9), Color(0xFF2E7D32))
    }

    // Dosage calculation for entered acreage
    val parsedAcre = acreageInput.toDoubleOrNull() ?: 1.0
    val waterNeededLiters = (parsedAcre * 200).toInt()
    val sprayerTanks16L = (waterNeededLiters / 16.0).toInt().coerceAtLeast(1)
    val approxFungicideGrams = (waterNeededLiters * 2.0).toInt()

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("disease_diagnosis_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header: Disease Name, Severity & Accuracy
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = severityBg
                        ) {
                            Text(
                                text = "${if (language == AppLanguage.TAMIL) "தீவிரம்" else "Severity"}: ${diagnosis.severityLevel}",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = severityText,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = primaryColor.copy(alpha = 0.12f)
                        ) {
                            Text(
                                text = diagnosis.detectionSource,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.SemiBold,
                                color = primaryColor,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL) diagnosis.tamilDiseaseName else diagnosis.diseaseName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${diagnosis.cropName} (${diagnosis.tamilCropName}) • ${diagnosis.scientificName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }

                // Accuracy Percentage Pill
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "${diagnosis.confidencePercent}%",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (language == AppLanguage.TAMIL) "துல்லியம்" else "Confidence",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 9.sp,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            // Google ML Kit On-Device Image Labels Display
            if (diagnosis.detectedLabels.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Label,
                            contentDescription = null,
                            tint = Color(0xFF0277BD),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (language == AppLanguage.TAMIL) "Google ML Kit கண்டறிந்த அடையாளங்கள்:" else "ML Kit Image Labeling Tags:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0277BD)
                        )
                    }

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        contentPadding = PaddingValues(vertical = 2.dp)
                    ) {
                        items(diagnosis.detectedLabels) { label ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xFFE1F5FE),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF81D4FA))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = label.text,
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color(0xFF01579B)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "${(label.confidence * 100).toInt()}%",
                                        style = MaterialTheme.typography.labelSmall,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF0277BD)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)

            // Observed Symptoms
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL) "கண்டறியப்பட்ட அறிகுறிகள்:" else "Observed Symptoms:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                val symptoms = if (language == AppLanguage.TAMIL) diagnosis.symptomsTamil else diagnosis.symptomsEnglish
                symptoms.forEach { sym ->
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("• ", color = Color(0xFFE65100), fontWeight = FontWeight.ExtraBold)
                        Text(sym, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }

            // Step-by-Step Exact Cure Process Timeline (User Promoted Core Feature)
            if (diagnosis.stepByStepCureProcess.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFF3E5F5),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFCE93D8)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.FormatListNumbered,
                                contentDescription = "Step Process",
                                tint = Color(0xFF6A1B9A),
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.TAMIL) "படிப்படியான குணப்படுத்தும் முறை (Step-by-Step Cure Process)" else "Exact Step-by-Step Cure Process",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF4A148C)
                            )
                        }

                        diagnosis.stepByStepCureProcess.forEach { step ->
                            val stepIcon = when (step.iconCategory) {
                                "sanitation" -> Icons.Default.CleaningServices
                                "organic" -> Icons.Default.Eco
                                "chemical" -> Icons.Default.Science
                                else -> Icons.Default.Shield
                            }
                            val stepColor = when (step.iconCategory) {
                                "sanitation" -> Color(0xFFD32F2F)
                                "organic" -> Color(0xFF2E7D32)
                                "chemical" -> Color(0xFF1565C0)
                                else -> Color(0xFFF57F17)
                            }

                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = Color.White,
                                border = androidx.compose.foundation.BorderStroke(1.dp, stepColor.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.padding(10.dp),
                                    verticalAlignment = Alignment.Top
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(stepColor.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = stepIcon,
                                            contentDescription = null,
                                            tint = stepColor,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Spacer(modifier = Modifier.width(10.dp))

                                    Column(modifier = Modifier.weight(1f)) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text(
                                                text = if (language == AppLanguage.TAMIL) step.titleTamil else step.titleEnglish,
                                                style = MaterialTheme.typography.titleSmall,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF333333)
                                            )
                                            Surface(
                                                shape = RoundedCornerShape(6.dp),
                                                color = stepColor.copy(alpha = 0.15f)
                                            ) {
                                                Text(
                                                    text = step.timeframe,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    color = stepColor,
                                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                            text = if (language == AppLanguage.TAMIL) step.descriptionTamil else step.descriptionEnglish,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Section 1: Organic & Bio Remedies
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = Color(0xFFF1F8E9),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Eco,
                            contentDescription = "Organic",
                            tint = Color(0xFF1B5E20),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.TAMIL) "இயற்கை & உயிரி முறையிலான தீர்வு:" else "Organic & Bio-Remedies:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                    diagnosis.organicRemedies.forEach { rem ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("🌱 ", fontSize = 12.sp)
                            Text(rem, style = MaterialTheme.typography.bodySmall, color = Color(0xFF1B5E20), fontWeight = FontWeight.Medium)
                        }
                    }
                }
            }

            // Section 2: Chemical Treatments & Precise Dosage
            Surface(
                shape = RoundedCornerShape(14.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Science,
                            contentDescription = "Chemical",
                            tint = primaryColor,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (language == AppLanguage.TAMIL) "இரசாயன பூஞ்சாணக்கொல்லி & தெளிக்கும் அளவு:" else "Chemical Treatments & Exact Dosage:",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }
                    diagnosis.chemicalTreatments.forEach { chem ->
                        Row(modifier = Modifier.padding(vertical = 2.dp)) {
                            Text("💊 ", fontSize = 12.sp)
                            Text(chem, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                        }
                    }
                }
            }

            // Section 3: Prevention & Cultural Practices
            if (diagnosis.preventiveCare.isNotEmpty()) {
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = Color(0xFFFFF8E1),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFE082))
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Prevention",
                                tint = Color(0xFFF57F17),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (language == AppLanguage.TAMIL) "முன்னெச்சரிக்கை & பண்ணை பராமரிப்பு:" else "Prevention & Cultural Practices:",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF57F17)
                            )
                        }
                        diagnosis.preventiveCare.forEach { prev ->
                            Row(modifier = Modifier.padding(vertical = 2.dp)) {
                                Text("🛡️ ", fontSize = 11.sp)
                                Text(prev, style = MaterialTheme.typography.bodySmall, color = Color(0xFF5D4037))
                            }
                        }
                    }
                }
            }

            // Interactive Acreage & Spray Water Calculator Toggle Button
            OutlinedButton(
                onClick = onToggleDosageCalculator,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("toggle_dosage_calculator_button"),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = if (showDosageCalculator) Icons.Default.ExpandLess else Icons.Default.Calculate,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.TAMIL)
                        if (showDosageCalculator) "தெளிப்பு மருந்தளவு கணக்கீட்டை மறை" else "உங்கள் ஏக்கருக்கான மருந்து & நீர் அளவு கணக்கிடு"
                    else
                        if (showDosageCalculator) "Hide Dosage Calculator" else "Calculate Water & Chemical Volume for Your Acreage",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Animated Dosage Calculator Card
            AnimatedVisibility(visible = showDosageCalculator) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE0F2F1)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80CBC4))
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "தெளிப்பு மருந்தளவு கணக்கீடு (TNAU விகிதம்)" else "Field Spray Calculation (TNAU Standards)",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00695C)
                        )

                        OutlinedTextField(
                            value = acreageInput,
                            onValueChange = onAcreageChange,
                            label = { Text(if (language == AppLanguage.TAMIL) "நில அளவு (ஏக்கர்)" else "Farm Size (Acres)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("acreage_input_field")
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = if (language == AppLanguage.TAMIL) "தேவையான நீர்" else "Required Water",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF004D40)
                                )
                                Text(
                                    text = "$waterNeededLiters Liters",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00695C)
                                )
                            }
                            Column {
                                Text(
                                    text = if (language == AppLanguage.TAMIL) "கைத்தெளிப்பான் தொட்டி" else "16L Sprayer Tanks",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF004D40)
                                )
                                Text(
                                    text = "$sprayerTanks16L Tanks",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00695C)
                                )
                            }
                            Column {
                                Text(
                                    text = if (language == AppLanguage.TAMIL) "மருந்தளவு (தோராயம்)" else "Chemical Needed",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color(0xFF004D40)
                                )
                                Text(
                                    text = "${approxFungicideGrams}g / ml",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color(0xFF00695C)
                                )
                            }
                        }
                    }
                }
            }

            // TNAU Scientific Advisory Note
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${if (language == AppLanguage.TAMIL) "TNAU வேளாண் பல்கலைக்கழக குறிப்பு: " else "TNAU Advisory: "} ${diagnosis.tnauexpertNote}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Button to check local retailer prices for recommended medicines
            FilledTonalButton(
                onClick = onNavigateToStores,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("check_pesticide_prices_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = Color(0xFFFFF3E0),
                    contentColor = Color(0xFFE65100)
                )
            ) {
                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (language == AppLanguage.TAMIL) "மருந்து மற்றும் உரங்களின் சில்லறை விலையை சரிபார்க்க" else "Check Retailer Prices for Recommended Treatments",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            // Action Buttons: Share & Save
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onShare,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("share_diagnosis_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL) "பகிர்" else "Share",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Button(
                    onClick = onSaveRecord,
                    modifier = Modifier
                        .weight(1.3f)
                        .height(48.dp)
                        .testTag("save_diagnosis_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = primaryColor)
                ) {
                    Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (language == AppLanguage.TAMIL) "சேமி" else "Save Record",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
