package com.github.ruediste1.i18n.messageFormat;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class DateTimeParser extends DateTimeParserBase {

	public DateTimeParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	private static Map<String, DateTimeFormatter> formatters = new HashMap<>();
	static {
		formatters.put("short",
				DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT));
		formatters.put("medium",
				DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM));
		formatters.put("long",
				DateTimeFormatter.ofLocalizedDateTime(FormatStyle.LONG));
		formatters.put("full",
				DateTimeFormatter.ofLocalizedDateTime(FormatStyle.FULL));
		formatters.put("iso", DateTimeFormatter.ISO_DATE_TIME);
		formatters.put("isoLocal", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
		formatters.put("isoOffset", DateTimeFormatter.ISO_OFFSET_DATE_TIME);
		formatters.put("zoned", DateTimeFormatter.ISO_ZONED_DATE_TIME);
		formatters.put("rfc1123", DateTimeFormatter.RFC_1123_DATE_TIME);
		formatters.put("isoInstant", DateTimeFormatter.ISO_INSTANT);
	}

	@Override
	protected Map<java.lang.String, DateTimeFormatter> getFormatters() {
		return formatters;
	}

	@Override
	protected DateTimeFormatter getDefaultFormatter() {
		return DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM);
	}
}