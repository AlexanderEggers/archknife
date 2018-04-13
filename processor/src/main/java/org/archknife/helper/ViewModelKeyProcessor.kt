package org.archknife.helper

import com.squareup.javapoet.*
import org.archknife.MainProcessor
import org.archknife.util.HelperProcessor
import org.archknife.util.ProcessorUtil
import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

class ViewModelKeyProcessor : HelperProcessor {

    private val classMapKey = ClassName.get("dagger", "MapKey")

    private val classClass = ClassName.get("java.lang", "Class")
    private val classElementTypeMethod = ClassName.get("java.lang.annotation.ElementType", "METHOD")
    private val classRetentionPolicyRuntime = ClassName.get("java.lang.annotation.RetentionPolicy", "RUNTIME")

    private val classViewModelParameter: TypeName = ParameterizedTypeName.get(classClass,
            WildcardTypeName.subtypeOf(ProcessorUtil.classViewModel()))

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.annotationBuilder("ViewModelKey")
                .addModifiers(Modifier.PUBLIC)
                .addAnnotation(MustBeDocumented::class.java)
                .addAnnotation(classMapKey)
                .addAnnotation(AnnotationSpec.builder(Target::class.java)
                        .addMember("value", "$classElementTypeMethod")
                        .build())
                .addAnnotation(AnnotationSpec.builder(Retention::class.java)
                        .addMember("value", "$classRetentionPolicyRuntime")
                        .build())
                .addMethod(MethodSpec.methodBuilder("value")
                        .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                        .returns(classViewModelParameter)
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder(MainProcessor.libraryPackage + ".util", file)
                .build()
                .writeTo(filer)
    }
}