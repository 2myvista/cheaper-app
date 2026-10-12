package ru.myvista.cheaper.data

import android.content.Context
import androidx.room.Room
import net.zetetic.database.sqlcipher.SupportOpenHelperFactory

object DatabaseProvider {

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
					.build()
					.also { INSTANCE = it }
			}
		}
	}
}