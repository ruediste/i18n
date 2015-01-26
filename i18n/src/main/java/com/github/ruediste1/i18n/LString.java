package com.github.ruediste1.i18n;

import java.util.Locale;

/**
 * Represents a localized string, which can be resolved for any locale.
 */
public interface LString {
	/**
	 * Resolve this string for the given locale.
	 */
	String resolve(Locale locale);
}
