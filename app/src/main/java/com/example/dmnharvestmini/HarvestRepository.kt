package com.example.dmnharvestmini

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HarvestRepository(private val thoughtDao: ThoughtDao) {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    suspend fun saveThought(thoughtContent: String) {
        val now = Date()
        val thought = Thought(
            content = thoughtContent,
            timestamp = now.time,
            dateString = dateFormat.format(now)
        )
        thoughtDao.insert(thought)
    }

    suspend fun getTodaysThoughts(): List<Thought> {
        val dateStr = dateFormat.format(Date())
        return thoughtDao.getThoughtsByDate(dateStr)
    }
}
