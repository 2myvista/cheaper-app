package ru.myvista.cheaper.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
	entities = [
		UnitEntity::class,
		HistoryEntity::class,
		SettingsEntity::class
	],
	version = 4,
	exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

	abstract fun unitDao(): UnitDao

	abstract fun historyDao(): HistoryDao

	abstract fun settingsDao(): SettingsDao
}