package org.archknife.annotation

/**
 * This annotation can be used to include the Dagger module to the underlying Dagger structure. Make
 * sure this class is using the dagger.Module annotation. Otherwise the Dagger compiler will fail.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ProvideModule