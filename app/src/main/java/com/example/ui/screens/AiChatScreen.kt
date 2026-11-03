package com.example.ui.screens

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicNone
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.VolumeUp
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
import com.example.data.TextToSpeechHelper
import com.example.model.AppLanguage
import com.example.model.ChatMessage
import com.example.viewmodel.UzhavViewModel

@Composable
fun AiChatScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary
    val listState = rememberLazyListState()

    var isListeningVoice by remember { mutableStateOf(false) }

    val ttsHelper = remember { TextToSpeechHelper(context) }
    DisposableEffect(Unit) {
        onDispose {
            ttsHelper.shutdown()
        }
    }

    val speechHelper = remember(lang) {
        SpeechRecognitionHelper(
            context = context,
            languageCode = if (lang == AppLanguage.TAMIL) "ta-IN" else "en-IN",
            onResult = { spokenText ->
                viewModel.updateAiInputText(
                    if (state.currentAiInputText.isBlank()) spokenText else "${state.currentAiInputText} $spokenText"
                )
            },
            onError = { err -> },
            onListeningChanged = { listening ->
                isListeningVoice = listening
            }
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            speechHelper.startListening()
        }
    }

    val modelOptions = listOf(
        "gemini-3.8-flash" to if (lang == AppLanguage.TAMIL) "Gemini 3.8 Flash (பொது)" else "Gemini 3.8 Flash (General)",
        "gemini-3.5-flash-lite" to if (lang == AppLanguage.TAMIL) "Gemini 3.5 Lite (வேகம்)" else "Gemini 3.5 Lite (Fast)",
        "gemini-4-argon" to if (lang == AppLanguage.TAMIL) "Gemini 4 Argon (உயர் ஆய்வு)" else "Gemini 4 Argon (Reasoning)"
    )

    val topicCategories = if (lang == AppLanguage.TAMIL) listOf(
        "🐄 கறவை மாடு பால் உற்பத்தி அதிகரிக்க வழிகள்?",
        "🐐 பரண் மேல் வெள்ளாடு வளர்ப்பு & நபார்ட் கடன்?",
        "🐔 நாட்டுக்கோழி தடுப்பூசி & பராமரிப்பு?",
        "🌾 நெல் இலைக்கருகல் நோய் மருந்து அளவு?",
        "🌱 CO-5 தீவனப்புல் & அசோலா வளர்ப்பு?",
        "🏛️ 2026-27 அரசு மானியங்கள் & கடன்கள்?"
    ) else listOf(
        "🐄 How to increase dairy cow milk yield?",
        "🐐 Elevated goat stall feeding & NABARD loan",
        "🐔 Country chicken vaccination schedule",
        "🌾 Paddy leaf blast remedy & dosage",
        "🌱 CO-5 green fodder & Azolla cultivation",
        "🏛️ 2026-27 Tamil Nadu Agri Subsidies"
    )

    LaunchedEffect(state.chatMessages.size) {
        if (state.chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(state.chatMessages.lastIndex)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .testTag("ai_chat_screen")
    ) {
        // Chat Header with GPT-5 & TANUVAS Badge
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 3.dp
        ) {
            Column {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(primaryColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "GPT-5 AI",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "உழவன் X GPT-5 AI ஆலோசகர்" else "Uzhav X GPT-5 AI Farm & Livestock Advisor",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color(0xFFE8F5E9)
                            ) {
                                Text(
                                    text = "PRO",
                                    style = MaterialTheme.typography.labelSmall,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF1B5E20),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பயிர் மருத்துவம், கறவை மாடு, ஆடு வளர்ப்பு & அரசு மானியங்கள்" else "Crops, Cattle, Dairy, Goat, Poultry & Govt Subsidies Expertise",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // AI Model Selection Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "மாடல்:" else "Model:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(modelOptions) { (modelKey, label) ->
                            val isSelected = state.selectedAiModel == modelKey
                            FilterChip(
                                selected = isSelected,
                                onClick = { viewModel.setSelectedAiModel(modelKey) },
                                label = { Text(label, fontSize = 10.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                modifier = Modifier
                                    .height(28.dp)
                                    .testTag("select_model_$modelKey"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = primaryColor,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }
        }

        // Messages list
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.chatMessages) { message ->
                ChatBubble(
                    message = message,
                    language = lang,
                    onPromptClick = { viewModel.sendAiMessage(it) },
                    onSpeakClick = { text -> ttsHelper.speak(text, lang) }
                )
            }

            if (state.isAiThinking) {
                item {
                    Row(
                        modifier = Modifier.padding(start = 8.dp, top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(
                            color = primaryColor,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "${state.selectedAiModel} பதிலளிக்கிறது..." else "${state.selectedAiModel} is generating response...",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        // Quick Topic Suggestion Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(topicCategories) { q ->
                Surface(
                    shape = RoundedCornerShape(16.dp),
                    color = Color(0xFFE8F5E9),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                    modifier = Modifier.clickable { viewModel.sendAiMessage(q) }
                ) {
                    Text(
                        text = q,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1B5E20),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                    )
                }
            }
        }

        // Input Field & Send/Voice Buttons
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice Dictation Button
                IconButton(
                    onClick = {
                        permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                    },
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            if (isListeningVoice) Color(0xFFFFEBEE) else MaterialTheme.colorScheme.surfaceVariant,
                            CircleShape
                        )
                        .testTag("ai_voice_dictate_btn")
                ) {
                    Icon(
                        imageVector = if (isListeningVoice) Icons.Default.Mic else Icons.Default.MicNone,
                        contentDescription = "Voice Dictate",
                        tint = if (isListeningVoice) Color(0xFFD32F2F) else primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = state.currentAiInputText,
                    onValueChange = { viewModel.updateAiInputText(it) },
                    placeholder = {
                        Text(
                            if (lang == AppLanguage.TAMIL) "பயிர், மாடு, ஆடு வளர்ப்பு பற்றி கேளுங்கள்..."
                            else "Ask about crops, cows, goats, poultry...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_chat_input"),
                    shape = RoundedCornerShape(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = { viewModel.sendAiMessage() },
                    modifier = Modifier
                        .size(46.dp)
                        .background(primaryColor, CircleShape)
                        .testTag("ai_send_message_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.White, modifier = Modifier.size(20.dp))
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    language: AppLanguage,
    onPromptClick: (String) -> Unit,
    onSpeakClick: (String) -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (message.isUser) Alignment.End else Alignment.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isUser) 16.dp else 4.dp,
                bottomEnd = if (message.isUser) 4.dp else 16.dp
            ),
            color = if (message.isUser) primaryColor else MaterialTheme.colorScheme.surfaceVariant,
            shadowElevation = 1.dp
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (!message.isUser) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSpeakClick(message.text) },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("speak_response_${message.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = "Listen to AI answer",
                                tint = primaryColor,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }

        // Followup suggestion pills if provided by AI
        if (!message.isUser && message.suggestedFollowups.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            Column(
                modifier = Modifier.padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                message.suggestedFollowups.forEach { followup ->
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0xFFE8F5E9),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFA5D6A7)),
                        modifier = Modifier.clickable { onPromptClick(followup) }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("💡 ", fontSize = 11.sp)
                            Text(
                                text = followup,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color(0xFF1B5E20),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
