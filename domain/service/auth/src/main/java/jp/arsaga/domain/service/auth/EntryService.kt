package jp.arsaga.domain.service.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import jp.arsaga.domain.entity.core.type.LocalDataKey
import jp.arsaga.domain.service.core.BaseService
import kotlinx.coroutines.flow.Flow

class EntryService<NavCallback>(
    override val dependency: Dependency<NavCallback>
) : BaseService<EntryService.Dependency<NavCallback>>  {

    val loginIdInput = MutableLiveData("").apply {
        dependency.query.queryLocalCacheData(LocalDataKey.String.Password) {
            postValue(it)
        }
        dependency.command.saveLocalCacheData(asFlow(), LocalDataKey.String.Password)
    }

    fun login() {
        dependency.command.login {
            dependency.navigator.successLogin
        }
    }

    data class Dependency<NavCallback>(
        override val navigator: Navigator<NavCallback>,
        override val command: Command<NavCallback>,
        override val query: Query
    ) : BaseService.Dependency

    interface Navigator<NavCallback> {
        val successLogin: NavCallback
    }

    interface Command<NavCallback> {
        fun login(onSuccess: () -> NavCallback)
        fun register(onSuccess: () -> NavCallback)
        fun saveLocalCacheData(flow: Flow<String?>, localDataKey: LocalDataKey<String?>)
    }

    interface Query {
        fun queryLocalCacheData(localDataKey: LocalDataKey<String?>, callback: (String?) -> Unit)
    }
}