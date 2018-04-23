package org.demo.archknife

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import org.archknife.annotation.ProvideApplication
import org.archknife.extension.AppInjector
import javax.inject.Inject

@ProvideApplication
class App : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var appInjector: AppInjector

    override fun onCreate() {
        super.onCreate()
        val appComponent: AppComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)

        appInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }
}