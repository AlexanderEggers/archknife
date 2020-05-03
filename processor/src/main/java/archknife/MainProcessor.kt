package archknife

import archknife.ProcessorUtil.getLibraryPackage
import archknife.annotation.*
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import net.ltgt.gradle.incap.IncrementalAnnotationProcessor
import net.ltgt.gradle.incap.IncrementalAnnotationProcessorType
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
@IncrementalAnnotationProcessor(IncrementalAnnotationProcessorType.AGGREGATING)
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
            //only in the first round has elements that can be processed
            if (set.isNotEmpty()) {
                //Determines the package and application for the processors
                val applicationElement = prepareMainProcessor(roundEnv)
                if (applicationElement != null) {
                    //Annotation processor part - like for the annotation @ProvideActivity
                    val fragmentModuleMap = ProvideFragmentProcessor().process(this, roundEnv)
                    ProvideActivityProcessor().process(this, roundEnv, fragmentModuleMap)
                    ProvideViewModelProcessor().process(this, roundEnv)
                    ProvideServiceProcessor().process(this, roundEnv)
                    ProvideBroadcastReceiverProcessor().process(this, roundEnv)

                    //AppComponent part - gathers all data from the other processors to build the dagger main file
                    ComponentProcessor().process(this, roundEnv, applicationElement)
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    private fun prepareMainProcessor(roundEnv: RoundEnvironment): Element? {
        for (applicationElement in roundEnv.getElementsAnnotatedWith(ProvideApplication::class.java)) {
            if (!applicationElement.kind.isClass) {
                messager.printMessage(Diagnostic.Kind.ERROR, "Can be only be " +
                        "applied to a class. Error for ${applicationElement.simpleName}")
            }

            val typeElement = applicationElement as TypeElement
            appComponentPackage = elements.getPackageOf(typeElement).qualifiedName.toString()
            libraryPackage = getLibraryPackage(appComponentPackage)
            applicationClassName = ClassName.get(appComponentPackage, typeElement.simpleName.toString())

            return applicationElement
        }

        return null
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ProvideActivity::class.java.name, ProvideFragment::class.java.name,
                ProvideViewModel::class.java.name, ProvideApplication::class.java.name,
                ProvideModule::class.java.name, ProvideService::class.java.name,
                ProvideBroadcastReceiver::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}