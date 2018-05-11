package archknife.annotation

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
annotation class ProvideApplication