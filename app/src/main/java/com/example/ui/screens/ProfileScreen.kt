package com.example.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.*
import com.example.ui.components.LanguageSelectionCard
import com.example.ui.components.SoilHealthCard
import com.example.ui.components.SoilTestDialog
import com.example.viewmodel.UzhavViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val profile = state.farmerProfile
    val primaryColor = MaterialTheme.colorScheme.primary

    var name by remember { mutableStateOf(profile.name) }
    var contact by remember { mutableStateOf(profile.contact) }
    var district by remember { mutableStateOf(profile.district) }
    var taluka by remember { mutableStateOf(profile.taluka) }
    var village by remember { mutableStateOf(profile.village) }
    var landSize by remember { mutableStateOf(profile.landSizeAcres.toString()) }
    var soilType by remember { mutableStateOf(profile.soilType) }
    var waterSource by remember { mutableStateOf(profile.waterSource) }

    var districtExpanded by remember { mutableStateOf(false) }
    var soilExpanded by remember { mutableStateOf(false) }
    var savedNotice by remember { mutableStateOf(false) }
    var showProfileExportDialog by remember { mutableStateOf(false) }
    var showSoilTestDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("profile_registration_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = "Profile",
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "விவசாயி பதிவு & பண்ணை விவரம்" else "Farmer Registration & Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = if (lang == AppLanguage.TAMIL) "உங்கள் ஊர் மற்றும் மண் வகையை பதிவு செய்வதன் மூலம் துல்லியமான பயிர் பரிந்துரைகளையும் மானிய தகவல்களையும் பெறலாம்."
                    else "Keep your farm details updated to receive localized mandi price alerts and tailored crop advisories.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Language Switcher Component
        item {
            LanguageSelectionCard(
                currentLanguage = lang,
                onLanguageSelected = { newLanguage ->
                    viewModel.setLanguage(newLanguage)
                }
            )
        }

        // Google Authentication Status & Login Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (profile.isRegistered) Color(0xFFE8F5E9) else Color(0xFFF1F8E9)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (profile.isRegistered) Icons.Default.Verified else Icons.Default.Lock,
                                contentDescription = null,
                                tint = if (profile.isRegistered) Color(0xFF2E7D32) else Color(0xFF388E3C),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (profile.isRegistered)
                                    (if (lang == AppLanguage.TAMIL) "Google கணக்கு இணைக்கப்பட்டது" else "Google Account Linked")
                                else
                                    (if (lang == AppLanguage.TAMIL) "பாதுகாப்பான உள்நுழைவு" else "Secure Login"),
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1B5E20)
                            )
                        }
                        Text(
                            text = if (profile.isRegistered)
                                profile.contact.ifBlank { profile.name }
                            else
                                (if (lang == AppLanguage.TAMIL) "மேகக்கணியில் தரவுகளை சேமிக்க உள்நுழைக" else "Sign in with Google to sync farm records"),
                            fontSize = 12.sp,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Button(
                        onClick = { viewModel.navigateTo(com.example.viewmodel.UzhavScreen.LOGIN) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = if (profile.isRegistered)
                                (if (lang == AppLanguage.TAMIL) "மாற்று" else "Manage")
                            else
                                (if (lang == AppLanguage.TAMIL) "உள்நுழை" else "Sign In"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Offline Data Export Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.4f)
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Download,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "ஆஃப்லைன் அறிக்கைகள் (PDF / CSV)" else "Offline Data Export (PDF / CSV)",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                        }
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மண்டி விலைகள் & மானிய தகவல்களை PDF/CSV ஆக பதிவிறக்குக" else "Export current mandi prices & saved subsidies to local files",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Button(
                        onClick = { showProfileExportDialog = true },
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("profile_export_reports_btn")
                    ) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பதிவிறக்கு" else "Export",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Soil Health Test Card Item
        item {
            SoilHealthCard(
                soilTestResult = profile.soilTestResult,
                language = lang,
                onEditClicked = { showSoilTestDialog = true }
            )
        }

        // Form Fields
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Full Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "விவசாயி பெயர்" else "Farmer Full Name") },
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Phone or Email ID
                    OutlinedTextField(
                        value = contact,
                        onValueChange = { contact = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "தொலைபேசி எண் / மின்னஞ்சல்" else "Mobile Number / Email ID") },
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // State (Tamil Nadu)
                    OutlinedTextField(
                        value = "Tamil Nadu (தமிழ்நாடு)",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(if (lang == AppLanguage.TAMIL) "மாநிலம்" else "State") },
                        leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // District Dropdown
                    ExposedDropdownMenuBox(
                        expanded = districtExpanded,
                        onExpandedChange = { districtExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = district,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == AppLanguage.TAMIL) "மாவட்டம்" else "District") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = districtExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = districtExpanded,
                            onDismissRequest = { districtExpanded = false }
                        ) {
                            TAMIL_NADU_DISTRICTS.forEach { dist ->
                                DropdownMenuItem(
                                    text = { Text(dist) },
                                    onClick = {
                                        district = dist
                                        districtExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Taluka / Region & Village
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = taluka,
                            onValueChange = { taluka = it },
                            label = { Text(if (lang == AppLanguage.TAMIL) "வட்டம் / தாலுகா" else "Taluk / Region") },
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = village,
                            onValueChange = { village = it },
                            label = { Text(if (lang == AppLanguage.TAMIL) "கிராமம்" else "Village") },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    // Land Size
                    OutlinedTextField(
                        value = landSize,
                        onValueChange = { landSize = it },
                        label = { Text(if (lang == AppLanguage.TAMIL) "நில அளவு (ஏக்கரில்)" else "Land Size (Acres)") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    // Soil Type
                    ExposedDropdownMenuBox(
                        expanded = soilExpanded,
                        onExpandedChange = { soilExpanded = it },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = soilType,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text(if (lang == AppLanguage.TAMIL) "மண் வகை" else "Soil Type") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = soilExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = soilExpanded,
                            onDismissRequest = { soilExpanded = false }
                        ) {
                            SOIL_TYPES.forEach { soil ->
                                DropdownMenuItem(
                                    text = { Text(soil) },
                                    onClick = {
                                        soilType = soil
                                        soilExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Role Selection (Farmer vs Admin)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "செயலி பயன்பாட்டு உரிமை" else "App Role Access",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = state.currentRole.getDisplayName(lang),
                                style = MaterialTheme.typography.bodySmall,
                                color = primaryColor
                            )
                        }
                        Button(
                            onClick = { viewModel.toggleUserRole() },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(if (state.currentRole == UserRole.FARMER) "Switch to Admin" else "Switch to Farmer")
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    // Save Button
                    Button(
                        onClick = {
                            val acres = landSize.toDoubleOrNull() ?: profile.landSizeAcres
                            val updated = profile.copy(
                                name = name,
                                contact = contact,
                                district = district,
                                taluka = taluka,
                                village = village,
                                landSizeAcres = acres,
                                soilType = soilType,
                                waterSource = waterSource
                            )
                            viewModel.updateProfile(updated)
                            savedNotice = true
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save")
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "சுயவிவரத்தை சேமிக்க" else "Save Farm Profile",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (savedNotice) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "✔ சுயவிவரம் வெற்றிகரமாக சேமிக்கப்பட்டது!" else "✔ Profile saved successfully!",
                            color = primaryColor,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // App Customization, Privacy & Full Data Access Controls
        item {
            var cloudSync by remember { mutableStateOf(state.isCloudSyncEnabled) }
            var phonePrivate by remember { mutableStateOf(state.isPhonePrivate) }
            var largeFont by remember { mutableStateOf(state.appFontSizeScale > 1.0f) }

            var showEditPriceDialog by remember { mutableStateOf(false) }
            var showAddSchemeDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SettingsSuggest, contentDescription = "Settings", tint = primaryColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "அமைப்புகள் & தரவு மேலாண்மை (Alterations)" else "App Settings & Data Alteration Control",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    Text(
                        text = if (lang == AppLanguage.TAMIL)
                            "செயலியின் லோகோ, எழுத்து அளவு, பயிர் சந்தை விலைகள், உரக்கடை இருப்பு மற்றும் சுயவிவர மேகக்கணி சேமிப்பை மாற்றியமைக்கலாம்."
                        else
                            "Full access control: modify prices, store stock, fonts, logo preferences, and cloud database privacy settings.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Privacy & Display Toggles
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பாதுகாப்பான மேகக்கணி சேமிப்பு (Firestore DB)" else "Secure Cloud Sync (Firestore DB)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "சுயவிவரம் & பண்ணை பதிவுகளை என்க்ரிப்ட் செய்து சேமிக்கும்" else "Encrypted profile & farm database backup",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Switch(
                            checked = cloudSync,
                            onCheckedChange = {
                                cloudSync = it
                                viewModel.updatePrivacyAndSettings(cloudSync, phonePrivate, if (largeFont) 1.2f else 1.0f)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "தொலைபேசி எண் ரகசியம் (Privacy Protection)" else "Hide Mobile No. in Public Forum",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "விவசாயிகள் மன்றத்தில் உங்கள் எண் பிறருக்கு தெரியாது" else "Keeps your contact private in community discussions",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Switch(
                            checked = phonePrivate,
                            onCheckedChange = {
                                phonePrivate = it
                                viewModel.updatePrivacyAndSettings(cloudSync, phonePrivate, if (largeFont) 1.2f else 1.0f)
                            }
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பெரிய எழுத்துக்கள் (Large Font Accessibility)" else "Large Text Mode (Elder Farmer Accessibility)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "வாசிப்பை எளிதாக்க எழுத்து அளவை அதிகரிக்கும்" else "Increases typography scale for easy reading",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Switch(
                            checked = largeFont,
                            onCheckedChange = {
                                largeFont = it
                                viewModel.updatePrivacyAndSettings(cloudSync, phonePrivate, if (largeFont) 1.2f else 1.0f)
                            }
                        )
                    }

                    // Daily Agri-Tip Notification Settings
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "தினசரி வேளாண் குறிப்பு அறிவிப்பு" else "Daily Agri-Tip Push Notifications",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "பருவமழை மற்றும் பயிர் நோய் தடுப்பு தகவல்கள் நாள்தோறும் கிடைக்கும்" else "Receive automated seasonal farming advice every morning",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                        Switch(
                            checked = state.isDailyNotificationEnabled,
                            onCheckedChange = { viewModel.toggleDailyNotifications(it) }
                        )
                    }

                    if (state.isDailyNotificationEnabled) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "அறிவிப்பு நேரம்:" else "Scheduled Time:",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                listOf("06:00 AM", "12:00 PM", "06:00 PM").forEach { time ->
                                    FilterChip(
                                        selected = state.preferredNotificationTime == time,
                                        onClick = { viewModel.setPreferredNotificationTime(time) },
                                        label = { Text(time, fontSize = 10.sp) }
                                    )
                                }
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    // Data Alteration Action Buttons
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "நேரடி தரவு திருத்தம் & புதுப்பிப்பு (Data Alterations):" else "Live Data & Price Alterations:",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { showEditPriceDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "மண்டி விலை மாற்ற" else "Edit Prices",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Button(
                            onClick = { showAddSchemeDialog = true },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "மானிய திட்டம் சேர்க்க" else "Add Scheme",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dialog for editing/adding mandi crop price
            if (showEditPriceDialog) {
                var newCropName by remember { mutableStateOf("") }
                var newTamilName by remember { mutableStateOf("") }
                var newMarket by remember { mutableStateOf("Oddanchatram / ஒட்டன்சத்திரம்") }
                var newPriceText by remember { mutableStateOf("2500") }

                AlertDialog(
                    onDismissRequest = { showEditPriceDialog = false },
                    title = { Text(if (lang == AppLanguage.TAMIL) "பயிர் மண்டி விலை மாற்றம் / சேர்க்கை" else "Update / Add Mandi Crop Price") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = newCropName,
                                onValueChange = { newCropName = it },
                                label = { Text("Crop Name (English)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newTamilName,
                                onValueChange = { newTamilName = it },
                                label = { Text("பயிர் பெயர் (தமிழ்)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newMarket,
                                onValueChange = { newMarket = it },
                                label = { Text("Market / மண்டி") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = newPriceText,
                                onValueChange = { newPriceText = it },
                                label = { Text("Price (₹ per Quintal)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            val p = newPriceText.toDoubleOrNull() ?: 2500.0
                            if (newCropName.isNotBlank()) {
                                viewModel.adminAddCustomCropPrice(
                                    cropName = newCropName,
                                    tamilName = newTamilName.ifBlank { newCropName },
                                    category = "Cereals & Pulses",
                                    marketName = newMarket,
                                    district = profile.district,
                                    modalPrice = p
                                )
                            }
                            showEditPriceDialog = false
                        }) {
                            Text(if (lang == AppLanguage.TAMIL) "சேமி" else "Save Price")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showEditPriceDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            // Dialog for adding custom scheme
            if (showAddSchemeDialog) {
                var schemeTitle by remember { mutableStateOf("") }
                var schemeTamilTitle by remember { mutableStateOf("") }
                var dept by remember { mutableStateOf("TN Agriculture Department") }
                var benefit by remember { mutableStateOf("Up to ₹50,000 / ha") }

                AlertDialog(
                    onDismissRequest = { showAddSchemeDialog = false },
                    title = { Text(if (lang == AppLanguage.TAMIL) "புதிய அரசு மானிய திட்டம் சேர்க்க" else "Add New Government Scheme") },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = schemeTitle,
                                onValueChange = { schemeTitle = it },
                                label = { Text("Scheme Title (English)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = schemeTamilTitle,
                                onValueChange = { schemeTamilTitle = it },
                                label = { Text("திட்டம் தலைப்பு (தமிழ்)") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = dept,
                                onValueChange = { dept = it },
                                label = { Text("Department") },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = benefit,
                                onValueChange = { benefit = it },
                                label = { Text("Financial Benefit") },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    },
                    confirmButton = {
                        Button(onClick = {
                            if (schemeTitle.isNotBlank()) {
                                viewModel.adminAddCustomScheme(
                                    title = schemeTitle,
                                    tamilTitle = schemeTamilTitle.ifBlank { schemeTitle },
                                    category = SchemeCategory.IRRIGATION_SUBSIDY,
                                    department = dept,
                                    maxBenefit = benefit,
                                    portalUrl = "https://tnagrisnet.tn.gov.in"
                                )
                            }
                            showAddSchemeDialog = false
                        }) {
                            Text(if (lang == AppLanguage.TAMIL) "சேர்" else "Add Scheme")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showAddSchemeDialog = false }) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }

        // Tech Stack & Full Stack Architecture Card
        item {
            var showTechStackDialog by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Layers, contentDescription = "Stack", tint = primaryColor)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "உழவன் X: தொழில்நுட்ப அடுக்கு (Tech Stack)" else "Uzhav X: Full Tech Stack Architecture",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = if (lang == AppLanguage.TAMIL) "Kotlin, Jetpack Compose M3, Room SQLite, AI கணிப்பான், மற்றும் இரட்டை பயனர்/நிர்வாகி அணுகல் கட்டமைப்பு."
                        else "Modern Android architecture powered by Kotlin, Jetpack Compose M3, Room DB, Gemini AI, and dual Role-Based Access Control.",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )

                    OutlinedButton(
                        onClick = { showTechStackDialog = true },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(if (lang == AppLanguage.TAMIL) "முழு தொழில்நுட்ப விவரங்களை காண்க" else "View Full Stack Architecture")
                    }
                }
            }

            if (showTechStackDialog) {
                AlertDialog(
                    onDismissRequest = { showTechStackDialog = false },
                    title = {
                        Text(
                            text = "Uzhav X - Full Stack Architecture",
                            fontWeight = FontWeight.Bold
                        )
                    },
                    text = {
                        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            item {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE8F5E9)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("1. Presentation Layer (Mobile UI)", fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
                                        Text("• Jetpack Compose Material 3 with adaptive layouts\n• Canvas graphics for 7-day Mandi line charts & TN agro-hub maps\n• Dynamic English & Tamil (தமிழ்) runtime localization", fontSize = 12.sp)
                                    }
                                }
                            }
                            item {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFE3F2FD)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("2. Business Logic & State", fontWeight = FontWeight.Bold, color = Color(0xFF1565C0))
                                        Text("• MVVM with StateFlow & collectAsStateWithLifecycle\n• Reactive unidirectional data flow\n• Coroutine dispatchers for asynchronous background calculations", fontSize = 12.sp)
                                    }
                                }
                            }
                            item {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFFFF3E0)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("3. AI Intelligence & Agronomy Engine", fontWeight = FontWeight.Bold, color = Color(0xFFE65100))
                                        Text("• Gemini AI API with Tamil agriculture system prompt\n• Computer Vision Leaf Disease Scanner\n• TNAU district-soil-season profit optimization algorithm", fontSize = 12.sp)
                                    }
                                }
                            }
                            item {
                                Surface(shape = RoundedCornerShape(8.dp), color = Color(0xFFF3E5F5)) {
                                    Column(modifier = Modifier.padding(10.dp)) {
                                        Text("4. Local & Cloud Data Persistence", fontWeight = FontWeight.Bold, color = Color(0xFF7B1FA2))
                                        Text("• Room SQLite Database with DAO for offline caching\n• Dual Role Access Control (Farmer User vs Store Admin)\n• Retrofit 2 + Moshi + OkHttp network stack", fontSize = 12.sp)
                                    }
                                }
                            }
                        }
                    },
                    confirmButton = {
                        Button(onClick = { showTechStackDialog = false }) {
                            Text("Close")
                        }
                    }
                )
            }
        }
    }

    if (showProfileExportDialog) {
        com.example.ui.components.ExportDialog(
            language = lang,
            exportType = com.example.ui.components.ExportType.ALL,
            mandiPrices = state.mandiPrices,
            schemes = state.schemes,
            onDismiss = { showProfileExportDialog = false }
        )
    }

    if (showSoilTestDialog) {
        SoilTestDialog(
            currentResult = profile.soilTestResult,
            language = lang,
            onDismiss = { showSoilTestDialog = false },
            onSave = { updatedResult ->
                viewModel.updateSoilTestResult(updatedResult)
                showSoilTestDialog = false
            }
        )
    }
}
