package ru.myvista.cheaper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

	@Query("SELECT * FROM history ORDER BY created_at DESC")
	fun getAll(): Flow<List<HistoryEntity>>

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(item: HistoryEntity)

	@Delete
	suspend fun delete(item: HistoryEntity)

	@Query("DELETE FROM history")
	suspend fun deleteAll()
}