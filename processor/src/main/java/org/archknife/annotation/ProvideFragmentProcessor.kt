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
import javax.lang.model.element.AnnotationValue
import javax.lang.model.type.DeclaredType


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

    @Suppress("LABEL_NAME_CLASH", "UNCHECKED_CAST")
    private fun prepareFragmentMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideFragment::class.java).forEach { fragmentElement ->
            if (fragmentElement.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                return
            }

            val typeElement = fragmentElement as TypeElement
            fragmentWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()

            fragmentElement.annotationMirrors.forEach {
                it.elementValues.forEach {
                    val key = it.key.simpleName.toString()
                    val value = it.value.value

                    if(key == "activityClasses") {
                        val typeMirrors = value as List<AnnotationValue>
                        typeMirrors.forEach {
                            val declaredType = it.value as DeclaredType
                            val objectActivity = declaredType.asElement()
                            val activityName = objectActivity.simpleName.toString()

                            var elements = activityFragmentMap[activityName]
                            if (elements == null) {
                                elements = ArrayList()
                            }

                            elements.add(fragmentElement)
                            activityFragmentMap[activityName] = elements
                        }
                        return@forEach
                    }
                }
            }
        }
    }
}
