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
import java.util.Collection;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste1.i18n.lString.TranslatedString;
import com.github.ruediste1.i18n.lString.TranslatedStringResolver;

public class LabelUtilTest {

    LabelUtil util;
    TranslatedStringResolver resolver;

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

    private static class TestTypeLabeledInherited extends
            TestImplicitelyLabeled {

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
        MEMBER_A, @Label("myB")
        @Label(value = "myFoo", variant = "foo")
        @Short("memberShort")
        MEMBER_B
    }

    private enum TestEnumUnlabeled {
        MEMBER_A
    }

    @Before
    public void setup() {
        util = new LabelUtil(null);
    }

    @Test
    public void testGetPropertyLabelsDirect() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.fooBar", "Foo Bar"),
                util.getPropertyLabel(TestClass.class, "fooBar"));
    }

    @Test
    public void testGetPropertyLabelsLabelAnnotation() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled", "myLabel"),
                util.getPropertyLabel(TestClass.class, "labeled"));
    }

    @Test
    public void testGetPropertyLabelsVariant() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled.foo", "myLabelFoo"),
                util.getPropertyLabel(TestClass.class, "labeled", "foo"));
    }

    @Test
    public void testGetPropertyLabelsVariantShort() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled.short", "propertyShort"),
                util.getPropertyLabel(TestClass.class, "labeled", "short"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelsInexistantVariant() throws Exception {
        util.getPropertyLabel(TestClass.class, "labeled", "bar");
    }

    @Test
    public void testGetPropertyLabeledOnly() throws Exception {
        assertEquals(
                new TranslatedString(resolver,
                        TestClassUnlabeled.class.getName() + ".foo", "Foo"),
                util.getPropertyLabel(TestClassUnlabeled.class, "foo"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelsUnlabeled() throws Exception {
        assertNull(util.getPropertyLabel(TestClassUnlabeled.class, "fooBar"));
    }

    @Test
    public void testGetPropertyLabelsUnicode() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.漢字", "漢字"),
                util.getPropertyLabel(TestClass.class, "漢字"));
    }

    @Test
    public void testGetPropertyLabelsDerived() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.fooBar", "Foo Bar"),
                util.getPropertyLabel(TestClassDerived.class, "fooBar"));
    }

    @Test
    public void testGetTypeLabel() throws Exception {
        assertEquals(new TranslatedString(resolver, TestClass.class.getName(),
                "Test Class"), util.getTypeLabel(TestClass.class));
    }

    @Test
    public void testGetTypeLabelImplicitely() throws Exception {
        assertEquals(new TranslatedString(resolver,
                TestImplicitelyLabeled.class.getName() + ".short", "myShort"),
                util.getTypeLabel(TestImplicitelyLabeled.class, "short"));
    }

    @Test
    public void testGetTypeLabelInherited() throws Exception {
        assertEquals(new TranslatedString(resolver,
                TestImplicitelyLabeled.class.getName() + ".short", "myShort"),
                util.getTypeLabel(TestTypeLabeledInherited.class, "short"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetTypeLabelUnlabeled() throws Exception {
        util.getTypeLabel(TestClassUnlabeled.class);
    }

    @Test(expected = RuntimeException.class)
    public void testGetTypeLabelMissingVariant() throws Exception {
        util.getTypeLabel(TestClass.class, "bar");
    }

    @Test
    public void testGetTypeLabelVariant() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.foo", "myFoo"),
                util.getTypeLabel(TestClass.class, "foo"));
    }

    @Test
    public void testGetTypeLabelVariantShort() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.short", "typeShort"),
                util.getTypeLabel(TestClass.class, "short"));
    }

    @Test
    public void testGetTypeLabelVariantNoLabel() throws Exception {
        assertEquals(new TranslatedString(resolver, TestClass.class.getName()
                + ".foo1", "Test Class"),
                util.getTypeLabel(TestClass.class, "foo1"));
    }

    @Test
    public void testGetEnumMemberLabel() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestEnum.MEMBER_A", "Member A"),
                util.getEnumMemberLabel(TestEnum.MEMBER_A));
    }

    @Test
    public void testGetEnumMemberLabelLabelAnnotation() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestEnum.MEMBER_B", "myB"),
                util.getEnumMemberLabel(TestEnum.MEMBER_B));
    }

    @Test(expected = RuntimeException.class)
    public void testGetEnumMemberLabelUnlabeled() throws Exception {
        util.getEnumMemberLabel(TestEnumUnlabeled.MEMBER_A);
    }

    @Test(expected = RuntimeException.class)
    public void testGetEnumMemberLabelInexistantVariant() throws Exception {
        util.getEnumMemberLabel(TestEnum.MEMBER_A, "bar");
    }

    @Test
    public void testGetEnumMemberLabelVariant() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestEnum.MEMBER_B.foo", "myFoo"),
                util.getEnumMemberLabel(TestEnum.MEMBER_B, "foo"));
    }

    @Test
    public void testGetEnumMemberLabelVariantShort() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestEnum.MEMBER_B.short", "memberShort"),
                util.getEnumMemberLabel(TestEnum.MEMBER_B, "short"));
    }

    @Test
    public void testGetTypeLabelsOf() throws Exception {
        Collection<TranslatedString> labels = util
                .getTypeLabelsOf(TestClass.class);
        assertEquals(4, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass", "Test Class")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.short", "typeShort")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.foo", "myFoo")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.foo1", "Test Class")));
    }

    @Test
    public void testGetLabelsDefinedOn() throws Exception {
        assertEquals(13, util.getLabelsDefinedOn(TestClass.class).size());
        assertEquals(1, util.getLabelsDefinedOn(TestClassUnlabeled.class)
                .size());
        assertEquals(1, util.getLabelsDefinedOn(TestTypeLabeledInherited.class)
                .size());
        assertEquals(6, util.getLabelsDefinedOn(TestEnum.class).size());
        assertEquals(0, util.getLabelsDefinedOn(TestEnumUnlabeled.class).size());
        assertEquals(1, util.getLabelsDefinedOn(TestMethodLabeled.class).size());
        assertEquals(8, util.getLabelsDefinedOn(TestMethodsLabeled.class)
                .size());
        assertEquals(1, util.getLabelsDefinedOn(TestMethodLabelsDerived.class)
                .size());
    }

    @Test
    public void testGetPropertyLabelssOf() throws Exception {
        Collection<? extends TranslatedString> labels = util
                .getPropertyLabelsOf(TestClass.class);
        assertEquals(9, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.fooBar", "Foo Bar")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.fooBar.foo", "Foo Bar")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestClass.fooBar.short", "Foo Bar")));
    }

    @Test
    public void testGetEnumMemberLabelsOf() throws Exception {
        Collection<? extends TranslatedString> labels = util
                .getEnumMemberLabelsOf(TestEnum.class);
        assertEquals(6, labels.size());
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_A", "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_A.foo", "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_A.short", "Member A")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_B", "myB")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_B.foo", "myFoo")));
        assertTrue(labels.contains(new TranslatedString(resolver, getClass()
                .getName() + "$TestEnum.MEMBER_B.short", "memberShort")));
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
        util.toProperties(util.getLabelsDefinedOn(TestClass.class)).store(
                writer, null);
        String contents = writer.toString();
        assertEquals(
                "\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.foo=myFoo\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar.foo=Foo Bar\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字.short=漢字\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字.foo=漢字\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar.short=Foo Bar\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.foo1=Test Class\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.short=typeShort\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.漢字=漢字\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass=Test Class\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled=myLabel\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled.foo=myLabelFoo\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.labeled.short=propertyShort\n"
                        + "com.github.ruediste1.i18n.label.LabelUtilTest$TestClass.fooBar=Foo Bar\n"
                        + "", contents.substring(contents.indexOf("\n")));
    }

    @Test
    public void testTryGetEnumLabelVariants() throws Exception {
        assertFalse(util.tryGetEnumLabelVariants(TestEnumUnlabeled.class)
                .isPresent());
        assertArrayEquals(new String[] { "foo", "short", "" }, util
                .tryGetEnumLabelVariants(TestEnum.class).get());
    }

    @Test
    public void testPropertiesLabeledDifferent() {
        assertEquals(
                "foo",
                util.getPropertyLabel(TestPropertiesLabeledDifferent.class,
                        x -> x.getFoo(), "v1").getFallback());
        assertEquals(
                "bar",
                util.getPropertyLabel(TestPropertiesLabeledDifferent.class,
                        x -> x.getBar(), "v2").getFallback());
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
                    util.tryGetMethodLabel(TestMethodLabeled.class, x -> x.foo()));
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodLabeled.class.getName() + ".bar", "the Bar")),
                    util.tryGetMethodLabel(TestMethodLabeled.class, x -> x.bar()));
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
            assertTrue(util.tryGetMethodLabel(TestMethodsLabeled.class, x -> x.foo())
                    .isPresent());
            assertEquals(
                    Optional.of(new TranslatedString(resolver,
                            TestMethodsLabeled.class.getName() + ".foo.long",
                            "Foo(long)")), util.getMethodLabel(
                            TestMethodsLabeled.class, x -> x.foo(), "long"));
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodsLabeled.class.getName() + ".bar", "the Bar")),
                    util.tryGetMethodLabel(TestMethodsLabeled.class, x -> x.bar()));
            assertTrue(util.getMethodLabel(TestMethodsLabeled.class, x -> x.bar(),
                    "long").isPresent());
        }

    @Test
        public void testTryGetMethodLabelOverridden() throws Exception {
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodsLabeled.class.getName() + ".overridden",
                    "Overridden")), util.tryGetMethodLabel(TestMethodsLabeled.class,
                    x -> x.overridden()));
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodsLabeled.class.getName() + ".overridden~1",
                    "Overridden")), util.tryGetMethodLabel(TestMethodsLabeled.class,
                    x -> x.overridden(1)));
        }

    @Test
        public void testTryGetMethodLabelInherit() throws Exception {
            assertEquals(
                    Optional.of(new TranslatedString(resolver,
                            TestMethodsLabeled.class.getName() + ".foo", "Foo")),
                    util.tryGetMethodLabel(TestMethodLabelsDerived.class, x -> x.foo()));
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodLabelsDerived.class.getName() + ".foo2", "Foo2")),
                    util.tryGetMethodLabel(TestMethodLabelsDerived.class,
                            x -> x.foo2()));
            assertEquals(
                    Optional.of(new TranslatedString(resolver,
                            TestMethodsLabeled.class.getName() + ".bar", "the Bar")),
                    util.tryGetMethodLabel(TestMethodLabelsDerived.class, x -> x.bar()));
            assertEquals(Optional.of(new TranslatedString(resolver,
                    TestMethodsLabeled.class.getName() + ".overridden~1",
                    "Overridden")), util.tryGetMethodLabel(
                    TestMethodLabelsDerived.class, x -> x.overridden(1)));
        }
}
