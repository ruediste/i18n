package com.github.ruediste1.i18n.label;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Collection;

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

    private static class TestTypeLabeledInherited {

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

    private static class TestClassUnlabeled {
        @SuppressWarnings("unused")
        public void setFooBar(int x) {
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
    public void testGetPropertyLabelDirect() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.fooBar", "Foo Bar"),
                util.getPropertyLabel(TestClass.class, "fooBar"));
    }

    @Test
    public void testGetPropertyLabelLabelAnnotation() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled", "myLabel"),
                util.getPropertyLabel(TestClass.class, "labeled"));
    }

    @Test
    public void testGetPropertyLabelVariant() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled.foo", "myLabelFoo"),
                util.getPropertyLabel(TestClass.class, "labeled", "foo"));
    }

    @Test
    public void testGetPropertyLabelVariantShort() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.labeled.short", "propertyShort"),
                util.getPropertyLabel(TestClass.class, "labeled", "short"));
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelInexistantVariant() throws Exception {
        util.getPropertyLabel(TestClass.class, "labeled", "bar");
    }

    @Test(expected = RuntimeException.class)
    public void testGetPropertyLabelUnlabeled() throws Exception {
        assertNull(util.getPropertyLabel(TestClassUnlabeled.class, "fooBar"));
    }

    @Test
    public void testGetPropertyLabelUnicode() throws Exception {
        assertEquals(new TranslatedString(resolver, getClass().getName()
                + "$TestClass.漢字", "漢字"),
                util.getPropertyLabel(TestClass.class, "漢字"));
    }

    @Test
    public void testGetPropertyLabelDerived() throws Exception {
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
        assertEquals(0, util.getLabelsDefinedOn(TestClassUnlabeled.class)
                .size());
        assertEquals(6, util.getLabelsDefinedOn(TestEnum.class).size());
        assertEquals(0, util.getLabelsDefinedOn(TestEnumUnlabeled.class).size());
    }

    @Test
    public void testGetPropertyLabelsOf() throws Exception {
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

}
