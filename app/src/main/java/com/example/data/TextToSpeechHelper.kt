package com.example.data

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.model.AppLanguage
import java.util.Locale

class TextToSpeechHelper(
    private val context: Context
) : TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isInitialized = false

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Failed to create TextToSpeech instance", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true
            tts?.language = Locale("ta", "IN")
        } else {
            Log.e("TextToSpeechHelper", "TTS Initialization failed with status: $status")
        }
    }

    fun speak(text: String, language: AppLanguage) {
        if (!isInitialized || tts == null) return

        try {
            val locale = if (language == AppLanguage.TAMIL) Locale("ta", "IN") else Locale("en", "IN")
            val langResult = tts?.setLanguage(locale)
            if (langResult == TextToSpeech.LANG_MISSING_DATA || langResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.ENGLISH)
            }

            // Clean text from emojis or markdown symbols for smoother speech
            val cleanText = text.replace(Regex("[*#•🐄🐐🐔🌾🌱🏛️💡★✔✖]"), "").take(400)
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "Utterance_${System.currentTimeMillis()}")
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Error speaking text", e)
        }
    }

    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Error stopping TTS", e)
        }
    }

    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
            tts = null
        } catch (e: Exception) {
            Log.e("TextToSpeechHelper", "Error shutting down TTS", e)
        }
    }
}
