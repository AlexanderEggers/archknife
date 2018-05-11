package org.archknife.annotation

import kotlin.reflect.KClass

/**
 * This annotation can be used to include the Fragment to the underlying Dagger structure. Make sure
 * that the related Activity, that is needed for this annotation, includes the HasFragmentInjector
 * interface. Addition to that, if you want to use the AppInjector, your Fragment should use the
 * interface Injectable. AppInjector and Injectable can be found using the artifact
 * 'archknife-extension'.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ProvideFragment(val activityClasses: Array<KClass<*>>)