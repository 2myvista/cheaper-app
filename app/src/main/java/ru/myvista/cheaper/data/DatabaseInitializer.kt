package ru.myvista.cheaper.data

import androidx.room.withTransaction
import kotlinx.coroutines.flow.first

class DatabaseInitializer(
	private val database: AppDatabase
) {

	suspend fun initialize() {
		database.withTransaction {
			val unitDao = database.unitDao()

			val defaultUnitNames = listOf("г", "кг", "мл", "л", "шт")

			val remainingUnits = unitDao.getAll().first()

			val existingNames = remainingUnits.map { it.name }.toSet()

			defaultUnitNames.forEachIndexed { index, name ->
				if (name !in existingNames) {
					unitDao.insert(
						UnitEntity(
							name = name,
							sortOrder = index
						)
					)
				}
			}
			val settings = database.settingsDao().getSettings().first()

			if (settings == null) {
				val kgUnit = unitDao.getAll()
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