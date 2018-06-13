package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classBinds
import archknife.ProcessorUtil.classIntoMap
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.classViewModel
import archknife.ProcessorUtil.classViewModelFactory
import archknife.ProcessorUtil.classViewModelKey
import archknife.ProcessorUtil.classViewModelProviderFactory
import archknife.ProcessorUtil.generatedViewModelBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

class ProvideViewModelProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        TypeSpec.classBuilder(generatedViewModelBuilderModuleClassName()).apply {
            addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            addAnnotation(classModule)

            generateViewModelProviderMethods(mainProcessor, roundEnv).forEach {
                addMethod(it)
            }

            addMethod(MethodSpec.methodBuilder("bindViewModelFactory").apply {
                addModifiers(Modifier.ABSTRACT)
                addAnnotation(classBinds)
                addParameter(classViewModelFactory, "factory")
                returns(classViewModelProviderFactory)
            }.build())
        }.build().also { file ->
            JavaFile.builder(mainProcessor.libraryPackage, file)
                    .build()
                    .writeTo(mainProcessor.filer)
        }
    }

    private fun generateViewModelProviderMethods(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): List<MethodSpec> {
        return ArrayList<MethodSpec>().apply {
            roundEnv.getElementsAnnotatedWith(ProvideViewModel::class.java).map {
                if (!it.kind.isClass) {
                    mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ProvideViewModel can be only be applied to a class. " +
                                    "Error for ${it.simpleName}")
                }

                val viewModelName = it.simpleName.toString()
                val packageName = mainProcessor.elements.getPackageOf(it).qualifiedName.toString()
                val classViewModelImpl = ClassName.get(packageName, viewModelName)

                MethodSpec.methodBuilder("bind$viewModelName").apply {
                    addModifiers(Modifier.ABSTRACT)
                    addAnnotation(classBinds)
                    addAnnotation(classIntoMap)
                    addAnnotation(AnnotationSpec.builder(classViewModelKey).apply {
                        addMember("value", "$classViewModelImpl.class")
                    }.build())
                    addParameter(classViewModelImpl, "viewModel")
                    returns(classViewModel)
                }.build()
            }
        }
    }
}
