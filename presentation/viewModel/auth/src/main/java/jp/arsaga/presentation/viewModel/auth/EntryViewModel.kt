package jp.arsaga.presentation.viewModel.auth

import android.app.Application
import androidx.lifecycle.viewModelScope
import jp.arsaga.dataStore.repository.auth.EntryCommandImpl
import jp.arsaga.dataStore.repository.auth.EntryQueryImpl
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.auth.EntryService.*
import jp.arsaga.domain.service.core.ActivityCallback
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class EntryViewModel(
    application: Application,
    navigator: Navigator<ActivityCallback>
) : BaseViewModel<Dependency<ActivityCallback>>(application) {
    override val service = EntryService(
        Dependency(
            navigator,
            EntryCommandImpl(application, viewModelScope),
            EntryQueryImpl(application, viewModelScope)
        )
    )
}