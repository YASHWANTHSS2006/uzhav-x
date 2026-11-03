package com.example.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalFlorist
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Store
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.outlined.AccountBalance
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.LocalFlorist
import androidx.compose.material.icons.outlined.MedicalServices
import androidx.compose.material.icons.outlined.Store
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.viewmodel.UzhavScreen

@Composable
fun UzhavBottomNav(
    currentScreen: UzhavScreen,
    onScreenSelected: (UzhavScreen) -> Unit,
    language: AppLanguage,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.navigationBarsPadding(),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = NavigationBarDefaults.Elevation
    ) {
        // 1. Home
        NavigationBarItem(
            selected = currentScreen == UzhavScreen.HOME,
            onClick = { onScreenSelected(UzhavScreen.HOME) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == UzhavScreen.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.TAMIL) "முகப்பு" else "Home",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == UzhavScreen.HOME) FontWeight.Bold else FontWeight.Normal
                )
            }
        )

        // 2. Mandi Prices
        NavigationBarItem(
            selected = currentScreen == UzhavScreen.MANDI_PRICES,
            onClick = { onScreenSelected(UzhavScreen.MANDI_PRICES) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == UzhavScreen.MANDI_PRICES) Icons.Filled.TrendingUp else Icons.Outlined.TrendingUp,
                    contentDescription = "Mandi Prices"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.TAMIL) "மண்டி" else "Mandi",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == UzhavScreen.MANDI_PRICES) FontWeight.Bold else FontWeight.Normal
                )
            }
        )

        // 3. Stores & Map
        NavigationBarItem(
            selected = currentScreen == UzhavScreen.STORES_MAP,
            onClick = { onScreenSelected(UzhavScreen.STORES_MAP) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == UzhavScreen.STORES_MAP) Icons.Filled.Store else Icons.Outlined.Store,
                    contentDescription = "Agri Stores"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.TAMIL) "உரக்கடை" else "Stores",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == UzhavScreen.STORES_MAP) FontWeight.Bold else FontWeight.Normal
                )
            }
        )

        // 4. 2026 Schemes
        NavigationBarItem(
            selected = currentScreen == UzhavScreen.SCHEMES_2026,
            onClick = { onScreenSelected(UzhavScreen.SCHEMES_2026) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == UzhavScreen.SCHEMES_2026) Icons.Filled.AccountBalance else Icons.Outlined.AccountBalance,
                    contentDescription = "Schemes"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.TAMIL) "மானியம்" else "Schemes",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == UzhavScreen.SCHEMES_2026) FontWeight.Bold else FontWeight.Normal
                )
            }
        )

        // 5. Plant Doctor
        NavigationBarItem(
            selected = currentScreen == UzhavScreen.PLANT_SCANNER,
            onClick = { onScreenSelected(UzhavScreen.PLANT_SCANNER) },
            icon = {
                Icon(
                    imageVector = if (currentScreen == UzhavScreen.PLANT_SCANNER) Icons.Filled.MedicalServices else Icons.Outlined.MedicalServices,
                    contentDescription = "Plant Doctor"
                )
            },
            label = {
                Text(
                    text = if (language == AppLanguage.TAMIL) "மருத்துவர்" else "Doctor",
                    fontSize = 11.sp,
                    fontWeight = if (currentScreen == UzhavScreen.PLANT_SCANNER) FontWeight.Bold else FontWeight.Normal
                )
            }
        )
    }
}
