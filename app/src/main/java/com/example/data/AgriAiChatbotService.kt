package com.example.data

import android.util.Log
import com.example.model.AppLanguage
import com.example.model.ChatMessage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

/**
 * Uzhav X GPT-5 Agriculture & Livestock AI Chatbot Engine
 * Powered by Gemini 3.8 Flash & TNAU / TANUVAS (Tamil Nadu Veterinary and Animal Sciences University) dataset.
 */
class AgriAiChatbotService {

    companion object {
        private const val TAG = "AgriAiChatbotService"
    }

    private val geminiModel by lazy {
        try {
            Firebase.ai.generativeModel("gemini-3.8-flash")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI Logic chatbot model init notice: ${e.message}")
            null
        }
    }

    suspend fun generateAiChatResponse(
        userQuery: String,
        chatHistory: List<ChatMessage>,
        language: AppLanguage = AppLanguage.TAMIL,
        selectedModel: String = "gemini-3.8-flash",
        farmerDistrict: String = "Thanjavur"
    ): ChatMessage = withContext(Dispatchers.IO) {
        val trimmed = userQuery.trim()
        val isTamil = language == AppLanguage.TAMIL

        try {
            val modelToUse = try {
                Firebase.ai.generativeModel(selectedModel)
            } catch (e: Exception) {
                geminiModel
            }

            if (modelToUse != null && trimmed.isNotBlank()) {
                Log.d(TAG, "Sending query to $selectedModel AI Chatbot (District: $farmerDistrict): $trimmed")
                val prompt = """
                    SYSTEM INSTRUCTION: You are 'Uzhav X GPT-5 AI Farm & Livestock Advisor' (உழவன் X - GPT-5 AI வேளாண் மற்றும் கால்நடை வல்லுநர்).
                    Role: Expert agronomist, veterinary scientist (TANUVAS), and agricultural economist for Tamil Nadu, India.
                    Location Context: Farmer is in district '$farmerDistrict', Tamil Nadu. Include accurate hyper-local information for $farmerDistrict (mandi prices, PACCS co-operative fertilizer stores, local crop calendar).

                    Respond with high expertise, clear bullet points, practical dosages, and actionable steps on:
                    - Cattle & Dairy Farming (Kangayam, Sahiwal, Gir, HF cross, Jersey milk yield, Mastitis cure, Green fodder CO-4/CO-5, Azolla).
                    - Goat & Sheep Farming (Kanni Aadu, Tellicherry, Mecheri sheep, Stall feeding, PPR vaccination, Kid mortality reduction).
                    - Poultry & Country Chicken (Aseel, Kadaknath, Ranikhet/Lasota vaccine, herbal deworming).
                    - Crops & Soil Health (Paddy, Turmeric, Cotton, Sugarcane, Coconut, Vegetables, Millets, Drip irrigation, Soil test amendments).
                    - Organic Bio-Inputs (Panchagavya, Jeevamrutham, Fish Amino acid, Neem oil 10000 ppm).
                    - Government Subsidies & Credit (NABARD 33% Dairy/Goat loan, KCC zero-interest crop loan, PM-KUSUM 70% solar pump).

                    USER QUESTION: "$trimmed"
                    PREFERRED LANGUAGE: ${if (isTamil) "TAMIL (தமிழ்)" else "ENGLISH"}

                    Guidelines:
                    1. Answer accurately with clear formatting, emojis, and exact steps or dosages.
                    2. If the user asks in Tamil, reply in clear, professional, friendly Tamil (தமிழ்).
                    3. If the user asks in English, reply in English.
                    4. End with 2-3 relevant follow-up suggestion prompts for the farmer.
                """.trimIndent()

                val response = withTimeout(30_000L) {
                    modelToUse.generateContent(prompt)
                }

                val text = response.text
                if (!text.isNullOrBlank()) {
                    val (cleanedText, followups) = extractTextAndFollowups(text, isTamil)
                    return@withContext ChatMessage(
                        id = "ai_${System.currentTimeMillis()}",
                        text = cleanedText,
                        isUser = false,
                        timestamp = "Just now",
                        suggestedFollowups = followups
                    )
                }
            }
        } catch (e: Exception) {
            Log.w(TAG, "Gemini AI Chatbot ($selectedModel) call failed or timed out: ${e.message}. Using TANUVAS/TNAU offline expert dataset.")
        }

        // Resilient Offline Fallback Engine covering Cattle, Goat, Poultry, Crops, and Subsidies
        return@withContext generateExpertOfflineResponse(trimmed, language)
    }

