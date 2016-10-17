package com.github.ruediste1.i18n.label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the methods of a type are labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface MethodsLabeled {
    /**
     * Available variants of the member labels.
     */
    String[] variants() default {};
}
