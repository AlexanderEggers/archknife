package org.archknife.annotation

import kotlin.reflect.KClass

@MustBeDocumented
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.CLASS)
annotation class ProvideFragment(val activityClass: KClass<*>)