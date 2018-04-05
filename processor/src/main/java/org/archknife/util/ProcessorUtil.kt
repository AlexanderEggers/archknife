package org.archknife.util

import com.squareup.javapoet.ClassName
import org.archknife.annotation.ProvideFragment
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class ProcessorUtil {

    companion object {

        const val EMPTY_FRAGMENT_MODULE = "EmptyFragmentModule"

        fun getProvideFragmentType(element: Element): TypeMirror? {
            try {
                element.getAnnotation(ProvideFragment::class.java).activityClass
            } catch (mte: MirroredTypeException) {
                return mte.typeMirror
            }
            return null
        }

        fun classInject(): ClassName {
            return ClassName.get("javax.inject", "Inject")
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

        fun nonNull(): ClassName {
            return ClassName.get("android.support.annotation", "NonNull")
        }

        fun nullable(): ClassName {
            return ClassName.get("android.support.annotation", "Nullable")
        }
    }
}