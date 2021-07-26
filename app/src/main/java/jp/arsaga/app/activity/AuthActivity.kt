package jp.arsaga.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import jp.arsaga.app.R
import jp.arsaga.app.databinding.ActivityAuthBinding
import jp.arsaga.presentation.viewModel.auth.EntryViewModel
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel
import jp.co.arsaga.extensions.view.bind

class AuthActivity : AppCompatActivity() {

    private val viewModel by viewModels<EntryViewModel> {
        BaseViewModel.Factory { EntryViewModel(application) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth)
            .bind(this) {
                it.viewModel = viewModel
            }
    }
}