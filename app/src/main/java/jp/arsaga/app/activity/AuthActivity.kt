package jp.arsaga.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ProgressBar
import androidx.databinding.DataBindingUtil
import jp.arsaga.app.R
import jp.arsaga.app.databinding.ActivityAuthBinding
import jp.co.arsaga.extensions.view.activity.HasProgressBarActivity
import jp.co.arsaga.extensions.view.bind

class AuthActivity : AppCompatActivity(), HasProgressBarActivity {

    private val binding by lazy {
        DataBindingUtil.setContentView<ActivityAuthBinding>(
            this,
            R.layout.activity_auth
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding.bind(this) {
        }
    }

    override fun getProgressBar(): ProgressBar = binding.loading
}