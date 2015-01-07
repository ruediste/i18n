package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static org.junit.Assert.assertEquals;

import java.util.Locale;

import org.junit.Test;

public class NumberParserTest extends FormatTypeParserTestBase {

	@Test
	public void testNoStyle() {
		assertEquals("the 1.1", format.format("the {param, number}",
				map("param", 1.1), Locale.ENGLISH));
	}

	@Test
	public void testInteger() {
		assertEquals("the 1", format.format("the {param, number, integer}",
				map("param", 1.1), Locale.ENGLISH));
	}

	@Test
	public void testCurrency() {
		assertEquals("the $1.10", format.format(
				"the {param, number, currency}", map("param", 1.1), Locale.US));
	}

	@Test
	public void testSubFormat() {
		assertEquals("the 01.10", format.format("the {param, number, 00.00}",
				map("param", 1.1), Locale.US));
	}

	@Test
	public void testSubFormatEscape() {
		assertEquals("the 01.10}", format.format(
				"the {param, number, 00.00'}'}", map("param", 1.1), Locale.US));
		assertEquals(
				"the 01.10}''",
				format.format("the {param, number, 00.00'}'''}",
						map("param", 1.1), Locale.US));
		assertEquals("the 01.10'", format.format(
				"the {param, number, 00.00''}", map("param", 1.1), Locale.US));
	}

	@Test
	public void testPercent() {
		assertEquals("the 110%", format.format("the {param, number, percent}",
				map("param", 1.1), Locale.US));
	}

}
