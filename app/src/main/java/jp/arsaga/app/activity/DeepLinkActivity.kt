package jp.arsaga.app.activity

import android.content.Intent
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
import android.content.Intent.FLAG_ACTIVITY_SINGLE_TOP
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.arsaga.app.App
import jp.arsaga.presentation.view.core.navigator.DeepLinkResolver

class DeepLinkActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DeepLinkResolver.setDeepLink(intent?.data)
        App.persistentContainer.add(DeepLinkResolver)
        executeDeepLink()
    }

    private fun executeDeepLink() {
        if (isAuth()) {
            Intent(this, MainActivity::class.java)
                .setFlags(FLAG_ACTIVITY_CLEAR_TOP)
        } else {
            Intent(this, AuthActivity::class.java)
                .setFlags(FLAG_ACTIVITY_SINGLE_TOP)
        }.let {
            startActivity(it, null)
        }
    }

    private fun isAuth(): Boolean = true

    override fun onPause() {
        super.onPause()
        finish()
    }
}
