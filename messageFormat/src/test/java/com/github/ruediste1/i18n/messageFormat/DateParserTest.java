package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.Test;

public class DateParserTest extends FormatTypeParserTestBase {

	@Test
	public void none() {
		assertEquals(
				"Jan 1, 2014",
				format.format(
						"{param, date}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
	}

	@Test
	public void predefined() {
		assertEquals(
				"2014-01-01Z",
				format.format(
						"{param, date, iso}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
		assertEquals(
				"2014-01-01",
				format.format(
						"{param, date, isoLocal}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
	}

}
