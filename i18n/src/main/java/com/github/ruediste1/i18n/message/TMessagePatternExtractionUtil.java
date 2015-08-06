package com.github.ruediste1.i18n.message;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste1.i18n.lString.StringUtil;
import com.google.common.base.CaseFormat;

public class TMessagePatternExtractionUtil {

    private TMessagePatternExtractionUtil() {
    }

    public static String getMessageFallback(Method method) {
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
        return fallback;
    }

    public static String getMethodKey(Method method) {
        return method.getDeclaringClass().getName() + "." + method.getName();
    }

    /**
     * Return the fallbacks along with the resource keys of the messages in the
     * given interface. If the supplied class is no interface or is not
     * annotated with {@link TMessages @TMessages}, an empty map is returned.
     */
    public static Map<String, String> getPatterns(Class<?> cls) {
        if (!cls.isInterface())
            return Collections.emptyMap();
        if (!cls.isAnnotationPresent(TMessages.class))
            return Collections.emptyMap();

        HashMap<String, String> result = new HashMap<String, String>();
        for (Method m : cls.getMethods()) {
            result.put(getMethodKey(m), getMessageFallback(m));
        }
        return result;
    }
}
