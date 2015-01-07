package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class PluralParserTest extends FormatTypeParserTestBase {

	@Test
	public void testPlural() {
		String template = "there {param, plural, one {is one onion} =2 {are two onions} other {are # on$#ions}}";
		assertEquals("there is one onion",
				format.format(template, map("param", 1), Locale.ENGLISH));
		assertEquals("there are two onions",
				format.format(template, map("param", 2), Locale.ENGLISH));
		assertEquals("there are 3 on#ions",
				format.format(template, map("param", 3), Locale.ENGLISH));
	}
}
