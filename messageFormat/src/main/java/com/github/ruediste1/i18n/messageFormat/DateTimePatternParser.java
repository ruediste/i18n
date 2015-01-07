package com.github.ruediste1.i18n.messageFormat;

import java.time.format.DateTimeFormatter;

import com.github.ruediste1.i18n.messageFormat.ast.DateTimeNode;
import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class DateTimePatternParser extends FormatTypeParserBase {

	public DateTimePatternParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	@Override
	public Node style(java.lang.String argumentName) {
		String(",");
		patternParser.whiteSpace();
		String pattern = subFormatPattern();
		patternParser.whiteSpace();
		DateTimeFormatter formatter;
		try {
			formatter = DateTimeFormatter.ofPattern(pattern);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Unable to parse pattern " + pattern, e);
		}
		return new DateTimeNode(argumentName, formatter);
	}
}
