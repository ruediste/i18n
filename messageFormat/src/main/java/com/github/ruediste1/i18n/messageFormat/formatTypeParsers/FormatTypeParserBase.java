package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static java.util.stream.Collectors.joining;

import com.github.ruediste1.i18n.messageFormat.PatternParser;
import com.github.ruediste1.lambdaPegParser.DefaultParser;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public abstract class FormatTypeParserBase extends DefaultParser implements
		FormatTypeParser {

	public FormatTypeParserBase(DefaultParsingContext ctx) {
		super(ctx);
	}

	public PatternParser patternParser;

	@Override
	public void setPatternParser(PatternParser patternParser) {
		this.patternParser = patternParser;

	}

	/**
	 * <pre>
	 * ( "''" | quoted | ./"}' ) +
	 * </pre>
	 */
	public String subFormatPattern() {
		return OneOrMore(
				() -> FirstOf(() -> String("''"),
						() -> quotedSubFormatPattern(), () -> NoneOf("}")))
				.stream().collect(joining());
	}

	/**
	 * <pre>
	 * "'" ("''" | ./' )* "'"
	 * </pre>
	 */
	public String quotedSubFormatPattern() {
		return String("'")
				+ this.<String> ZeroOrMore(
						() -> FirstOf(() -> String("''"), () -> NoneOf("'")))
						.stream().collect(joining())
				+ Optional(() -> String("'")).orElse("");
	}
}
