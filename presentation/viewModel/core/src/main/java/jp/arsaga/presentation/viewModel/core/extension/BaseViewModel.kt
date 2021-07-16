package jp.arsaga.presentation.viewModel.core.extension

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import jp.arsaga.domain.service.core.BaseService

abstract class BaseViewModel<Dependency>(
    private val application: Application
) : ViewModel() {

    abstract val service: BaseService<Dependency>

    class Factory<Dependency, VM : BaseViewModel<Dependency>?>(
        private val factory: () -> VM
    ) : ViewModelProvider.NewInstanceFactory() {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel?> create(modelClass: Class<T>) = factory() as T
    }
}

