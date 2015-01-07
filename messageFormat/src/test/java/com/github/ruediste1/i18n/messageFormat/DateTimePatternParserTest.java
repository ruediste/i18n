package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.Test;

public class DateTimePatternParserTest extends FormatTypeParserTestBase {

	@Test
	public void pattern() {
		assertEquals("01 01 2014 (hello AM) }  foo", format.format(
				"{param, dateTimePattern, dd MM yyyy ('hello' a) '}' } foo",
				map("param", ZonedDateTime.of(
						LocalDateTime.of(2014, 1, 1, 10, 01), ZoneId.of("Z"))),
				Locale.ENGLISH));
	}
}
