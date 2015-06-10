package com.github.ruediste1.i18n.lString;

import java.util.Locale;

public interface TranslatedStringResolver {

	String resolve(TranslatedString str, Locale locale);
}
