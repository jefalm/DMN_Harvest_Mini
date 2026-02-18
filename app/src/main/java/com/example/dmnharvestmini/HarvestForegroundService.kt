package com.example.dmnharvestmini

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class HarvestForegroundService : Service() {

    companion object {
        private const val CHANNEL_ID = "harvest_service_channel"
        private const val NOTIFICATION_ID = 1001
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = createNotification()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createNotification(): Notification {
        val captureIntent = Intent(this, VoiceCaptureActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        
        val pendingIntent = PendingIntent.getActivity(
            this, 
            0, 
            captureIntent, 
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("DMN Harvest")
            .setContentText("Tap to capture a thought")
            .setSmallIcon(R.drawable.ic_brain)
            .setOngoing(true) // This is the key flag to make it non-dismissible
            .setSilent(true)   // Keep it quiet as a background utility
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Increased priority for older Android
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setContentIntent(pendingIntent)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            builder.setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
        }

        return builder.build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Harvest Service Channel",
                NotificationManager.IMPORTANCE_DEFAULT // Increased importance for modern Android
            ).apply {
                description = "Keeps DMN Harvest active for quick access"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(serviceChannel)
        }
    }
}
