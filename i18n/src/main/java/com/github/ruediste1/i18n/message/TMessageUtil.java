package com.github.ruediste1.i18n.message;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;

@Singleton
public class TMessageUtil {

    @Inject
    PatternStringResolver pStringResolver;

    @Inject
    TranslatedStringResolver tStringResovler;

    @SuppressWarnings("unchecked")
    public <T> T getMessageInterfaceInstance(Class<T> clazz) {
        if (!clazz.isAnnotationPresent(TMessages.class)) {
            throw new RuntimeException(
                    "Message interfaces need to be annotated with @TMessages");
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[] { clazz }, this::invoke);
    }

    private Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        // calculate fallback
        String fallback = TMessagePatternExtractionUtil
                .getMessageFallback(method);

        // build string
        TranslatedString tString = new TranslatedString(tStringResovler,
                TMessagePatternExtractionUtil.getMethodKey(method), fallback);

        if (args == null || args.length == 0) {
            // no arguments
            if (method.getReturnType().isAssignableFrom(TranslatedString.class))
                return tString;
            if (method.getReturnType().isAssignableFrom(PatternString.class))
                return new PatternString(pStringResolver, tString,
                        Collections.emptyMap());

            throw new RuntimeException(
                    "The return type of "
                            + method
                            + " must be assigneable from LString, TranslatedString or PatternString");
        } else {
            // there are arguments
            if (method.getReturnType().isAssignableFrom(PatternString.class)) {
                // build parameter map
                HashMap<String, Object> parameters = new HashMap<>();
                for (int i = 0; i < method.getParameters().length; i++) {
                    parameters
                            .put(method.getParameters()[i].getName(), args[i]);
                }
                return new PatternString(pStringResolver, tString, parameters);
            }
            throw new RuntimeException("The return type of " + method
                    + " must be assigneable from LString or PatternString");
        }
    }

}
