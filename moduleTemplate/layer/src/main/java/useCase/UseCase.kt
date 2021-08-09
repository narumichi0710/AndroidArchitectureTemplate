package jp.arsaga.domain.useCase.{Small}

import jp.arsaga.domain.useCase.core.BaseUseCase

class {Large}UseCase<NavCallback>(
    override val dependency: Dependency<NavCallback>
) : BaseUseCase<{Large}UseCase.Dependency<NavCallback>>  {

    data class Dependency<NavCallback>(
        override val navigator: Navigator<NavCallback>,
        override val command: Command<NavCallback>,
        override val query: Query
    ) : BaseUseCase.Dependency

    interface Navigator<NavCallback>

    interface Command<NavCallback>

    interface Query
}