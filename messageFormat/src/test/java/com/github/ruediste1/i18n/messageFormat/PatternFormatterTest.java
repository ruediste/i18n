package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

public class PatternFormatterTest {
	PatternFormatter format;

	@Before
	public void setup() {
		format = new PatternFormatter(PatternFormatter.defaultHandlers());
	}

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

	@Test
	public void testEscaping() {
		String template = "foo ${ bar";
		assertEquals("foo { bar",
				format.format(template, map("param", 1), Locale.ENGLISH));
	}

	Map<String, Object> map(String key, Object value) {
		Map<String, Object> result = new HashMap<>();
		result.put(key, value);
		return result;
	}
}
