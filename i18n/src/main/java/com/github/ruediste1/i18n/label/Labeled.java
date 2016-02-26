package com.github.ruediste1.i18n.label;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a type (class, interface or enum), property or method is
 * labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE,
        ElementType.PARAMETER })
public @interface Labeled {
    /**
     * Variants of the label, in addition to the variants specified by the
     * {@link Label} annotations.
     */
    String[] variants() default { "" };
}
