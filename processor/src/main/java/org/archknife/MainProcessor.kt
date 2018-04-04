package org.archknife

import com.google.auto.service.AutoService
import org.archknife.annotation.*
import org.archknife.helper.ViewModelFactoryProcessor
import java.io.IOException
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements

@AutoService(Processor::class)
class MainProcessor : AbstractProcessor() {

    var filer: Filer? = null
    var messager: Messager? = null
    var elements: Elements? = null
    var fragmentModuleMap: HashMap<String, String>? = null

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
            //Helper processor part - like for the class ContextProvider
            ViewModelFactoryProcessor().process(filer!!)

            //Annotation processor part - like for the annotation @ProvideActivity
            ProvideFragmentProcessor().process(this, roundEnv)
            ProvideActivityProcessor().process(this, roundEnv)
            ProvideViewModelProcessor().process(this, roundEnv)
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return true
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return mutableSetOf(ProvideActivity::class.java.name, ProvideFragment::class.java.name,
                ProvideViewModel::class.java.name)
    }

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latest()
    }
}