package com.github.ruediste1.i18n.messageFormat;

import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Map;

import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class TimeParser extends DateTimeParserBase {

	public TimeParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	private static Map<String, DateTimeFormatter> formatters = new HashMap<>();
	static {
		formatters.put("short",
				DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT));
		formatters.put("medium",
				DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM));
		formatters.put("long",
				DateTimeFormatter.ofLocalizedTime(FormatStyle.LONG));
		formatters.put("full",
				DateTimeFormatter.ofLocalizedTime(FormatStyle.FULL));
		formatters.put("iso", DateTimeFormatter.ISO_TIME);
		formatters.put("isoLocal", DateTimeFormatter.ISO_LOCAL_TIME);
		formatters.put("isoOffset", DateTimeFormatter.ISO_OFFSET_TIME);
	}

	@Override
	protected Map<java.lang.String, DateTimeFormatter> getFormatters() {
		return formatters;
	}

	@Override
	protected DateTimeFormatter getDefaultFormatter() {
		return DateTimeFormatter.ofLocalizedTime(FormatStyle.MEDIUM);
	}
}