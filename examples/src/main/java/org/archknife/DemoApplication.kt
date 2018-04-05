package org.archknife

import android.app.Application
import javax.inject.Inject
import android.app.Activity
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import org.archknife.support.AppInjector

class DemoApplication: Application(), HasActivityInjector {

    @Inject
    lateinit var dispatchingAndroidInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var appInjector: AppInjector

    private var appComponent: AppComponent? = null

    override fun onCreate() {
        super.onCreate()

        this.appComponent = DaggerAppComponent.builder().application(this).build()
        appComponent!!.inject(this)

        appInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return dispatchingAndroidInjector
    }
}