package ru.myvista.cheaper.data

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import java.security.SecureRandom

object DatabaseKeyManager {

	private const val KEY_ALIAS = "cheaper_database_key"
	private const val PREFS_NAME = "database_key_storage"
	private const val PREF_KEY = "encrypted_passphrase"

	fun getOrCreatePassphrase(context: Context): ByteArray {
		val prefs = context.getSharedPreferences(
			PREFS_NAME,
			Context.MODE_PRIVATE
		)

		val encrypted = prefs.getString(PREF_KEY, null)

		if (encrypted != null) {
			return decrypt(encrypted)
		}

		val passphrase = ByteArray(32)
		SecureRandom().nextBytes(passphrase)

		val encryptedPassphrase = encrypt(passphrase)

		prefs.edit()
			.putString(PREF_KEY, encryptedPassphrase)
			.apply()

		return passphrase
	}

	private fun encrypt(data: ByteArray): String {
		val cipher = Cipher.getInstance("AES/GCM/NoPadding")
		cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())

		val iv = cipher.iv
		val encrypted = cipher.doFinal(data)

		val combined = iv + encrypted

		return Base64.encodeToString(combined, Base64.NO_WRAP)
	}

	private fun decrypt(value: String): ByteArray {
		val combined = Base64.decode(value, Base64.NO_WRAP)

		val iv = combined.copyOfRange(0, 12)
		val encrypted = combined.copyOfRange(12, combined.size)

		val cipher = Cipher.getInstance("AES/GCM/NoPadding")
		cipher.init(
			Cipher.DECRYPT_MODE,
			getOrCreateSecretKey(),
			GCMParameterSpec(128, iv)
		)

		return cipher.doFinal(encrypted)
	}

	private fun getOrCreateSecretKey(): SecretKey {
		val keyStore = KeyStore.getInstance("AndroidKeyStore")
		keyStore.load(null)

		val existingKey = keyStore.getKey(KEY_ALIAS, null)

		if (existingKey is SecretKey) {
			return existingKey
		}

		val keyGenerator = KeyGenerator.getInstance(
			KeyProperties.KEY_ALGORITHM_AES,
			"AndroidKeyStore"
		)

		keyGenerator.init(
			KeyGenParameterSpec.Builder(
				KEY_ALIAS,
				KeyProperties.PURPOSE_ENCRYPT or
						KeyProperties.PURPOSE_DECRYPT
			)
				.setBlockModes(KeyProperties.BLOCK_MODE_GCM)
				.setEncryptionPaddings(
					KeyProperties.ENCRYPTION_PADDING_NONE
				)
				.setKeySize(256)
				.build()
		)

		return keyGenerator.generateKey()
	}
}