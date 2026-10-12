package ru.myvista.cheaper.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SettingsDao {

	@Query("SELECT * FROM settings LIMIT 1")
	fun getSettings(): Flow<SettingsEntity?>

	@Insert(onConflict = OnConflictStrategy.REPLACE)
	suspend fun save(settings: SettingsEntity)

	@Update
	suspend fun update(settings: SettingsEntity)

	@Query("UPDATE settings SET default_unit_id = :defaultUnitId")
	suspend fun updateDefaultUnit(defaultUnitId: String)


	@Query("""
    UPDATE settings
    SET tank_capacity = :tankCapacity,
        fuel_percent = :fuelPercent
	""")
	suspend fun updateFuelSettings(
		tankCapacity: Double,
		fuelPercent: Double
	)

	@Query("UPDATE settings SET tank_capacity = :tankCapacity")
	suspend fun updateTankCapacity(tankCapacity: Double)
}