package org.archknife.annotation

import com.squareup.javapoet.*
import org.archknife.MainProcessor
import org.archknife.util.AnnotationProcessor
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideAppComponentProcessor: AnnotationProcessor {

    private var classApplication = ClassName.get("android.app", "Application")
    private var classSingleton = ClassName.get("javax.inject", "Singleton")
    private var classComponent = ClassName.get("dagger", "Component")
    private var classComponentBuilder = ClassName.get("dagger.Component", "Builder")
    private var classBindsInstance = ClassName.get("dagger", "BindsInstance")
    private var classAndroidInjectionModule = ClassName.get("dagger.android", "AndroidInjectionModule")

    private var classActivityBuilder = ClassName.get(MainProcessor.libraryPackage, "ActivityBuilderModule")
    private var classViewModelBuilder = ClassName.get(MainProcessor.libraryPackage, "ViewModelBuilderModule")

    private val modulesWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        prepareModulesPackageMap(mainProcessor, roundEnv)

        val fileBuilder = TypeSpec.interfaceBuilder("AppComponent")
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
                                        MainProcessor.appComponentPackage + ".AppComponent",
                                        "Builder"
                                ))
                                .build())
                        .addMethod(MethodSpec.methodBuilder("build")
                                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                                .returns(ClassName.get(MainProcessor.appComponentPackage,
                                        "AppComponent"))
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
        roundEnv.getElementsAnnotatedWith(ProvideModule::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
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
            format += ", ${ClassName.get(moduleName, packageName)}.class"
        }

        return "$format}"
    }
}
