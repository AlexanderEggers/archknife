package org.archknife.util

import com.squareup.javapoet.ClassName
import org.archknife.annotation.ProvideFragment
import javax.lang.model.element.Element
import javax.lang.model.type.DeclaredType
import javax.lang.model.type.MirroredTypeException

class ProcessorUtil {

    companion object {

        const val EMPTY_FRAGMENT_MODULE = "EmptyFragmentModule"

        fun getProvideFragmentType(element: Element): List<DeclaredType>? {
            try {
                element.getAnnotation(ProvideFragment::class.java).activityClasses
            } catch (mte: MirroredTypeException) {
                return mte.typeMirror as List<DeclaredType>
            }
            return null
        }

        fun classAndroidInjector(): ClassName {
            return ClassName.get("dagger.android", "ContributesAndroidInjector")
        }

        fun classModule(): ClassName {
            return ClassName.get("dagger", "Module")
        }

        fun classViewModel(): ClassName {
            return ClassName.get("android.arch.lifecycle", "ViewModel")
        }
    }
}