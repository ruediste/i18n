package com.github.ruediste1.i18n.label;

import java.lang.annotation.*;

/**
 * Indicates that the members of an enum are labeled
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface MembersLabeled {
	/**
	 * Available variants of the member labels
	 */
	String[] variants() default {};
}
