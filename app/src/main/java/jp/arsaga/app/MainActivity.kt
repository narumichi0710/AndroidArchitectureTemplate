package jp.arsaga.app

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import jp.arsaga.app.databinding.ActivityMainBinding
import jp.arsaga.domain.service.auth.EntryService
import jp.arsaga.presentation.viewModel.auth.EntryViewModel
import jp.arsaga.presentation.viewModel.core.extension.BaseViewModel

class MainActivity : AppCompatActivity() {

    private val viewModel by viewModels<EntryViewModel> {
        BaseViewModel.Factory { EntryViewModel(application) }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main).also {
            it.lifecycleOwner = this
            it.viewModel = viewModel
        }
    }
}