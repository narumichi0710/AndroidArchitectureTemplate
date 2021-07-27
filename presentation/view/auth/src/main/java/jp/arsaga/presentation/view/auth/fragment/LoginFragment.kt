package jp.arsaga.presentation.view.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.domain.service.core.NavigationCallback
import jp.arsaga.presentation.view.auth.databinding.FragmentLoginBinding
import jp.arsaga.presentation.viewModel.auth.EntryViewModel
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel
import jp.co.arsaga.extensions.view.bind
import jp.co.arsaga.extensions.view.getNavController

class LoginFragment : Fragment() {

    class EntryNavigator : EntryService.Navigator<NavigationCallback> {
        override val successLogin: NavigationCallback = {
            it.getNavController()
                ?.navigate(LoginFragmentDirections.actionLoginFragmentToMainActivity())
        }
    }

    private val viewModel by viewModels<EntryViewModel> {
        BaseViewModel.Factory {
            EntryViewModel(
                requireActivity().application,
                EntryNavigator()
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(inflater, container, false)
        .bind(viewLifecycleOwner) {
            it.viewModel = viewModel
        }
}