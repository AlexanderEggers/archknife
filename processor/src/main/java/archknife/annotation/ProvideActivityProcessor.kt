package archknife.annotation

import archknife.MainProcessor
import archknife.util.AnnotationProcessor
import archknife.util.ProcessorUtil.classContributesAndroidInjector
import archknife.util.ProcessorUtil.classEmptyFragmentModule
import archknife.util.ProcessorUtil.classModule
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

class ProvideActivityProcessor : AnnotationProcessor {

    private val activitiesWithPackage: HashMap<String, String> = HashMap()
    private val activitiesHasFragments: HashMap<String, Boolean> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder("Generated_ActivityBuilderModule")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(classModule)

        prepareActivityPackageMap(mainProcessor, roundEnv)
        generateActivityProviderMethods(fileBuilder, mainProcessor)

        val file = fileBuilder.build()
        JavaFile.builder(MainProcessor.libraryPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareActivityPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideActivity::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                        return
                    }

                    val typeElement = it as TypeElement
                    typeElement.interfaces.forEach {
                        val dc = it as DeclaredType
                        if (dc.asElement().simpleName.toString() == "HasSupportFragmentInjector") {
                            activitiesHasFragments[typeElement.simpleName.toString()] = true
                        }
                    }

                    activitiesWithPackage[typeElement.simpleName.toString()] =
                            mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
                }
    }

    private fun generateActivityProviderMethods(fileBuilder: TypeSpec.Builder, mainProcessor: MainProcessor) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val activityClass = ClassName.get(packageName, activityName)
            val activityHasFragment: Boolean = activitiesHasFragments[activityName] ?: false
            val annotationBuilder = AnnotationSpec.builder(classContributesAndroidInjector)

            if (activityHasFragment) {
                val fragmentModuleName = mainProcessor.fragmentModuleMap!![activityName]
                val classFragmentModule = if (fragmentModuleName != null) {
                    ClassName.get(MainProcessor.libraryPackage + ".fragment", fragmentModuleName)
                } else {
                    classEmptyFragmentModule
                }

                annotationBuilder.addMember("modules", "$classFragmentModule.class")
            }

            fileBuilder.addMethod(MethodSpec.methodBuilder("contribute$activityName")
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(annotationBuilder.build())
                    .returns(activityClass)
                    .build())
        }
    }
}
