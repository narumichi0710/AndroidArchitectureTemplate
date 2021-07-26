package jp.arsaga.presentation.view.core.container

import android.app.Activity
import android.app.Application
import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment

object NavControllerContainer : Application.ActivityLifecycleCallbacks {
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) {}
    override fun onActivityStarted(activity: Activity) {}
    override fun onActivityStopped(activity: Activity) {}
    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) {}
    override fun onActivityDestroyed(activity: Activity) {}
    override fun onActivityResumed(activity: Activity) {
        if (activity is FragmentActivity) currentActivity = activity
    }
    override fun onActivityPaused(activity: Activity) {
        currentActivity = null
    }

    private var currentActivity: FragmentActivity? = null

    fun getNavController(): NavController? = currentActivity
        ?.supportFragmentManager?.fragments
        ?.find { it is NavHostFragment }
        ?.let { it as NavHostFragment }
        ?.navController
}