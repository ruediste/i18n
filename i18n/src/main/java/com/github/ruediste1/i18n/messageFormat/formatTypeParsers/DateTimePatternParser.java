package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import java.time.format.DateTimeFormatter;

import com.github.ruediste1.i18n.messageFormat.ast.DateTimeNode;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class DateTimePatternParser extends FormatTypeParserBase {

	public DateTimePatternParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	@Override
	public PatternNode style(java.lang.String argumentName) {
		String(",");
		whiteSpace();
		String pattern = subFormatPattern();
		whiteSpace();
		DateTimeFormatter formatter;
		try {
			formatter = DateTimeFormatter.ofPattern(pattern);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("Unable to parse pattern " + pattern, e);
		}
		return new DateTimeNode(argumentName, formatter);
	}
}
