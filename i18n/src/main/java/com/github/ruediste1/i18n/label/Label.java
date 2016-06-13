package com.github.ruediste1.i18n.label;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Indicates that a type (class, interface or enum) is labeled, and specifies a
 * label
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
@Repeatable(Labels.class)
@Documented
public @interface Label {
    String value();

    String variant() default "";
}
