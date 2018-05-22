package archknife.annotation

import archknife.MainProcessor
import archknife.util.AnnotationProcessor
import archknife.util.ProcessorUtil.classAndroidInjectionModule
import archknife.util.ProcessorUtil.classApplication
import archknife.util.ProcessorUtil.classBindsInstance
import archknife.util.ProcessorUtil.classComponent
import archknife.util.ProcessorUtil.classComponentBuilder
import archknife.util.ProcessorUtil.classSingleton
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class AppComponentProcessor : AnnotationProcessor {

    private var classActivityBuilder = ClassName.get(MainProcessor.libraryPackage, "Generated_ActivityBuilderModule")
    private var classViewModelBuilder = ClassName.get(MainProcessor.libraryPackage, "Generated_ViewModelBuilderModule")

    private val modulesWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        prepareModulesPackageMap(mainProcessor, roundEnv)

        val fileBuilder = TypeSpec.interfaceBuilder("ArchknifeComponent")
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
                                        MainProcessor.appComponentPackage + ".ArchknifeComponent",
                                        "Builder"
                                ))
                                .build())
                        .addMethod(MethodSpec.methodBuilder("build")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(ClassName.get(MainProcessor.appComponentPackage,
                                        "ArchknifeComponent"))
                                .build())
                        .build())
                .addMethod(MethodSpec.methodBuilder("inject")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .addParameter(MainProcessor.applicationClassName, "application")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder(MainProcessor.appComponentPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareModulesPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideModule::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class.")
                return
            }

            val typeElement = it as TypeElement
            modulesWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun createComponentAnnotationFormat(): String {
        var format = "{$classAndroidInjectionModule.class, $classActivityBuilder.class, $classViewModelBuilder.class"

        modulesWithPackage.forEach { moduleName, packageName ->
            format += ", ${ClassName.get(packageName, moduleName)}.class"
        }

        return "$format}"
    }
}
