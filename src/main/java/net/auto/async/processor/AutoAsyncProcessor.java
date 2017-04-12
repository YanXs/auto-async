package net.auto.async.processor;

import com.google.auto.service.AutoService;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.squareup.javapoet.*;
import net.auto.async.Asyncable;
import net.auto.async.AutoAsync;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Xs
 */
@AutoService(Processor.class)
public class AutoAsyncProcessor extends AbstractProcessor {

    private static final String INTERFACE_PREFIX = "Unified_";
    private static final String METHOD_PREFIX = "async_";

    private Elements elementUtils;
    private Filer filer;
    private Messager messager;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        elementUtils = processingEnv.getElementUtils();
        filer = processingEnv.getFiler();
        messager = processingEnv.getMessager();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return ImmutableSet.of(AutoAsync.class.getName());
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (Element annotatedElement : roundEnv.getElementsAnnotatedWith(AutoAsync.class)) {
            if (annotatedElement.getKind() != ElementKind.INTERFACE) {
                error(annotatedElement, "Only classes can be annotated with @%s", AutoAsync.class.getSimpleName());
                return true;
            }
            try {
                TypeElement typeElement = (TypeElement) annotatedElement;
                String simpleName = typeElement.getSimpleName().toString();
                String qualifiedName = typeElement.getQualifiedName().toString();
                String packageName = qualifiedName.substring(0, qualifiedName.lastIndexOf("."));

                List<MethodSpec> methodSpecs = new ArrayList<MethodSpec>();
                for (Element enclosed : typeElement.getEnclosedElements()) {
                    if (enclosed.getKind() == ElementKind.METHOD) {
                        ExecutableElement executableElement = (ExecutableElement) enclosed;
                        Asyncable asyncable = executableElement.getAnnotation(Asyncable.class);
                        if (asyncable != null) {
                            methodSpecs.add(processExecutableElement(executableElement));
                        }
                    }
                }

                TypeSpec asyncInterface = TypeSpec.interfaceBuilder(INTERFACE_PREFIX + simpleName)
                        .addModifiers(Modifier.PUBLIC)
                        .addSuperinterface(TypeName.get(typeElement.asType()))
                        .addMethods(methodSpecs)
                        .build();

                JavaFile javaFile = JavaFile.builder(packageName, asyncInterface).build();
                javaFile.writeTo(filer);
            } catch (Exception e) {
                error(annotatedElement, "Create javaFile failed cause @%s ", e.getMessage());
                return true;
            }
        }
        return false;
    }

    private MethodSpec processExecutableElement(ExecutableElement executableElement) {
        List<ParameterSpec> parameterSpecs = new ArrayList<ParameterSpec>();
        for (Element element : executableElement.getParameters()) {
            ParameterSpec parameterSpec = processVariableElement((VariableElement) element);
            parameterSpecs.add(parameterSpec);
        }
        ClassName listenableFuture = ClassName.bestGuess(ListenableFuture.class.getName());
        TypeName originReturnTypeName = TypeName.get(executableElement.getReturnType());
        if (originReturnTypeName.isPrimitive()) {
            originReturnTypeName = originReturnTypeName.box();
        }
        TypeName newReturnTypeName = ParameterizedTypeName.get(listenableFuture, originReturnTypeName);
        return MethodSpec
                .methodBuilder(METHOD_PREFIX + executableElement.getSimpleName().toString())
                .addModifiers(Modifier.PUBLIC, Modifier.ABSTRACT)
                .returns(newReturnTypeName)
                .addParameters(parameterSpecs)
                .build();
    }


    private ParameterSpec processVariableElement(VariableElement element) {
        return ParameterSpec.builder(TypeName.get(element.asType()), element.getSimpleName().toString()).build();
    }

    private TypeMirror getTypeMirror(String typeName) {
        return elementUtils.getTypeElement(typeName).asType();
    }

    private void error(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
