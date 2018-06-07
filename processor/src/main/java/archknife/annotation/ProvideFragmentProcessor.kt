package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedFragmentModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.AnnotationValue
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.DeclaredType
import javax.tools.Diagnostic

class ProvideFragmentProcessor {

    private var activityFragmentMap: HashMap<String, ArrayList<Element>> = HashMap()
    private val fragmentWithPackage: HashMap<String, String> = HashMap()

    fun process(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        prepareFragmentMap(mainProcessor, roundEnv)

        activityFragmentMap.forEach {
            val activityName = it.key
            val elements: ArrayList<Element> = it.value
            val fragmentModelName = generatedFragmentModuleClassName(activityName)

            val fileBuilder = TypeSpec.classBuilder(fragmentModelName)
                    .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                    .addAnnotation(classModule)

            elements.forEach {
                val fragmentPackage = fragmentWithPackage[it.simpleName.toString()]
                val classFragment: TypeName = ClassName.get(fragmentPackage, it.simpleName.toString())

                fileBuilder.addMethod(MethodSpec.methodBuilder("contribute${it.simpleName}")
                        .addModifiers(Modifier.ABSTRACT)
                        .addAnnotation(classContributesAndroidInjector)
                        .returns(classFragment)
                        .build())
            }

            val file = fileBuilder.build()
            JavaFile.builder(mainProcessor.libraryPackage + ".fragment", file)
                    .build()
                    .writeTo(mainProcessor.filer)

            mainProcessor.fragmentModuleMap[activityName] = fragmentModelName
        }
    }

    @Suppress("LABEL_NAME_CLASH", "UNCHECKED_CAST")
    private fun prepareFragmentMap(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (fragmentElement in roundEnv.getElementsAnnotatedWith(ProvideFragment::class.java)) {
            if (!fragmentElement.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for ${fragmentElement.simpleName}")
                continue
            }

            val typeElement = fragmentElement as TypeElement
            fragmentWithPackage[typeElement.simpleName.toString()] =
                    mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()

            getActivityNames(fragmentElement).forEach { name ->
                val elements = activityFragmentMap[name] ?: ArrayList()
                elements.add(fragmentElement)
                activityFragmentMap[name] = elements
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun getActivityNames(fragmentElement: Element): List<String> {
        fragmentElement.annotationMirrors.forEach { classAnnotations ->
            classAnnotations.elementValues.forEach { classAnnotationFields ->
                val key = classAnnotationFields.key.simpleName.toString()
                val value = classAnnotationFields.value.value

                if (key == "activityClasses") {
                    val activityNameList = ArrayList<String>()

                    val typeMirrors = value as List<AnnotationValue>
                    typeMirrors.forEach { annotationValue ->
                        val declaredType = annotationValue.value as DeclaredType
                        val objectActivity = declaredType.asElement()
                        val activityName = objectActivity.simpleName.toString()
                        activityNameList.add(activityName)
                    }

                    return activityNameList
                }
            }
        }
        return emptyList()
    }
}
