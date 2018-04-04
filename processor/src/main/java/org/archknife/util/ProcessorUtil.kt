package org.archknife.util

import com.squareup.javapoet.ClassName
import org.archknife.annotation.ProvideFragment
import javax.lang.model.element.Element
import javax.lang.model.type.MirroredTypeException
import javax.lang.model.type.TypeMirror

class ProcessorUtil {

    companion object {

        fun getProvideFragmentType(element: Element): TypeMirror? {
            try {
                element.getAnnotation(ProvideFragment::class.java).activityClass
            } catch (mte: MirroredTypeException) {
                return mte.typeMirror
            }
            return null
        }

        fun classAndroidInjector(): ClassName {
            return ClassName.get("dagger.android", "ContributesAndroidInjector")
        }

        fun classDaggerModule(): ClassName {
            return ClassName.get("dagger", "Module")
        }

        fun classBinds(): ClassName {
            return ClassName.get("dagger", "Binds")
        }

        fun classIntoMap(): ClassName {
            return ClassName.get("dagger.multibindings", "IntoMap")
        }

        fun classViewModel(): ClassName {
            return ClassName.get("android.arch.lifecycle", "ViewModel")
        }

        fun nonNull(): ClassName {
            return ClassName.get("android.support.annotation", "NonNull")
        }

        fun classFragmentModule(activityName: String): ClassName {
            return ClassName.get("org.archknife.generated.fragment", activityName + "Module")
        }
    }
}