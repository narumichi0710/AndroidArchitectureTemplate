package jp.arsaga.domain.service.auth

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asFlow
import jp.arsaga.domain.entity.core.type.LocalDataKey
import jp.arsaga.domain.service.core.BaseService
import kotlinx.coroutines.flow.Flow

class EntryService(
    override val dependency: Dependency
) : BaseService<EntryService.Dependency>  {

    val passwordInput = MutableLiveData("").apply {
        dependency.query.queryLocalCacheData(LocalDataKey.String.Password) {
            postValue(it)
        }
        dependency.command.saveLocalCacheData(asFlow(), LocalDataKey.String.Password)
    }

    data class Dependency(
        override val command: Command,
        override val query: Query
    ) : BaseService.Dependency

    interface Command {
        fun login()
        fun register()
        fun saveLocalCacheData(flow: Flow<String?>, localDataKey: LocalDataKey<String?>)
    }

    interface Query {
        fun queryLocalCacheData(localDataKey: LocalDataKey<String?>, callback: (String?) -> Unit)
    }
}