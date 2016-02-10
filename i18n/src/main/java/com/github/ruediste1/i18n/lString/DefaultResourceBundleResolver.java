package com.github.ruediste1.i18n.lString;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;

public class DefaultResourceBundleResolver implements ResourceBundleResolver {

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
    public ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(baseName, locale, loader,
                new ResourceBundle.Control() {
                    @Override
                    public ResourceBundle newBundle(String baseName,
                            Locale locale, String format, ClassLoader loader,
                            boolean reload) throws IllegalAccessException,
                                    InstantiationException, IOException {
                        if (!useDefaultLocale && Locale.ROOT.equals(locale)) {
                            return new ResourceBundle() {

                                @Override
                                protected Object handleGetObject(String key) {
                                    return null;
                                }

                                @Override
                                public Enumeration<String> getKeys() {
                                    return new Enumeration<String>() {

                                        @Override
                                        public boolean hasMoreElements() {
                                            return false;
                                        }

                                        @Override
                                        public String nextElement() {
                                            throw new NoSuchElementException();
                                        }
                                    };
                                }
                            };
                        } else
                            return super.newBundle(baseName, locale, format,
                                    loader, reload);
                    }

                });
    }
}
