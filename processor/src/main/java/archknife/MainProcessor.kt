package archknife

import archknife.ProcessorUtil.getLibraryPackage
import archknife.annotation.*
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    lateinit var filer: Filer
        private set
    lateinit var messager: Messager
        private set
    lateinit var elements: Elements
        private set

    lateinit var applicationClassName: ClassName
        private set
    lateinit var libraryPackage: String
        private set
    lateinit var appComponentPackage: String
        private set

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            //Determines the package and application for the processors
            prepareMainProcessor(this, roundEnv)

            //Annotation processor part - like for the annotation @ProvideActivity
            val fragmentModuleMap = ProvideFragmentProcessor().process(this, roundEnv)
            ProvideActivityProcessor().process(this, roundEnv, fragmentModuleMap)
            ProvideViewModelProcessor().process(this, roundEnv)

            //AppComponent part - gathers all data from the other processors to build the dagger main file
            ComponentProcessor().process(this, roundEnv)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    private fun prepareMainProcessor(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        for (applicationElement in roundEnv.getElementsAnnotatedWith(ProvideApplication::class.java)) {
            if (!applicationElement.kind.isClass) {
                mainProcessor.messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for ${applicationElement.simpleName}")
            }

            val typeElement = applicationElement as TypeElement
            appComponentPackage = mainProcessor.elements.getPackageOf(typeElement).qualifiedName.toString()
            libraryPackage = getLibraryPackage(appComponentPackage)
            applicationClassName = ClassName.get(appComponentPackage, typeElement.simpleName.toString())

            break
        }
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ProvideActivity::class.java.name, ProvideFragment::class.java.name,
                ProvideViewModel::class.java.name, ProvideApplication::class.java.name,
                ProvideModule::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}