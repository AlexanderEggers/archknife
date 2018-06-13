package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classAndroidInjectionModule
import archknife.ProcessorUtil.classApplication
import archknife.ProcessorUtil.classBindsInstance
import archknife.ProcessorUtil.classComponent
import archknife.ProcessorUtil.classComponentBuilder
import archknife.ProcessorUtil.classContextModule
import archknife.ProcessorUtil.classSingleton
import archknife.ProcessorUtil.generatedActivityBuilderModuleClassName
import archknife.ProcessorUtil.generatedComponentClassName
import archknife.ProcessorUtil.generatedViewModelBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

class ComponentProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        TypeSpec.interfaceBuilder(generatedComponentClassName()).apply {
            addModifiers(Modifier.PUBLIC)
            addAnnotation(classSingleton)
            addAnnotation(AnnotationSpec.builder(classComponent).apply {
                addMember("modules", createComponentAnnotationFormat(mainProcessor, roundEnv))
            }.build())
            addType(TypeSpec.interfaceBuilder("Builder").apply {
                addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                addAnnotation(classComponentBuilder)
                addMethod(MethodSpec.methodBuilder("application").apply {
                    addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    addAnnotation(classBindsInstance)
                    addParameter(classApplication, "application")
                    returns(ClassName.get(
                            mainProcessor.appComponentPackage + ".${generatedComponentClassName()}",
                            "Builder"
                    ))
                }.build())
                addMethod(MethodSpec.methodBuilder("build").apply {
                    addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    returns(ClassName.get(mainProcessor.appComponentPackage, generatedComponentClassName()))
                }.build())
            }.build())
            addMethod(MethodSpec.methodBuilder("inject").apply {
                addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                addParameter(mainProcessor.applicationClassName, "application")
            }.build())
        }.build().also { file ->
            JavaFile.builder(mainProcessor.appComponentPackage, file)
                    .build()
                    .writeTo(mainProcessor.filer)
        }
    }

    private fun createComponentAnnotationFormat(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): String {
        val classActivityBuilder = ClassName.get(mainProcessor.libraryPackage, generatedActivityBuilderModuleClassName())
        val classViewModelBuilder = ClassName.get(mainProcessor.libraryPackage, generatedViewModelBuilderModuleClassName())

        return ArrayList<String>().apply {
            addAll(roundEnv.getElementsAnnotatedWith(ProvideModule::class.java).map {
                if (!it.kind.isClass) {
                    mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ProvideModule can be only be applied to a class. " +
                                    "Error for ${it.simpleName}")
                }

                val moduleName = it.simpleName.toString()
                val packageName = mainProcessor.elements.getPackageOf(it).qualifiedName.toString()
                ", ${ClassName.get(packageName, moduleName)}.class"
            })
        }.joinToString(prefix = "{$classAndroidInjectionModule.class, $classActivityBuilder.class, " +
                "$classViewModelBuilder.class, $classContextModule.class", postfix = "}", separator = "")
    }
}
