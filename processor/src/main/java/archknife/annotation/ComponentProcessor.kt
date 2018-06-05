package archknife.annotation

import archknife.MainProcessor
import archknife.util.AnnotationProcessor
import archknife.util.ProcessorUtil.classAndroidInjectionModule
import archknife.util.ProcessorUtil.classApplication
import archknife.util.ProcessorUtil.classBindsInstance
import archknife.util.ProcessorUtil.classComponent
import archknife.util.ProcessorUtil.classComponentBuilder
import archknife.util.ProcessorUtil.classContextModule
import archknife.util.ProcessorUtil.classSingleton
import archknife.util.ProcessorUtil.generatedActivityBuilderModuleClassName
import archknife.util.ProcessorUtil.generatedComponentClassName
import archknife.util.ProcessorUtil.generatedViewModelBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ComponentProcessor : AnnotationProcessor {

    private lateinit var classActivityBuilder: ClassName
    private lateinit var classViewModelBuilder: ClassName

    private val modulesWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
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
        roundEnv.getElementsAnnotatedWith(ProvideModule::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class.")
                return
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
