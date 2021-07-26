package jp.arsaga.presentation.view.auth.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import jp.arsaga.presentation.view.auth.databinding.FragmentLoginBinding
import jp.co.arsaga.extensions.view.bind

class LoginFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = FragmentLoginBinding.inflate(inflater, container, false)
        .bind(viewLifecycleOwner) {
            LoginFragmentDirections
        }
}