package com.github.auto.async;

import java.lang.annotation.*;

/**
 * @author Xs
 */
@Documented
@Retention(RetentionPolicy.SOURCE)
@Target(ElementType.TYPE)
public @interface AutoAsync {

    boolean generateFacade() default false;

}
