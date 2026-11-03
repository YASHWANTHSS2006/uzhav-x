package com.example.model

enum class ForumPostType {
    QUESTION,
    TIP,
    EXPERIENCE,
    ALERT;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        QUESTION -> if (lang == AppLanguage.TAMIL) "கேள்வி" else "Question"
        TIP -> if (lang == AppLanguage.TAMIL) "விவசாய குறிப்பு" else "Farming Tip"
        EXPERIENCE -> if (lang == AppLanguage.TAMIL) "அனுபவப் பகிர்வு" else "Success Story"
        ALERT -> if (lang == AppLanguage.TAMIL) "பூச்சி எச்சரிக்கை" else "Pest Alert"
    }
}

data class ForumComment(
    val id: String,
    val authorName: String,
    val authorRole: String, // Farmer, KVK Agronomist, TNAU Specialist
    val timeAgo: String,
    val content: String,
    val isExpertAnswer: Boolean = false,
    val upvotes: Int = 0,
    val hasLiked: Boolean = false
)

data class ForumPost(
    val id: String,
    val authorName: String,
    val authorDistrict: String,
    val authorRole: String,
    val isVerifiedExpert: Boolean,
    val timeAgo: String,
    val title: String,
    val body: String,
    val cropTag: String,
    val upvotes: Int,
    val hasUserUpvoted: Boolean = false,
    val comments: List<ForumComment> = emptyList(),
    val postType: ForumPostType = ForumPostType.QUESTION,
    val likesCount: Int = upvotes,
    val hasLiked: Boolean = hasUserUpvoted
)
