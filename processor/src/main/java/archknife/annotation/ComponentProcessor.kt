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
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ComponentProcessor {

    private lateinit var classActivityBuilder: ClassName
    private lateinit var classViewModelBuilder: ClassName

    private val modulesWithPackage: HashMap<String, String> = HashMap()

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        classActivityBuilder = ClassName.get(mainProcessor.libraryPackage, generatedActivityBuilderModuleClassName())
        classViewModelBuilder = ClassName.get(mainProcessor.libraryPackage, generatedViewModelBuilderModuleClassName())

        prepareModulesPackageMap(mainProcessor, roundEnv)

        val componentName = generatedComponentClassName()

        val fileBuilder = TypeSpec.interfaceBuilder(componentName)
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(classSingleton)
                .addAnnotation(AnnotationSpec.builder(classComponent)
                        .addMember("modules", createComponentAnnotationFormat())
                        .build())
                .addType(TypeSpec.interfaceBuilder("Builder")
                        .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                        .addAnnotation(classComponentBuilder)
                        .addMethod(MethodSpec.methodBuilder("application")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .addAnnotation(classBindsInstance)
                                .addParameter(classApplication, "application")
                                .returns(ClassName.get(
                                        mainProcessor.appComponentPackage + ".$componentName",
                                        "Builder"
                                ))
                                .build())
                        .addMethod(MethodSpec.methodBuilder("build")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(ClassName.get(mainProcessor.appComponentPackage,
                                        componentName))
                                .build())
                        .build())
                .addMethod(MethodSpec.methodBuilder("inject")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(mainProcessor.applicationClassName, "application")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder(mainProcessor.appComponentPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareModulesPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(ProvideModule::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for class: ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            modulesWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun createComponentAnnotationFormat(): String {
        var format = "{$classAndroidInjectionModule.class, $classActivityBuilder.class, " +
                "$classViewModelBuilder.class, $classContextModule.class"

        modulesWithPackage.forEach { moduleName, packageName ->
            format += ", ${ClassName.get(packageName, moduleName)}.class"
        }

        return "$format}"
    }
}
