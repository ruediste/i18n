package com.github.ruediste1.i18n.message;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;

import javax.inject.Inject;

import com.github.ruediste1.i18n.lString.PatternString;
import com.github.ruediste1.i18n.lString.PatternStringResolver;
import com.github.ruediste1.i18n.lString.StringUtil;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.google.common.base.CaseFormat;

public class TMessageUtil {

    @Inject
    PatternStringResolver pStringResolver;

    @Inject
    TranslatedStringResolver tStringResovler;

    @SuppressWarnings("unchecked")
    public <T> T getMessageInterfaceInstance(Class<T> clazz) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(),
                new Class<?>[] { clazz }, this::invoke);
    }

    private Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        // calculate fallback
        String fallback;
        TMessage tMessage = method.getAnnotation(TMessage.class);
        if (tMessage != null) {
            fallback = tMessage.value();
        } else {
            fallback = StringUtil
                    .insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
                            CaseFormat.UPPER_CAMEL, method.getName()))
                    + ".";
        }

        // build string
        TranslatedString tString = new TranslatedString(tStringResovler, method
                .getDeclaringClass().getName() + "." + method.getName(),
                fallback);

        // check return type
        if (TranslatedString.class.equals(method.getReturnType())) {
            if (args != null && args.length > 0) {
                throw new RuntimeException(
                        "The return type of "
                                + method
                                + " is TranslatedString but there are parameters. Change the return type to PatternString instead");
            }
            return tString;
        }

        if (PatternString.class.equals(method.getReturnType())) {
            // build parameter map
            HashMap<String, Object> parameters = new HashMap<>();
            for (int i = 0; i < method.getParameters().length; i++) {
                parameters.put(method.getParameters()[i].getName(), args[i]);
            }

            return new PatternString(pStringResolver, tString, parameters);
        }

        throw new RuntimeException("The return type of " + method
                + " must be TString or PString");
    }

}
