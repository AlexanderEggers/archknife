package archknife.annotation

import archknife.MainProcessor
import archknife.ProcessorUtil.classContributesAndroidInjector
import archknife.ProcessorUtil.classModule
import archknife.ProcessorUtil.generatedFragmentModuleClassName
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.*
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

            fragmentElement.annotationMirrors.forEach {
                it.elementValues.forEach {
                    val key = it.key.simpleName.toString()
                    val value = it.value.value

                    if (key == "activityClasses") {
                        val typeMirrors = value as List<AnnotationValue>
                        typeMirrors.forEach {
                            val declaredType = it.value as DeclaredType
                            val objectActivity = declaredType.asElement()
                            val activityName = objectActivity.simpleName.toString()

                            val elements = activityFragmentMap[activityName] ?: ArrayList()
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
