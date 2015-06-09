package com.github.ruediste1.i18n.label;

import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import javax.inject.Inject;

import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste1.i18n.lString.StringUtil;
import com.github.ruediste1.i18n.lString.TStringResolver;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;

public class LabelUtil {

    @Inject
    TStringResolver resolver;

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(PropertyPath path) {
        return getPropertyLabel(path.getAccessedProperty());
    }

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(PropertyInfo property) {
        return getPropertyLabel(property.getDeclaringType(), property.getName());
    }

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(Class<?> type, String propertyName) {
        return getPropertyLabel(type, propertyName, "");
    }

    /**
     * Get the label of a certain property in a type
     */
    public TranslatedString getPropertyLabel(PropertyPath path, String variant) {
        return getPropertyLabel(path.getAccessedProperty(), variant);
    }

    /**
     * Get the label of a certain property in a type
     */
    public TranslatedString getPropertyLabel(PropertyInfo info, String variant) {
        return getPropertyLabel(info.getDeclaringType(), info.getName(),
                variant);
    }

    /**
     * Get the label of a certain property in a type
     */
    public <T> TranslatedString getPropertyLabel(Class<T> startClass,
            Consumer<T> propertyAccessor) {
        PropertyPath propertyPath = PropertyUtil.getPropertyPath(startClass,
                propertyAccessor);
        return getPropertyLabel(propertyPath.getAccessedProperty());
    }

    /**
     * Get the label of a certain property in a type
     */
    public <T> TranslatedString getPropertyLabel(Class<T> startClass,
            Consumer<T> propertyAccessor, String variant) {
        PropertyPath propertyPath = PropertyUtil.getPropertyPath(startClass,
                propertyAccessor);
        return getPropertyLabel(propertyPath.getAccessedProperty(), variant);
    }

    /**
     * Get the label of a certain property in a type
     */
    public TranslatedString getPropertyLabel(Class<?> type,
            String propertyName, String variant) {
        // find property
        PropertyDeclaration property = PropertyUtil.getPropertyIntroduction(
                type, propertyName);
        if (property == null) {
            throw new IllegalArgumentException("No property " + propertyName
                    + " found in " + type);
        }

        // check that propertiesLabeled annotation is present
        {
            PropertiesLabeled propertiesLabeled = property.getDeclaringType()
                    .getAnnotation(PropertiesLabeled.class);

            if (propertiesLabeled == null) {
                throw new RuntimeException(
                        "No PropertiesLabeled annotation present on " + type
                                + " while retrieving label for property "
                                + propertyName);
            }

            if (!"".equals(variant)
                    && !Arrays.asList(propertiesLabeled.variants()).contains(
                            variant)) {
                throw new RuntimeException("Label variant " + variant
                        + " not available for " + type
                        + ". Available variants: <"
                        + Joiner.on(", ").join(propertiesLabeled.variants())
                        + ">");
            }
        }

        // build and return TString
        return new TranslatedString(
                resolver,
                property.getDeclaringType().getName() + "." + propertyName
                        + (variant.isEmpty() ? "" : "." + variant),
                calculatePropertyFallback(type, propertyName, variant, property));
    }

    public TranslatedString getEnumMemberLabel(Enum<?> member) {
        return getEnumMemberLabel(member, "");
    }

    public TranslatedString getEnumMemberLabel(Enum<?> member, String variant) {
        MembersLabeled membersLabeled = member.getDeclaringClass()
                .getAnnotation(MembersLabeled.class);

        // check that MembersLabeled annotation is present
        if (membersLabeled == null) {
            throw new RuntimeException("Missing MembersLabeled annotation for "
                    + member.getDeclaringClass()
                    + " while retrieving label for " + member);
        }

        // check that variant is allowed
        if (!"".equals(variant)
                && !Arrays.asList(membersLabeled.variants()).contains(variant)) {
            throw new RuntimeException("Label variant " + variant
                    + " not available for member " + member + " of "
                    + member.getDeclaringClass() + ". Available variants: <"
                    + Joiner.on(", ").join(membersLabeled.variants()) + ">");
        }

        return new TranslatedString(resolver, member.getDeclaringClass()
                .getName()
                + "."
                + member.name()
                + (variant.isEmpty() ? "" : "." + variant),
                calculateEnumMemberFallback(member, variant));
    }

