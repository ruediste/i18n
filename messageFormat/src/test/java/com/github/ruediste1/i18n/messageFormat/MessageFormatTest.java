package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.FormatTypeParserTestBase;

public class MessageFormatTest extends FormatTypeParserTestBase {

	@Test
	public void testEscaping() {
		String template = "foo ${ bar";
		assertEquals("foo { bar",
				format.format(template, map("param", 1), Locale.ENGLISH));
	}

}
