package com.example.model

data class MlKitLabelResult(
    val text: String,
    val confidence: Float,
    val index: Int = 0
)

data class TreatmentStep(
    val stepNumber: Int,
    val titleEnglish: String,
    val titleTamil: String,
    val descriptionEnglish: String,
    val descriptionTamil: String,
    val timeframe: String, // e.g. "Day 1: Immediate Sanitation", "Day 2-3: Bio/Fungicide Spray", "Day 7: Follow-up & Soil Care"
    val iconCategory: String = "sanitation" // "sanitation", "organic", "chemical", "prevention"
)

data class PlantDiseaseDiagnosis(
    val id: String,
    val cropName: String,
    val tamilCropName: String,
    val diseaseName: String,
    val tamilDiseaseName: String,
    val scientificName: String,
    val confidencePercent: Int,
    val severityLevel: String, // Mild, Moderate, Severe
    val symptomsEnglish: List<String>,
    val symptomsTamil: List<String>,
    val organicRemedies: List<String>,
    val chemicalTreatments: List<String>,
    val preventiveCare: List<String>,
    val tnauexpertNote: String,
    val detectedLabels: List<MlKitLabelResult> = emptyList(),
    val detectionSource: String = "Google ML Kit Image Labeler (On-Device)",
    val detectedStage: String = "Active Vegetative",
    val urgency: String = "Prompt Action Recommended",
    val stepByStepCureProcess: List<TreatmentStep> = emptyList()
)
