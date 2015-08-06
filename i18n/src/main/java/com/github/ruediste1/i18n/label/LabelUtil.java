package com.github.ruediste1.i18n.label;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Stream;

import javax.inject.Inject;

import org.objectweb.asm.Type;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste.c3java.linearization.JavaC3;
import com.github.ruediste.c3java.method.MethodUtil;
import com.github.ruediste.c3java.properties.PropertyDeclaration;
import com.github.ruediste.c3java.properties.PropertyInfo;
import com.github.ruediste.c3java.properties.PropertyPath;
import com.github.ruediste.c3java.properties.PropertyUtil;
import com.github.ruediste1.i18n.lString.StringUtil;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.google.common.base.CaseFormat;
import com.google.common.base.Joiner;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
import com.google.common.reflect.TypeToken;

public class LabelUtil {

    private TranslatedStringResolver resolver;

    private static class TypeLabel {
        String variant;
        Class<?> definingClass;
        String fallback;

        TypeLabel(String variant, Class<?> definingClass, String fallback) {
            super();
            this.variant = variant;
            this.definingClass = definingClass;
            this.fallback = fallback;
        }

        TranslatedString toTranslatedString(TranslatedStringResolver resolver) {
            return new TranslatedString(resolver, definingClass.getName()
                    + (variant.isEmpty() ? "" : "." + variant), fallback);
        }
    }

    private Map<String, TypeLabel> getTypeLabels(Class<?> cls) {
        return tryGetTypeLabels(cls).orElseThrow(
                () -> new RuntimeException(
                        "Missing @Label or @Labeled annotation on " + cls
                                + " and all it's ancestors"));

    }

    private Optional<Map<String, TypeLabel>> tryGetTypeLabels(Class<?> cls) {
        Map<String, TypeLabel> result = getTypeLabelsNoInherit(cls);
        if (!result.isEmpty())
            return Optional.of(result);
        for (Class<?> superClass : Iterables.skip(JavaC3.allSuperclasses(cls),
                1)) {
            result = getTypeLabelsNoInherit(superClass);
            if (!result.isEmpty())
                return Optional.of(result);
        }
        return Optional.empty();
    }

    private Map<String, TypeLabel> getTypeLabelsNoInherit(Class<?> cls) {
        Map<String, TypeLabel> labels = new HashMap<>();

        // add variants from label annotations
        processLabelAnnotationsNew(cls, (variant, label) -> labels.put(variant,
                new TypeLabel(variant, cls, label)),
                v -> calculateTypeFallbackNew(cls, v));

        // add variants from Labeled
        {
            Labeled labeled = cls.getAnnotation(Labeled.class);
            if (labeled != null) {
                Stream.concat(Stream.of(""), Arrays.stream(labeled.variants()))
                        .forEach(
                                variant -> labels.computeIfAbsent(
                                        variant,
                                        x -> new TypeLabel(variant, cls,
                                                calculateTypeFallbackNew(cls,
                                                        variant))));
            }
        }

        return labels;
    }

    /**
     * 
     * @param consumer
     *            consumer of (variant, label)
     */
    private void processVariantAnnotations(AnnotatedElement element,
            BiConsumer<String, String> consumer) {
        for (Annotation a : element.getAnnotations()) {
            LabelVariant labelVariant = a.annotationType().getAnnotation(
                    LabelVariant.class);
            if (labelVariant != null) {
                String variant = labelVariant.value();
                consumer.accept(variant, getLabelOfVariantAnnotation(a));
            }
        }
    }

    /**
     * get the labels of a type. The returned map is
     * propertyName->variant->label
     */
    private Map<String, Map<String, TranslatedString>> getPropertyLabels(
            Class<?> type) {
        Map<String, Map<String, TranslatedString>> result = new HashMap<>();

        for (PropertyDeclaration property : PropertyUtil
                .getPropertyIntroductionMap(type).values()) {
            Map<String, TranslatedString> variantMap = new HashMap<>();
            getPropertyLabels(property).forEach(
                    (variant, label) -> variantMap.put(variant,
                            new TranslatedString(resolver, property
                                    .getDeclaringType().getName()
                                    + "."
                                    + property.getName()
                                    + (variant.isEmpty() ? "" : "." + variant),
                                    label)));
            if (!variantMap.isEmpty())
                result.put(property.getName(), variantMap);
        }
        return result;
    }

