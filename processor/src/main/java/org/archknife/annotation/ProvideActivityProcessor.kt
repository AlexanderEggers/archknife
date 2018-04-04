package org.archknife.annotation

import com.squareup.javapoet.AnnotationSpec
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.archknife.MainProcessor
import org.archknife.util.AnnotationProcessor
import org.archknife.util.ProcessorUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideActivityProcessor : AnnotationProcessor {

    private val activitiesWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        val fileBuilder = TypeSpec.classBuilder("ActivityBuilderModule")
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .addAnnotation(ProcessorUtil.classDaggerModule())

        prepareActivityPackageMap(mainProcessor, roundEnv)
        generateActivityProviderMethods(fileBuilder)

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated", file)
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
                    activitiesWithPackage[typeElement.simpleName.toString()] =
                            mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
                }
    }

    private fun generateActivityProviderMethods(fileBuilder: TypeSpec.Builder) {
        activitiesWithPackage.forEach { activityName, packageName ->
            val activityClass = ClassName.get(packageName, activityName)
            val classFragmentModule = ProcessorUtil.classFragmentModule(activityName)

            fileBuilder.addMethod(MethodSpec.methodBuilder("contribute$activityName")
                    .addModifiers(Modifier.ABSTRACT)
                    .addAnnotation(AnnotationSpec.builder(ProcessorUtil.classAndroidInjector())
                            .addMember("modules", "$classFragmentModule.class")
                            .build())
                    .returns(activityClass)
                    .build())
        }
    }
}
