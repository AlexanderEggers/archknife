package org.archknife.annotation

import com.squareup.javapoet.*
import org.archknife.MainProcessor
import org.archknife.util.AnnotationProcessor
import org.archknife.util.ProcessorUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideViewModelProcessor : AnnotationProcessor {

    private var classViewModelProvider: TypeName = ClassName.get("android.arch.lifecycle.ViewModelProvider", "Factory")
    private var classViewModelFactory = ClassName.get("org.archknife.generated", "ViewModelFactory")

    private val viewModelWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder("ViewModelBuilderModule")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(ProcessorUtil.classDaggerModule())

        prepareViewModelPackageMap(mainProcessor, roundEnv)
        generateViewModelProviderMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated", file)
                .build()
                .writeTo(mainProcessor.filer)
    }

    private fun prepareViewModelPackageMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideViewModel::class.java)
                .forEach {
                    if (it.kind != ElementKind.CLASS) {
                        mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                        return
                    }

                    val typeElement = it as TypeElement
                    viewModelWithPackage[typeElement.simpleName.toString()] =
                            mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
                }
    }

    private fun generateViewModelProviderMethods(fileBuilder: TypeSpec.Builder) {
        viewModelWithPackage.forEach { viewModelName, packageName ->
            val classViewModel = ClassName.get(packageName, viewModelName)

            fileBuilder.addMethod(MethodSpec.methodBuilder("bind$viewModelName")
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(ProcessorUtil.classBinds())
                    .addAnnotation(ProcessorUtil.classIntoMap())
                    .addAnnotation(AnnotationSpec.builder(ViewModelKey::class.java)
                            .addMember("viewModelClass", "$classViewModel.class")
                            .build())
                    .addParameter(classViewModel, "viewModel")
                    .returns(ProcessorUtil.classViewModel())
                    .build())
        }

        fileBuilder.addMethod(MethodSpec.methodBuilder("bindViewModelFactory")
                .addModifiers(Modifier.ABSTRACT)
                .addAnnotation(ProcessorUtil.classBinds())
                .addParameter(classViewModelFactory, "factory")
                .returns(classViewModelProvider)
                .build())
    }
}
