package jp.arsaga.presentation.view.{Small}.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.arsaga.domain.useCase.{Small}.{Large}UseCase
import jp.arsaga.domain.useCase.core.ActivityCallback
import jp.arsaga.presentation.viewModel.{Small}.{Large}ViewModel
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel
import jp.co.arsaga.extensions.view.bind
import jp.co.arsaga.extensions.view.getNavController

class {Large}Fragment : Fragment() {

    class {Large}Navigator : {Large}UseCase.Navigator<ActivityCallback> {
        override val successLogin: ActivityCallback = {
            TODO()
        }
    }

    private val viewModel by viewModels<{Large}ViewModel> {
        BaseViewModel.Factory {
            {Large}ViewModel(
                requireActivity().application,
                {Large}Navigator()
            )
        }
    }
}