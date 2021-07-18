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
    sharedPreferencesFactory: suspend () -> SharedPreferences?
) {
    init {
        coroutineScope.launch(Dispatchers.IO) {
            val sharedPreferences = sharedPreferencesFactory()
            flow.collect {
                sharedPreferences?.edit()?.also { editor ->
                    SharedPreferenceController.put(
                        editor,
                        localDataKey,
                        it
                    )
                }?.apply()
            }
        }
    }
}
