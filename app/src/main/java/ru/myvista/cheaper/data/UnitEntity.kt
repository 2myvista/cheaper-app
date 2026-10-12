package ru.myvista.cheaper.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "units")
data class UnitEntity(
	@PrimaryKey
	val id: String = UUID.randomUUID().toString(),

	val name: String,

	@ColumnInfo(name = "sort_order")
	val sortOrder: Int
)