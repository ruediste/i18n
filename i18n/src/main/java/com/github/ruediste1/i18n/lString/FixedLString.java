package com.github.ruediste1.i18n.lString;

import java.util.Locale;

/**
 * {@link LString} implementation resolving to a fixed string, idependant of the
 * locale.
 */
public class FixedLString implements LString {

    private final String value;

    public FixedLString(String value) {
        this.value = value;
    }

    @Override
    public String resolve(Locale locale) {
        return value;
    }

}
