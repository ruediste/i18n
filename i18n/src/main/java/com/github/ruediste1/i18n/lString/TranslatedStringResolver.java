package com.github.ruediste1.i18n.lString;

import java.util.Locale;

/**
 * Resolves a translated string respective to a {@link Locale}
 */
public interface TranslatedStringResolver {

    /**
     * Resolve the given string in the given locale
     */
    String resolve(TranslatedString str, Locale locale);
}
