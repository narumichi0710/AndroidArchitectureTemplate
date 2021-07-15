package jp.arsaga.app

import android.app.Application

class App : Application() {

    override fun onCreate() {
        super.onCreate()
        saveSingleton()
    }

    private fun saveSingleton() {
        listOf<Any>(
        ).forEach { persistentContainer.add(it) }
    }

    private fun initLogging() {
//        Timber.plant(Timber.DebugTree())
    }

    companion object {
        val persistentContainer = mutableSetOf<Any>()
    }
}