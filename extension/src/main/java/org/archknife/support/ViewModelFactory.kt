package org.archknife.support

import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider.Factory

import javax.inject.Inject
import javax.inject.Provider

class ViewModelFactory @Inject
constructor(private val creators: Map<Class<out ViewModel>, Provider<ViewModel>>) : Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return creators[modelClass]!!.get() as T
    }
}
