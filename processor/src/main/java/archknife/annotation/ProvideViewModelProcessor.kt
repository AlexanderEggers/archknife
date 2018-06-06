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
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideViewModelProcessor {

    private val viewModelWithPackage: HashMap<String, String> = HashMap()

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder(generatedViewModelBuilderModuleClassName())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(classModule)

        prepareViewModelPackageMap(mainProcessor, roundEnv)
        generateViewModelProviderMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder(mainProcessor.libraryPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareViewModelPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (it in roundEnv.getElementsAnnotatedWith(ProvideViewModel::class.java)) {
            if (it.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for class: ${it.simpleName}")
                continue
            }

            val typeElement = it as TypeElement
            viewModelWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()
        }
    }

    private fun generateViewModelProviderMethods(fileBuilder: TypeSpec.Builder) {
        viewModelWithPackage.forEach { viewModelName, packageName ->
            val classViewModelImpl = ClassName.get(packageName, viewModelName)

            fileBuilder.addMethod(MethodSpec.methodBuilder("bind$viewModelName")
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(classBinds)
                    .addAnnotation(classIntoMap)
                    .addAnnotation(AnnotationSpec.builder(classViewModelKey)
                            .addMember("value", "$classViewModelImpl.class")
                            .build())
                    .addParameter(classViewModelImpl, "viewModel")
                    .returns(classViewModel)
                    .build())
        }

        fileBuilder.addMethod(MethodSpec.methodBuilder("bindViewModelFactory")
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(classBinds)
                .addParameter(classViewModelFactory, "factory")
                .returns(classViewModelProviderFactory)
                .build())
    }
}
