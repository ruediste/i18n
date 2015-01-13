package com.github.ruediste1.i18n.label;

import java.lang.annotation.*;

/**
 * Indicates that the properties of a type are labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface PropertiesLabeled {
	/**
	 * Available variants of the member labels.
	 */
	String[] variants() default {};
}
