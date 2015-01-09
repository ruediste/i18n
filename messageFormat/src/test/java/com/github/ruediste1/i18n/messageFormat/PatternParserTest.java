package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.FormatTypeParserTestBase;

public class PatternParserTest extends FormatTypeParserTestBase {

	@Test
	public void testEscaping() {
		assertEquals("foo { bar",
				format.format("foo ${ bar", map("param", 1), Locale.ENGLISH));
	}

}