    private fun extractTextAndFollowups(rawText: String, isTamil: Boolean): Pair<String, List<String>> {
        val lines = rawText.lines()
        val mainTextLines = mutableListOf<String>()
        val followups = mutableListOf<String>()

        for (line in lines) {
            if (line.contains("Follow-up", ignoreCase = true) || line.contains("தொடர்புடைய கேள்விகள்", ignoreCase = true) || line.contains("💡")) {
                val clean = line.replace("Follow-up:", "").replace("💡", "").trim()
                if (clean.isNotBlank() && clean.length > 5) {
                    followups.add(clean.take(60))
                }
            } else {
                mainTextLines.add(line)
            }
        }

        val defaultFollowups = if (isTamil) listOf(
            "கறவை மாடு பால் உற்பத்தி அதிகரிக்கும் தீவனம் எது?",
            "பரண் மேல் வெள்ளாடு வளர்ப்பு கடன்கள் எவ்வாறு பெறுவது?",
            "2026-27 அரசு வேளாண் மானியங்கள் என்ன?"
        ) else listOf(
            "Best fodder to increase dairy milk yield?",
            "How to get NABARD goat farming loan?",
            "Check 2026-27 Tamil Nadu Agri Subsidies"
        )

        val finalFollowups = if (followups.isNotEmpty()) followups.take(3) else defaultFollowups
        return Pair(mainTextLines.joinToString("\n").trim(), finalFollowups)
    }

