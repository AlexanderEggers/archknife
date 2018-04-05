package org.archknife.helper

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.JavaFile
import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeSpec
import org.archknife.annotation.Injectable
import org.archknife.util.HelperProcessor
import org.archknife.util.ProcessorUtil
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class AppInjectorProcessor : HelperProcessor {

    private val classSingleton = ClassName.get("javax.inject", "Singleton")

    private val classApplication = ClassName.get("android.app", "Application")
    private val classActivity = ClassName.get("android.app", "Activity")
    private val classBundle = ClassName.get("android.os", "Bundle")
    private val classActivityLifecycleCallbacks = ClassName.get("android.app.Application", "ActivityLifecycleCallbacks")

    private val classFragment = ClassName.get("android.support.v4.app", "Fragment")
    private val classFragmentActivity = ClassName.get("android.support.v4.app", "FragmentActivity")
    private val classFragmentManager = ClassName.get("android.support.v4.app", "FragmentManager")
    private val classFragmentLifecycleCallbacks = ClassName.get("android.support.v4.app.FragmentManager", "FragmentLifecycleCallbacks")

    private val classAndroidInjector = ClassName.get("dagger.android", "AndroidInjection")
    private val classAndroidSupportInjection = ClassName.get("dagger.android.support", "AndroidSupportInjection")
    private val classHasSupportFragmentInjector = ClassName.get("dagger.android.support", "HasSupportFragmentInjector")

    private val classInjectable = ClassName.get(Injectable::class.java)

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("AppInjector")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(classSingleton)
                .superclass(classFragmentLifecycleCallbacks)
                .addSuperinterface(classActivityLifecycleCallbacks)
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(ProcessorUtil.classInject())
                        .build())
                .addMethod(MethodSpec.methodBuilder("init")
                        .addModifiers(Modifier.PUBLIC)
                        .addParameter(classApplication, "application")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityCreated")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .addParameter(classBundle, "bundle")
                        .addStatement("handleActivity(activity)")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityStarted")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityResumed")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityPaused")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityStopped")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivitySaveInstanceState")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .addParameter(classBundle, "bundle")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onActivityDestroyed")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classActivity, "activity")
                        .build())
                .addMethod(MethodSpec.methodBuilder("onFragmentCreated")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(Override::class.java)
                        .addParameter(classFragmentManager, "fm")
                        .addParameter(classFragment, "f")
                        .addParameter(classBundle, "savedInstanceState")
                        .addCode("if (f instanceof $classInjectable) {\n" +
                                "   $classAndroidSupportInjection.inject(f);\n" +
                                "}\n")
                        .build())
                .addMethod(MethodSpec.methodBuilder("handleActivity")
                        .addModifiers(Modifier.PROTECTED)
                        .addParameter(classActivity, "activity")
                        .addCode("if (activity instanceof $classHasSupportFragmentInjector) {\n" +
                                "   $classAndroidInjector.inject(activity);\n" +
                                "}\n\n" +
                                "if (activity instanceof $classFragmentActivity) {\n" +
                                "   (($classFragmentActivity) activity).getSupportFragmentManager().registerFragmentLifecycleCallbacks(this, true);\n" +
                                "}\n")
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated.util", file)
                .build()
                .writeTo(filer)
    }
}