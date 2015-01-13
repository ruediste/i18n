package com.github.ruediste1.i18n;

import java.util.Locale;

import javax.inject.Inject;

import com.github.ruediste1.i18n.messageFormat.MessageFormat;

/**
 * {@link PatternStringResolver} implementation using a {@link MessageFormat}.
 */
public class DefaultPatternStringResolver implements PatternStringResolver{

	@Inject
	MessageFormat format;
	
	@Override
	public String resolve(PatternString str, Locale locale) {
		String pattern = str.getPattern().resolve(locale);
		return format.format(pattern, str.getArguments(), locale);
	}

}
