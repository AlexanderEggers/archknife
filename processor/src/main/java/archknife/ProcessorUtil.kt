package archknife

import com.squareup.javapoet.ClassName

object ProcessorUtil {

    val classViewModelFactory: ClassName = ClassName.get("archknife.viewmodel", "ViewModelFactory")
    val classViewModelKey: ClassName = ClassName.get("archknife.viewmodel", "ViewModelKey")
    val classEmptyFragmentModule: ClassName = ClassName.get("archknife.fragment", "EmptyFragmentModule")
    val classContextModule: ClassName = ClassName.get("archknife.context", "ContextModule")

    val classViewModelProviderFactory: ClassName = ClassName.get("android.arch.lifecycle.ViewModelProvider", "Factory")
    val classViewModel: ClassName = ClassName.get("android.arch.lifecycle", "ViewModel")
    val classApplication: ClassName = ClassName.get("android.app", "Application")

    val classSingleton: ClassName = ClassName.get("javax.inject", "Singleton")
    val classComponent: ClassName = ClassName.get("dagger", "Component")
    val classComponentBuilder: ClassName = ClassName.get("dagger.Component", "Builder")
    val classBindsInstance: ClassName = ClassName.get("dagger", "BindsInstance")
    val classAndroidInjectionModule: ClassName = ClassName.get("dagger.android", "AndroidInjectionModule")
    val classModule: ClassName = ClassName.get("dagger", "Module")
    val classContributesAndroidInjector: ClassName = ClassName.get("dagger.android", "ContributesAndroidInjector")
    val classBinds: ClassName = ClassName.get("dagger", "Binds")
    val classIntoMap: ClassName = ClassName.get("dagger.multibindings", "IntoMap")

    fun generatedViewModelBuilderModuleClassName(): String = "Generated_ViewModelBuilderModule"
    fun generatedFragmentModuleClassName(activityName: String): String = "Generated_" + activityName + "Module"
    fun generatedActivityBuilderModuleClassName(): String = "Generated_ActivityBuilderModule"
    fun generatedComponentClassName(): String = "ArchknifeComponent"

    fun getLibraryPackage(basePackage: String): String = "$basePackage.di"
}