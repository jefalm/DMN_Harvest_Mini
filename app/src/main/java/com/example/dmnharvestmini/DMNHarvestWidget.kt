package com.example.dmnharvestmini

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.action.actionStartActivity
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.padding
import androidx.glance.text.Text
import androidx.compose.ui.unit.dp
import androidx.glance.ExperimentalGlanceApi
import androidx.glance.action.clickable
import androidx.glance.appwidget.cornerRadius
import androidx.glance.background
import androidx.glance.layout.Column
import androidx.glance.text.TextStyle
import androidx.glance.GlanceTheme
import androidx.glance.text.FontWeight
import androidx.glance.unit.ColorProvider
import androidx.glance.layout.fillMaxHeight
import androidx.glance.layout.fillMaxWidth

class DMNHarvestWidget : GlanceAppWidget() {
    @OptIn(ExperimentalGlanceApi::class)
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                Box(
                    modifier = GlanceModifier
                        .fillMaxSize()
                        .background(GlanceTheme.colors.primaryContainer)
                        // Updated to Android 16 standard 24dp for better One UI 8 integration
                        .cornerRadius(24.dp)
                        .clickable(
                            actionStartActivity(
                                Intent(context, VoiceCaptureActivity::class.java),
                                activityOptions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                                    ActivityOptions.makeBasic().apply {
                                        launchDisplayId = 0 // Using Kotlin property syntax
                                    }.toBundle()
                                } else null
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = GlanceModifier.padding(8.dp).fillMaxSize()
                    ) {
                        Text(
                            text = "Harvest",
                            style = TextStyle(
                                fontWeight = FontWeight.Bold,
                                color = GlanceTheme.colors.onPrimaryContainer
                            )
                        )
                        Text(
                            text = "Tap to Record",
                            style = TextStyle(
                                color = GlanceTheme.colors.onPrimaryContainer
                            )
                        )
                    }
                }
            }
        }
    }
}

class DMNHarvestWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = DMNHarvestWidget()
}
