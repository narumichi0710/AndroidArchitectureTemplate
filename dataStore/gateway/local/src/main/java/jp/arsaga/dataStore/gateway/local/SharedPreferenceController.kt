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
    fun get(
        sharedPreferences: SharedPreferences?,
        localDataKey: LocalDataKey.StringSet
    ): MutableSet<String>? = getValue(sharedPreferences, localDataKey.key()) {
        sharedPreferences?.getStringSet(localDataKey.key(), localDataKey.defaultValue)
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


    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.Int,
        value: Int
    ) {
        sharedPreferences?.putInt(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.Long,
        value: Long
    ) {
        sharedPreferences?.putLong(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.Float,
        value: Float
    ) {
        sharedPreferences?.putFloat(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.Boolean,
        value: Boolean
    ) {
        sharedPreferences?.putBoolean(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.String,
        value: String?
    ) {
        sharedPreferences?.putString(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    fun put(
        sharedPreferences: SharedPreferences.Editor?,
        localDataKey: LocalDataKey.StringSet,
        value: Set<String>?
    ) {
        sharedPreferences?.putStringSet(localDataKey.key(), value)
        putLog(localDataKey, value)
    }
    private fun putLog(localDataKey: LocalDataKey<*>, value: Any?) {
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