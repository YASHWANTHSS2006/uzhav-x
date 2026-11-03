package com.example.ui.components

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SpeechRecognitionHelper
import com.example.model.AppLanguage
import com.example.model.GovtScheme
import com.example.model.MandiPrice
import com.example.model.AgriStore
import com.example.viewmodel.UzhavScreen
import com.example.viewmodel.UzhavUiState

sealed class SearchResultItem {
    data class CropResult(val mandi: MandiPrice) : SearchResultItem()
    data class StoreProductResult(val store: AgriStore, val productName: String, val priceText: String) : SearchResultItem()
    data class SchemeResult(val scheme: GovtScheme) : SearchResultItem()
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PredictiveSearchBar(
    state: UzhavUiState,
    onNavigate: (UzhavScreen) -> Unit,
    onSelectMandiCrop: (MandiPrice) -> Unit,
    onSelectStore: (AgriStore) -> Unit,
    onSelectScheme: (GovtScheme) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = state.language
    var searchQuery by remember { mutableStateOf("") }
    var isListening by remember { mutableStateOf(false) }
    var isExpanded by remember { mutableStateOf(false) }

    val speechHelper = remember(lang) {
        val localeCode = if (lang == AppLanguage.TAMIL) "ta-IN" else "en-IN"
        SpeechRecognitionHelper(
            context = context,
            languageCode = localeCode,
            onResult = { voiceText ->
                searchQuery = voiceText
                isExpanded = true
                Toast.makeText(context, "Voice input: $voiceText", Toast.LENGTH_SHORT).show()
            },
            onError = { err ->
                Toast.makeText(context, err, Toast.LENGTH_SHORT).show()
            },
            onListeningChanged = { listening ->
                isListening = listening
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            speechHelper.startListening()
        } else {
            Toast.makeText(context, "Audio permission required for voice search", Toast.LENGTH_SHORT).show()
        }
    }

    // Filter Logic
    val searchResults = remember(searchQuery, state.mandiPrices, state.stores, state.schemes) {
        val q = searchQuery.trim().lowercase()
        if (q.length < 2) {
            emptyList()
        } else {
            val results = mutableListOf<SearchResultItem>()

            // 1. Filter Crops
            state.mandiPrices.filter {
                it.cropName.lowercase().contains(q) ||
                it.tamilName.lowercase().contains(q) ||
                it.marketName.lowercase().contains(q) ||
                it.district.lowercase().contains(q)
            }.take(4).forEach {
                results.add(SearchResultItem.CropResult(it))
            }

            // 2. Filter Fertilizer Stores / Products
            state.stores.forEach { store ->
                val storeTitle = if (lang == AppLanguage.TAMIL && store.tamilName.isNotBlank()) store.tamilName else store.name
                if (store.name.lowercase().contains(q) || store.tamilName.lowercase().contains(q) || store.district.lowercase().contains(q)) {
                    results.add(SearchResultItem.StoreProductResult(store, storeTitle, "View Shop Stock"))
                } else {
                    store.products.filter {
                        it.name.lowercase().contains(q) || it.tamilName.lowercase().contains(q) || it.brand.lowercase().contains(q)
                    }.take(2).forEach { prod ->
                        val price = if (prod.subsidizedPrice != null) "₹${prod.subsidizedPrice} (Subsidy)" else "₹${prod.standardPrice}"
                        results.add(SearchResultItem.StoreProductResult(store, "${prod.name} (${prod.tamilName})", price))
                    }
                }
            }

            // 3. Filter Government Schemes
            state.schemes.filter {
                it.title.lowercase().contains(q) ||
                it.tamilTitle.lowercase().contains(q) ||
                it.schemeCode.lowercase().contains(q) ||
                it.summaryEnglish.lowercase().contains(q) ||
                it.summaryTamil.lowercase().contains(q)
            }.take(3).forEach {
                results.add(SearchResultItem.SchemeResult(it))
            }

            results.distinctBy {
                when (it) {
                    is SearchResultItem.CropResult -> "crop_${it.mandi.id}"
                    is SearchResultItem.StoreProductResult -> "store_${it.store.id}_${it.productName}"
                    is SearchResultItem.SchemeResult -> "scheme_${it.scheme.id}"
                }
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("predictive_search_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    isExpanded = it.trim().length >= 2
                },
                placeholder = {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "பயிர், உரம், மண்டி விலை அல்லது மானியம் தேடுக..." else "Search crops, fertilizers, mandi prices, or schemes...",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.outline
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = MaterialTheme.colorScheme.primary
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = {
                                searchQuery = ""
                                isExpanded = false
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = MaterialTheme.colorScheme.outline)
                            }
                        }

                        // Voice Search Button
                        IconButton(
                            onClick = {
                                if (isListening) {
                                    speechHelper.stopListening()
                                } else {
                                    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                                        speechHelper.startListening()
                                    } else {
                                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            },
                            modifier = Modifier.testTag("search_voice_mic_btn")
                        ) {
                            Icon(
                                imageVector = if (isListening) Icons.Default.GraphicEq else Icons.Default.Mic,
                                contentDescription = "Voice Search",
                                tint = if (isListening) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("predictive_search_input")
            )

            // Voice Speech Indicator
            if (isListening) {
                Spacer(modifier = Modifier.height(6.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.errorContainer
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Mic, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "குரல் பதிவு செயலில் உள்ளது... தமிழில் பேசுங்கள்" else "Listening... Speak crop or fertilizer name now",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            }

            // Predictive Search Dropdown Panel
            AnimatedVisibility(visible = isExpanded && searchResults.isNotEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "தேடல் முடிவுகள் (${searchResults.size}):" else "Search Suggestions (${searchResults.size}):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(bottom = 6.dp)
                    )

                    Divider(color = MaterialTheme.colorScheme.outlineVariant)

                    LazyColumn(
                        modifier = Modifier
                            .heightIn(max = 240.dp)
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(searchResults) { item ->
                            when (item) {
                                is SearchResultItem.CropResult -> {
                                    val m = item.mandi
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSelectMandiCrop(m)
                                                onNavigate(UzhavScreen.MANDI_PRICES)
                                                isExpanded = false
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🌾", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (lang == AppLanguage.TAMIL && m.tamilName.isNotBlank()) m.tamilName else m.cropName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${m.marketName}, ${m.district}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            Text(
                                                text = "₹${m.modalPrice}/${m.unit.substringBefore(" ")}",
                                                fontWeight = FontWeight.ExtraBold,
                                                color = MaterialTheme.colorScheme.primary,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }
                                is SearchResultItem.StoreProductResult -> {
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSelectStore(item.store)
                                                onNavigate(UzhavScreen.STORES_MAP)
                                                isExpanded = false
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🧪", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = item.productName,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold
                                                )
                                                Text(
                                                    text = "${if (lang == AppLanguage.TAMIL && item.store.tamilName.isNotBlank()) item.store.tamilName else item.store.name} (${item.store.district})",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            Text(
                                                text = item.priceText,
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF2E7D32),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                                is SearchResultItem.SchemeResult -> {
                                    val s = item.scheme
                                    Surface(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable {
                                                onSelectScheme(s)
                                                onNavigate(UzhavScreen.SCHEMES_2026)
                                                isExpanded = false
                                            },
                                        shape = RoundedCornerShape(8.dp),
                                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    ) {
                                        Row(
                                            modifier = Modifier.padding(10.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("🏛️", fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(10.dp))
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = if (lang == AppLanguage.TAMIL && s.tamilTitle.isNotBlank()) s.tamilTitle else s.title,
                                                    style = MaterialTheme.typography.titleSmall,
                                                    fontWeight = FontWeight.Bold,
                                                    maxLines = 1
                                                )
                                                Text(
                                                    text = "${s.department} • Code: ${s.schemeCode}",
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                            Text(
                                                text = s.subsidyPercentage.take(12),
                                                fontWeight = FontWeight.Bold,
                                                color = Color(0xFF1565C0),
                                                fontSize = 11.sp
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (isExpanded && searchResults.isEmpty() && searchQuery.trim().length >= 2) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = if (lang == AppLanguage.TAMIL) "பொருந்தக்கூடிய பயிர் அல்லது மானியங்கள் காணப்படவில்லை." else "No matching crop, store or government scheme found.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}
