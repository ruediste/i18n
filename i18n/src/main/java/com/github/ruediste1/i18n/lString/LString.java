package com.github.ruediste1.i18n.lString;

import java.util.Locale;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Represents a localized string, which can be resolved for any locale.
 */
public class LString {
    /**
     * Resolve this string for the given locale.
     */
    public String resolve(Locale locale) {
        return "";
    }

    private static LString EMPTY = new LString();

    public static LString empty() {
        return EMPTY;
    }

    public static LString of(String string) {
        return new LString() {
            @Override
            public String resolve(Locale l) {
                return string;
            }
        };
    }

    public static LString of(Supplier<String> string) {
        return new LString() {

            @Override
            public String resolve(Locale locale) {
                return string.get();
            }
        };
    }

    public static LString of(Function<Locale, String> string) {
        return new LString() {

            @Override
            public String resolve(Locale locale) {
                return string.apply(locale);
            }
        };
    }
}