    protected String calculateEnumMemberFallback(Enum<?> member, String variant) {
        Field memberField;
        try {
            memberField = member.getDeclaringClass().getField(member.name());
        } catch (NoSuchFieldException | SecurityException e) {
            throw new RuntimeException(e);
        }
        List<String> labels = extractLabels(memberField, variant);
        if (labels.size() > 1) {
            throw new RuntimeException(
                    "Multiple Label annotations found for member " + member
                            + " of " + member.getDeclaringClass()
                            + " using variant " + variant);
        }
        if (labels.size() == 1) {
            return labels.get(0);
        }

        return StringUtil
                .insertSpacesIntoCamelCaseString(CaseFormat.UPPER_UNDERSCORE
                        .to(CaseFormat.UPPER_CAMEL, member.name()));
    }

    protected List<String> extractLabels(AnnotatedElement annotated,
            String variant) {
        return Stream.concat(
                Arrays.stream(annotated.getAnnotationsByType(Label.class))
                        .filter(l -> variant.equals(l.variant()))
                        .map(l -> l.value()),
                extractLabelsOfVariantAnnotations(annotated, variant)).collect(
                toList());
    }

    public TranslatedString getTypeLabel(Class<?> type) {
        return getTypeLabel(type, "");
    }

    /**
     * Return the label variants available for a type
     */
    public Set<String> availableTypeLabelVariants(Class<?> type) {
        Set<String> result = new HashSet<String>();
        result.add("");

        // add variants from Labeled
        Arrays.stream(type.getAnnotation(Labeled.class).variants()).forEach(
                result::add);

        // add variants from Label
        Arrays.stream(type.getAnnotationsByType(Label.class))
                .map(Label::variant).forEach(result::add);

        // add variants from variant annotations
        for (Annotation a : type.getAnnotations()) {
            LabelVariant labelVariant = a.annotationType().getAnnotation(
                    LabelVariant.class);
            if (labelVariant != null) {
                result.add(labelVariant.value());
            }
        }

        return result;
    }

    public TranslatedString getTypeLabel(Class<?> type, String variant) {
        Labeled labeled = type.getAnnotation(Labeled.class);
        if (labeled == null) {
            throw new RuntimeException("Missing Labeled annotation on " + type);
        }

        Set<String> availableVariants = availableTypeLabelVariants(type);
        if (!availableVariants.contains(variant)) {
            throw new RuntimeException("Label variant " + variant
                    + " not available for " + type + ". Available variants: <"
                    + Joiner.on(", ").join(availableVariants) + ">");
        }

        return new TranslatedString(resolver, type.getName()
                + (variant.isEmpty() ? "" : "." + variant),
                calculateTypeFallback(type, variant));
    }

