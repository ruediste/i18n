package com.github.ruediste1.i18n.label;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import com.github.ruediste1.i18n.lString.TranslatedString;

/**
 * Additional label extractor adding labels of annotations bearing a stereotype
 * meta annotation.
 * 
 * <p>
 * To use, define a stereotype annotation
 * 
 * <pre>
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * private @interface Stereotype {
 * }
 * </pre>
 * 
 * and an annotation for the label
 * 
 * <pre>
 * &#64;Stereotype
 * &#64;Retention(RetentionPolicy.RUNTIME)
 * &#64;Label("anAction")
 * private @interface TestAction {
 * }
 * </pre>
 * 
 * Next, place the {@code @TestAction} annotation on a method and register the
 * {@link StereotypeAdditionalLabelExtractor} using
 * {@link LabelUtil#setAdditionalLabelsExtractor(Function)}. This causes the
 * label of the annotation to be added to the labels of the method.
 */
public class StereotypeAdditionalLabelExtractor implements Function<AnnotatedElement, Map<String, TranslatedString>> {

    private Class<? extends Annotation> stereotypeClass;
    private LabelUtil labelUtil;

    public StereotypeAdditionalLabelExtractor(Class<? extends Annotation> stereotypeClass, LabelUtil labelUtil) {
        this.stereotypeClass = stereotypeClass;
        this.labelUtil = labelUtil;
    }

    @Override
    public Map<String, TranslatedString> apply(AnnotatedElement annotated) {
        HashMap<String, TranslatedString> result = new HashMap<>();
        for (Annotation a : annotated.getAnnotations()) {
            if (a.annotationType().isAnnotationPresent(stereotypeClass)) {
                result.putAll(labelUtil.typeLabels(a.annotationType()));
            }
        }
        return result;
    }

}
