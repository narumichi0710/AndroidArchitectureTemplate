package jp.arsaga.app.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import jp.arsaga.app.R
import jp.arsaga.app.databinding.ActivityAuthBinding
import jp.co.arsaga.extensions.view.bind

class AuthActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityAuthBinding>(this, R.layout.activity_auth)
            .bind(this) {
            }
    }
}