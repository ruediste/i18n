package com.github.ruediste1.i18n.lString;

/**
 * Interface to add resource keys programmatically.
 * 
 * <p>
 * Implementations have to be registered with
 * {@link ResouceBundleTranslatedStringResolver#registerAdditionalResourceKeys(Iterable)}
 * (runtime). The maven plugin picks them up automatically if they are within a
 * scanned package.
 * 
 * <p>
 * Implementations are instantiated using the default constructor and the
 * {@link #provideKeys(KeyReceiver)} method is called.
 */
public interface AdditionalResourceKeyProvider {

    void provideKeys(KeyReceiver receiver);

    interface KeyReceiver {
        void add(String key);

        void add(String key, String fallback);

        /**
         * Add a key corresponding to the fully qualified class name of the
         * given class
         */
        void add(Class<?> cls, String fallback);

        /**
         * Add a key corresponding to the fully qualified class name of the
         * given class with a ".message" suffix added.
         */
        void addMessage(Class<?> cls, String fallback);

        /**
         * add the resource keys of the given providers
         */
        void addProvider(
                Class<? extends AdditionalResourceKeyProvider> providerClass);
    }
}
