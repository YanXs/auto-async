package net.vakilla.auto.async;

import java.lang.annotation.*;

/**
 * @author Xs
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoAsync {

    /**
     * generate abstract facade class or not
     *
     * @return true if generate
     */
    boolean generateFacade() default false;

    /**
     * test return type or not, in case of return type is Future
     *
     * @return true if strict
     */
    boolean strict() default false;
}
