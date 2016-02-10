package com.github.ruediste1.i18n.lString;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.inject.Inject;

public class ResouceBundleTranslatedStringResolver
        implements TranslatedStringResolver {

    @Inject
    ResourceBundleResolver resolver;

    Map<String, String> additionalResourceKeys = new HashMap<>();

    public void registerAdditionalResourceKeys(
            Iterable<Class<? extends AdditionalResourceKeyProvider>> providerClasses) {
        additionalResourceKeys.putAll(
                AdditionalResourceKeyCollector.collectKeys(providerClasses));
    }

    @Override
    public String resolve(TranslatedString str, Locale locale) {
        ResourceBundle bundle = resolver.getResourceBundle(locale);

        // return string from bundle if available
        if (bundle.containsKey(str.getResourceKey())) {
            return bundle.getString(str.getResourceKey());
        }

        // check additional resources
        {
            String value = additionalResourceKeys.get(str.getResourceKey());
            if (value != null)
                return value;
        }

        // check if fallback is available
        if (str.getFallback() == null) {
            throw new MissingResourceException(
                    "resource for key <" + str.getResourceKey()
                            + "> not found, and no fallback present",
                    getClass().getName(), str.getResourceKey());
        }

        // return fallback string
        return str.getFallback();

    }

}
