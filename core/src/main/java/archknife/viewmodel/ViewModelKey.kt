package archknife.viewmodel

import androidx.lifecycle.ViewModel
import dagger.MapKey
import kotlin.reflect.KClass

/**
 * Internal use only. This annotation will be used by the ArchKnife annotation processor to attach
 * the viewModel to the dagger scope.
 *
 * @since 1.0.0
 */
@MustBeDocumented
@MapKey
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.PROPERTY_SETTER)
annotation class ViewModelKey(val value: KClass<out ViewModel>)