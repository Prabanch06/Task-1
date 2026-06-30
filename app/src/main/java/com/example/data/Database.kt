package com.example.data

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "history")
data class HistoryItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val expression: String,
    val result: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false,
    val aiExplanation: String? = null
)

@Dao
interface HistoryDao {
    @Query("SELECT * FROM history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<HistoryItem>>

    @Query("SELECT * FROM history WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getFavorites(): Flow<List<HistoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: HistoryItem)

    @Query("UPDATE history SET isFavorite = :isFav WHERE id = :id")
    suspend fun updateFavoriteStatus(id: Int, isFav: Boolean)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteItemById(id: Int)
    
    @Query("DELETE FROM history")
    suspend fun clearHistory()
}

@Database(entities = [HistoryItem::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
}

class HistoryRepository(private val historyDao: HistoryDao) {
    val allHistory: Flow<List<HistoryItem>> = historyDao.getAllHistory()
    val allFavorites: Flow<List<HistoryItem>> = historyDao.getFavorites()

    suspend fun insert(item: HistoryItem) = historyDao.insertItem(item)
    suspend fun deleteById(id: Int) = historyDao.deleteItemById(id)
    suspend fun clearAll() = historyDao.clearHistory()
    suspend fun toggleFavorite(id: Int, isFavorite: Boolean) = historyDao.updateFavoriteStatus(id, isFavorite)
}