    private fun generateExpertOfflineResponse(query: String, language: AppLanguage): ChatMessage {
        val q = query.lowercase()
        val isTamil = language == AppLanguage.TAMIL

        val (replyText, followups) = when {
            // Cattle, Cow, Dairy Farming
            q.contains("மாடு") || q.contains("பசு") || q.contains("cow") || q.contains("cattle") || q.contains("dairy") || q.contains("பால்") || q.contains("milk") || q.contains("கறவை") -> {
                if (isTamil) {
                    Pair(
                        """
                        🐄 **கறவை மாடு வளர்ப்பு & பால் உற்பத்தி மேலாண்மை (TANUVAS வழிகாட்டல்):**

                        1. **பால் உற்பத்தியை உயர்த்தும் தீவன மேலாண்மை:**
                           • பசுந்தீவனம்: CO-4 அல்லது CO-5 தண்டுப்புல் (ஏக்கருக்கு 15 டன்/வெட்டு) + சூப்பர் நேப்பியர்.
                           • புரதச் சத்து தீவனம்: வேலிக்காத்தான், அசோலா (Azolla) தினமும் 1-2 கிலோ அளிக்கலாம்.
                           • தாது உப்பு கலவை (Mineral Mixture): தினமும் மாடு ஒன்றுக்கு 50 கிராம் தீவனத்துடன் கலந்து தர வேண்டும்.

                        2. **மடிநோய் (Mastitis) தடுப்புமுறை:**
                           • பால் கறந்த பின் 10 நிமிடங்களுக்கு மாடு கீழே படுக்கக் கூடாது (மடி தாரை திறந்திருக்கும்).
                           • பொட்டாசியம் பெர்மாங்கனேட் கரைசலில் மடியைக் கழுவி சுத்தமான துணியால் துடைக்க வேண்டும்.

                        3. **அரசு மானியம் & கடன்கள்:**
                           • நபார்ட் (NABARD) & கூட்டுறவு வங்கி மூலம் 2 அல்லது 10 கறவை மாடுகள் வாங்க 25% முதல் 33% வரை அரசு மானிய கடன் கிடைக்கும்.
                        """.trimIndent(),
                        listOf("அசோலா தயாரிக்கும் முறை என்ன?", "மடிநோய் குணமாக்கும் இயற்கை மருந்து?", "காங்கேயம் காளைகள் வளர்ப்பு முறை")
                    )
                } else {
                    Pair(
                        """
                        🐄 **Cattle & Dairy Farming Management (TANUVAS Guidelines):**

                        1. **Maximizing Milk Yield:**
                           • High-protein Green Fodder: CO-4 / CO-5 Cumbu Napier grass + Super Napier.
                           • Azolla Feeding: Feed 1-2 kg fresh Azolla daily mixed with concentrate feed.
                           • Mineral Mixture: Supplement 50g TANUVAS mineral mixture daily per cow.

                        2. **Mastitis Prevention & Udder Hygiene:**
                           • Ensure cows stay standing for 15-20 minutes post-milking.
                           • Clean udder before milking with 0.1% Potassium Permanganate solution.

                        3. **Bank Loans & Subsidies:**
                           • NABARD Dairy Entrepreneurship Scheme offers 25% to 33.33% capital subsidy for 2 to 10 dairy unit setups.
                        """.trimIndent(),
                        listOf("How to cultivate Azolla fodder?", "Organic cure for Mastitis in cows", "NABARD dairy loan application process")
                    )
                }
            }

            // Goat & Sheep Farming
            q.contains("ஆடு") || q.contains("goat") || q.contains("sheep") || q.contains("வெள்ளாடு") || q.contains("செம்மறி") -> {
                if (isTamil) {
                    Pair(
                        """
                        🐐 **ஆடு வளர்ப்பு & பரண் மேல் ஆடு பண்ணை மேலாண்மை:**

                        1. **சிறந்த தமிழக ஆடு இரகங்கள்:**
                           • கன்னி ஆடு, கொடி ஆடு (தென் மாவட்டங்கள்), சேலம் கருப்பு, மேச்சேரி செம்மறியாடு (இறைச்சி உற்பத்திக்கு மிக உகந்தது).

                        2. **பரண் மேல் ஆடு வளர்ப்பு (Stall Feeding):**
                           • மரக் கட்டைகளால் தரைமட்டத்தில் இருந்து 4-5 அடி உயரத்தில் பரண் அமைக்க வேண்டும்.
                           • குட்டிகள் இறப்பைத் தவிர்க்க முதல் 3 நாட்கள் கட்டாயமாக சீம்பால் (Colostrum) புகட்ட வேண்டும்.

                        3. **தடுப்பூசி அட்டவணை:**
                           • PPR (ஆட்டுக்கொல்லி நோய்): ஆண்டுக்கு ஒருமுறை ஜனவரி/பிப்ரவரி மாதங்களில் தடுப்பூசி போட வேண்டும்.
                           • குடல் புழு நீக்கம் (Deworming): 3 மாதங்களுக்கு ஒருமுறை சுழற்சி முறையில் செய்ய வேண்டும்.

                        4. **மானியம்:**
                           • ஆடு பண்ணை அமைக்க நபார்ட் மூலம் 33% மானியத்துடன் கூடிய வங்கிக் கடன் வசதி உண்டு.
                        """.trimIndent(),
                        listOf("50 ஆடுகள் வளர்க்க பரண் அமைக்கும் செலவு?", "ஆடு குடல் புழு நீக்க அட்டவணை", "மேச்சேரி ஆடு விற்பனை மையங்கள்")
                    )
                } else {
                    Pair(
                        """
                        🐐 **Goat & Sheep Farming (Stall Feeding & Health Advisory):**

                        1. **Best Native Breeds:**
                           • Kanni Aadu, Kodi Aadu, Salem Black, Tellicherry, and Mecheri Sheep for rapid weight gain.

                        2. **Stall Feeding (Elevated Slotted Flooring):**
                           • Construct slatted wooden floor 4 feet above ground level for clean hygiene.
                           • Ensure newborn kids receive mother's Colostrum within 1 hour of birth.

                        3. **Vaccination & Health Calendar:**
                           • PPR Vaccine: Annual dose in Jan/Feb.
                           • Enterotoxemia (ET) Vaccine: Pre-monsoon dose in May/June.
                           • Deworming: Every 3 months using broad-spectrum anthelmintic.
                        """.trimIndent(),
                        listOf("Cost of 50-goat stall shed construction", "Goat deworming dosage chart", "NABARD 33% goat farming subsidy")
                    )
                }
            }

            // Poultry & Country Chicken
            q.contains("கோழி") || q.contains("chicken") || q.contains("poultry") || q.contains("நாட்டுக்கோழி") -> {
                if (isTamil) {
                    Pair(
                        """
                        🐔 **நாட்டுக்கோழி வளர்ப்பு & நோய் தடுப்பு வழிகாட்டல்:**

                        1. **சிறந்த இரகங்கள்:**
                           • சிறுவிடை, பெருவிடை, அசீல் (Aseel), கடக்நாத் (Kadaknath), கிராமப்பிரியா.

                        2. **வெள்ளைக் கழிச்சல் (Ranikhet / Newcastle) நோய் தடுப்பு:**
                           • 7ஆம் நாள்: F1 / Lasota கண் சொட்டு மருந்து.
                           • 21ஆம் நாள்: IBD தடுப்பூசி.
                           • 8ஆம் வாரம்: RDVK தடுப்பூசி.

                        3. **இயற்கை நோய் எதிர்ப்பு மருந்து:**
                           • சீரகம், மிளகு, பூண்டு, மஞ்சள் தூள், கீழாநெல்லி அரைத்து குடிநீரில் கலந்து தரலாம்.
                        """.trimIndent(),
                        listOf("நாட்டுக்கோழிக்கு இயற்கை நோய் எதிர்ப்பு மருந்து", "100 கோழி வளர்க்க தேவையான இடம்", "நாட்டுக்கோழி முட்டை அடைகாக்கும் முறை")
                    )
                } else {
                    Pair(
                        """
                        🐔 **Poultry & Country Chicken Farming Advisory:**

                        1. **Popular Breeds:**
                           • Native Aseel, Kadaknath (Black Meat), Giriraja, and Gramapriya.

                        2. **Ranikhet / Newcastle Disease Vaccination:**
                           • Day 7: Lasota eye drops.
                           • Day 21: IBD / Gumboro vaccine.
                           • Week 8: RDVK booster shot.

                        3. **Herbal Immunity Booster:**
                           • Mix Cumin, Black Pepper, Garlic, and Turmeric paste in drinking water to boost flock immunity organically.
                        """.trimIndent(),
                        listOf("Herbal medicine for country chicken diarrhea", "Shed size requirement for 100 birds", "Incubator hatching guidelines")
                    )
                }
            }

            // Organic Farming & Bio-Inputs
            q.contains("பஞ்சகாவ்யா") || q.contains("panchagavya") || q.contains("இயற்கை") || q.contains("organic") || q.contains("மீன் அமிலம்") -> {
                if (isTamil) {
                    Pair(
                        """
                        🌱 **இயற்கை இடுபொருட்கள் தயாரிப்பு & பயன்பாடு (TNAU Organic Certification):**

                        1. **பஞ்சகாவ்யா தயாரிக்கும் முறை:**
                           • மாட்டு சாணம் 5 கிலோ + பசு நெய் 1 கிலோ சேர்த்து 3 நாட்கள் ஊறவைக்கவும்.
                           • 4ஆம் நாள்: பசுவின் சிறுநீர் 5 லிட்டர், பால் 2 லிட்டர், தயிர் 2 லிட்டர், இளநீர் 2 லிட்டர், கரும்பு சாறு 3 லிட்டர், கனிந்த வாழைப்பழம் 12 சேர்த்து தினமும் 2 முறை கலக்கவும். 18 நாட்களில் தயாராகும்.
                           • பயன்பாடு: 3% கரைசல் (1 லிட்டர் நீரில் 30 மி.லி) பயிர்களுக்கு தெளிக்கவும்.

                        2. **மீன் அமிலம் (Fish Amino Acid):**
                           • 1 கிலோ கழிவு மீன் + 1 கிலோ நாட்டுச்சர்க்கரை 20 நாட்கள் பிளாஸ்டிக் வாளியில் மூடி வைத்தால் தயாராகும். 2 மி.லி/லிட்டர் நீரில் தெளிக்கலாம்.
                        """.trimIndent(),
                        listOf("ஜீவாமிர்தம் தயாரிப்பது எப்படி?", "ஐந்திலை கரைசல் தயாரிப்பு முறை", "இயற்கை சான்றிதழ் பெறுவது எப்படி?")
                    )
                } else {
                    Pair(
                        """
                        🌱 **Organic Bio-Inputs Preparation (TNAU Standards):**

                        1. **Panchagavya Preparation:**
                           • Mix 5 kg fresh Cow Dung + 1 kg Cow Ghee; let sit for 3 days.
                           • Day 4: Add 5L Cow Urine, 2L Milk, 2L Curd, 2L Tender Coconut water, 3L Sugarcane juice, 12 ripe Bananas. Stir twice daily for 18 days.
                           • Dosage: 3% foliar spray (30 ml per Liter of water).

                        2. **Fish Amino Acid:**
                           • Mix 1 kg fish waste + 1 kg jaggery in an airtight container for 20 days. Spray @ 2ml/L water.
                        """.trimIndent(),
                        listOf("How to prepare Jeevamrutham?", "5-Leaf Herbal pest repellent recipe", "Organic farm certification steps")
                    )
                }
            }

            // Default
            else -> {
                if (isTamil) {
                    Pair(
                        """
                        🌾 **வணக்கம்! நான் உழவன் X - GPT-5 AI வேளாண் மற்றும் கால்நடை ஆலோசகர்.**

                        நீங்கள் என்னிடம் பின்வரும் தலைப்புகளில் கேள்விகள் கேட்கலாம்:
                        • **கால்நடை வளர்ப்பு**: கறவை மாடு பால் உற்பத்தி, மடிநோய், பசுந்தீவனம் CO-5, அசோலா.
                        • **ஆடு & கோழி பண்ணை**: பரண் மேல் ஆடு வளர்ப்பு, நாட்டுக்கோழி தடுப்பூசி, மேச்சேரி ஆடு.
                        • **பயிர் பாதுகாப்பு**: நெல், தக்காளி, மிளகாய், மஞ்சள் நோய்கள் & இயற்கை/இரசாயன மருந்துகள்.
                        • **சந்தை & மானியம்**: இன்றைய மண்டி விலைகள், 100% சொட்டு நீர் மானியம், நபார்ட் கடன்கள்.
                        """.trimIndent(),
                        listOf("கறவை மாடு பால் உற்பத்தி அதிகரிக்கும் தீவனம்?", "பரண் மேல் வெள்ளாடு வளர்ப்பு கடன்கள்?", "2026-27 அரசு வேளாண் மானியங்கள் என்ன?")
                    )
                } else {
                    Pair(
                        """
                        🌾 **Welcome! I am Uzhav X - GPT-5 AI Agriculture & Livestock Advisor.**

                        You can ask me questions on:
                        • **Livestock & Cattle**: Dairy cow milk yield, Mastitis cure, CO-5 fodder, Azolla.
                        • **Goat & Poultry**: Elevated goat stall feeding, Country chicken vaccination, Mecheri sheep.
                        • **Crop Protection**: Paddy, Tomato, Chilli, Turmeric diseases & exact dosages.
                        • **Markets & Subsidies**: Today's Mandi prices, 100% Drip Irrigation subsidy, NABARD loans.
                        """.trimIndent(),
                        listOf("Fodder to increase dairy cow milk yield", "How to apply for NABARD goat loan", "2026-27 Tamil Nadu Agriculture Subsidies")
                    )
                }
            }
        }

        return ChatMessage(
            id = "ai_${System.currentTimeMillis()}",
            text = replyText,
            isUser = false,
            timestamp = "Just now",
            suggestedFollowups = followups
        )
    }
}
