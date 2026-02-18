package com.example.dmnharvestmini

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class VoiceCaptureActivity : ComponentActivity() {

    private lateinit var harvestRepository: HarvestRepository
    private var speechRecognizer: SpeechRecognizer? = null
    private var isRecognizing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setupLockScreenVisibility()
        
        setContentView(R.layout.activity_voice_capture)
        
        val database = HarvestDatabase.getDatabase(this)
        harvestRepository = HarvestRepository(database.thoughtDao())
        
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onResume() {
        super.onResume()
        if (!isRecognizing) {
            checkPermissionAndStart()
        }
    }

    private fun setupLockScreenVisibility() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON or
                        WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
            )
        }
    }

    private fun checkPermissionAndStart() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            startSpeechRecognition()
        } else {
            Toast.makeText(this, "Microphone permission required", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun startSpeechRecognition() {
        isRecognizing = true
        performMaxHapticFeedback()
        
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this)
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, packageName)
        }

        speechRecognizer?.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) {
                findViewById<TextView>(R.id.status_text).text = getString(R.string.status_listening)
            }

            override fun onBeginningOfSpeech() {
                findViewById<TextView>(R.id.status_text).text = getString(R.string.status_listening)
            }

            override fun onRmsChanged(rmsdB: Float) {}
            override fun onBufferReceived(buffer: ByteArray?) {}
            override fun onEndOfSpeech() {}

            override fun onError(error: Int) {
                if (error == SpeechRecognizer.ERROR_NO_MATCH || error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
                    Toast.makeText(this@VoiceCaptureActivity, "No speech detected", Toast.LENGTH_SHORT).show()
                } else if (error != SpeechRecognizer.ERROR_CLIENT) {
                    Toast.makeText(this@VoiceCaptureActivity, "Error: $error", Toast.LENGTH_SHORT).show()
                }
                finish()
            }

            override fun onResults(results: Bundle?) {
                val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                val text = matches?.get(0)
                
                text?.let { thought ->
                    lifecycleScope.launch {
                        harvestRepository.saveThought(thought)
                        Toast.makeText(this@VoiceCaptureActivity, "Harvested: \"$thought\"", Toast.LENGTH_SHORT).show()
                        performMaxHapticFeedback()
                        finish()
                    }
                } ?: finish()
            }

            override fun onPartialResults(partialResults: Bundle?) {}
            override fun onEvent(eventType: Int, params: Bundle?) {}
        })

        speechRecognizer?.startListening(intent)
    }

    private fun performMaxHapticFeedback() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vibratorManager.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
        
        // Pattern: 0ms delay, 250ms vibrate (Longer), 60ms rest, 350ms vibrate (Even Longer)
        val timings = longArrayOf(0, 250, 60, 350)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Amplitudes: 255 is the absolute maximum ceiling
            val amplitudes = intArrayOf(0, 255, 0, 255)
            val effect = VibrationEffect.createWaveform(timings, amplitudes, -1)
            vibrator.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(timings, -1)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer?.destroy()
    }
}
