package jp.arsaga.domain.service.auth

import androidx.lifecycle.MutableLiveData
import jp.arsaga.domain.service.core.BaseService

class EntryService(
    override val dependency: Dependency
) : BaseService<EntryService.Dependency>  {

    val passwordInput = MutableLiveData("")

    data class Dependency(
        override val command: Command,
        override val query: Any?
    ) : BaseService.Dependency

    interface Command {
        fun login()
        fun register()
    }
}