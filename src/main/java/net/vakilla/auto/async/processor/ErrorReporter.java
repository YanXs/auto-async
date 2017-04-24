package net.vakilla.auto.async.processor;

import javax.annotation.processing.Messager;
import javax.lang.model.element.Element;
import javax.tools.Diagnostic;

/**
 * @author Xs
 */
public class ErrorReporter {

    private final Messager messager;

    public ErrorReporter(Messager messager) {
        this.messager = messager;
    }

    public void report(Element e, String msg, Object... args) {
        messager.printMessage(Diagnostic.Kind.ERROR, String.format(msg, args), e);
    }
}
