package ru.myvista.cheaper.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "settings")
data class SettingsEntity(
	@PrimaryKey
	@ColumnInfo(name = "default_unit_id")
	val defaultUnitId: String,

	@ColumnInfo(name = "tank_capacity")
	val tankCapacity: Double,

	@ColumnInfo(name = "fuel_percent")
	val fuelPercent: Double,

	@ColumnInfo(name = "first_run_completed")
	val firstRunCompleted: Boolean
)