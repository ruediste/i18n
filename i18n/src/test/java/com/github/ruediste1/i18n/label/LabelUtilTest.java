package com.github.ruediste1.i18n.label;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste.c3java.invocationRecording.MethodInvocationRecorder;
import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;
import com.google.common.reflect.TypeToken;

public class LabelUtilTest {

    LabelUtil util;
    TranslatedStringResolver resolver = (a, b) -> null;

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ ElementType.FIELD, ElementType.METHOD, ElementType.TYPE })
    @Documented
    @LabelVariant("short")
    public @interface Short {
        String value();
    }

    @Short("myShort")
    private static class TestImplicitelyLabeled {

    }

    private static class TestTypeLabeledInherited
            extends TestImplicitelyLabeled {

    }

    @PropertiesLabeled(variants = { "foo", "short" })
    @Labeled(variants = { "foo1" })
    @Label(value = "myFoo", variant = "foo")
    @Short("typeShort")
    private static class TestClass {
        @SuppressWarnings("unused")
        public void setFooBar(int x) {
        }

        @Label("myLabel")
        @Label(value = "myLabelFoo", variant = "foo")
        @Short("propertyShort")
        public int getLabeled() {
            return 0;
        }

        @SuppressWarnings("unused")
        public void get漢字() {

        }
    }

    static class TestPropertiesLabeledDifferent {
        @Label(variant = "v1", value = "foo")
        public void getFoo() {

        }

        @Label(variant = "v2", value = "bar")
        public void getBar() {

        }
    }

    private static class TestClassUnlabeled {
        @SuppressWarnings("unused")
        public void setFooBar(int x) {
        }

        @Labeled
        public void setFoo(int x) {

        }
    }

    private static class TestClassDerived extends TestClass {

    }

    @MembersLabeled(variants = { "foo", "short" })
    private enum TestEnum {
        MEMBER_A, @Label("myB") @Label(value = "myFoo", variant = "foo") @Short("memberShort") MEMBER_B
    }

    private enum TestEnumUnlabeled {
        MEMBER_A
    }

    @Before
    public void setup() {
        util = new LabelUtil(resolver);
    }

    @Test
    public void testGetPropertyLabelsDirect() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.fooBar", "Foo Bar"),
                util.property(TestClass.class, "fooBar").label());
    }

    @Test
    public void testGetPropertyLabelsLabelAnnotation() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.labeled", "myLabel"),
                util.property(TestClass.class, "labeled").label());
    }

    @Test
    public void testGetPropertyLabelsVariant() throws Exception {
        assertEquals(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.labeled.foo", "myLabelFoo"),
                util.property(TestClass.class, "labeled").variant("foo")
                        .label());
    }

    @Test
    public void testGetPropertyLabelsVariantShort() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.labeled.short",
                        "propertyShort"),
                util.property(TestClass.class, "labeled").variant("short")
                        .label());
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelsInexistantVariant() throws Exception {
        util.property(TestClass.class, "labeled").variant("bar").label();
    }

    @Test
    public void testGetPropertyLabeledOnly() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        TestClassUnlabeled.class.getName() + ".foo", "Foo"),
                util.property(TestClassUnlabeled.class, "foo").label());
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelsUnlabeled() throws Exception {
        assertNull(util.property(TestClassUnlabeled.class, "fooBar").label());
    }

    @Test
    public void testGetPropertyLabelsUnicode() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.漢字", "漢字"),
                util.property(TestClass.class, "漢字").label());
    }

    @Test
    public void testGetPropertyLabelsDerived() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.fooBar", "Foo Bar"),
                util.property(TestClassDerived.class, "fooBar").label());
    }

    @Test
    public void testGetTypeLabel() throws Exception {
        assertEquals(new TranslatedString(resolver, TestClass.class.getName(),
                "Test Class"), util.type(TestClass.class).label());
    }

    @Test
    public void testGetTypeLabelImplicitely() throws Exception {
        assertEquals(new TranslatedString(resolver,
                TestImplicitelyLabeled.class.getName() + ".short", "myShort"),
                util.type(TestImplicitelyLabeled.class).variant("short")
                        .label());
    }

    @Test
    public void testGetTypeLabelInherited() throws Exception {
        assertEquals(new TranslatedString(resolver,
                TestImplicitelyLabeled.class.getName() + ".short", "myShort"),
                util.type(TestTypeLabeledInherited.class).variant("short")
                        .label());
    }

    @Test(expected = RuntimeException.class)
    public void testGetTypeLabelUnlabeled() throws Exception {
        util.type(TestClassUnlabeled.class).label();
    }

    @Test(expected = RuntimeException.class)
    public void testGetTypeLabelMissingVariant() throws Exception {
        util.type(TestClass.class).variant("bar").label();
    }

    @Test
    public void testGetTypeLabelVariant() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.foo", "myFoo"),
                util.type(TestClass.class).variant("foo").label());
    }

    @Test
    public void testGetTypeLabelVariantShort() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestClass.short", "typeShort"),
                util.type(TestClass.class).variant("short").label());
    }

    @Test
    public void testGetTypeLabelVariantNoLabel() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        TestClass.class.getName() + ".foo1", "Test Class"),
                util.type(TestClass.class).variant("foo1").label());
    }

    @Test
    public void testGetEnumMemberLabel() throws Exception {
        assertEquals(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_A", "Member A"),
                util.enumMember(TestEnum.MEMBER_A).label());
    }

    @Test
    public void testGetEnumMemberLabelLabelAnnotation() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestEnum.MEMBER_B", "myB"),
                util.enumMember(TestEnum.MEMBER_B).label());
    }

    @Test(expected = RuntimeException.class)
    public void testGetEnumMemberLabelUnlabeled() throws Exception {
        util.enumMember(TestEnumUnlabeled.MEMBER_A).label();
    }

    @Test(expected = RuntimeException.class)
    public void testGetEnumMemberLabelInexistantVariant() throws Exception {
        util.enumMember(TestEnum.MEMBER_A).variant("bar").label();
    }

    @Test
    public void testGetEnumMemberLabelVariant() throws Exception {
        assertEquals(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_B.foo", "myFoo"),
                util.enumMember(TestEnum.MEMBER_B).variant("foo").label());
    }

    @Test
    public void testGetEnumMemberLabelVariantShort() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        getClass().getName() + "$TestEnum.MEMBER_B.short",
                        "memberShort"),
                util.enumMember(TestEnum.MEMBER_B).variant("short").label());
    }

    @Test
    public void testGetTypeLabelsOf() throws Exception {
        Collection<TranslatedString> labels = util
                .getTypeLabelsOf(TestClass.class);
        assertEquals(4, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass", "Test Class")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.short", "typeShort")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.foo", "myFoo")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.foo1", "Test Class")));
    }

    @Test
    public void testGetLabelsDefinedOn() throws Exception {
        assertEquals(13, util.getLabelsDefinedOn(TestClass.class).size());
        assertEquals(1,
                util.getLabelsDefinedOn(TestClassUnlabeled.class).size());
        assertEquals(1,
                util.getLabelsDefinedOn(TestTypeLabeledInherited.class).size());
        assertEquals(6, util.getLabelsDefinedOn(TestEnum.class).size());
        assertEquals(0,
                util.getLabelsDefinedOn(TestEnumUnlabeled.class).size());
        assertEquals(1,
                util.getLabelsDefinedOn(TestMethodLabeled.class).size());
        assertEquals(8,
                util.getLabelsDefinedOn(TestMethodsLabeled.class).size());
        assertEquals(1,
                util.getLabelsDefinedOn(TestMethodLabelsDerived.class).size());
    }

    @Test
    public void testGetPropertyLabelssOf() throws Exception {
        Collection<? extends TranslatedString> labels = util
                .getPropertyLabelsOf(TestClass.class);
        assertEquals(9, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.fooBar", "Foo Bar")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.fooBar.foo", "Foo Bar")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestClass.fooBar.short", "Foo Bar")));
    }

    @Test
    public void testGetEnumMemberLabelsOf() throws Exception {
        Collection<? extends TranslatedString> labels = util
                .getEnumMemberLabelsOf(TestEnum.class);
        assertEquals(6, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_A", "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_A.foo", "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_A.short",
                "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_B", "myB")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_B.foo", "myFoo")));
        assertTrue(labels.contains(new TranslatedString(resolver,
                getClass().getName() + "$TestEnum.MEMBER_B.short",
                "memberShort")));
    }

    @Test
    public void testAvailableEnumMemberLabelVariants() throws Exception {
        Collection<String> variants = util
                .availableEnumMemberLabelVariants(TestEnum.class);
        assertEquals(3, variants.size());
        assertTrue(variants.contains(""));
        assertTrue(variants.contains("foo"));
        assertTrue(variants.contains("short"));
    }

    @Test
    public void testToProperties() throws Exception {
        StringWriter writer = new StringWriter();
        util.toProperties(util.getLabelsDefinedOn(TestClass.class))
                .store(writer, null);
        String contents = writer.toString();
        assertEquals(
                "\ncom.github.ruediste1.i18n.label.LabelUtilTest$TestClass=Test Class\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.foo=myFoo\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.foo1=Test Class\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar=Foo Bar\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar.foo=Foo Bar\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar.short=Foo Bar\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled=myLabel\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled.foo=myLabelFoo\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled.short=propertyShort\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.short=typeShort\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字=漢字\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字.foo=漢字\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字.short=漢字"
                        + "\n",
                contents.substring(contents.indexOf("\n")));
    }

    @Test
    public void testTryGetEnumLabelVariants() throws Exception {
        assertFalse(
                util.enum_(TestEnumUnlabeled.class).tryVariants().isPresent());
        assertArrayEquals(new String[] { "foo", "short", "" },
                util.enum_(TestEnum.class).tryVariants().get());
    }

    @Test
    public void testPropertiesLabeledDifferent() {
        assertEquals(
                "foo", util
                        .property(TestPropertiesLabeledDifferent.class,
                                x -> x.getFoo())
                        .variant("v1").label().getFallback());
        assertEquals(
                "bar", util
                        .property(TestPropertiesLabeledDifferent.class,
                                x -> x.getBar())
                        .variant("v2").label().getFallback());
    }

    static class TestMethodLabeled {
        void foo() {
        }

        @Label("the Bar")
        void bar() {
        }
    }

    @Test
    public void testTryGetMethodLabel() throws Exception {
        assertEquals(Optional.empty(),
                util.method(TypeToken.of(TestMethodLabeled.class), x -> x.foo())
                        .tryLabel());
        assertEquals(
                Optional.of(new TranslatedString(resolver,
                        TestMethodLabeled.class.getName() + ".bar", "the Bar")),
                util.method(TypeToken.of(TestMethodLabeled.class), x -> x.bar())
                        .tryLabel());
    }

    @MethodsLabeled(variants = { "long" })
    static class TestMethodsLabeled {
        void foo() {
        }

        @Label("the Bar")
        void bar() {
        }

        void overridden() {
        }

        void overridden(int i) {
        }
    }

    static class TestMethodLabelsDerived extends TestMethodsLabeled {

        @Override
        void bar() {
        }

        @Override
        void overridden(int i) {
        }

        @Labeled
        void foo2() {
        }
    }

    @Test
    public void testTryGetMethodLabel2() throws Exception {
        assertTrue(util
                .method(TypeToken.of(TestMethodsLabeled.class), x -> x.foo())
                .tryLabel().isPresent());
        assertEquals(
                Optional.of(
                        new TranslatedString(resolver,
                                TestMethodsLabeled.class.getName()
                                        + ".foo.long",
                                "Foo(long)")),
                util.method(TypeToken.of(TestMethodsLabeled.class),
                        x -> x.foo()).variant("long").tryLabel());
        assertEquals(
                Optional.of(new TranslatedString(resolver,
                        TestMethodsLabeled.class.getName() + ".bar",
                        "the Bar")),
                util.method(TypeToken.of(TestMethodsLabeled.class),
                        x -> x.bar()).tryLabel());
        assertTrue(util
                .method(TypeToken.of(TestMethodsLabeled.class), x -> x.bar())
                .variant("long").tryLabel().isPresent());
    }

    @Test
    public void testTryGetMethodLabelOverridden() throws Exception {
        assertEquals(
                Optional.of(
                        new TranslatedString(resolver,
                                TestMethodsLabeled.class.getName()
                                        + ".overridden",
                                "Overridden")),
                util.method(TypeToken.of(TestMethodsLabeled.class),
                        x -> x.overridden()).tryLabel());
        assertEquals(
                Optional.of(
                        new TranslatedString(resolver,
                                TestMethodsLabeled.class.getName()
                                        + ".overridden~1",
                                "Overridden")),
                util.method(TypeToken.of(TestMethodsLabeled.class),
                        x -> x.overridden(1)).tryLabel());
    }

    @Test
    public void testTryGetMethodLabelInherit() throws Exception {
        assertEquals(
                Optional.of(new TranslatedString(resolver,
                        TestMethodsLabeled.class.getName() + ".foo", "Foo")),
                util.method(TypeToken.of(TestMethodLabelsDerived.class),
                        x -> x.foo()).tryLabel());
        assertEquals(
                Optional.of(
                        new TranslatedString(resolver,
                                TestMethodLabelsDerived.class.getName()
                                        + ".foo2",
                                "Foo2")),
                util.method(TypeToken.of(TestMethodLabelsDerived.class),
                        x -> x.foo2()).tryLabel());
        assertEquals(
                Optional.of(new TranslatedString(resolver,
                        TestMethodsLabeled.class.getName() + ".bar",
                        "the Bar")),
                util.method(TypeToken.of(TestMethodLabelsDerived.class),
                        x -> x.bar()).tryLabel());
        assertEquals(
                Optional.of(
                        new TranslatedString(resolver,
                                TestMethodsLabeled.class.getName()
                                        + ".overridden~1",
                                "Overridden")),
                util.method(TypeToken.of(TestMethodLabelsDerived.class),
                        x -> x.overridden(1)).tryLabel());
    }

    interface TestMethodParametersLabeled {
        @ParametersLabeled
        void allLabeled(String a, @Label("foo") int b);

        void firstLabeled(@Labeled String a, int b);

        void firstHasLabel(@Label("Hello") String a, int b);

        void labelWithVariant(@Label(value = "Hello", variant = "bar") String a,
                int b);
    }

    <T> String parameterId(Class<T> cls, Consumer<T> accessor,
            String parameter) {
        Method method = MethodInvocationRecorder
                .getLastInvocation(cls, accessor).getMethod();
        return method.getDeclaringClass().getName() + "." + method.getName()
                + "." + parameter;
    }

    @Test
    public void testParametersLabeled() {
        assertEquals(new TranslatedString(resolver,
                parameterId(TestMethodParametersLabeled.class,
                        x -> x.allLabeled(null, 0), "a"),
                "A"), util
                        .method(TestMethodParametersLabeled.class,
                                x -> x.allLabeled(null, 0))
                        .parameter("a").label());
        assertEquals(new TranslatedString(resolver,
                parameterId(TestMethodParametersLabeled.class,
                        x -> x.allLabeled(null, 0), "b"),
                "foo"), util
                        .method(TestMethodParametersLabeled.class,
                                x -> x.allLabeled(null, 0))
                        .parameter("b").label());
    }

    @Test
    public void testParameterLabeled() {
        assertEquals(new TranslatedString(resolver,
                parameterId(TestMethodParametersLabeled.class,
                        x -> x.firstLabeled(null, 0), "a"),
                "A"), util
                        .method(TestMethodParametersLabeled.class,
                                x -> x.firstLabeled(null, 0))
                        .parameter("a").label());

        assertFalse(util
                .method(TestMethodParametersLabeled.class,
                        x -> x.firstLabeled(null, 0))
                .parameter("b").tryLabel().isPresent());
    }

    @Test
    public void testParameterLabel() {
        assertEquals(new TranslatedString(resolver,
                parameterId(TestMethodParametersLabeled.class,
                        x -> x.firstHasLabel(null, 0), "a"),
                "Hello"), util
                        .method(TestMethodParametersLabeled.class,
                                x -> x.firstHasLabel(null, 0))
                        .parameter("a").label());

        assertFalse(util
                .method(TestMethodParametersLabeled.class,
                        x -> x.firstHasLabel(null, 0))
                .parameter("b").tryLabel().isPresent());

        assertEquals(new TranslatedString(resolver,
                parameterId(TestMethodParametersLabeled.class,
                        x -> x.labelWithVariant(null, 0), "a") + ".bar",
                "Hello"), util
                        .method(TestMethodParametersLabeled.class,
                                x -> x.labelWithVariant(null, 0))
                        .parameter("a").variant("bar").label());

        assertFalse(util
                .method(TestMethodParametersLabeled.class,
                        x -> x.labelWithVariant(null, 0))
                .parameter("a").tryLabel().isPresent());
    }

    @Test
    public void testGetMethodParameterLabelsOf() throws Exception {
        assertEquals(5, util
                .getMethodParameterLabelsOf(TestMethodParametersLabeled.class)
                .size());
    }
}
