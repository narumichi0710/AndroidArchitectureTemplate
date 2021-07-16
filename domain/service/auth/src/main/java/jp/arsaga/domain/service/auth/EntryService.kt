package jp.arsaga.domain.service.auth

import jp.arsaga.domain.service.core.BaseService
import kotlinx.coroutines.flow.MutableStateFlow

class EntryService(
    override val dependency: Dependency
) : BaseService<EntryService.Dependency>  {

    val passwordInput = MutableStateFlow("")

    data class Dependency(
        override val command: Command,
        override val query: Any?
    ) : BaseService.Dependency

    interface Command {
        fun login()
        fun register()
    }
}