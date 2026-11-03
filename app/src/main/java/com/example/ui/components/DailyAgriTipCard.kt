package com.example.ui.components

import android.content.Intent
import android.speech.tts.TextToSpeech
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AgriTipNotificationHelper
import com.example.model.AppLanguage
import com.example.model.DailyAgriTip
import java.util.Locale

@Composable
fun DailyAgriTipCard(
    tip: DailyAgriTip,
    language: AppLanguage,
    onRefreshTip: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var isSpeaking by remember { mutableStateOf(false) }
    var ttsEngine by remember { mutableStateOf<TextToSpeech?>(null) }
    var isExpanded by remember { mutableStateOf(false) }

    DisposableEffect(Unit) {
        val tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val locale = if (language == AppLanguage.TAMIL) Locale("ta", "IN") else Locale.ENGLISH
                ttsEngine?.language = locale
            }
        }
        ttsEngine = tts
        onDispose {
            tts?.stop()
            tts?.shutdown()
        }
    }

    val titleText = if (language == AppLanguage.TAMIL) tip.titleTamil else tip.titleEnglish
    val seasonText = if (language == AppLanguage.TAMIL) tip.seasonNameTamil else tip.seasonNameEnglish
    val contentText = if (language == AppLanguage.TAMIL) tip.contentTamil else tip.contentEnglish
    val categoryText = tip.category.getDisplayName(language)
    val cropsList = if (language == AppLanguage.TAMIL) tip.targetedCropsTamil else tip.targetedCropsEnglish
    val actionSteps = if (language == AppLanguage.TAMIL) tip.keyActionStepsTamil else tip.keyActionStepsEnglish

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_agri_tip_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header Bar with Warm Harvest Gradient
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xFFE65100), // Rich Pumpkin / Sunset Orange
                                Color(0xFFF57C00),
                                Color(0xFFFF9800)
                            )
                        )
                    )
                    .padding(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color.White.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lightbulb,
                                contentDescription = "Daily Tip",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = Color.White.copy(alpha = 0.3f)
                                ) {
                                    Text(
                                        text = "${tip.month} ${if (tip.isAiGenerated) "• Gemini AI" else "• TNAU"}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = categoryText,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                            }
                            Text(
                                text = if (language == AppLanguage.TAMIL) "இன்றைய வேளாண் குறிப்பு" else "Daily Agri-Tip of the Day",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    // Refresh Button
                    IconButton(
                        onClick = onRefreshTip,
                        enabled = !isLoading,
                        modifier = Modifier.testTag("refresh_daily_tip_btn")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Refresh Tip",
                                tint = Color.White
                            )
                        }
                    }
                }
            }

            // Body Content
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = titleText,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = contentText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Targeted Crops Pills
                if (cropsList.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "உகந்த பயிர்கள்:" else "Target Crops:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            cropsList.take(3).forEach { crop ->
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.6f)
                                ) {
                                    Text(
                                        text = crop,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Medium,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                // Expandable Action Steps
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "முக்கிய செயல்முறைகள்:" else "Key Action Steps:",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        actionSteps.forEachIndexed { idx, step ->
                            Row(verticalAlignment = Alignment.Top) {
                                Text("${idx + 1}. ", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                                Text(step, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Action Bar: Speak TTS, Share, Notification Test, Expand
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        // Text to Speech Read Aloud Button
                        IconButton(
                            onClick = {
                                if (isSpeaking) {
                                    ttsEngine?.stop()
                                    isSpeaking = false
                                } else {
                                    val textToRead = "$titleText. $contentText"
                                    val loc = if (language == AppLanguage.TAMIL) Locale("ta", "IN") else Locale.ENGLISH
                                    ttsEngine?.language = loc
                                    ttsEngine?.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, "agri_tip_speak")
                                    isSpeaking = true
                                }
                            },
                            modifier = Modifier.testTag("tts_daily_tip_btn")
                        ) {
                            Icon(
                                imageVector = if (isSpeaking) Icons.Default.VolumeOff else Icons.Default.VolumeUp,
                                contentDescription = "Read Aloud",
                                tint = if (isSpeaking) Color.Red else MaterialTheme.colorScheme.primary
                            )
                        }

                        // Share Button
                        IconButton(
                            onClick = {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, titleText)
                                    putExtra(Intent.EXTRA_TEXT, "🌾 Uzhav X - Daily Agri Tip ($seasonText):\n\n$titleText\n\n$contentText\n\nShared via Uzhav X App")
                                }
                                context.startActivity(Intent.createChooser(shareIntent, "Share Daily Agri-Tip"))
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Share",
                                tint = MaterialTheme.colorScheme.outline
                            )
                        }

                        // Local Notification Test Button
                        IconButton(
                            onClick = {
                                AgriTipNotificationHelper.showDailyTipNotification(context, tip, language)
                                Toast.makeText(context, if (language == AppLanguage.TAMIL) "அறிவிப்பு அனுப்பப்பட்டது!" else "Notification sent!", Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier.testTag("test_tip_notification_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.NotificationsActive,
                                contentDescription = "Send Notification",
                                tint = Color(0xFFE65100)
                            )
                        }
                    }

                    TextButton(onClick = { isExpanded = !isExpanded }) {
                        Text(
                            text = if (isExpanded)
                                (if (language == AppLanguage.TAMIL) "சுருக்கு" else "Hide Steps")
                            else
                                (if (language == AppLanguage.TAMIL) "செயல்முறைகள்" else "View Steps"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }
    }
}
