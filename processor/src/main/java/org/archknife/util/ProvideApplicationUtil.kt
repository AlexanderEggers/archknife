package org.archknife.util

import com.squareup.javapoet.ClassName
import org.archknife.MainProcessor
import org.archknife.annotation.ProvideApplication
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideApplicationUtil {

    companion object {

        fun prepareMainProcessor(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
            roundEnv.getElementsAnnotatedWith(ProvideApplication::class.java)
                    .forEach {
                        if (it.kind != ElementKind.CLASS) {
                            mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                            return
                        }

                        val typeElement = it as TypeElement

                        MainProcessor.libraryPackage = mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString() + ".di"
                        MainProcessor.appComponentPackage = mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
                        MainProcessor.applicationClassName = ClassName.get(typeElement.simpleName.toString(),
                                mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString())
                    }
        }
    }
}
