package org.archknife.helper

import com.squareup.javapoet.*
import org.archknife.util.HelperProcessor
import org.archknife.util.ProcessorUtil
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class EmptyFragmentModuleProcessor : HelperProcessor {

    private val classFragment = ClassName.get("android.support.v4.app", "Fragment")

    override fun process(filer: Filer) {
        createEmptyFragment(filer)
        createEmptyFragmentModule(filer)
    }

    private fun createEmptyFragment(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("EmptyFragment")
                .addModifiers(Modifier.PUBLIC)
                .superclass(classFragment)

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated.util", file)
                .build()
                .writeTo(filer)
    }

    private fun createEmptyFragmentModule(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder(ProcessorUtil.EMPTY_FRAGMENT_MODULE)
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(ProcessorUtil.classModule())

        val classFragment: TypeName = ClassName.get("org.archknife.generated.util", "EmptyFragment")

        fileBuilder.addMethod(MethodSpec.methodBuilder("contributeEmptyFragment")
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(ProcessorUtil.classAndroidInjector())
                .returns(classFragment)
                .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated.fragment", file)
                .build()
                .writeTo(filer)
    }
}