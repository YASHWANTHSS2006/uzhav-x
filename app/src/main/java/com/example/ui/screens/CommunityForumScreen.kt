package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.ForumComment
import com.example.model.ForumPost
import com.example.model.ForumPostType
import com.example.viewmodel.UzhavViewModel
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import com.example.data.SpeechRecognitionHelper

enum class ForumFilterCategory {
    ALL,
    QUESTIONS,
    TIPS,
    EXPERIENCES,
    ALERTS,
    TRENDING
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityForumScreen(
    viewModel: UzhavViewModel,
    state: com.example.viewmodel.UzhavUiState,
    modifier: Modifier = Modifier
) {
    val lang = state.language
    val primaryColor = MaterialTheme.colorScheme.primary

    var showCreatePostDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf(ForumFilterCategory.ALL) }

    // Filter discussions safely
    val filteredPosts = remember(state.forumPosts, searchQuery, selectedFilter) {
        state.forumPosts.filter { post ->
            val matchesQuery = searchQuery.isBlank() ||
                    post.title.contains(searchQuery, ignoreCase = true) ||
                    post.body.contains(searchQuery, ignoreCase = true) ||
                    post.cropTag.contains(searchQuery, ignoreCase = true) ||
                    post.authorName.contains(searchQuery, ignoreCase = true) ||
                    post.authorDistrict.contains(searchQuery, ignoreCase = true)

            val matchesFilter = when (selectedFilter) {
                ForumFilterCategory.ALL -> true
                ForumFilterCategory.QUESTIONS -> post.postType == ForumPostType.QUESTION
                ForumFilterCategory.TIPS -> post.postType == ForumPostType.TIP
                ForumFilterCategory.EXPERIENCES -> post.postType == ForumPostType.EXPERIENCE
                ForumFilterCategory.ALERTS -> post.postType == ForumPostType.ALERT
                ForumFilterCategory.TRENDING -> post.upvotes >= 30
            }

            matchesQuery && matchesFilter
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .testTag("community_forum_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Header & Ask / Share CTA
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)
                )
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Forum,
                                    contentDescription = "Forum",
                                    tint = Color.White,
                                    modifier = Modifier.size(26.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "விவசாயிகள் கலந்துரையாடல் மன்றம்" else "Farmer Community Forum",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer
                                )
                                Text(
                                    text = if (lang == AppLanguage.TAMIL) "விவசாய கேள்விகள் & பயிர் அனுபவ பகிர்வுகள்" else "Ask Agricultural Questions & Share Farming Tips",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.outline
                                )
                            }
                        }

                        Button(
                            onClick = { showCreatePostDialog = true },
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("ask_forum_question_btn")
                        ) {
                            Icon(Icons.Default.AddComment, contentDescription = "Add", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (lang == AppLanguage.TAMIL) "புதிய பதிவு" else "Post",
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Search input
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = {
                            Text(
                                if (lang == AppLanguage.TAMIL) "கேள்விகள், குறிப்புகள், பயிர் பெயர் தேடுக..."
                                else "Search discussions, tips, crops..."
                            )
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = "Search", tint = primaryColor)
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("forum_search_input"),
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    // Filter chips row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.ALL,
                            onClick = { selectedFilter = ForumFilterCategory.ALL },
                            label = { Text(if (lang == AppLanguage.TAMIL) "அனைத்தும் (${state.forumPosts.size})" else "All (${state.forumPosts.size})") }
                        )
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.QUESTIONS,
                            onClick = { selectedFilter = ForumFilterCategory.QUESTIONS },
                            leadingIcon = { Icon(Icons.Default.HelpOutline, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "கேள்விகள்" else "Questions") }
                        )
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.TIPS,
                            onClick = { selectedFilter = ForumFilterCategory.TIPS },
                            leadingIcon = { Icon(Icons.Default.TipsAndUpdates, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "விவசாய குறிப்புகள்" else "Farming Tips") }
                        )
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.EXPERIENCES,
                            onClick = { selectedFilter = ForumFilterCategory.EXPERIENCES },
                            leadingIcon = { Icon(Icons.Default.MilitaryTech, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "அனுபவம் / வெற்றி கதை" else "Success Stories") }
                        )
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.ALERTS,
                            onClick = { selectedFilter = ForumFilterCategory.ALERTS },
                            leadingIcon = { Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "பூச்சி எச்சரிக்கை" else "Pest Alerts") }
                        )
                        FilterChip(
                            selected = selectedFilter == ForumFilterCategory.TRENDING,
                            onClick = { selectedFilter = ForumFilterCategory.TRENDING },
                            leadingIcon = { Icon(Icons.Default.TrendingUp, contentDescription = null, modifier = Modifier.size(16.dp)) },
                            label = { Text(if (lang == AppLanguage.TAMIL) "பிரபலமானவை" else "Trending") }
                        )
                    }
                }
            }
        }

        // Forum Posts List
        if (filteredPosts.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.SearchOff,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (lang == AppLanguage.TAMIL) "பொருத்தமான விவாதங்கள் எதுவும் இல்லை" else "No matching forum discussions found",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        } else {
            items(filteredPosts) { post ->
                ForumPostCard(
                    post = post,
                    language = lang,
                    onToggleLike = { viewModel.toggleUpvote(post.id) },
                    onToggleCommentLike = { commentId -> viewModel.toggleCommentLike(post.id, commentId) },
                    onAddComment = { viewModel.addCommentToPost(post.id, it) }
                )
            }
        }
    }

    // Create Post Dialog
    if (showCreatePostDialog) {
        CreateForumPostDialog(
            language = lang,
            defaultCrop = "Paddy (நெல்)",
            onDismiss = { showCreatePostDialog = false },
            onSubmit = { title, body, cropTag, postType ->
                viewModel.createNewForumPost(title, body, cropTag, postType)
                showCreatePostDialog = false
            }
        )
    }
}

