package ru.myvista.cheaper.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface UnitDao {

	@Query("SELECT * FROM units ORDER BY sort_order ASC")
	fun getAll(): Flow<List<UnitEntity>>

	@Insert(onConflict = OnConflictStrategy.ABORT)
	suspend fun insert(unit: UnitEntity)

	@Update
	suspend fun update(unit: UnitEntity)

	@Delete
	suspend fun delete(unit: UnitEntity)
}