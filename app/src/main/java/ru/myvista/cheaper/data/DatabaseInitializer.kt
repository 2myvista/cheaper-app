package ru.myvista.cheaper.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first

class DatabaseInitializer(
	private val database: AppDatabase
) {

	suspend fun initialize() {
		database.withTransaction {
			val units = database.unitDao().getAll().first()

			if (units.isEmpty()) {
				val defaultUnits = listOf(
					UnitEntity(name = "г", sortOrder = 0),
					UnitEntity(name = "кг", sortOrder = 1),
					UnitEntity(name = "мл", sortOrder = 2),
					UnitEntity(name = "л", sortOrder = 3),
					UnitEntity(name = "шт", sortOrder = 4)
				)

				defaultUnits.forEach { unit ->
					database.unitDao().insert(unit)
				}
			}

			val settings = database.settingsDao().getSettings().first()

			if (settings == null) {
				val kgUnit = database.unitDao()
					.getAll()
					.first()
					.first { it.name == "кг" }

				database.settingsDao().save(
					SettingsEntity(
						defaultUnitId = kgUnit.id,
						tankCapacity = 55.0,
						fuelPercent = 55.0,
						firstRunCompleted = false
					)
				)
			}
		}
	}
}