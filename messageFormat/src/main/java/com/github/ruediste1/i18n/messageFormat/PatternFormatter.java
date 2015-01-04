package com.github.ruediste1.i18n.messageFormat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.lambdaPegParser.ParserFactory;

public class PatternFormatter {

	private Map<String, FormatTypeHandler> handlers;

	public PatternFormatter(Map<String, FormatTypeHandler> handlers) {
		this.handlers = handlers;
	}

	public String format(String pattern, Map<String, Object> arguments,
			Locale locale) {
		DefaultParsingContext ctx = new DefaultParsingContext(pattern);
		PatternParser parser = ParserFactory.create(PatternParser.class, ctx);
		handlers.entrySet()
				.stream()
				.forEach(
						e -> {
							parser.getFormatParsers().put(e.getKey(),
									e.getValue().createParser(parser, ctx));
						});
		Node node = parser.fullPattern();
		FormattingContext fCtx = new FormattingContext();
		fCtx.locale = locale;
		fCtx.arguments = arguments;
		return node.format(fCtx);
	}

	public static Map<String, FormatTypeHandler> defaultHandlers() {
		HashMap<String, FormatTypeHandler> result = new HashMap<>();
		result.put("plural", new PluralHandler());
		return result;
	}
}