    /**
     * Iterate over all annotations having the meta-annotation
     * {@link LabelVariant}, filter those matching the specified variant and
     * extract their value.
     */
    public static Stream<String> extractLabelsOfVariantAnnotations(
            AnnotatedElement annotated, String variant) {
        return Arrays
                .stream(annotated.getAnnotations())
                .filter(a -> {
                    LabelVariant labelVariant = a.annotationType()
                            .getAnnotation(LabelVariant.class);

                    return labelVariant != null
                            && variant.equals(labelVariant.value());
                })
                .map(a -> {
                    try {
                        return (String) a.annotationType().getMethod("value")
                                .invoke(a);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                });
    }

    protected String calculateTypeFallback(Class<?> type, String variant) {
        List<String> annotations = extractLabels(type, variant);
        if (annotations.isEmpty()) {
            return StringUtil.insertSpacesIntoCamelCaseString(type
                    .getSimpleName());
        }

        if (annotations.size() == 1) {
            return annotations.get(0);
        }

        throw new RuntimeException("Multiple Label annotations found for type "
                + type + " and variant " + variant);
    }

    /**
     * Calculate the fallback string for a property. Override to customize
     */
    protected String calculatePropertyFallback(Class<?> type, String name,
            String variant, PropertyDeclaration property) {

        ArrayList<String> labels = new ArrayList<>();
        ArrayList<String> labelLocations = new ArrayList<>();

        processLabelAnnotations(property.getGetter(), "getter", labels,
                labelLocations, variant);
        processLabelAnnotations(property.getSetter(), "setter", labels,
                labelLocations, variant);
        processLabelAnnotations(property.getBackingField(), "backingField",
                labels, labelLocations, variant);

        if (labels.size() == 0) {
            return calculateFallbackFromPropertyName(name);
        } else if (labels.size() == 1) {
            return labels.get(0);
        } else {
            throw new RuntimeException("Multiple label annotations found on\n"
                    + Joiner.on(",\n").join(labelLocations) + "\nfor property "
                    + property);
        }
    }

    private void processLabelAnnotations(AnnotatedElement annotated,
            String location, ArrayList<String> labels,
            ArrayList<String> labelLocations, String variant) {
        if (annotated != null) {
            List<String> labelsFound = extractLabels(annotated, variant);
            if (labelsFound.size() > 1) {
                throw new RuntimeException(
                        "More than one Label annotation found on " + annotated
                                + " for variant " + variant);
            } else if (labelsFound.size() == 1) {
                labelLocations.add(location);
                labels.add(labelsFound.get(0));
            }
        }
    }

    /**
     * Calculate the fallback string for a property from it's name. Override to
     * customize
     */
    protected String calculateFallbackFromPropertyName(String name) {
        return StringUtil
                .insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.UPPER_CAMEL, name));
    }

    /**
     * Return all labels defined on a type like type label, property labels or
     * enum member labels.
     */
    public List<TranslatedString> getLabelsDefinedOn(Class<?> type) {
        ArrayList<TranslatedString> result = new ArrayList<>();
        result.addAll(getTypeLabelsOf(type));
        result.addAll(getPropertyLabelsOf(type));
        result.addAll(getEnumMemberLabelsOf(type));
        return result;
    }

    /**
     * Return the variants available for the members of the specified enum. If
     * the type is no enum, an empty collection is returned
     */
    public Collection<String> availableEnumMemberLabelVariants(Class<?> type) {
        ArrayList<String> result = new ArrayList<>();
        if (!type.isEnum())
            return result;
        MembersLabeled membersLabeled = type
                .getAnnotation(MembersLabeled.class);
        if (membersLabeled == null)
            return result;
        result.add("");
        result.addAll(Arrays.asList(membersLabeled.variants()));
        return result;
    }

    public Collection<TranslatedString> getEnumMemberLabelsOf(Class<?> type) {
        Collection<TranslatedString> result = new ArrayList<>();
        for (String variant : availableEnumMemberLabelVariants(type)) {
            for (Object value : type.getEnumConstants()) {
                result.add(getEnumMemberLabel((Enum<?>) value, variant));
            }
        }

        return result;
    }

    public Collection<TranslatedString> getPropertyLabelsOf(Class<?> type) {
        Collection<TranslatedString> result = new ArrayList<>();
        for (PropertyDeclaration property : PropertyUtil
                .getPropertyIntroductionMap(type).values()) {
            PropertiesLabeled propertiesLabeled = property.getDeclaringType()
                    .getAnnotation(PropertiesLabeled.class);
            if (propertiesLabeled == null)
                continue;
            result.add(getPropertyLabel(type, property.getName()));
            for (String variant : propertiesLabeled.variants()) {
                result.add(getPropertyLabel(type, property.getName(), variant));
            }
        }
        return result;
    }

    public Collection<TranslatedString> getTypeLabelsOf(Class<?> type) {
        Collection<TranslatedString> result = new ArrayList<>();
        if (type.isAnnotationPresent(Labeled.class))
            for (String variant : availableTypeLabelVariants(type)) {
                result.add(getTypeLabel(type, variant));
            }
        return result;
    }

    public Properties toProperties(Collection<TranslatedString> strings) {
        Properties result = new Properties();
        strings.stream()
                .sorted((a, b) -> a.getResourceKey().compareTo(
                        b.getResourceKey())).forEach(string -> {
                    result.put(string.getResourceKey(), string.getFallback());
                });
        return result;
    }
}
