package archknife.extension

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

abstract class ArchknifeApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var appInjector: AppInjector

    override fun onCreate() {
        super.onCreate()

        val daggerClass = Class.forName(javaClass.`package`.name.toString() + ".DaggerAppComponent")
        val daggerBuilderClass = Class.forName(javaClass.`package`.name.toString() + ".DaggerAppComponent\$Builder")
        val appComponentClass = Class.forName(javaClass.`package`.name.toString() + ".AppComponent")

        var builder: Any = daggerClass.getMethod("builder").invoke(null)
        builder = daggerBuilderClass.getDeclaredMethod("application", Application::class.java).invoke(builder, this@ArchknifeApplication)
        val appComponent = daggerBuilderClass.getDeclaredMethod("build").invoke(builder)
        appComponentClass.getDeclaredMethod("inject", javaClass).invoke(appComponent, this)

        appInjector.init(this)
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }
}