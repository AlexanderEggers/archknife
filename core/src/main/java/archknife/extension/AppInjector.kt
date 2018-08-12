package archknife.extension

import android.app.Activity
import android.app.Application
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentManager.FragmentLifecycleCallbacks
import archknife.context.ContextProvider
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection
import dagger.android.support.HasSupportFragmentInjector
import javax.inject.Inject
import javax.inject.Singleton

/**
 * This class can be used to establish the Activity and Fragment lifecycle regarding the usage of
 * the AndroidInjection class.
 *
 * @since 1.0.0
 * @see AndroidInjection
 */
@Singleton
open class AppInjector
@Inject constructor(private val contextProvider: ContextProvider) : FragmentLifecycleCallbacks(), ActivityLifecycleCallbacks {

    /**
     * Attaches custom Activity lifecycle callbacks to the given Application object. These
     * callbacks will be used to determine when a new Activity is created which leads to resolving
     * it's Dagger dependencies.
     *
     * @since 1.0.0
     * @see ActivityLifecycleCallbacks
     */
    open fun init(application: Application) {
        application.registerActivityLifecycleCallbacks(this)
    }

    @CallSuper
    override fun onActivityCreated(activity: Activity, bundle: Bundle?) {
        handleActivity(activity)
        setContext(activity)
    }

    override fun onActivityStarted(activity: Activity) {
        //do nothing by default
    }

    @CallSuper
    override fun onActivityResumed(activity: Activity) {
        setContext(activity)
    }

    override fun onActivityPaused(activity: Activity) {
        //do nothing by default
    }

    override fun onActivityStopped(activity: Activity) {
        //do nothing by default
    }

    override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle?) {
        //do nothing by default
    }

    override fun onActivityDestroyed(activity: Activity) {
        //do nothing by default
    }

    override fun onFragmentCreated(fm: FragmentManager?, f: Fragment?, savedInstanceState: Bundle?) {
        //Determines if the given Fragment is part of the Dagger structure.
        if (f is Injectable) {
            AndroidSupportInjection.inject(f)
        }
    }

    /**
     * Assigns a context object to the context provider so it can be used for dependencies that
     * need an activity context.
     *
     * @param activity an Activity object
     *
     * @since 1.0.0
     */
    protected open fun setContext(activity: Activity) {
        contextProvider.context = activity
    }

    /**
     * Handles the given Activity object which has been created recently. This step will resolve
     * the Dagger dependencies and attaches custom Fragment lifecycle callbacks to the Activity.
     *
     * @since 1.0.0
     * @see HasSupportFragmentInjector
     * @see FragmentLifecycleCallbacks
     */
    protected open fun handleActivity(activity: Activity) {
        //Determines if the given Activity is part of the Dagger structure.
        if (activity is Injectable || activity is HasSupportFragmentInjector) {
            AndroidInjection.inject(activity)
        }

        if (activity is FragmentActivity) {
            activity.supportFragmentManager.registerFragmentLifecycleCallbacks(this, true)
        }
    }
}