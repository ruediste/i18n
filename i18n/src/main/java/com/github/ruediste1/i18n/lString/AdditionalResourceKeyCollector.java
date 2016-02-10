package com.github.ruediste1.i18n.lString;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste1.i18n.lString.AdditionalResourceKeyProvider.KeyReceiver;

/**
 * Utility class to collect the resource keys contained in
 * {@link AdditionalResourceKeyProvider}s
 */
public class AdditionalResourceKeyCollector {

    /**
     * Collect the additional keys present in the specified providers
     */
    public static Map<String, String> collectKeys(
            Class<? extends AdditionalResourceKeyProvider> providerClass) {
        return collectKeys(Arrays.asList(providerClass));
    }

    /**
     * Collect the additional keys present in the specified providers
     */
    public static Map<String, String> collectKeys(
            Iterable<Class<? extends AdditionalResourceKeyProvider>> providerClasses) {
        ArrayDeque<Class<? extends AdditionalResourceKeyProvider>> providerQueue = new ArrayDeque<>();
        providerClasses.forEach(providerQueue::add);

        HashMap<String, String> result = new HashMap<>();
        KeyReceiver receiver = new KeyReceiver() {

            @Override
            public void add(String key, String fallback) {
                result.put(key, fallback);
            }

            @Override
            public void add(String key) {
                // extract the part after the last dot as fallback
                int idx = key.lastIndexOf('.');
                if (idx < 0)
                    idx = 0;
                if (idx < key.length() - 1) {
                    idx++;
                }

                result.put(key, key.substring(idx));
            }

            @Override
            public void add(Class<?> cls, String fallback) {
                add(cls.getName(), fallback);
            }

            @Override
            public void addMessage(Class<?> cls, String fallback) {
                add(cls.getName() + ".message", fallback);
            }

            @Override
            public void addProvider(
                    Class<? extends AdditionalResourceKeyProvider> providerClass) {
                providerQueue.add(providerClass);
            }
        };

        Class<? extends AdditionalResourceKeyProvider> cls;
        while ((cls = providerQueue.poll()) != null) {
            if (Modifier.isAbstract(cls.getModifiers()))
                continue;

            try {
                Constructor<? extends AdditionalResourceKeyProvider> constructor = cls
                        .getDeclaredConstructor();
                constructor.setAccessible(true);
                AdditionalResourceKeyProvider provider = constructor
                        .newInstance();
                provider.provideKeys(receiver);
            } catch (Exception e) {
                throw new RuntimeException(
                        "Error while collecting additional keys from " + cls,
                        e);
            }
        }
        return result;
    }
}
