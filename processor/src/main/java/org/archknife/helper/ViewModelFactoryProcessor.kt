package org.archknife.helper

import com.squareup.javapoet.*
import org.archknife.util.HelperProcessor
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier
import com.squareup.javapoet.TypeVariableName
import org.archknife.util.ProcessorUtil

class ViewModelFactoryProcessor: HelperProcessor {

    private var classViewModelProvider: TypeName = ClassName.get("android.arch.lifecycle.ViewModelProvider", "Factory")
    private var typeViewModel = TypeVariableName.get("T", ProcessorUtil.classViewModel())

    private var classProvider = ClassName.get("javax.inject", "Provider")
    private var classMap = ClassName.get("java.util", "Map")
    private val classClass = ClassName.get("java.lang", "Class")

    private val providerViewModelType = ParameterizedTypeName.get(classProvider, ProcessorUtil.classViewModel())
    private val classViewModelParameter: TypeName = ParameterizedTypeName.get(classClass, WildcardTypeName.subtypeOf(ProcessorUtil.classViewModel()))
    private val classWildViewModelParameter: TypeName = ParameterizedTypeName.get(classClass, typeViewModel)

    private val classMapType: TypeName = ParameterizedTypeName.get(classMap, classViewModelParameter, providerViewModelType)

    override fun process(filer: Filer) {
        val fileBuilder = TypeSpec.classBuilder("ViewModelFactory")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(classViewModelProvider)
                .addField(FieldSpec.builder(classMapType, "creators")
                        .addModifiers(Modifier.PRIVATE, Modifier.FINAL)
                        .build())
                .addMethod(MethodSpec.constructorBuilder()
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(ProcessorUtil.classInject())
                        .addParameter(classMapType, "creators")
                        .addCode("this.creators = creators;\n")
                        .build())
                .addMethod(MethodSpec.methodBuilder("create")
                        .addModifiers(Modifier.PUBLIC)
                        .addAnnotation(ProcessorUtil.nonNull())
                        .addAnnotation(Override::class.java)
                        .addTypeVariable(typeViewModel)
                        .addParameter(ParameterSpec.builder(classWildViewModelParameter, "modelClass")
                                .addAnnotation(ProcessorUtil.nonNull())
                                .build())
                        .addCode("return (T) creators.get(modelClass).get();\n")
                        .returns(typeViewModel)
                        .build())

        val file = fileBuilder.build()
        JavaFile.builder("org.archknife.generated", file)
                .build()
                .writeTo(filer)
    }
}