    @Inject
    public LabelUtil(TranslatedStringResolver resolver) {
        this.resolver = resolver;
    }

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(PropertyPath path) {
        return getPropertyLabel(path.getAccessedProperty());
    }

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(PropertyDeclaration property) {
        return getPropertyLabel(property.getDeclaringType(), property.getName());
    }

    /**
     * Get the label of a certain property in a type in the default variant
     */
    public TranslatedString getPropertyLabel(PropertyDeclaration property,
            String variant) {
        return getPropertyLabel(property.getDeclaringType(),
                property.getName(), variant);
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
        Map<String, Map<String, TranslatedString>> labels = getPropertyLabels(type);
        Map<String, TranslatedString> variantMap = labels.get(propertyName);
        if (variantMap == null)
            throw new RuntimeException("No labels defined for property "
                    + propertyName + " on " + type);
        TranslatedString label = variantMap.get(variant);
        if (label == null)
            throw new RuntimeException("Variant " + variant
                    + " not defined for property " + propertyName + " on "
                    + type + ".\nAvailable variants: "
                    + Joiner.on(",").join(variantMap.keySet()));

        return label;
    }

    public TranslatedString getEnumMemberLabel(Enum<?> member) {
        return getEnumMemberLabel(member, "");
    }

    public String[] getEnumLabelVariants(Class<? extends Enum<?>> enumClass) {
        return tryGetEnumLabelVariants(enumClass).orElseThrow(
                () -> new RuntimeException(
                        "Missing @MembersLabeled annotation for " + enumClass));
    }

    public Optional<String[]> tryGetEnumLabelVariants(
            Class<? extends Enum<?>> enumClass) {
        MembersLabeled membersLabeled = enumClass
                .getAnnotation(MembersLabeled.class);

        if (membersLabeled != null) {
            LinkedHashSet<String> result = new LinkedHashSet<String>();
            result.addAll(Arrays.asList(membersLabeled.variants()));
            result.add("");
            return Optional.of(result.toArray(new String[] {}));
        } else
            return Optional.empty();
    }

    private <T extends Enum<T>> Map<T, Map<String, TranslatedString>> getEnumMemberLabelMap(
            Class<T> enumClass) {
        return tryGetEnumMemberLabelMap(enumClass).orElseThrow(
                () -> new RuntimeException(
                        "Missing @MembersLabeled annotation for " + enumClass));
    }

    private <T extends Enum<T>> Optional<Map<T, Map<String, TranslatedString>>> tryGetEnumMemberLabelMap(
            Class<T> enumClass) {

        Optional<String[]> variantsArray = tryGetEnumLabelVariants(enumClass);
        if (!variantsArray.isPresent())
            return Optional.empty();

        Map<T, Map<String, TranslatedString>> result = new HashMap<>();
        HashSet<String> variants = new HashSet<>(Arrays.asList(variantsArray
                .get()));
        for (T member : enumClass.getEnumConstants()) {

            Field enumField;
            try {
                enumField = enumClass.getDeclaredField(member.name());
            } catch (NoSuchFieldException | SecurityException e) {
                throw new RuntimeException(
                        "Error while getting field for enum member", e);
            }

            Map<String, String> definedVariantMap = new HashMap<>();

            processLabelAnnotationsNew(
                    enumField,
                    (variant, label) -> {
                        String existing = definedVariantMap.put(variant, label);
                        if (existing != null) {
                            throw new RuntimeException(
                                    "Multiple labels defined for enum constant "
                                            + enumField.getName() + " of "
                                            + enumClass);
                        }
                    },
                    variant -> calculateEnumMemberFallbackNew(member, variant));

            for (String definedVariant : definedVariantMap.keySet()) {
                if (!variants.contains(definedVariant))
                    throw new RuntimeException(
                            "Variant "
                                    + definedVariant
                                    + " defined on "
                                    + enumField.getName()
                                    + " of "
                                    + enumClass
                                    + " but not declared in the @MemberLabeled annotation");
            }
            Map<String, TranslatedString> labelMap = new HashMap<>();
            result.put(member, labelMap);
            for (String variant : variants) {
                String key = enumClass.getName() + "." + enumField.getName()
                        + (variant.isEmpty() ? "" : "." + variant);
                String label = definedVariantMap.get(variant);
                if (label != null) {
                    labelMap.put(variant, new TranslatedString(resolver, key,
                            label));
                } else {
                    labelMap.put(variant, new TranslatedString(resolver, key,
                            calculateEnumMemberFallbackNew(member, variant)));

                }
            }

        }
        return Optional.of(result);

    }

