package jp.arsaga.dataStore.repository.auth

import android.content.Context
import jp.arsaga.dataStore.gateway.local.ReactiveLocalDataSaver
import jp.arsaga.dataStore.repository.core.EncryptedSharedPreferencesStore
import jp.arsaga.dataStore.repository.core.EncryptedSharedPreferencesStore.Companion.getSharedPreferences
import jp.arsaga.dataStore.repository.core.TransitionCallbackHandler
import jp.arsaga.domain.entity.core.type.LocalDataKey
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.core.NavigationCallback
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

class EntryCommandImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : EntryService.Command<NavigationCallback> {

    private val reactiveLocalDataSaver = mutableSetOf<ReactiveLocalDataSaver<*>>()

    override fun saveLocalCacheData(flow: Flow<String?>, localDataKey: LocalDataKey<String?>) {
        ReactiveLocalDataSaver(
            coroutineScope, flow, localDataKey
        ) { EncryptedSharedPreferencesStore.USER_DEFAULT.getSharedPreferences(context, "") }
            .apply(reactiveLocalDataSaver::add)
    }

    override fun login(onSuccess: () -> NavigationCallback) {
        Thread.sleep(1500L)
        TransitionCallbackHandler.post(onSuccess())
    }

    override fun register(onSuccess: () -> NavigationCallback) {
    }

}