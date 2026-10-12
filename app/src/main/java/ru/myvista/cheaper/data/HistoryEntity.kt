package ru.myvista.cheaper.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "history")
data class HistoryEntity(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),

	@ColumnInfo(name = "comparison_name")
	val comparisonName: String,

	@ColumnInfo(name = "created_at")
	val createdAt: Long,

	@ColumnInfo(name = "unit_id")
	val unitId: String,

	val price1: Double,

	val quantity1: Double,

	val price2: Double,

	val quantity2: Double,

	@ColumnInfo(name = "result_price1")
	val resultPrice1: Double,

	@ColumnInfo(name = "result_price2")
	val resultPrice2: Double
)