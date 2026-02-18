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
        // simple haptic handshake
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        try {
            vibrator?.let {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    it.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    it.vibrate(40)
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
