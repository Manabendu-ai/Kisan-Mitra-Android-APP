package com.riku.kisanmitra.util

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TtsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false
    private var pendingSpeech: Pair<String, String>? = null
    
    private var fastSpeechInterpreter: Interpreter? = null
    private var hiFiGanInterpreter: Interpreter? = null

    init {
        initializeStandardTts()
        initializeTfliteTts()
    }

    private fun initializeStandardTts() {
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isInitialized = true
                Log.d("TtsManager", "Standard TTS Initialized")
                pendingSpeech?.let { (text, lang) ->
                    speak(text, lang)
                    pendingSpeech = null
                }
            } else {
                Log.e("TtsManager", "Standard TTS Initialization failed")
            }
        }
    }

    private fun initializeTfliteTts() {
        try {
            val options = Interpreter.Options().apply {
                setNumThreads(4)
            }
            fastSpeechInterpreter = Interpreter(FileUtil.loadMappedFile(context, "fastspeech_quant.tflite"), options)
            hiFiGanInterpreter = Interpreter(FileUtil.loadMappedFile(context, "hifigan_dr.tflite"), options)
            Log.d("TtsManager", "TFLite TTS Models loaded successfully")
        } catch (e: Exception) {
            Log.e("TtsManager", "Error loading TFLite models: ${e.message}")
        }
    }

    /**
     * Speaks the given text.
     * @param text The text to speak.
     * @param language The language code ("en" or "kn").
     * @param queueMode TextToSpeech.QUEUE_FLUSH (default) to interrupt current speech, 
     *                  or TextToSpeech.QUEUE_ADD to play after current speech.
     */
    fun speak(text: String, language: String = "en", queueMode: Int = TextToSpeech.QUEUE_ADD) {
        if (!isInitialized) {
            Log.w("TtsManager", "TTS not initialized yet. Queueing speech: $text")
            pendingSpeech = text to language
            return
        }
        
        val locale = if (language == "kn" || language == "kn-IN" || language == "Kannada") {
            Locale("kn", "IN")
        } else {
            Locale.US
        }

        val result = tts?.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TtsManager", "Language $language is not supported")
            tts?.language = Locale.US
        }
        
        Log.d("TtsManager", "Speaking in $language: $text (Mode: $queueMode)")
        tts?.speak(text, queueMode, null, "kisan_mitra_tts_${System.currentTimeMillis()}")
    }

    fun stop() {
        tts?.stop()
    }

    fun release() {
        tts?.shutdown()
        fastSpeechInterpreter?.close()
        hiFiGanInterpreter?.close()
    }
}
