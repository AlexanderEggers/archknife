package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classEmptyFragmentModule
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedActivityBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideActivityProcessor {

    private val activitiesWithPackage: HashMap<String, String> = HashMap()

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder(generatedActivityBuilderModuleClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(classModule)

        prepareActivityPackageMap(mainProcessor, roundEnv)
        generateActivityProviderMethods(fileBuilder, mainProcessor)

        val file = fileBuilder.build()
        JavaFile.builder(mainProcessor.libraryPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareActivityPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(ProvideActivity::class.java)) {
            if (!it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            activitiesWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun generateActivityProviderMethods(fileBuilder: TypeSpec.Builder, mainProcessor: MainProcessor) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val activityClass = ClassName.get(packageName, activityName)
            val fragmentModuleName = mainProcessor.fragmentModuleMap[activityName]

            val classFragmentModule = if (fragmentModuleName != null) {
                ClassName.get(mainProcessor.libraryPackage + ".fragment", fragmentModuleName)
            } else {
                classEmptyFragmentModule
            }

            fileBuilder.addMethod(MethodSpec.methodBuilder("contribute$activityName")
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(AnnotationSpec.builder(classContributesAndroidInjector)
                            .addMember("modules", "$classFragmentModule.class")
                            .build())
                    .returns(activityClass)
                    .build())
        }
    }
}
