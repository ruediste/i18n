package com.github.ruediste1.i18n.lString;

import java.util.Locale;

import com.google.common.base.Objects;

/**
 * A translated string.
 *
 * <p>
 * A {@link TranslatedString} represents a string in all possible languages. It
 * contains a resource key and a fallback. When resolving the string in a given
 * locale, the resources for the locale are searched for the resourceKey. If no
 * resource is found, the fallback is used (if not null).
 * </p>
 */
public class TranslatedString extends LString {
    final private String resourceKey;
    private final String fallback;
    private final TranslatedStringResolver resolver;

    public TranslatedString(TranslatedStringResolver resolver, String resourceKey) {
        this(resolver, resourceKey, null);
    }

    public TranslatedString(TranslatedStringResolver resolver, String resourceKey, String fallback) {
        this.resolver = resolver;
        this.resourceKey = resourceKey;
        this.fallback = fallback;
    }

    public String getResourceKey() {
        return resourceKey;
    }

    public String getFallback() {
        return fallback;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("resourceKey", resourceKey).add("fallback", fallback).toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(fallback, resourceKey, resolver);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        TranslatedString other = (TranslatedString) obj;
        return Objects.equal(resourceKey, other.resourceKey) && Objects.equal(fallback, other.fallback)
                && Objects.equal(resolver, other.resolver);
    }

    @Override
    public String resolve(Locale locale) {
        return resolver.resolve(this, locale);
    }

}
