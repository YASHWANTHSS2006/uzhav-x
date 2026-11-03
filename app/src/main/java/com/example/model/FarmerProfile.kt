package com.example.model

data class FarmerProfile(
    val name: String = "Sundaram Murugan",
    val contact: String = "9842109876",
    val state: String = "Tamil Nadu",
    val district: String = "Thanjavur",
    val taluka: String = "Orathanadu",
    val village: String = "Pudur",
    val landSizeAcres: Double = 3.5,
    val soilType: String = "Alluvial Loam (வண்டல் மண்)",
    val waterSource: String = "Canal & Borewell (வாய்க்கால் & ஆழ்துளை)",
    val primaryCrops: List<String> = listOf("Paddy (நெல்)", "Black Gram (உளுந்து)"),
    val soilTestResult: SoilTestResult = SoilTestResult(),
    val isRegistered: Boolean = true,
    val currentRole: UserRole = UserRole.FARMER
)

enum class UserRole {
    FARMER,
    ADMIN;

    fun getDisplayName(lang: AppLanguage): String = when (this) {
        FARMER -> if (lang == AppLanguage.TAMIL) "விவசாயி (பயனர்)" else "Farmer (User Mode)"
        ADMIN -> if (lang == AppLanguage.TAMIL) "உரக்கடை நிர்வாகி (அட்மின்)" else "Store Admin (Manage Stock & Prices)"
    }
}

val TAMIL_NADU_DISTRICTS = listOf(
    "Thanjavur",
    "Tiruvarur",
    "Nagapattinam",
    "Madurai",
    "Coimbatore",
    "Erode",
    "Salem",
    "Dindigul",
    "Tiruchirappalli",
    "Theni",
    "Tirunelveli",
    "Cuddalore",
    "Villupuram",
    "Vellore",
    "Kallakurichi",
    "Dharmapuri",
    "Krishnagiri",
    "Namakkal",
    "Karur",
    "Pudukkottai",
    "Sivaganga",
    "Ramanathapuram",
    "Virudhunagar",
    "Tenkasi",
    "Thoothukudi",
    "Kanyakumari"
)

val SOIL_TYPES = listOf(
    "Alluvial Loam (வண்டல் மண்)",
    "Red Loam (செம்மண்)",
    "Black Cotton (கரிசல் மண்)",
    "Clay Loam (களிமண்)",
    "Sandy Loam (மணல் சார்ந்த மண்)",
    "Laterite Soil (சரளை மண்)"
)

val WATER_SOURCES = listOf(
    "Canal Irrigation (கால்வாய் பாசனம்)",
    "Borewell / Tube well (ஆழ்துளை கிணறு)",
    "Open Well (திறந்தவெளி கிணறு)",
    "Drip / Sprinkler (சொட்டு நீர் பாசனம்)",
    "Rainfed Only (மானாவாரி / மழைப்பொழிவு)"
)

val TAMIL_NADU_SEASONS = listOf(
    "Samba / Thaladi / Rabi (சம்பா பருவம் - Oct to Jan)",
    "Kuruvai / Early Kharif (குறுவை பருவம் - Jun to Sep)",
    "Navarai / Summer / Kodai (நவரை / கோடை பருவம் - Feb to May)"
)
