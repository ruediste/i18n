package com.github.ruediste1.i18n;

import java.util.Locale;

public interface TStringResolver {

	String resolve(TranslatedString str, Locale locale);
}
