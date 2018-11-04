package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedServiceBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

class ProvideServiceProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        TypeSpec.classBuilder(generatedServiceBuilderModuleClassName()).apply {
            addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            addAnnotation(classModule)

            generateServiceProviderMethods(mainProcessor, roundEnv).forEach {
                addMethod(it)
            }
        }.build().also { file ->
            JavaFile.builder(mainProcessor.libraryPackage, file)
                    .build()
                    .writeTo(mainProcessor.filer)
        }
    }

    private fun generateServiceProviderMethods(mainProcessor: MainProcessor,
                                                roundEnv: RoundEnvironment): ArrayList<MethodSpec> {

        return ArrayList<MethodSpec>().apply {
            addAll(roundEnv.getElementsAnnotatedWith(ProvideService::class.java).map {
                if (!it.kind.isClass) {
                    mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ProvideService can be only be applied to a class. " +
                                    "Error for ${it.simpleName}")
                }

                val serviceName = it.simpleName.toString()
                val packageName = mainProcessor.elements.getPackageOf(it).qualifiedName.toString()

                MethodSpec.methodBuilder("contribute$serviceName").apply {
                    addModifiers(Modifier.ABSTRACT)
                    addAnnotation(AnnotationSpec.builder(classContributesAndroidInjector).build())
                    returns(ClassName.get(packageName, serviceName))
                }.build()
            })
        }
    }
}
