package jp.arsaga.presentation.viewModel.auth

import android.app.Application
import androidx.lifecycle.viewModelScope
import jp.arsaga.dataStore.repository.auth.AuthCommandImpl
import jp.arsaga.dataStore.repository.auth.AuthQueryImpl
import jp.arsaga.domain.useCase.auth.AuthUseCase
import jp.arsaga.domain.useCase.auth.AuthUseCase.*
import jp.arsaga.domain.useCase.core.ActivityCallback
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class AuthViewModel(
    application: Application,
    navigator: Navigator<ActivityCallback>
) : BaseViewModel<Dependency<ActivityCallback>>(application) {
    override val useCase = AuthUseCase(
        Dependency(
            navigator,
            AuthCommandImpl(application, viewModelScope),
            AuthQueryImpl(application, viewModelScope)
        )
    )
}