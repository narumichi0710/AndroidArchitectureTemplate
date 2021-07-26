package jp.arsaga.app.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import jp.arsaga.app.R
import jp.arsaga.app.databinding.ActivityMainBinding
import jp.co.arsaga.extensions.view.bind

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)
            .bind(this) {
            }
    }
}