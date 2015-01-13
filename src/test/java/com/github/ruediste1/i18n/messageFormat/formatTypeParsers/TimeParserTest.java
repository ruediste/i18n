package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static org.junit.Assert.assertEquals;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;

import org.junit.Test;

public class TimeParserTest extends FormatTypeParserTestBase {

	@Test
	public void none() {
		assertEquals(
				"10:01:00 AM",
				format.format(
						"{param, time}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
	}

	@Test
	public void predefined() {
		assertEquals(
				"10:01:00Z",
				format.format(
						"{param, time, iso}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
		assertEquals(
				"10:01:00",
				format.format(
						"{param, time, isoLocal}",
						map("param", ZonedDateTime.of(
								LocalDateTime.of(2014, 1, 1, 10, 01),
								ZoneId.of("Z"))), Locale.ENGLISH));
	}

}