@Composable
fun ForumPostCard(
    post: ForumPost,
    language: AppLanguage,
    onToggleLike: () -> Unit,
    onToggleCommentLike: (String) -> Unit,
    onAddComment: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    var showComments by remember { mutableStateOf(false) }
    var commentInput by remember { mutableStateOf("") }

    val postTypeColor = when (post.postType) {
        ForumPostType.QUESTION -> Color(0xFF1976D2)
        ForumPostType.TIP -> Color(0xFF2E7D32)
        ForumPostType.EXPERIENCE -> Color(0xFFF57F17)
        ForumPostType.ALERT -> Color(0xFFD32F2F)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("forum_post_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Author details & Post Type badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    // Avatar circle with author initials
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(primaryColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = post.authorName.take(1).uppercase(),
                            fontWeight = FontWeight.Bold,
                            color = primaryColor
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = post.authorName,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold
                            )
                            if (post.isVerifiedExpert) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = "Verified Expert",
                                    tint = primaryColor,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        Text(
                            text = "${post.authorRole} • ${post.authorDistrict} • ${post.timeAgo}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }

                // Post Type Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = postTypeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = post.postType.getDisplayName(language),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.ExtraBold,
                        color = postTypeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Title & Crop Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = post.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Text(
                        text = post.cropTag,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Body text
            Text(
                text = post.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.88f)
            )

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(8.dp))

            // Simple Like & Comment Action Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Like / Helpful Button
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleLike() }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("like_post_btn_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (post.hasLiked) Icons.Default.ThumbUp else Icons.Default.ThumbUpOffAlt,
                        contentDescription = "Like",
                        tint = if (post.hasLiked) primaryColor else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.likesCount} ${if (language == AppLanguage.TAMIL) "பயனுள்ளது" else "Helpful"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (post.hasLiked) FontWeight.Bold else FontWeight.Normal,
                        color = if (post.hasLiked) primaryColor else MaterialTheme.colorScheme.outline
                    )
                }

                // Comment Button / Discussion toggle
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showComments = !showComments }
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                        .testTag("comment_toggle_btn_${post.id}"),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ChatBubbleOutline,
                        contentDescription = "Comments",
                        tint = if (showComments) primaryColor else MaterialTheme.colorScheme.outline,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${post.comments.size} ${if (language == AppLanguage.TAMIL) "பதில்கள்" else "Replies"}",
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = if (showComments) FontWeight.Bold else FontWeight.Normal,
                        color = if (showComments) primaryColor else MaterialTheme.colorScheme.outline
                    )
                }
            }

            // Expandable Comments Section
            AnimatedVisibility(visible = showComments) {
                Column(
                    modifier = Modifier.padding(top = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (post.comments.isEmpty()) {
                        Text(
                            text = if (language == AppLanguage.TAMIL) "இதுவரை பதில்கள் இல்லை. உங்கள் பதிலை முதலாவதாக எழுதுங்கள்!"
                            else "No replies yet. Be the first to advise or answer!",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.outline,
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    } else {
                        post.comments.forEach { comment ->
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = if (comment.isExpertAnswer) Color(0xFFE8F5E9) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = comment.authorName,
                                                style = MaterialTheme.typography.labelMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (comment.isExpertAnswer) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Surface(
                                                    shape = RoundedCornerShape(4.dp),
                                                    color = Color(0xFF2E7D32)
                                                ) {
                                                    Text(
                                                        text = if (language == AppLanguage.TAMIL) "KVK வேளாண் விஞ்ஞானி" else "KVK Agronomist",
                                                        style = MaterialTheme.typography.labelSmall,
                                                        fontSize = 9.sp,
                                                        color = Color.White,
                                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        // Comment like button
                                        Row(
                                            modifier = Modifier
                                                .clickable { onToggleCommentLike(comment.id) }
                                                .padding(2.dp),
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Icon(
                                                imageVector = if (comment.hasLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                                contentDescription = "Like comment",
                                                tint = if (comment.hasLiked) Color(0xFFD32F2F) else MaterialTheme.colorScheme.outline,
                                                modifier = Modifier.size(14.dp)
                                            )
                                            if (comment.upvotes > 0) {
                                                Spacer(modifier = Modifier.width(2.dp))
                                                Text(
                                                    text = "${comment.upvotes}",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.outline
                                                )
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(2.dp))

                                    Text(
                                        text = comment.content,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }
                            }
                        }
                    }

                    // Add reply input field
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentInput,
                            onValueChange = { commentInput = it },
                            placeholder = {
                                Text(
                                    if (language == AppLanguage.TAMIL) "விவசாயிகளுக்கு உங்கள் ஆலோசனை / பதில் எழுதுங்கள்..."
                                    else "Write your answer or advice...",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .testTag("forum_comment_input_${post.id}"),
                            shape = RoundedCornerShape(10.dp),
                            maxLines = 3
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        IconButton(
                            onClick = {
                                if (commentInput.isNotBlank()) {
                                    onAddComment(commentInput)
                                    commentInput = ""
                                }
                            },
                            modifier = Modifier.testTag("send_comment_btn_${post.id}")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = "Send", tint = primaryColor)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CreateForumPostDialog(
    language: AppLanguage,
    defaultCrop: String,
    onDismiss: () -> Unit,
    onSubmit: (title: String, body: String, cropTag: String, postType: ForumPostType) -> Unit
) {
    var postTitle by remember { mutableStateOf("") }
    var postBody by remember { mutableStateOf("") }
    var cropTag by remember { mutableStateOf(defaultCrop) }
    var selectedType by remember { mutableStateOf(ForumPostType.QUESTION) }

    val context = LocalContext.current
    var isListeningVoice by remember { mutableStateOf(false) }
    val speechHelper = remember(language) {
        SpeechRecognitionHelper(
            context = context,
            languageCode = if (language == AppLanguage.TAMIL) "ta-IN" else "en-IN",
            onResult = { spokenText ->
                postBody = if (postBody.isBlank()) spokenText else "$postBody $spokenText"
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

    val cropSuggestions = listOf("Paddy (நெல்)", "Turmeric (மஞ்சள்)", "Cotton (பருத்தி)", "Coconut (தென்னை)", "Banana (வாழை)", "Millets (சிறுதானியங்கள்)", "Vegetables (காய்கறிகள்)", "General Farming")

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (language == AppLanguage.TAMIL) "விவசாய மன்றத்தில் பதிவிடுங்கள்" else "Post to Farmer Forum",
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                // Post Type selector (Question vs Tip vs Alert)
                Text(
                    text = if (language == AppLanguage.TAMIL) "பதிவு வகை தேர்வு செய்க:" else "Select Post Type:",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ForumPostType.values().forEach { type ->
                        FilterChip(
                            selected = selectedType == type,
                            onClick = { selectedType = type },
                            label = { Text(type.getDisplayName(language)) }
                        )
                    }
                }

                OutlinedTextField(
                    value = postTitle,
                    onValueChange = { postTitle = it },
                    label = {
                        Text(
                            if (language == AppLanguage.TAMIL) "தலைப்பு (எ.கா: மஞ்சள் மேட்டுப்பாத்தி சொட்டு நீர் முறை)"
                            else "Title (e.g. Drip Irrigation in Turmeric)"
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("create_post_title_input"),
                    singleLine = true
                )

                // Quick crop selector chips
                Text(
                    text = if (language == AppLanguage.TAMIL) "பயிர் குறிச்சொல்:" else "Crop Tag:",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    cropSuggestions.forEach { crop ->
                        FilterChip(
                            selected = cropTag == crop,
                            onClick = { cropTag = crop },
                            label = { Text(crop.substringBefore(" ("), fontSize = 11.sp) }
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (language == AppLanguage.TAMIL) "விவரம் (Voice Dictation Supported):" else "Description (Voice Dictation Supported):",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    OutlinedButton(
                        onClick = {
                            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.testTag("forum_voice_dictate_btn")
                    ) {
                        Icon(
                            imageVector = if (isListeningVoice) Icons.Default.Mic else Icons.Default.MicNone,
                            contentDescription = "Voice Dictate",
                            tint = if (isListeningVoice) Color(0xFFD32F2F) else MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isListeningVoice)
                                (if (language == AppLanguage.TAMIL) "கேட்கிறது..." else "Listening...")
                            else
                                (if (language == AppLanguage.TAMIL) "குரல் பதிவு" else "Voice Dictate"),
                            fontSize = 11.sp
                        )
                    }
                }

                OutlinedTextField(
                    value = postBody,
                    onValueChange = { postBody = it },
                    label = {
                        Text(
                            if (language == AppLanguage.TAMIL) "உங்கள் கேள்வி அல்லது விவசாய குறிப்பை விரிவாக எழுதவும்..."
                            else "Describe your question or farming tip in detail..."
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .testTag("create_post_body_input")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (postTitle.isNotBlank() && postBody.isNotBlank()) {
                        onSubmit(postTitle, postBody, cropTag, selectedType)
                    }
                },
                modifier = Modifier.testTag("submit_forum_post_btn")
            ) {
                Text(if (language == AppLanguage.TAMIL) "பதிவிடு" else "Publish Post")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(if (language == AppLanguage.TAMIL) "ரத்து" else "Cancel")
            }
        }
    )
}
