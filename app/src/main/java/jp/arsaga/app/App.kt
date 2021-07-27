package jp.arsaga.app

import android.app.Application
import jp.arsaga.app.lifecycleCallback.ProgressLoadingHandler
import jp.arsaga.presentation.view.core.container.NavControllerContainer
import timber.log.Timber

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        saveSingleton()
        if (BuildConfig.IS_DEBUG_LOGGING) initLogging()
        lifecycleCallbacksList.forEach {
            registerActivityLifecycleCallbacks(it)
        }
    }

    private val lifecycleCallbacksList = listOf(
        NavControllerContainer,
        ProgressLoadingHandler
    )

    private fun saveSingleton() {
        listOf<Any>(
        ).forEach { persistentContainer.add(it) }
    }

    private fun initLogging() {
        Timber.plant(Timber.DebugTree())
    }

    companion object {
        val persistentContainer = mutableSetOf<Any>()
    }
}