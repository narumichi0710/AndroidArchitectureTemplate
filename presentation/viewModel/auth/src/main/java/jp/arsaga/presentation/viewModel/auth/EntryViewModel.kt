package jp.arsaga.presentation.viewModel.auth

import android.app.Application
import jp.arsaga.dataStore.repository.auth.EntryRepository
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.core.BaseService
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class EntryViewModel(
    application: Application,
    dependency: EntryService.Dependency = EntryService.Dependency(
        EntryRepository(), null
    )
) : BaseViewModel<EntryService.Dependency>(application) {

    override val service = EntryService(dependency)
}