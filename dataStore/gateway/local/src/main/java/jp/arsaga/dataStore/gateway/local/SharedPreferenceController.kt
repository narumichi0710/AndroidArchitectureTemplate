package jp.arsaga.dataStore.gateway.local

import android.content.SharedPreferences
import jp.arsaga.domain.entity.core.type.LocalDataKey
import timber.log.Timber


object SharedPreferenceController {

    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.Int
    ): Int? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getInt(localDataKey.key(), localDataKey.defaultValue)
    }
    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.Long
    ): Long? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getLong(localDataKey.key(), localDataKey.defaultValue)
    }
    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.Float
    ): Float? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getFloat(localDataKey.key(), localDataKey.defaultValue)
    }
    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.Boolean
    ): Boolean? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getBoolean(localDataKey.key(), localDataKey.defaultValue)
    }
    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.String
    ): String? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getString(localDataKey.key(), localDataKey.defaultValue)
    }
    private fun <T> getValue(
        sharedPreferences: SharedPreferences?,
        key: String,
        query: () -> T?
    ): T? = onBootCheck(sharedPreferences, key)
        ?.run { query() }
        .also { Timber.d("getLocalPref:${key}：$it") }
    private fun onBootCheck( // 起動直後のSharedPreferencesはキーチェックしないと正常動作しない
        sharedPreferences: SharedPreferences?,
        key: String
    ): SharedPreferences? = sharedPreferences
        ?.takeIf { it.contains(key) }


    fun <T>put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey<T>,
        value: T
    ) {
        when (value) {
            is Int -> sharedPreferences?.putInt(localDataKey.key(), value)
            is Long -> sharedPreferences?.putLong(localDataKey.key(), value)
            is Float -> sharedPreferences?.putFloat(localDataKey.key(), value)
            is Boolean -> sharedPreferences?.putBoolean(localDataKey.key(), value)
            is String? -> sharedPreferences?.putString(localDataKey.key(), value)
        }
        Timber.d("putLocalPref:${localDataKey.key()}：$value")
    }


    fun <T> remove(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey<*>
    ) {
        sharedPreferences?.remove(localDataKey.key())
        Timber.d("removeLocalPref:${localDataKey.key()}")
    }
}