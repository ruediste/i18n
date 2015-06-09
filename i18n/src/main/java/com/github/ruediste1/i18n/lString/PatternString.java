package com.github.ruediste1.i18n.lString;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.google.common.base.Objects;

/**
 * A translated, pattern based string.
 *
 * <p>
 * Represents a string in all possible languages. The pattern is represented as
 * {@link TranslatedString}. After resolving the pattern, the contained
 * placeholders are substituted with the corresponding arguments.
 * </p>
 */
public class PatternString implements LString {
    private final LString pattern;
    private final Map<String, Object> arguments;
    private final PatternStringResolver resolver;

    public PatternString(PatternStringResolver resolver, LString pattern,
            Map<String, Object> arguments) {
        this.resolver = resolver;
        this.pattern = pattern;
        this.arguments = new HashMap<>(arguments);
    }

    /**
     * Construct a {@link PatternString} using a key and a parameter list. The
     * parameters are pairs of parameter names and parameter values
     */
    public PatternString(PatternStringResolver resolver, LString pattern,
            Object... parameters) {
        this.resolver = resolver;
        this.pattern = pattern;
        this.arguments = new HashMap<>();

        if ((parameters.length % 2) != 0) {
            throw new IllegalArgumentException(
                    "The number of parameters has to be even (key-value pairs)");
        }
        for (int i = 0; i < parameters.length; i += 2) {
            Object key = parameters[i];
            Object value = parameters[i + 1];
            if (!(key instanceof String)) {
                throw new IllegalArgumentException("index " + i
                        + ": parameter keys have to be strings");
            }
            this.arguments.put((String) key, value);
        }
    }

    public Map<String, Object> getArguments() {
        return Collections.unmodifiableMap(arguments);
    }

    public Object getArgument(String key) {
        return arguments.get(key);
    }

    public boolean hasArgument(String key) {
        return arguments.containsKey(key);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("template", pattern)
                .add("arguments", arguments).toString();
    }

    public LString getPattern() {
        return pattern;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(pattern, arguments, resolver);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }

        if (getClass() != obj.getClass()) {
            return false;
        }
        PatternString other = (PatternString) obj;
        return Objects.equal(pattern, other.pattern)
                && Objects.equal(arguments, other.arguments)
                && Objects.equal(resolver, other.resolver);
    }

    @Override
    public String resolve(Locale locale) {
        return resolver.resolve(this, locale);
    }
}
