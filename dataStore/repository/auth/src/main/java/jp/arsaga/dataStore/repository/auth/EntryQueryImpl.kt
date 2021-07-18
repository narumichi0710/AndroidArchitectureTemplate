package jp.arsaga.dataStore.repository.auth

import android.content.Context
import jp.arsaga.dataStore.gateway.local.SharedPreferenceController
import jp.arsaga.dataStore.repository.core.EncryptedSharedPreferencesStore
import jp.arsaga.dataStore.repository.core.EncryptedSharedPreferencesStore.Companion.getSharedPreferences
import jp.arsaga.domain.entity.core.type.LocalDataKey
import jp.arsaga.domain.service.auth.EntryService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class EntryQueryImpl(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : EntryService.Query {

    override fun queryLocalCacheData(
        localDataKey: LocalDataKey<String?>,
        callback: (String?) -> Unit
    ) {
        coroutineScope.launch(Dispatchers.IO) {
            SharedPreferenceController.get(
                EncryptedSharedPreferencesStore.USER_DEFAULT.getSharedPreferences(context, ""),
                localDataKey as LocalDataKey.String
            ).run {
                callback(this)
            }
        }
    }
}