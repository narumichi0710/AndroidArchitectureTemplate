package jp.arsaga.presentation.viewModel.auth

import android.app.Application
import androidx.lifecycle.viewModelScope
import jp.arsaga.dataStore.repository.auth.EntryCommandImpl
import jp.arsaga.dataStore.repository.auth.EntryQueryImpl
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.auth.EntryService.*
import jp.arsaga.domain.service.core.NavigationCallback
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class EntryViewModel(
    application: Application,
    navigator: Navigator<NavigationCallback>
) : BaseViewModel<Dependency<NavigationCallback>>(application) {

    private val command = EntryCommandImpl(application, viewModelScope)

    private val query = EntryQueryImpl(application, viewModelScope)

    private val dependency = Dependency(navigator, command, query)

    override val service = EntryService(dependency)
}