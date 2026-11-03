package com.example.data

import android.util.Log
import com.example.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

/**
 * Centralized Firestore Service for Uzhav X
 * Manages Cloud Synchronization for:
 * - Farmer Profiles
 * - Real-time Mandi Prices across Tamil Nadu
 * - Farmer & KVK Expert Forum discussions & comments
 * - Agri Store Inventories & Fertilizer Pricing
 * - Farmer Grievances and Reports
 */
class FirestoreAgriService {
    companion object {
        private const val TAG = "FirestoreAgriService"
        const val COLLECTION_FARMERS = "farmers"
        const val COLLECTION_MANDI = "mandiPrices"
        const val COLLECTION_FORUM = "forumPosts"
        const val COLLECTION_GRIEVANCES = "grievanceReports"
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance("ai-studio-android-uzhavx-1d673e88-413f-4185-a3b4-a3414e7b3198")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase Firestore not initialized or Google Play Services unavailable: ${e.message}")
            null
        }
    }

    // ----------------------------------------------------
    // Farmer Profile
    // ----------------------------------------------------
    suspend fun saveFarmerProfile(profile: FarmerProfile): Boolean {
        val db = firestore ?: return false
        return try {
            val docId = if (profile.contact.isNotBlank()) profile.contact else "farmer_default"
            val data = hashMapOf(
                "name" to profile.name,
                "contact" to profile.contact,
                "state" to profile.state,
                "district" to profile.district,
                "taluka" to profile.taluka,
                "village" to profile.village,
                "landSizeAcres" to profile.landSizeAcres,
                "soilType" to profile.soilType,
                "waterSource" to profile.waterSource,
                "primaryCrops" to profile.primaryCrops,
                "isRegistered" to profile.isRegistered,
                "currentRole" to profile.currentRole.name,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection(COLLECTION_FARMERS)
                .document(docId)
                .set(data, SetOptions.merge())
                .await()
            Log.d(TAG, "Farmer profile saved to Firestore: $docId")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error saving farmer profile: ${e.message}", e)
            false
        }
    }

    // ----------------------------------------------------
    // Mandi Prices
    // ----------------------------------------------------
    fun listenToMandiPrices(
        onUpdate: (List<MandiPrice>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {
        val db = firestore ?: return null
        return try {
            db.collection(COLLECTION_MANDI)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Mandi prices listen error: ${error.message}", error)
                        onError(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val prices = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val cropName = doc.getString("cropName") ?: ""
                                val tamilName = doc.getString("tamilName") ?: ""
                                val category = doc.getString("category") ?: "Vegetables"
                                val marketName = doc.getString("marketName") ?: ""
                                val district = doc.getString("district") ?: ""
                                val minPrice = (doc.getLong("minPrice") ?: 0L).toInt()
                                val maxPrice = (doc.getLong("maxPrice") ?: 0L).toInt()
                                val modalPrice = (doc.getLong("modalPrice") ?: 0L).toInt()
                                val unit = doc.getString("unit") ?: "Quintal (100 kg)"
                                val changeAmount = (doc.getLong("changeAmount") ?: 0L).toInt()
                                val isPositive = doc.getBoolean("isPositive") ?: true
                                val arrivalVolume = doc.getString("arrivalVolume") ?: "Regular"
                                val lastUpdated = doc.getString("lastUpdated") ?: "Today"
                                @Suppress("UNCHECKED_CAST")
                                val historyList = (doc.get("priceHistory7Days") as? List<*>)
                                    ?.mapNotNull { (it as? Number)?.toInt() }
                                    ?: listOf(
                                        (modalPrice * 0.95).toInt(),
                                        (modalPrice * 0.97).toInt(),
                                        (modalPrice * 0.96).toInt(),
                                        (modalPrice * 0.98).toInt(),
                                        (modalPrice * 0.99).toInt(),
                                        modalPrice
                                    )

                                MandiPrice(
                                    id = id,
                                    cropName = cropName,
                                    tamilName = tamilName,
                                    category = category,
                                    marketName = marketName,
                                    district = district,
                                    minPrice = minPrice,
                                    maxPrice = maxPrice,
                                    modalPrice = modalPrice,
                                    unit = unit,
                                    changeAmount = changeAmount,
                                    isPositive = isPositive,
                                    arrivalVolume = arrivalVolume,
                                    lastUpdated = lastUpdated,
                                    priceHistory7Days = historyList
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error mapping mandi doc ${doc.id}: ${e.message}")
                                null
                            }
                        }
                        if (prices.isNotEmpty()) {
                            onUpdate(prices)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing mandi listener: ${e.message}", e)
            null
        }
    }

    suspend fun syncInitialMandiPrices(prices: List<MandiPrice>) {
        val db = firestore ?: return
        try {
            val batch = db.batch()
            for (price in prices) {
                val docRef = db.collection(COLLECTION_MANDI).document(price.id)
                val map = hashMapOf(
                    "id" to price.id,
                    "cropName" to price.cropName,
                    "tamilName" to price.tamilName,
                    "category" to price.category,
                    "marketName" to price.marketName,
                    "district" to price.district,
                    "minPrice" to price.minPrice,
                    "maxPrice" to price.maxPrice,
                    "modalPrice" to price.modalPrice,
                    "unit" to price.unit,
                    "changeAmount" to price.changeAmount,
                    "isPositive" to price.isPositive,
                    "arrivalVolume" to price.arrivalVolume,
                    "lastUpdated" to price.lastUpdated,
                    "priceHistory7Days" to price.priceHistory7Days,
                    "updatedAt" to System.currentTimeMillis()
                )
                batch.set(docRef, map, SetOptions.merge())
            }
            batch.commit().await()
            Log.d(TAG, "Mandi prices synced to Firestore successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync mandi prices: ${e.message}", e)
        }
    }

    // ----------------------------------------------------
    // Community Forum Posts
    // ----------------------------------------------------
    fun listenToForumPosts(
        onUpdate: (List<ForumPost>) -> Unit,
        onError: (Exception) -> Unit
    ): ListenerRegistration? {
        val db = firestore ?: return null
        return try {
            db.collection(COLLECTION_FORUM)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e(TAG, "Forum listen error: ${error.message}", error)
                        onError(error)
                        return@addSnapshotListener
                    }
                    if (snapshot != null && !snapshot.isEmpty) {
                        val posts = snapshot.documents.mapNotNull { doc ->
                            try {
                                val id = doc.getString("id") ?: doc.id
                                val authorName = doc.getString("authorName") ?: ""
                                val authorDistrict = doc.getString("authorDistrict") ?: ""
                                val authorRole = doc.getString("authorRole") ?: "Farmer"
                                val isVerifiedExpert = doc.getBoolean("isVerifiedExpert") ?: false
                                val timeAgo = doc.getString("timeAgo") ?: "Recently"
                                val title = doc.getString("title") ?: ""
                                val body = doc.getString("body") ?: ""
                                val cropTag = doc.getString("cropTag") ?: "General"
                                val upvotes = (doc.getLong("upvotes") ?: 0L).toInt()

                                ForumPost(
                                    id = id,
                                    authorName = authorName,
                                    authorDistrict = authorDistrict,
                                    authorRole = authorRole,
                                    isVerifiedExpert = isVerifiedExpert,
                                    timeAgo = timeAgo,
                                    title = title,
                                    body = body,
                                    cropTag = cropTag,
                                    upvotes = upvotes,
                                    hasUserUpvoted = false,
                                    comments = emptyList()
                                )
                            } catch (e: Exception) {
                                Log.e(TAG, "Error mapping forum doc ${doc.id}: ${e.message}")
                                null
                            }
                        }
                        if (posts.isNotEmpty()) {
                            onUpdate(posts)
                        }
                    }
                }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing forum listener: ${e.message}", e)
            null
        }
    }

    suspend fun addForumPost(post: ForumPost): Boolean {
        val db = firestore ?: return false
        return try {
            val map = hashMapOf(
                "id" to post.id,
                "authorName" to post.authorName,
                "authorDistrict" to post.authorDistrict,
                "authorRole" to post.authorRole,
                "isVerifiedExpert" to post.isVerifiedExpert,
                "timeAgo" to post.timeAgo,
                "title" to post.title,
                "body" to post.body,
                "cropTag" to post.cropTag,
                "upvotes" to post.upvotes,
                "timestamp" to System.currentTimeMillis()
            )
            db.collection(COLLECTION_FORUM).document(post.id).set(map).await()
            Log.d(TAG, "Forum post added to Firestore: ${post.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error adding forum post: ${e.message}", e)
            false
        }
    }

    suspend fun upvoteForumPost(postId: String, currentUpvotes: Int): Boolean {
        val db = firestore ?: return false
        return try {
            db.collection(COLLECTION_FORUM)
                .document(postId)
                .update("upvotes", currentUpvotes + 1)
                .await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error upvoting post $postId: ${e.message}", e)
            false
        }
    }

    // ----------------------------------------------------
    // Grievance / Feedback Reports
    // ----------------------------------------------------
    suspend fun submitGrievance(report: FeedbackReport): Boolean {
        val db = firestore ?: return false
        return try {
            val map = hashMapOf(
                "id" to report.id,
                "farmerName" to report.farmerName,
                "district" to report.district,
                "taluka" to report.taluka,
                "village" to report.village,
                "phone" to report.phone,
                "category" to report.category.name,
                "description" to report.description,
                "timestamp" to report.timestamp,
                "status" to report.status.name,
                "createdTime" to System.currentTimeMillis()
            )
            db.collection(COLLECTION_GRIEVANCES).document(report.id).set(map).await()
            Log.d(TAG, "Grievance submitted to Firestore: ${report.id}")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Error submitting grievance: ${e.message}", e)
            false
        }
    }
}
