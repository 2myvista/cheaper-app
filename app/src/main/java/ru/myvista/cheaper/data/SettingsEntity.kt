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
	val firstRunCompleted: Boolean,

	@ColumnInfo(name = "theme_mode")
	val themeMode: String = "light",

	@ColumnInfo(name = "theme_manually_selected")
	val themeManuallySelected: Boolean = false,

	@ColumnInfo(name = "unit_order")
	val unitOrder: String = """["г","кг","мл","л","шт"]"""
)