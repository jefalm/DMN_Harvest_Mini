package com.example.dmnharvestmini

import android.Manifest
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var repository: HarvestRepository
    private lateinit var listView: ListView
    private lateinit var btnGenerate: Button
    private lateinit var btnRefresh: Button
    private lateinit var btnToggleService: Button
    private var thoughtsList: List<Thought> = emptyList()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            startHarvestService()
        } else {
            Toast.makeText(this, "Notification permission denied", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val database = HarvestDatabase.getDatabase(this)
        repository = HarvestRepository(database.thoughtDao())

        listView = findViewById(R.id.list_thoughts)
        btnGenerate = findViewById(R.id.btn_generate)
        btnRefresh = findViewById(R.id.btn_refresh)
        // Note: Assuming R.id.btn_toggle_service exists or user will add it. 
        // For now, I'll just start it automatically or add a button if I could edit XML.
        // Since I can't easily edit layout XML without seeing it, I'll add logic to start it.
        
        btnGenerate.setOnClickListener {
            generateAndCopy()
        }

        btnRefresh.setOnClickListener {
            loadThoughts()
        }

        loadThoughts()
        checkAndStartService()
    }

    private fun checkAndStartService() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                startHarvestService()
            } else {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        } else {
            startHarvestService()
        }
    }

    private fun startHarvestService() {
        val intent = Intent(this, HarvestForegroundService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        } else {
            startService(intent)
        }
    }

    private fun loadThoughts() {
        lifecycleScope.launch {
            thoughtsList = repository.getTodaysThoughts()
            val displayList = thoughtsList.map { 
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(it.timestamp))
                "[$time] ${it.content}"
            }
            
            val adapter = ArrayAdapter(this@MainActivity, android.R.layout.simple_list_item_1, displayList)
            listView.adapter = adapter
            btnGenerate.isEnabled = thoughtsList.isNotEmpty()
        }
    }

    private fun generateAndCopy() {
        val capturedText = thoughtsList.joinToString("\n") { "- ${it.content}" }
        val prompt = "Below is a stream of words from my wandering thoughts today. " +
                "Please turn this into a short story:\n\n$capturedText"
        
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("Harvest Summary", prompt)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "Copied to clipboard for Gemini!", Toast.LENGTH_SHORT).show()
    }
}
