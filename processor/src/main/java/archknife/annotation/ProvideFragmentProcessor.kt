package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedFragmentModuleClassName
import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

class ProvideFragmentProcessor {

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): HashMap<String, String> {
        return HashMap<String, String>().apply {
            prepareFragmentMap(mainProcessor, roundEnv).forEach {
                val activityName = it.key
                val elements: ArrayList<Element> = it.value
                val fragmentModelName = generatedFragmentModuleClassName(activityName)

                TypeSpec.classBuilder(fragmentModelName).apply {
                    addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    addAnnotation(classModule)

                    elements.forEach {
                        val fragmentName = it.simpleName.toString()
                        val packageName = mainProcessor.elements.getPackageOf(it).qualifiedName.toString()

                        addMethod(MethodSpec.methodBuilder("contribute${it.simpleName}").apply {
                            addModifiers(Modifier.ABSTRACT)
                            addAnnotation(classContributesAndroidInjector)
                            returns(ClassName.get(packageName, fragmentName))
                        }.build())
                    }
                }.build().also { file ->
                    JavaFile.builder(mainProcessor.libraryPackage + ".fragment", file)
                            .build()
                            .writeTo(mainProcessor.filer)
                }

                put(activityName, fragmentModelName)
            }
        }
    }

    private fun prepareFragmentMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment): HashMap<String, ArrayList<Element>> {
        return HashMap<String, ArrayList<Element>>().apply {
            roundEnv.getElementsAnnotatedWith(ProvideFragment::class.java).map {
                if (!it.kind.isClass) {
                    mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR,
                            "@ProvideFragment can be only be applied to a class. " +
                                    "Error for ${it.simpleName}")
                }

                getActivityNames(it).forEach { name ->
                    val elements = get(name) ?: ArrayList()
                    elements.add(it)
                    put(name, elements)
                }
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getActivityNames(fragmentElement: Element): List<String> {
        return ArrayList<String>().apply {
            fragmentElement.annotationMirrors.forEach { classAnnotations ->
                classAnnotations.elementValues.forEach { classAnnotationFields ->
                    val key = classAnnotationFields.key.simpleName.toString()
                    val value = classAnnotationFields.value.value

                    if (key == "activityClasses") {
                        val typeMirrors = value as List<AnnotationValue>
                        typeMirrors.forEach { annotationValue ->
                            val declaredType = annotationValue.value as DeclaredType
                            val objectActivity = declaredType.asElement()
                            val activityName = objectActivity.simpleName.toString()
                            add(activityName)
                        }
                    }
                }
            }
        }
    }
}
