package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classEmptyFragmentModule
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedActivityBuilderModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Modifier
import javax.tools.Diagnostic

class ProvideActivityProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment,
                fragmentModuleMap: HashMap<String, String>) {

        TypeSpec.classBuilder(generatedActivityBuilderModuleClassName()).apply {
            addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
            addAnnotation(classModule)

            generateActivityProviderMethods(mainProcessor, roundEnv, fragmentModuleMap).forEach {
                addMethod(it)
            }
        }.build().also { file ->
            JavaFile.builder(mainProcessor.libraryPackage, file)
                    .build()
                    .writeTo(mainProcessor.filer)
        }
    }

    private fun generateActivityProviderMethods(mainProcessor: MainProcessor,
                                                roundEnv: RoundEnvironment,
                                                fragmentModuleMap: HashMap<String, String>): ArrayList<MethodSpec> {

        return ArrayList<MethodSpec>().apply {
            addAll(roundEnv.getElementsAnnotatedWith(ProvideActivity::class.java).map {
                if (!it.kind.isClass) {
                    mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ProvideActivityCan be only be applied to a class. " +
                                    "Error for ${it.simpleName}")
                }

                val activityName = it.simpleName.toString()
                val packageName = mainProcessor.elements.getPackageOf(it).qualifiedName.toString()
                val fragmentModuleName = fragmentModuleMap[activityName]

                val classFragmentModule = fragmentModuleName?.let {
                    ClassName.get(mainProcessor.libraryPackage + ".fragment", fragmentModuleName)
                } ?: classEmptyFragmentModule

                MethodSpec.methodBuilder("contribute$activityName").apply {
                    addModifiers(Modifier.ABSTRACT)
                    addAnnotation(AnnotationSpec.builder(classContributesAndroidInjector).apply {
                        addMember("modules", "$classFragmentModule.class")
                    }.build())
                    returns(ClassName.get(packageName, activityName))
                }.build()
            })
        }
    }
}
