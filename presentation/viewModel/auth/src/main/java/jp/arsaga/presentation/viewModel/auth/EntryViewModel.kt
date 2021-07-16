package jp.arsaga.presentation.viewModel.auth

import android.app.Application
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.core.BaseService
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class EntryViewModel(
    application: Application,
    dependency: EntryService.Dependency
) : BaseViewModel<EntryService.Dependency>(application) {

    override val service: BaseService<EntryService.Dependency> = EntryService(dependency)
}