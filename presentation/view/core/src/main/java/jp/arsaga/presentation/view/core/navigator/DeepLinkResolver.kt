package jp.arsaga.presentation.view.core.navigator

import android.net.Uri
import androidx.fragment.app.Fragment
import timber.log.Timber

interface DeepLinkFacade {

}


object DeepLinkResolver {
    interface Activity {
        val deepLinkFacade: DeepLinkFacade
    }

    private var deepLinkUri: Uri? = null

    fun setDeepLink(uri: Uri?) {
        deepLinkUri = uri
        Timber.d("setDeepLink: ${uri.toString()}")
    }

    fun handleDeepLink(resolver: (Uri) -> Boolean?) {
        deepLinkUri?.run {
            val isTerminus = resolver(this)
            if (isTerminus == true) setDeepLink(null)
        }
    }

    inline fun <T> checkAuthority(isAuth: Boolean?, successResult: T): T? {
        isAuth ?: return null
        return if (!isAuth) {
            setDeepLink(null)
            null
        } else successResult
    }
}

fun Fragment.deepLinkFacade(): DeepLinkFacade? = requireActivity().let {
    if (it is DeepLinkResolver.Activity) it.deepLinkFacade
    else null
}