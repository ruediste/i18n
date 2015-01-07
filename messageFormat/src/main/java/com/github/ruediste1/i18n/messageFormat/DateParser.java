package com.github.ruediste1.i18n.messageFormat;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class DateParser extends DateTimeParserBase {

	public DateParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	static Map<String, DateTimeFormatter> formatters = new HashMap<>();
	static {
		formatters.put("short",
				DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT));
		formatters.put("medium",
				DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM));
		formatters.put("long",
				DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
		formatters.put("full",
				DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
		formatters.put("iso", DateTimeFormatter.ISO_DATE);
		formatters.put("isoLocal", DateTimeFormatter.ISO_LOCAL_DATE);
		formatters.put("isoOffset", DateTimeFormatter.ISO_OFFSET_DATE);
		formatters.put("isoWeek", DateTimeFormatter.ISO_WEEK_DATE);
	}

	@Override
	protected Map<java.lang.String, DateTimeFormatter> getFormatters() {
		return formatters;
	}

	@Override
	protected DateTimeFormatter getDefaultFormatter() {
		return DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);
	}
}