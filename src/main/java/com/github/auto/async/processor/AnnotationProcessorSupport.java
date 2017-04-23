package com.github.auto.async.processor;

import com.squareup.javapoet.*;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import java.io.IOException;

/**
 * @author Xs
 */
public abstract class AnnotationProcessorSupport extends AbstractProcessor {

    protected Elements elementUtils;
    protected Filer filer;
    private ErrorReporter errorReporter;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        errorReporter = new ErrorReporter(processingEnv.getMessager());
    }

    protected void reportError(Element e, String msg, Object... args) {
        errorReporter.report(e, msg, args);
    }

    protected TypeMirror getTypeMirror(String typeName) {
        return elementUtils.getTypeElement(typeName).asType();
    }

    protected String getSimpleTypeName(TypeElement typeElement) {
        return typeElement.getSimpleName().toString();
    }

    protected String getPackageName(TypeElement typeElement) {
        String qualifiedName = typeElement.getQualifiedName().toString();
        return qualifiedName.substring(0, qualifiedName.lastIndexOf("."));
    }

    protected ParameterSpec processVariableElement(VariableElement element) {
        return ParameterSpec.builder(TypeName.get(element.asType()), element.getSimpleName().toString()).build();
    }

    protected void generateJavaFile(String packageName, TypeSpec typeSpec) throws IOException {
        JavaFile javaFile = JavaFile.builder(packageName, typeSpec).build();
        javaFile.writeTo(filer);
    }

    protected abstract MethodSpec processExecutableElement(ExecutableElement executableElement);
}
