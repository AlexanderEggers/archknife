package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classAndroidInjectionModule
import archknife.ProcessorUtil.classAndroidSupportInjectionModule
import archknife.ProcessorUtil.classAppContextModule
import archknife.ProcessorUtil.classApplication
import archknife.ProcessorUtil.classBindsInstance
import archknife.ProcessorUtil.classComponent
import archknife.ProcessorUtil.classComponentBuilder
import archknife.ProcessorUtil.classContextProviderModule
import archknife.ProcessorUtil.classKeepClass
import archknife.ProcessorUtil.classSingleton
import archknife.ProcessorUtil.generatedActivityBuilderModuleClassName
import archknife.ProcessorUtil.generatedBroadcastReceiverBuilderModuleClassName
import archknife.ProcessorUtil.generatedComponentClassName
import archknife.ProcessorUtil.generatedServiceBuilderModuleClassName
import archknife.ProcessorUtil.generatedViewModelBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

class ComponentProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment, provideApplicationElement: Element) {
        TypeSpec.interfaceBuilder(generatedComponentClassName()).apply {
            addModifiers(Modifier.PUBLIC)
            addAnnotation(classSingleton)
            addSuperinterface(classKeepClass)
            addAnnotation(AnnotationSpec.builder(classComponent).apply {
                addMember("modules", createComponentAnnotationFormat(mainProcessor, roundEnv,
                        provideApplicationElement))
            }.build())
            addType(TypeSpec.interfaceBuilder("Builder").apply {
                addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                addAnnotation(classComponentBuilder)
                addSuperinterface(classKeepClass)
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

    private fun createComponentAnnotationFormat(mainProcessor: MainProcessor,
                                                roundEnv: RoundEnvironment,
                                                provideApplicationElement: Element): String {

        val classActivityBuilder = ClassName.get(mainProcessor.libraryPackage, generatedActivityBuilderModuleClassName())
        val classViewModelBuilder = ClassName.get(mainProcessor.libraryPackage, generatedViewModelBuilderModuleClassName())
        val classServiceBuilder = ClassName.get(mainProcessor.libraryPackage, generatedServiceBuilderModuleClassName())
        val classBroadcastReceiverBuilder = ClassName.get(mainProcessor.libraryPackage, generatedBroadcastReceiverBuilderModuleClassName())
        val externalModuleClasses = getExternalModuleClasses(mainProcessor, provideApplicationElement)

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
            addAll(externalModuleClasses.map { ", $it" })
        }.joinToString(prefix = "{$classAndroidInjectionModule.class, $classAndroidSupportInjectionModule.class, " +
                "$classActivityBuilder.class, $classViewModelBuilder.class, $classServiceBuilder.class, " +
                "$classBroadcastReceiverBuilder.class, $classContextProviderModule.class, " +
                "$classAppContextModule.class", postfix = "}", separator = "")
    }

    @Suppress("UNCHECKED_CAST")
    private fun getExternalModuleClasses(mainProcessor: MainProcessor, provideApplicationElement: Element): List<String> {
        return ArrayList<String>().apply {
            provideApplicationElement.annotationMirrors.forEach { classAnnotations ->
                classAnnotations.elementValues.forEach { classAnnotationFields ->
                    val key = classAnnotationFields.key.simpleName.toString()
                    val value = classAnnotationFields.value.value

                    if (key == "externalModuleClasses") {
                        val typeMirrors = value as List<AnnotationValue>
                        typeMirrors.forEach { annotationValue ->
                            val declaredType = annotationValue.value as DeclaredType
                            val objectModule = declaredType.asElement()

                            val moduleName = objectModule.simpleName.toString()
                            val packageName = mainProcessor.elements.getPackageOf(objectModule).qualifiedName.toString()

                            add("${ClassName.get(packageName, moduleName)}.class")
                        }
                    }
                }
            }
        }
    }
}
