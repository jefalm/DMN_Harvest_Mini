package com.example.dmnharvestmini

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import java.time.Instant

class CaptureAction : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        // Strong confirmation haptic
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        try {
            vibrator?.let {
                // Pattern: 0ms delay, 250ms vibrate (Longer), 60ms rest, 350ms vibrate (Even Longer)
                val timings = longArrayOf(0, 250, 60, 350)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    // Amplitudes: 255 is the absolute maximum ceiling
                    val amplitudes = intArrayOf(0, 255, 0, 255)
                    it.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(timings, -1)
                }
            }
        } catch (e: Exception) {
            // ignore vibration errors
        }

        // store a simple timestamp in preferences
        val key = longPreferencesKey("last_capture")
        val now = System.currentTimeMillis()
        
        updateAppWidgetState(context, glanceId) { prefs ->
            prefs[key] = now
        }
        
        // Refresh the widget
        DMNHarvestWidget().update(context, glanceId)
    }
}
