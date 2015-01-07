package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class PatternFormatterTest extends FormatTypeParserTestBase {

	@Test
	public void testEscaping() {
		String template = "foo ${ bar";
		assertEquals("foo { bar",
				format.format(template, map("param", 1), Locale.ENGLISH));
	}

}
