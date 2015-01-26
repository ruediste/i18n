package com.github.ruediste1.i18n;

import java.util.Locale;

/**
 * A resolver for {@link PatternString}s.
 * 
 */
public interface PatternStringResolver {
	/**
	 * Resolve the {@link PatternString#getPattern()} and format it based on the locale and the {@link PatternString#getArguments()}
	 */
	String resolve(PatternString str, Locale locale);
}
