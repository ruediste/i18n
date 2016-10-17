package com.github.ruediste1.i18n.label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that the parameters of a method are labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD })
public @interface ParametersLabeled {
    /**
     * Available variants of the member labels.
     */
    String[] variants() default {};
}
