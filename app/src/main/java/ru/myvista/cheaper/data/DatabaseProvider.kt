package ru.myvista.cheaper.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

object DatabaseProvider {

	private val MIGRATION_1_2 = object : androidx.room.migration.Migration(1, 2) {
		override fun migrate(db: androidx.sqlite.db.SupportSQLiteDatabase) {
			db.execSQL(
				"ALTER TABLE settings ADD COLUMN theme_mode TEXT NOT NULL DEFAULT 'light'"
			)
		}
	}

	private val MIGRATION_2_3 =
		object : androidx.room.migration.Migration(2, 3) {
			override fun migrate(
				db: androidx.sqlite.db.SupportSQLiteDatabase
			) {
				db.execSQL(
					"ALTER TABLE settings ADD COLUMN theme_manually_selected INTEGER NOT NULL DEFAULT 0"
				)
			}
		}

	private val MIGRATION_3_4 =
		object : androidx.room.migration.Migration(3, 4) {
			override fun migrate(
				db: androidx.sqlite.db.SupportSQLiteDatabase
			) {
				db.execSQL(
					"""ALTER TABLE settings
                   ADD COLUMN unit_order TEXT NOT NULL
                   DEFAULT '["г","кг","мл","л","шт"]'"""
				)
			}
		}

	@Volatile
	private var INSTANCE: AppDatabase? = null

	fun getDatabase(context: Context): AppDatabase {
		return INSTANCE ?: synchronized(this) {
			INSTANCE ?: run {
				System.loadLibrary("sqlcipher")

				val passphrase =
					DatabaseKeyManager.getOrCreatePassphrase(context)

				val factory = SupportOpenHelperFactory(passphrase)

				Room.databaseBuilder(
					context.applicationContext,
					AppDatabase::class.java,
					"cheaper_database_encrypted"
				)
					.openHelperFactory(factory)
					.addMigrations(
						MIGRATION_1_2,
						MIGRATION_2_3,
						MIGRATION_3_4
					)
					.build()
					.also { INSTANCE = it }
			}
		}
	}
}