package jp.arsaga.dataStore.repository.core

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import timber.log.Timber
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface SharedPreferencesFile {
    fun fileName(param: String): String
}

enum class EncryptedSharedPreferencesStore : SharedPreferencesFile {
    USER_DEFAULT {
        override fun fileName(param: String): String = name
    },
    ;

    companion object {
        private val store = ConcurrentHashMap<EncryptedSharedPreferencesStore, SharedPreferences>()

        private val lockCache = mutableMapOf<String, Any>()

        suspend fun EncryptedSharedPreferencesStore.getSharedPreferences(
            context: Context,
            param: String
        ): SharedPreferences? = suspendCoroutine { continuation ->
            val fileName = fileName(param)
            val lock = lockCache.getOrPut(fileName) { Any() }
            runCatching {
                synchronized(lock) {
                    generate(context, this, fileName)
                }.also {
                    lockCache.remove(fileName)
                }
            }.onSuccess { continuation.resume(it) }
                .onFailure {
                    Timber.e("EncryptedSharedPreferences crashed:$it")
                    continuation.resume(null)
                }.getOrNull()
        }

        private fun generate(
            context: Context,
            key: EncryptedSharedPreferencesStore,
            fileName: String
        ): SharedPreferences = store.getOrPut(key) {
            EncryptedSharedPreferences.create(
                fileName,
                MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }
}