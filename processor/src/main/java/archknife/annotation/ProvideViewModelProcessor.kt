package archknife.annotation

import archknife.MainProcessor
import archknife.util.AnnotationProcessor
import archknife.util.ProcessorUtil.classBinds
import archknife.util.ProcessorUtil.classIntoMap
import archknife.util.ProcessorUtil.classModule
import archknife.util.ProcessorUtil.classViewModel
import archknife.util.ProcessorUtil.classViewModelFactory
import archknife.util.ProcessorUtil.classViewModelKey
import archknife.util.ProcessorUtil.classViewModelProviderFactory
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideViewModelProcessor : AnnotationProcessor {

    private val viewModelWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder("Generated_ViewModelBuilderModule")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(classModule)

        prepareViewModelPackageMap(mainProcessor, roundEnv)
        generateViewModelProviderMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder(MainProcessor.libraryPackage, file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareViewModelPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideViewModel::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class.")
                return
            }

            val typeElement = it as TypeElement
            viewModelWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
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
