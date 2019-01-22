package archknife.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory

import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

/**
 * This class is used to create new ViewModels or retain already existing ones. The underlying map
 * for this object will be filled by Dagger when using the @ProvideViewModel annotation.
 *
 * @since 1.0.0
 */
@Suppress("UNCHECKED_CAST")
@Singleton
class ViewModelFactory @Inject
constructor(private val creators: Map<Class<out ViewModel>, @JvmSuppressWildcards Provider<ViewModel>>) : Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        var creator: Provider<out ViewModel>? = creators[modelClass]
        if (creator == null) {
            for ((key, value) in creators) {
                if (modelClass.isAssignableFrom(key)) {
                    creator = value
                    break
                }
            }
        }

        if (creator == null) {
            throw IllegalArgumentException("unknown class $modelClass")
        }

        try {
            return creator.get() as T
        } catch (e: Exception) {
            throw RuntimeException(e)
        }
    }
}