package archknife

import archknife.annotation.*
import archknife.helper.EmptyFragmentModuleProcessor
import archknife.helper.ViewModelKeyProcessor
import com.google.auto.service.AutoService
import com.squareup.javapoet.ClassName
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.tools.Diagnostic

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    var filer: Filer? = null
    var messager: Messager? = null
    var elements: Elements? = null
    var fragmentModuleMap: HashMap<String, String>? = null

    companion object {
        var applicationClassName: ClassName? = null
        var libraryPackage: String? = null
        var appComponentPackage: String? = null
    }

    @Synchronized
    override fun init(processingEnvironment: ProcessingEnvironment) {
        super.init(processingEnvironment)
        filer = processingEnvironment.filer
        messager = processingEnvironment.messager
        elements = processingEnvironment.elementUtils
        fragmentModuleMap = HashMap()
    }

    override fun process(set: MutableSet<out TypeElement>, roundEnv: RoundEnvironment): Boolean {
        try {
            //Determines the package and application for the processors
            prepareMainProcessor(this, roundEnv)

            //Helper processor part - like for the class EmptyFragmentModule
            EmptyFragmentModuleProcessor().process(filer!!)
            ViewModelKeyProcessor().process(filer!!)

            //Annotation processor part - like for the annotation @ProvideActivity
            ProvideFragmentProcessor().process(this, roundEnv)
            ProvideActivityProcessor().process(this, roundEnv)
            ProvideViewModelProcessor().process(this, roundEnv)

            //AppComponent part - gathers all data from the other processors to build the dagger main file
            AppComponentProcessor().process(this, roundEnv)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return true
    }

    private fun prepareMainProcessor(mainProcessor: MainProcessor, roundEnv: RoundEnvironment) {
        roundEnv.getElementsAnnotatedWith(ProvideApplication::class.java).forEach {
            if (it.kind != ElementKind.CLASS) {
                mainProcessor.messager!!.printMessage(Diagnostic.Kind.ERROR, "Can be applied to class.")
                return
            }

            val typeElement = it as TypeElement
            libraryPackage = mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString() + ".di"
            appComponentPackage = mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString()
            applicationClassName = ClassName.get(
                    mainProcessor.elements!!.getPackageOf(typeElement).qualifiedName.toString(),
                    typeElement.simpleName.toString())
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