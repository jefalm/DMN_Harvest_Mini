package com.example.dmnharvestmini

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase

@Entity(tableName = "thoughts")
data class Thought(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val content: String,
    val timestamp: Long,
    val dateString: String // yyyy-MM-dd for easy filtering
)

@Dao
interface ThoughtDao {
    @Insert
    suspend fun insert(thought: Thought)

    @Query("SELECT * FROM thoughts WHERE dateString = :date ORDER BY timestamp DESC")
    suspend fun getThoughtsByDate(date: String): List<Thought>

    @Query("SELECT * FROM thoughts ORDER BY timestamp DESC")
    suspend fun getAllThoughts(): List<Thought>
}

@Database(entities = [Thought::class], version = 1)
abstract class HarvestDatabase : RoomDatabase() {
    abstract fun thoughtDao(): ThoughtDao

    companion object {
        @Volatile
        private var INSTANCE: HarvestDatabase? = null

        fun getDatabase(context: Context): HarvestDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HarvestDatabase::class.java,
                    "harvest_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
