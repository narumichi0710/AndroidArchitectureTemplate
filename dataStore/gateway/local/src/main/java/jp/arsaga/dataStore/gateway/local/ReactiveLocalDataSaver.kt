package jp.arsaga.dataStore.gateway.local

import android.content.SharedPreferences
import jp.arsaga.domain.entity.core.type.LocalDataKey
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class ReactiveLocalDataSaver<T>(
    coroutineScope: CoroutineScope,
    flow: Flow<T>,
    localDataKey: LocalDataKey<T>,
    saveAction: (SharedPreferences.Editor?, LocalDataKey<T>, T) -> Unit = ::primitiveDataSave,
    sharedPreferencesFactory: suspend () -> SharedPreferences?
) {
    init {
        coroutineScope.launch(Dispatchers.IO) {
            val sharedPreferences = sharedPreferencesFactory()
            flow.collect {
                sharedPreferences?.edit()?.also { editor ->
                    saveAction(editor, localDataKey, it)
                }?.apply()
            }
        }
    }
}

private fun <T> primitiveDataSave(
    editor: SharedPreferences.Editor?,
    localDataKey: LocalDataKey<T>,
    value: T
) {
    SharedPreferenceController.put(
        editor,
        localDataKey,
        value
    )
}
