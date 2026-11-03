package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.viewmodel.UzhavScreen
import com.example.viewmodel.UzhavUiState

@Composable
fun HomeScreen(
    state: UzhavUiState,
    onNavigate: (UzhavScreen) -> Unit,
    onSelectMandiCrop: (com.example.model.MandiPrice) -> Unit = {},
    onSelectStore: (com.example.model.AgriStore) -> Unit = {},
    onSelectScheme: (com.example.model.GovtScheme) -> Unit = {},
    onRefreshDailyTip: () -> Unit = {},
    onToggleTaskCompletion: (com.example.model.FarmTaskReminder) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("home_screen_scroll"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Hero Brand Banner with Uzhav X Logo & Welcome
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_brand_card"),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1B5E20),
                                    Color(0xFF2E7D32),
                                    Color(0xFF388E3C)
                                )
                            )
                        )
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.uzhav_x_logo_1793458271833),
                            contentDescription = "Uzhav X Logo",
                            modifier = Modifier
                                .size(72.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .border(2.dp, Color.White.copy(alpha = 0.8f), RoundedCornerShape(16.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(14.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "வணக்கம், ${state.farmerProfile.name}" else "Vanakkam, ${state.farmerProfile.name}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "உழவன் X: விவசாயத்தின் எதிர்காலம்" else "Uzhav X: The Future of Farming",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = "${state.farmerProfile.village}, ${state.farmerProfile.district} • ${state.farmerProfile.soilType.substringBefore(" (")}",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.85f)
                            )
                        }
                    }
                }
            }
        }

        // 1b. Predictive Search Bar with Voice Input
        item {
            com.example.ui.components.PredictiveSearchBar(
                state = state,
                onNavigate = onNavigate,
                onSelectMandiCrop = onSelectMandiCrop,
                onSelectStore = onSelectStore,
                onSelectScheme = onSelectScheme
            )
        }

        // 1c. Daily Agri-Tip of the Day (Seasonal Gemini AI Guidance)
        state.dailyAgriTip?.let { tip ->
            item {
                com.example.ui.components.DailyAgriTipCard(
                    tip = tip,
                    language = lang,
                    onRefreshTip = onRefreshDailyTip,
                    isLoading = state.isTipLoading
                )
            }
        }

        // 2. Weather & Agro-Advisory Pill
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.WEATHER_ADVISORY) }
                    .testTag("weather_advisory_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color(0xFFFFECB3), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WbSunny,
                            contentDescription = "Weather",
                            tint = Color(0xFFE65100),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${state.weatherReport.currentTempC}°C",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) state.weatherReport.tamilCondition else state.weatherReport.condition,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium
                            )
                        }
                        Text(
                            text = if (lang == AppLanguage.TAMIL) state.weatherReport.currentAdvisoryTamil else state.weatherReport.currentAdvisoryEnglish,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            maxLines = 2
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "View Weather",
                        tint = MaterialTheme.colorScheme.outline
                    )
                }
            }
        }

        // 3. Featured Feature: AI Crop Recommendation Callout (User's primary prompt request)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.CROP_RECOMMENDATION) }
                    .testTag("crop_recommendation_promo_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(primaryColor, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Psychology,
                            contentDescription = "AI Crop Advisor",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = primaryColor
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "AI பயிர் பரிந்துரை & லாபம்" else "AI Crop Recommendation",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "மண், பருவமழை & சந்தை விலைக்கு உகந்த பயிரை கண்டறி" else "Find Most Suitable & Profitable Crop for your Soil",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "ஏக்கர் வாரியான செலவு, விளைச்சல் மற்றும் நிகர லாபக் கணக்கீடு" else "Yield, cost of cultivation and net profit projection",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open",
                        tint = primaryColor
                    )
                }
            }
        }

        // 3a. AI Smart Irrigation & Water Management Scheduler
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.IRRIGATION_SCHEDULER) }
                    .testTag("dashboard_irrigation_tool_card"),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFFE0F7FA)
                ),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF80DEEA))
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(Color(0xFF00838F), RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WaterDrop,
                            contentDescription = "AI Irrigation Scheduler",
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = Color(0xFF00838F)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "AI நீர் பாசன மேலாண்மை" else "AI Irrigation & Water Saver",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பயிர் வகை & மண் ஈரப்பதம் கொண்டு பாசன அட்டவணை பெறுக" else "Input Crop & Soil Moisture for Smart Water Schedule",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF004D40)
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "சொட்டு நீர் பாசன நேரம், நீர் அளவு மற்றும் 40% வரை நீர் சேமிப்பு" else "Optimal runtime, water volume & up to 40% water savings",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF00695C)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Open Irrigation Tool",
                        tint = Color(0xFF00838F)
                    )
                }
            }
        }

        // 3b. Interactive Shared Agricultural Task Calendar Integration
        item {
            com.example.ui.components.SharedAgriCalendarCard(
                tasks = state.taskReminders,
                language = lang,
                onToggleTaskCompletion = onToggleTaskCompletion,
                onNavigateToFullSchedule = { onNavigate(UzhavScreen.CROP_TASK_SCHEDULE) }
            )
        }

        // 4. Quick Action Grid (Equipment purchase, Daily news, Forum, Plant scanner, Grievance report)
        item {
            Text(
                text = if (lang == AppLanguage.TAMIL) "முக்கிய சேவைகள் & கருவிகள்" else "Core Services & Tools",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile(
                    title = if (lang == AppLanguage.TAMIL) "வேளாண் கருவிகள் வாங்க" else "Buy Equipments",
                    subtitle = if (lang == AppLanguage.TAMIL) "டிராக்டர், ஸ்ப்ரேயர்" else "Tractors & Drip",
                    icon = Icons.Default.Agriculture,
                    iconColor = Color(0xFFD84315),
                    containerColor = Color(0xFFFFCCBC).copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UzhavScreen.EQUIPMENT_MARKET) }
                )
                QuickTile(
                    title = if (lang == AppLanguage.TAMIL) "தினசரி செய்திகள்" else "Agri News",
                    subtitle = if (lang == AppLanguage.TAMIL) "அரசு அறிவிப்புகள்" else "Govt & Dam Alerts",
                    icon = Icons.Default.Newspaper,
                    iconColor = Color(0xFF00695C),
                    containerColor = Color(0xFFB2DFDB).copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UzhavScreen.DAILY_NEWS) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickTile(
                    title = if (lang == AppLanguage.TAMIL) "விவசாயிகள் மன்றம்" else "Farmers Forum",
                    subtitle = if (lang == AppLanguage.TAMIL) "விஞ்ஞானிகள் பதில்" else "KVK Experts Q&A",
                    icon = Icons.Default.Forum,
                    iconColor = Color(0xFF1565C0),
                    containerColor = Color(0xFFBBDEFB).copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UzhavScreen.COMMUNITY_FORUM) }
                )
                QuickTile(
                    title = if (lang == AppLanguage.TAMIL) "கருத்து & புகார்" else "Feedback & Issue",
                    subtitle = if (lang == AppLanguage.TAMIL) "வாய்க்கால், உர தட்டுப்பாடு" else "Report Canal / PACCS",
                    icon = Icons.Default.ReportProblem,
                    iconColor = Color(0xFFC2185B),
                    containerColor = Color(0xFFF8BBD0).copy(alpha = 0.5f),
                    modifier = Modifier.weight(1f),
                    onClick = { onNavigate(UzhavScreen.FEEDBACK_REPORTS) }
                )
            }
        }

        // Fertilizer Stores Map & Subsidized Price Matrix
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.STORES_MAP) }
                    .testTag("stores_map_home_banner"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFF2E7D32), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = "Stores Map",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "உரக்கடைகள் வரைபடம் & நேரடி விலை" else "Fertilizer Stores Map & Live Prices",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "PACCS கூட்டுறவு, மானிய யூரியா ₹266, DAP ₹1350 இருப்பு மற்றும் தூர வடிகட்டி" else "PACCS co-ops, Urea ₹266, DAP ₹1350 live stock & proximity filter",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF388E3C)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "View Map",
                        tint = Color(0xFF2E7D32)
                    )
                }
            }
        }

        // Live Fertilizer Pricing & Retailer Comparison Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.FERTILIZER_PRICING) }
                    .testTag("dashboard_fertilizer_price_card"),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFFB74D))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(Color(0xFFE65100), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Sell,
                            contentDescription = "Fertilizer Prices",
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFE65100)
                        ) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "நேரடி உர விலை & ஒப்பீடு" else "Live Fertilizer Prices & MRP",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "சில்லறை கடை விலை ஒப்பீடு & தேவை மூட்டை கணக்கீடு" else "Compare Retailer Prices vs PACCS MRP & Calculate Bags",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFBF360C)
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "யூரியா ₹266/மூட்டை • DAP ₹1350 • பொட்டாஷ் ₹1700" else "Neem Urea ₹266 • DAP ₹1350 • MOP Potash ₹1700",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFFE65100)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "View Prices",
                        tint = Color(0xFFE65100)
                    )
                }
            }
        }

        // 5. Today's Mandi Prices Snapshot
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (lang == AppLanguage.TAMIL) "இன்றைய மண்டி நிலவரம் (தமிழ்நாடு)" else "Today's Mandi Prices (Tamil Nadu)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(onClick = { onNavigate(UzhavScreen.MANDI_PRICES) }) {
                    Text(text = if (lang == AppLanguage.TAMIL) "மேலும் காண்க" else "View All")
                }
            }
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.mandiPrices.take(4)) { mandi ->
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .clickable { onNavigate(UzhavScreen.MANDI_PRICES) },
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) mandi.tamilName else mandi.cropName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Text(
                                text = mandi.marketName,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.outline,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "₹${mandi.modalPrice}",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = primaryColor
                                )
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = if (mandi.isPositive) Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                                ) {
                                    Text(
                                        text = "${if (mandi.isPositive) "▲ +" else "▼ "}${mandi.changeAmount}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (mandi.isPositive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // 6. 2026 Schemes Highlights Banner
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(UzhavScreen.SCHEMES_2026) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "2026 Schemes",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.size(32.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "2026-27 தமிழக அரசு மானியங்கள்" else "2026-27 Government Agri Schemes",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "100% சொட்டு நீர், சோலார் பம்பு செட் 70%, வட்டி இல்லா பயிர் கடன்" else "100% Drip subsidy, 70% Solar pump, 0% KCC loan",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.85f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Explore",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}

@Composable
fun QuickTile(
    title: String,
    subtitle: String,
    icon: ImageVector,
    iconColor: Color,
    containerColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = iconColor,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                maxLines = 1
            )
        }
    }
}
