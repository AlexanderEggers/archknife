package archknife.annotation

import kotlin.reflect.KClass

/**
 * This annotation can be used to include the application to the underlying Dagger structure. This
 * application is used for the AppComponent class and to determine the folder for the generated
 * classes. Make sure to only declare one ProvideApplication in your app.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ProvideApplication(
        /**
         * Returns an array of module classes. These module classes are outside of the classpath
         * for the current project. Due to the processor restrictions, archknife won't be able to
         * pick up any 3rd party module classes. This can be avoided by adding these 3rd party
         * module to this array. Ensure that the modules are annotated with @Module, otherwise
         * the dagger-v2 processor will fail.
         */
        val externalModuleClasses: Array<KClass<*>> = [])