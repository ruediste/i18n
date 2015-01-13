package com.github.ruediste1.i18n.messageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Context information for formatting a pattern
 */
public class FormattingContext {
	private final Locale locale;
	private final Map<String, Object> arguments;

	public FormattingContext(Locale locale, Map<String, Object> arguments) {
		super();
		this.locale = locale;
		this.arguments = new HashMap<>(arguments);
	}

	public Locale getLocale() {
		return locale;
	}

	public Object getArgument(String key) {
		return arguments.get(key);
	}

	public Map<String, Object> getArguments() {
		return arguments;
	}
}