package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.AppLanguage
import com.example.ui.components.UzhavBottomNav
import com.example.ui.components.UzhavTopBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.UzhavScreen
import com.example.viewmodel.UzhavViewModel
import androidx.work.*
import com.example.data.worker.UzhavSyncWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create Daily Agri-Tip notification channel
        com.example.data.AgriTipNotificationHelper.createNotificationChannel(applicationContext)

        // Schedule background synchronization worker when internet connectivity is restored
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncRequest = PeriodicWorkRequestBuilder<UzhavSyncWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "UzhavSyncWork",
            ExistingPeriodicWorkPolicy.KEEP,
            syncRequest
        )

        // Schedule Daily Agri-Tip Notification Worker (24 Hour Cycle)
        val dailyTipRequest = PeriodicWorkRequestBuilder<com.example.data.worker.DailyAgriTipWorker>(24, TimeUnit.HOURS)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "DailyAgriTipWork",
            ExistingPeriodicWorkPolicy.KEEP,
            dailyTipRequest
        )

        setContent {
            val viewModel: UzhavViewModel = viewModel()
            val state by viewModel.uiState.collectAsStateWithLifecycle()
            MyApplicationTheme(darkTheme = state.isDarkTheme) {
                UzhavApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun UzhavApp(viewModel: UzhavViewModel = viewModel()) {
    val context = androidx.compose.ui.platform.LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initScheduler(context)
        viewModel.initRoomCache(context)
    }

    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    // Hardware / System Back handler
    if (state.currentScreen != UzhavScreen.HOME) {
        BackHandler {
            viewModel.navigateTo(UzhavScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            UzhavTopBar(
                language = lang,
                currentRole = state.currentRole,
                isDarkTheme = state.isDarkTheme,
                onLanguageToggle = { viewModel.toggleLanguage() },
                onRoleToggle = { viewModel.toggleUserRole() },
                onThemeToggle = { viewModel.toggleDarkTheme() },
                onProfileClick = { viewModel.navigateTo(UzhavScreen.PROFILE_REGISTRATION) },
                onLogoClick = { viewModel.navigateTo(UzhavScreen.HOME) }
            )
        },
        bottomBar = {
            UzhavBottomNav(
                currentScreen = state.currentScreen,
                onScreenSelected = { viewModel.navigateTo(it) },
                language = lang
            )
        },
        floatingActionButton = {
            if (state.currentScreen != UzhavScreen.AI_CHATBOT) {
                ExtendedFloatingActionButton(
                    onClick = { viewModel.navigateTo(UzhavScreen.AI_CHATBOT) },
                    containerColor = primaryColor,
                    contentColor = Color.White,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.testTag("ai_assistant_fab")
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "AI Assistant",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "உழவன் AI" else "Uzhav AI",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        snackbarHost = {
            state.adminNoticeMessage?.let { notice ->
                Snackbar(
                    modifier = Modifier.padding(16.dp),
                    action = {
                        TextButton(onClick = { viewModel.dismissAdminNotice() }) {
                            Text("OK", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                ) {
                    Text(notice)
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (state.currentScreen) {
                UzhavScreen.HOME -> HomeScreen(
                    state = state,
                    onNavigate = { viewModel.navigateTo(it) },
                    onSelectMandiCrop = { viewModel.selectMandiPriceForDetail(it) },
                    onSelectStore = { viewModel.selectStoreForMap(it) },
                    onSelectScheme = { viewModel.selectSchemeForDetail(it) },
                    onRefreshDailyTip = { viewModel.fetchDailyAgriTip(forceRefresh = true) },
                    onToggleTaskCompletion = { viewModel.toggleTaskCompleted(it) }
                )
                UzhavScreen.CROP_RECOMMENDATION -> CropRecommendationScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.MANDI_PRICES -> MandiPricesScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.STORES_MAP -> StoreFinderScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.SCHEMES_2026 -> GovtSchemesScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.EQUIPMENT_MARKET -> EquipmentMarketScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.WEATHER_ADVISORY -> WeatherScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.PLANT_SCANNER -> PlantScannerScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.COMMUNITY_FORUM -> CommunityForumScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.DAILY_NEWS -> DailyNewsScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.FEEDBACK_REPORTS -> FeedbackReportScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.AI_CHATBOT -> AiChatScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.PROFILE_REGISTRATION -> ProfileScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.LOGIN -> LoginScreen(
                    language = lang,
                    onLoginSuccess = { user ->
                        viewModel.syncWithFirebaseUser(user)
                        viewModel.navigateTo(UzhavScreen.HOME)
                    },
                    onContinueAsGuest = {
                        viewModel.navigateTo(UzhavScreen.HOME)
                    }
                )
                UzhavScreen.ADMIN_DASHBOARD -> StoreFinderScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.CROP_TASK_SCHEDULE -> CropTaskScheduleScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.IRRIGATION_SCHEDULER -> IrrigationSchedulerScreen(
                    viewModel = viewModel,
                    state = state
                )
                UzhavScreen.FERTILIZER_PRICING -> FertilizerPriceScreen(
                    uiState = state,
                    viewModel = viewModel,
                    onNavigateBack = { viewModel.navigateTo(UzhavScreen.HOME) }
                )
            }
        }
    }
}
