package com.github.ruediste1.i18n.messageFormat;

import java.util.Locale;
import java.util.Map;

/**
 * Context information for formatting a pattern
 */
public class FormattingContext {
	public Locale locale;
	public Map<String, Object> arguments;
}