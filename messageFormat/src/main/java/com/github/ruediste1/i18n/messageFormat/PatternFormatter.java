package com.github.ruediste1.i18n.messageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.lambdaPegParser.ParserFactory;
import com.github.ruediste1.lambdaPegParser.Tracer;

public class PatternFormatter {

	public static boolean trace;

	private Map<String, Class<? extends FormatTypeParser>> handlers;

	public PatternFormatter() {
		this(defaultHandlers());
	}

	public PatternFormatter(
			Map<String, Class<? extends FormatTypeParser>> handlers) {
		this.handlers = handlers;
	}

	public String format(String pattern, Map<String, Object> arguments,
			Locale locale) {
		DefaultParsingContext ctx = new DefaultParsingContext(pattern);
		if (trace) {
			new Tracer(ctx, System.out);
		}
		PatternParser parser = ParserFactory.create(PatternParser.class, ctx);
		handlers.entrySet()
				.stream()
				.forEach(
						e -> {
							FormatTypeParser tmp = ParserFactory.create(
									e.getValue(), FormatTypeParser.class, ctx);
							tmp.setPatternParser(parser);
							parser.getFormatParsers().put(e.getKey(), tmp);
						});
		Node node = parser.fullPattern();
		FormattingContext fCtx = new FormattingContext();
		fCtx.locale = locale;
		fCtx.arguments = arguments;
		return node.format(fCtx);
	}

	public static Map<String, Class<? extends FormatTypeParser>> defaultHandlers() {
		HashMap<String, Class<? extends FormatTypeParser>> result = new HashMap<>();
		result.put("plural", PluralParser.class);
		result.put("number", NumberParser.class);
		result.put("date", DateParser.class);
		result.put("time", TimeParser.class);
		result.put("dateTimePattern", DateTimePatternParser.class);
		result.put("dateTime", DateTimeParser.class);
		return result;
	}
}