    public TranslatedString getEnumMemberLabel(Enum<?> member, String variant) {
        TranslatedString result = getEnumMemberLabelMap(
                member.getDeclaringClass()).get(member).get(variant);
        if (result == null) {
            throw new RuntimeException("Variant <" + variant
                    + "> is not defined on " + member.getDeclaringClass());
        }
        return result;
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

    protected String calculateEnumMemberFallbackNew(Enum<?> member,
            String variant) {
        return StringUtil
                .insertSpacesIntoCamelCaseString(CaseFormat.UPPER_UNDERSCORE
                        .to(CaseFormat.UPPER_CAMEL, member.name()));
    }

    protected String calculateMethodLabelFallback(Method method, String variant) {
        return StringUtil
                .insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.UPPER_CAMEL, method.getName()))
                + ("".equals(variant) ? "" : "(" + variant + ")");
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
        return getTypeLabels(type).keySet();
    }

    public TranslatedString getTypeLabel(Class<?> type, String variant) {
        Map<String, TypeLabel> labels = getTypeLabels(type);
        TypeLabel typeLabel = labels.get(variant);

        if (typeLabel == null) {
            throw new RuntimeException("Label variant " + variant
                    + " not available for " + type + ". Available variants: <"
                    + Joiner.on(", ").join(labels.keySet()) + ">");
        }

        return typeLabel.toTranslatedString(resolver);
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
                }).map(a -> {
                    return getLabelOfVariantAnnotation(a);
                });
    }

    private static String getLabelOfVariantAnnotation(Annotation a) {
        try {
            return (String) a.annotationType().getMethod("value").invoke(a);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String calculateTypeFallbackNew(Class<?> type, String variant) {
        return StringUtil.insertSpacesIntoCamelCaseString(type.getSimpleName());
    }

    private static class Pair<A, B> {
        A a;
        B b;

        static <A, B> Pair<A, B> of(A a, B b) {
            Pair<A, B> result = new Pair<>();
            result.a = a;
            result.b = b;
            return result;
        }
    }

    private void processPropertyLabelAnnotations(
            Multimap<String, Pair<String, String>> labels,
            PropertyDeclaration property, AnnotatedElement e, String location) {

        processLabelAnnotationsNew(
                e,
                (variant, label) -> labels.put(variant,
                        Pair.of(location, label)),
                v -> calculateFallbackFromPropertyName(property.getName(), v));
    }

    /**
     * get a Map from variant to label of a property
     */
    private Map<String, String> getPropertyLabels(PropertyDeclaration property) {

        Multimap<String, Pair<String, String>> labels = MultimapBuilder
                .hashKeys().arrayListValues().build();

        processPropertyLabelAnnotations(labels, property, property.getGetter(),
                "getter");
        processPropertyLabelAnnotations(labels, property, property.getSetter(),
                "setter");
        processPropertyLabelAnnotations(labels, property,
                property.getBackingField(), "backingField");

        Map<String, String> result = new HashMap<>();

        for (Entry<String, Collection<Pair<String, String>>> entry : labels
                .asMap().entrySet()) {
            String variant = entry.getKey();
            if (entry.getValue().size() > 1) {
                throw new RuntimeException(
                        "Multiple labels found for property "
                                + property
                                + ":\n"
                                + entry.getValue().stream()
                                        .map(p -> p.a + ": " + p.b)
                                        .collect(joining(",\n")));
            }
            result.put(variant, Iterables.getOnlyElement(entry.getValue()).b);
        }

        PropertiesLabeled propertiesLabeled = property.getDeclaringType()
                .getAnnotation(PropertiesLabeled.class);
        if (propertiesLabeled != null) {
            for (String variant : Iterables.concat(
                    Arrays.asList(propertiesLabeled.variants()),
                    Arrays.asList(""))) {
                if (!result.containsKey(variant)) {
                    result.put(
                            variant,
                            calculateFallbackFromPropertyName(
                                    property.getName(), variant));
                }
            }
        }
        return result;
    }

    /**
     * @param consumer
     *            consumer of (variant, label)
     */
    private void processLabelAnnotationsNew(AnnotatedElement annotated,
            BiConsumer<String, String> consumer,
            Function<String, String> fallbackFunction) {
        if (annotated == null)
            return;
        HashSet<String> seenVariants = new HashSet<>();

        for (Label label : annotated.getAnnotationsByType(Label.class)) {
            if (seenVariants.add(label.variant()))
                consumer.accept(label.variant(), label.value());
        }

        Labeled labeled = annotated.getAnnotation(Labeled.class);
        if (labeled != null) {
            Stream.concat(Arrays.asList("").stream(),
                    Arrays.stream(labeled.variants())).forEach(v -> {
                if (seenVariants.add(v))
                    consumer.accept(v, fallbackFunction.apply(v));
            });
        }
        processVariantAnnotations(annotated, consumer);
    }

    /**
     * Calculate the fallback string for a property from it's name. Override to
     * customize
     */
    protected String calculateFallbackFromPropertyName(String name,
            String variant) {
        return StringUtil
                .insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.UPPER_CAMEL, name));
    }

    /**
     * Return all labels defined on a type like type label, property labels or
     * enum member labels.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public List<TranslatedString> getLabelsDefinedOn(Class<?> type) {
        ArrayList<TranslatedString> result = new ArrayList<>();
        result.addAll(getTypeLabelsOf(type));
        result.addAll(getPropertyLabelsOf(type));
        if (type.isEnum()) {
            result.addAll(getEnumMemberLabelsOf((Class) type));
        }
        result.addAll(getDeclaredMethodLabels(type));
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

    public <T extends Enum<T>> Collection<TranslatedString> getEnumMemberLabelsOf(
            Class<T> type) {
        return tryGetEnumMemberLabelMap(type).map(
                map -> map.values().stream().flatMap(x -> x.values().stream())
                        .collect(toList())).orElseGet(
                () -> Collections.emptyList());
    }

    public Collection<TranslatedString> getPropertyLabelsOf(Class<?> type) {
        return getPropertyLabels(type).values().stream()
                .flatMap(map -> map.values().stream()).collect(toList());
    }

    public Collection<TranslatedString> getTypeLabelsOf(Class<?> type) {
        return tryGetTypeLabels(type).map(
                map -> map.values().stream()
                        .map(x -> x.toTranslatedString(resolver))
                        .collect(toList())).orElse(Collections.emptyList());
    }

    public Properties toProperties(Collection<TranslatedString> strings) {
        Properties result = new Properties() {
            private static final long serialVersionUID = 1L;

            @Override
            public synchronized Enumeration<Object> keys() {
                return Collections.enumeration(new TreeSet<Object>(super
                        .keySet()));
            }
        };
        strings.forEach(string -> {
            result.put(string.getResourceKey(), string.getFallback());
        });
        return result;
    }

    /**
     * Get the declared labels of all declared methods. Does not return labels
     * inherited from parent classes. This method is suitable when processing
     * all method labels.
     */
    public Collection<TranslatedString> getDeclaredMethodLabels(Class<?> cls) {
        return getDirectLabelsOfDeclaredMethods(cls).values().stream()
                .flatMap(m -> m.values().stream()).collect(toList());
    }

    /**
     * Get the label of the last accessed method
     */
    public <T> Optional<TranslatedString> tryGetMethodLabel(Class<T> cls,
            Consumer<T> accessor) {
        return getMethodLabel(cls, accessor, "");
    }

    /**
     * Get the label of the last accessed method
     */
    public <T> Optional<TranslatedString> getMethodLabel(Class<T> cls,
            Consumer<T> accessor, String variant) {
        return getMethodLabel(TypeToken.of(cls), accessor, variant);
    }

    /**
     * Get the label of the last accessed method
     */
    public <T> Optional<TranslatedString> getMethodLabel(TypeToken<T> cls,
            Consumer<T> accessor, String variant) {
        return tryGetMethodLabel(
                MethodInvocationRecorder.getLastInvocation(cls, accessor)
                        .getMethod(), variant);
    }

    public TranslatedString getMethodLabel(Method method) {
        return getMethodLabel(method, "");
    }

    public Optional<TranslatedString> tryGetMethodLabel(Method method) {
        return tryGetMethodLabel(method, "");
    }

    public TranslatedString getMethodLabel(Method method, String variant) {
        return tryGetMethodLabel(method, variant).orElseThrow(
                () -> new RuntimeException("Unable to find method label for "
                        + method + " and variant <" + variant + ">"));
    }

    public Optional<TranslatedString> tryGetMethodLabel(Method method,
            String variant) {
        for (Method m : MethodUtil.getDeclarations(method)) {
            Map<String, TranslatedString> labels = getDirectLabelsOfDeclaredMethods(
                    m.getDeclaringClass()).get(m);
            if (labels != null && !labels.isEmpty()) {
                return Optional.ofNullable(labels.get(variant));
            }
        }
        return Optional.empty();
    }

    /**
     * Get the labels of all declared methods, without taking inheritance into
     * account
     */
    private Map<Method, Map<String, TranslatedString>> getDirectLabelsOfDeclaredMethods(
            Class<?> cls) {
        // name->signature->method
        Map<String, Map<String, Method>> methods = new HashMap<>();
        for (Method m : cls.getDeclaredMethods()) {
            // skip property accessors
            if (PropertyUtil.getAccessor(m) != null)
                continue;
            // add method to map
            String signature = Type.getMethodDescriptor(m);
            methods.computeIfAbsent(m.getName(), x -> new TreeMap<>()).put(
                    signature, m);
        }

        // method->variant->label
        Map<Method, Map<String, TranslatedString>> result = new HashMap<>();
        // generate the translated strings, making duplicate method names unique
        // using a count
        for (Entry<Method, String> entry : getDirectlyDeclaredMethods(cls)
                .entrySet()) {
            Method m = entry.getKey();
            processDirectMethodLabels(
                    m,
                    (variant, label) -> result.computeIfAbsent(m,
                            x -> new HashMap<>()).put(
                            variant,
                            new TranslatedString(resolver, getMethodKey(m,
                                    entry.getValue(), variant), label)));
        }

        return result;
    }

    /**
     * Get all directly declared methods, without property accessors, and their
     * unique name.
     */
    public static Map<Method, String> getDirectlyDeclaredMethods(Class<?> cls) {
        // name->signature->method
        Map<String, Map<String, Method>> methods = new HashMap<>();
        for (Method m : cls.getDeclaredMethods()) {
            // skip property accessors
            if (PropertyUtil.getAccessor(m) != null)
                continue;
            // add method to map
            String signature = Type.getMethodDescriptor(m);
            methods.computeIfAbsent(m.getName(), x -> new TreeMap<>()).put(
                    signature, m);
        }

        // method->variant->label
        Map<Method, String> result = new LinkedHashMap<>();
        // generate the translated strings, making duplicate method names unique
        // using a count
        for (Map<String, Method> map : methods.values()) {
            int count = 0;
            for (Method m : map.values()) {
                int mNr = count++;
                String name = m.getName() + (mNr == 0 ? "" : "~" + mNr);
                result.put(m, name);
            }
        }
        return result;
    }

    /**
     * Process labels defined directly on the declaration of the method, or via
     * {@link MethodsLabeled} annotation of the declaring class
     * 
     * @param consumer
     *            consumer of (variant,label)
     */
    private void processDirectMethodLabels(Method method,
            BiConsumer<String, String> consumer) {
        HashSet<String> seenVariants = new HashSet<>();
        processLabelAnnotationsNew(method, (variant, label) -> {
            if (seenVariants.add(variant))
                consumer.accept(variant, label);
        }, v -> calculateMethodLabelFallback(method, v));

        MethodsLabeled methodsLabeled = method.getDeclaringClass()
                .getAnnotation(MethodsLabeled.class);
        if (methodsLabeled != null) {
            for (String variant : Iterables.concat(Arrays.asList(""),
                    Arrays.asList(methodsLabeled.variants()))) {
                if (seenVariants.add(variant)) {
                    consumer.accept(variant,
                            calculateMethodLabelFallback(method, variant));
                }
            }
        }
    }

    private String getMethodKey(Method method, String uniqueMethodName,
            String variant) {
        return method.getDeclaringClass().getName() + "." + uniqueMethodName
                + ("".equals(variant) ? "" : "." + variant);
    }
}
