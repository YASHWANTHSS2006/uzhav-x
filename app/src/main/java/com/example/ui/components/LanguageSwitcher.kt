package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage

/**
 * Reusable Language Switcher component that toggles UI between Tamil and English.
 * Uses a simple Compose State variable [currentLocale] to manage the active locale.
 */
@Composable
fun LanguageSwitcherStateful(
    initialLanguage: AppLanguage = AppLanguage.TAMIL,
    onLanguageChanged: (AppLanguage) -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Simple state variable managing the current locale
    var currentLocale by remember { mutableStateOf(initialLanguage) }

    LanguageSwitcher(
        currentLanguage = currentLocale,
        onLanguageChanged = { newLanguage ->
            currentLocale = newLanguage
            onLanguageChanged(newLanguage)
        },
        modifier = modifier
    )
}

/**
 * Material 3 Segmented Pill Language Switcher.
 * Displays "தமிழ்" and "English" tabs with smooth animated indicator and accessible touch target.
 */
@Composable
fun LanguageSwitcher(
    currentLanguage: AppLanguage,
    onLanguageChanged: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    val languages = listOf(AppLanguage.TAMIL, AppLanguage.ENGLISH)

    Surface(
        modifier = modifier
            .testTag("language_switcher_container")
            .clip(RoundedCornerShape(24.dp))
            .border(
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(24.dp)
            ),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(4.dp)
                .height(IntrinsicSize.Min),
            verticalAlignment = Alignment.CenterVertically
        ) {
            languages.forEach { language ->
                val isSelected = currentLanguage == language
                val containerColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tabContainerColor"
                )
                val contentColor by animateColorAsState(
                    targetValue = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
                    label = "tabContentColor"
                )

                Box(
                    modifier = Modifier
                        .testTag("language_option_${language.name.lowercase()}")
                        .defaultMinSize(minHeight = 44.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .background(containerColor)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = ripple(),
                            role = Role.RadioButton,
                            onClick = {
                                if (!isSelected) {
                                    onLanguageChanged(language)
                                }
                            }
                        )
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                modifier = Modifier.size(15.dp),
                                tint = contentColor
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        } else {
                            Icon(
                                imageVector = Icons.Default.Language,
                                contentDescription = null,
                                modifier = Modifier.size(14.dp),
                                tint = contentColor.copy(alpha = 0.7f)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                        }

                        Text(
                            text = language.displayName,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

/**
 * Compact Chip variant optimized for TopAppBar action buttons.
 * Single tap toggles between Tamil and English.
 */
@Composable
fun LanguageCompactChip(
    currentLanguage: AppLanguage,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .testTag("language_toggle_chip")
            .clip(RoundedCornerShape(18.dp))
            .clickable(
                role = Role.Button,
                onClick = onToggle
            ),
        color = MaterialTheme.colorScheme.primaryContainer,
        shape = RoundedCornerShape(18.dp),
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .defaultMinSize(minHeight = 36.dp)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Translate,
                contentDescription = "Switch Language (மொழி மாற்று)",
                modifier = Modifier.size(16.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = if (currentLanguage == AppLanguage.TAMIL) "English" else "தமிழ்",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

/**
 * Full card selector with rich context, ideal for Profile, Settings, and Onboarding screens.
 */
@Composable
fun LanguageSelectionCard(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("language_selection_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Translate,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (currentLanguage == AppLanguage.TAMIL) "பயன்பாட்டு மொழி (Language)" else "App Language (மொழி)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (currentLanguage == AppLanguage.TAMIL) "தமிழ் மற்றும் ஆங்கிலத்தில் உடனடியாக மாறலாம்" else "Seamlessly switch between Tamil and English",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LanguageSwitcher(
                currentLanguage = currentLanguage,
                onLanguageChanged = onLanguageSelected,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
