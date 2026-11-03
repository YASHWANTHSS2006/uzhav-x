package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AgriNews
import com.example.model.AppLanguage
import com.example.model.NewsCategory
import com.example.viewmodel.UzhavViewModel

@Composable
fun DailyNewsScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    val filteredNews = state.newsList.filter {
        state.selectedNewsCategory == NewsCategory.ALL || it.category == state.selectedNewsCategory
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("daily_news_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Title
        item {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Newspaper,
                        contentDescription = "Agri News",
                        tint = primaryColor,
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (lang == AppLanguage.TAMIL) "தினசரி வேளாண் செய்திகள் & அறிவிப்புகள்" else "Daily Agriculture News & Updates",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
                Text(
                    text = if (lang == AppLanguage.TAMIL) "தமிழக அரசு அரசாணைகள், மேட்டூர் அணை நீர் திறப்பு, குறைந்தபட்ச ஆதார விலை மற்றும் TNAU எச்சரிக்கைகள்."
                    else "Tamil Nadu Government agriculture orders, Mettur Dam storage, MSP updates, and university advisories.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }

        // Category Filter
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(NewsCategory.values()) { cat ->
                    FilterChip(
                        selected = state.selectedNewsCategory == cat,
                        onClick = { viewModel.setNewsCategory(cat) },
                        label = { Text(cat.getDisplayName(lang)) }
                    )
                }
            }
        }

        // News Items
        items(filteredNews) { news ->
            NewsCard(
                news = news,
                language = lang,
                onSelect = { viewModel.selectNewsForDetail(it) }
            )
        }
    }

    // Full Story Dialog
    state.selectedNewsForDetail?.let { item ->
        AlertDialog(
            onDismissRequest = { viewModel.selectNewsForDetail(null) },
            title = {
                Text(
                    text = if (lang == AppLanguage.TAMIL) item.tamilTitle else item.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Source: ${item.source}",
                            style = MaterialTheme.typography.labelSmall,
                            color = primaryColor,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${item.timeAgo} • ${item.readTimeMinutes} min read",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }

                    Text(
                        text = if (lang == AppLanguage.TAMIL) item.summaryTamil else item.summaryEnglish,
                        style = MaterialTheme.typography.bodyLarge,
                        lineHeight = 24.sp
                    )
                }
            },
            confirmButton = {
                Button(onClick = { viewModel.selectNewsForDetail(null) }) {
                    Text(if (lang == AppLanguage.TAMIL) "முடிந்தது" else "Close")
                }
            }
        )
    }
}

@Composable
fun NewsCard(
    news: AgriNews,
    language: AppLanguage,
    onSelect: (AgriNews) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onSelect(news) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (news.isBreaking) Color(0xFFFFEBEE) else primaryColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = if (news.isBreaking) (if (language == AppLanguage.TAMIL) "முக்கிய அறிவிப்பு" else "Breaking News")
                        else news.source,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (news.isBreaking) Color(0xFFC62828) else primaryColor,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
                Text(
                    text = news.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (language == AppLanguage.TAMIL) news.tamilTitle else news.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = if (language == AppLanguage.TAMIL) news.summaryTamil else news.summaryEnglish,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = if (language == AppLanguage.TAMIL) "முழு செய்தி வாசிக்க ▶" else "Read Full Story ▶",
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    color = primaryColor
                )
            }
        }
    }
}
