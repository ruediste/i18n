package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static java.util.stream.Collectors.joining;

import java.time.format.DateTimeFormatter;
import java.util.Map;

import com.github.ruediste.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.i18n.messageFormat.ast.DateTimeNode;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;

public abstract class DateTimeParserBase extends FormatTypeParserBase {

	protected abstract Map<java.lang.String, DateTimeFormatter> getFormatters();

	protected abstract DateTimeFormatter getDefaultFormatter();

	public DateTimeParserBase(DefaultParsingContext ctx) {
		super(ctx);
	}

	@Override
	public PatternNode style(String argumentName) {
		return Opt(
				() -> {
					Str(",");
					whiteSpace();

					java.lang.String style = OneOrMoreChars(
							Character::isLetterOrDigit, "style");
					whiteSpace();
					DateTimeFormatter formatter = getFormatters().get(style);
					if (formatter == null) {
						throw new RuntimeException("unknown style "
								+ style
								+ ". Available styles: "
								+ getFormatters().keySet().stream()
										.collect(joining(", ")));
					}
					return new DateTimeNode(argumentName, formatter);
				}).orElseGet(
				() -> new DateTimeNode(argumentName, getDefaultFormatter()));
	}

}