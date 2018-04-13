package org.archknife.annotation

import com.squareup.javapoet.*
import org.archknife.MainProcessor
import org.archknife.util.AnnotationProcessor
import org.archknife.util.ProcessorUtil
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.ElementKind
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

class ProvideFragmentProcessor : AnnotationProcessor {

    private var activityFragmentMap: HashMap<String, ArrayList<Element>> = HashMap()
    private val fragmentWithPackage: HashMap<String, String> = HashMap()

    override fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        prepareFragmentMap(mainProcessor, roundEnv)

        activityFragmentMap.forEach {
            val activityName = it.key
            val elements: ArrayList<Element> = it.value
            val fragmentModelName = activityName + "Module"

            val fileBuilder = TypeSpec.classBuilder(fragmentModelName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(ProcessorUtil.classModule())

            elements.forEach {
                val fragmentPackage = fragmentWithPackage[it.simpleName.toString()]
                val classFragment: TypeName = ClassName.get(fragmentPackage, it.simpleName.toString())

                fileBuilder.addMethod(MethodSpec.methodBuilder("contribute${it.simpleName}")
                        .addModifiers(Modifier.ABSTRACT)
                        .addAnnotation(ProcessorUtil.classAndroidInjector())
                        .returns(classFragment)
                        .build())
            }

            val file = fileBuilder.build()
            JavaFile.builder(MainProcessor.libraryPackage + ".fragment", file)
                    .build()
                    .writeTo(mainProcessor.filer)

            mainProcessor.fragmentModuleMap!![activityName] = fragmentModelName
        }
    }

    private fun prepareFragmentMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideFragment::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                return
            }

            val typeElement = it as TypeElement
            fragmentWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()

            var activityName = ClassName.get(ProcessorUtil.getProvideFragmentType(it)).toString()
            val splitName = activityName.split(".")
            activityName = splitName[splitName.size - 1]

            var elements = activityFragmentMap[activityName]
            if (elements == null) {
                elements = ArrayList()
            }
            elements.add(it)
            activityFragmentMap[activityName] = elements
        }
    }
}
