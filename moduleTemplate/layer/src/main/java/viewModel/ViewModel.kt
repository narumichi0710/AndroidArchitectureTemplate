package jp.arsaga.presentation.viewModel.{Small}

import android.app.Application
import androidx.lifecycle.viewModelScope
import jp.arsaga.dataStore.repository.{Small}.{Large}CommandImpl
import jp.arsaga.dataStore.repository.{Small}.{Large}QueryImpl
import jp.arsaga.domain.useCase.{Small}.{Large}UseCase
import jp.arsaga.domain.useCase.{Small}.{Large}UseCase.*
import jp.arsaga.domain.useCase.core.ActivityCallback
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class {Large}ViewModel(
    application: Application,
    navigator: Navigator<ActivityCallback>
) : BaseViewModel<Dependency<ActivityCallback>>(application) {
    override val useCase = {Large}UseCase(
        Dependency(
            navigator,
            {Large}CommandImpl(application, viewModelScope),
            {Large}QueryImpl(application, viewModelScope)
        )
    )
}