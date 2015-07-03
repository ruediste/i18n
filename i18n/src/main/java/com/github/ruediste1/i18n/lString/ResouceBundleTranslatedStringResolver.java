package com.github.ruediste1.i18n.lString;

import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ResouceBundleTranslatedStringResolver implements
        TranslatedStringResolver {

    private String baseName;
    private ClassLoader loader;
    private boolean useDefaultLocale;

    /**
     * Initialize the resolver using the current context class loader
     * 
     * @param baseName
     *            see
     *            {@link ResourceBundle#getBundle(String, Locale, ClassLoader)}
     */
    public void initialize(String baseName) {
        initialize(baseName, Thread.currentThread().getContextClassLoader(),
                false);
    }

    /**
     * Initialize the resolver
     * 
     * @param baseName
     *            see
     *            {@link ResourceBundle#getBundle(String, Locale, ClassLoader)}
     * @param loader
     *            class loader to load the bundles with
     * @param useDefaultLocale
     *            If set to true, the default locale resource bundle is used.
     *            Otherwise, the fallback translations come into play. Should
     *            usually be set to false
     */
    public void initialize(String baseName, ClassLoader loader,
            boolean useDefaultLocale) {
        this.baseName = baseName;
        this.loader = loader;
        this.useDefaultLocale = useDefaultLocale;

    }

    @Override
    public String resolve(TranslatedString str, Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
                loader, new ResourceBundle.Control() {
                    @Override
                    public List<Locale> getCandidateLocales(String baseName,
                            Locale locale) {
                        List<Locale> result = super.getCandidateLocales(
                                baseName, locale);
                        if (!useDefaultLocale)
                            result.remove(Locale.ROOT);
                        return result;
                    }
                });

        // return string from bundle if available
        if (bundle.containsKey(str.getResourceKey())) {
            return bundle.getString(str.getResourceKey());
        }

        // check if fallback is available
        if (str.getFallback() == null) {
            throw new MissingResourceException("resource for key <"
                    + str.getResourceKey()
                    + "> not found, and no fallback present", getClass()
                    .getName(), str.getResourceKey());
        }

        // return fallback string
        return str.getFallback();

    }
}
