package org.archknife.annotation

import kotlin.reflect.KClass

@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
annotation class ProvideFragment(val activityClass: KClass<*>)