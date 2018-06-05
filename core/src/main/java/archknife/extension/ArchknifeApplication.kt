package archknife.extension

import android.app.Activity
import android.app.Application
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

/**
 * Abstract class which takes care of the initialising of the Dagger related components. It also
 * starts the AppInjector which is injecting any dependencies inside project classes.
 *
 * @since 1.0.0
 */
abstract class ArchknifeApplication : Application(), HasActivityInjector {

    @Inject
    lateinit var activityInjector: DispatchingAndroidInjector<Activity>

    @Inject
    lateinit var appInjector: AppInjector

    private lateinit var appComponent: Any

    override fun onCreate() {
        super.onCreate()

        val daggerClass = Class.forName(javaClass.`package`.name.toString() + ".DaggerArchknifeComponent")
        val daggerBuilderClass = Class.forName(javaClass.`package`.name.toString() + ".DaggerArchknifeComponent\$Builder")
        val appComponentClass = Class.forName(javaClass.`package`.name.toString() + ".ArchknifeComponent")

        var builder: Any = daggerClass.getMethod("builder").invoke(null)
        builder = daggerBuilderClass.getDeclaredMethod("application", Application::class.java).invoke(builder, this@ArchknifeApplication)
        appComponent = daggerBuilderClass.getDeclaredMethod("build").invoke(builder)
        appComponentClass.getDeclaredMethod("inject", javaClass).invoke(appComponent, this)

        appInjector.init(this)
    }

    @Suppress("UNCHECKED_CAST")
    fun <T> getAppComponent(): T {
        return appComponent as T
    }

    override fun activityInjector(): AndroidInjector<Activity> {
        return activityInjector
    }
}