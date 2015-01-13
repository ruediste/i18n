package com.github.ruediste1.i18n.messageFormat;

import static java.util.stream.Collectors.joining;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

import com.github.ruediste1.i18n.messageFormat.ast.ArgumentNode;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;
import com.github.ruediste1.i18n.messageFormat.ast.SequenceNode;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.FormatTypeParser;
import com.github.ruediste1.lambdaPegParser.DefaultParser;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class PatternParser extends DefaultParser {

	private final Map<String, FormatTypeParser> formatParsers = new HashMap<>();

	public PatternParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	// @formatter:off
	
	/**
	 * <pre>
	 * fullPattern = pattern EOI
	 * </pre>
	 */
	public PatternNode fullPattern(){
		PatternNode result = pattern();
		EOI();
		return result;
	}

	/**
	 * <pre>
	 * pattern() = pattern(null)
	 * </pre>
	 */
	public PatternNode pattern() {
		return pattern(null);
	}

	/**
	 * <pre>
	 * pattern = (additionalChoice | placeHolder | literalChar)*
	 * </pre>
	 */
	public PatternNode pattern(Supplier<Object> additionalChoice) {
		return  SequenceNode.fromObjects(this.ZeroOrMore(() -> FirstOf(
				additionalChoice, 
				() -> placeHolder(),
				() -> literalChar())));
	}

	/**
	 * <pre>
	 * literalChar = '$'. | ./'}'
	 * </pre>
	 */
	public String literalChar() {
		return  FirstOf(
						() -> {
							String("$");
							return AnyChar();
						},
						() -> NoneOf("}"));
	}
	
	/**
	 * <pre>
	 * placeHolder = '{' identifier (',' identifier style)?
	 * </pre>
	 */
	public PatternNode placeHolder() {
		String("{");
		whiteSpace();
		String argumentName = identifier();
		whiteSpace();
		String type = Optional(() -> {
			String(",");
			whiteSpace();
			String result = identifier();
			whiteSpace();
			return result;
		}).orElse(null);

		PatternNode result;
		if (type == null) {
			result = new ArgumentNode(argumentName) {
				@Override
				public String format(FormattingContext ctx) {
					return Objects.toString(ctx.getArgument(argumentName));
				}
			};
		} else {
			FormatTypeParser parser = formatParsers.get(type);
			if (parser == null) {
				throw new RuntimeException("Unknown format type <"+type+"> known types: "+ formatParsers.keySet().stream().collect(joining(", ")));
			}
			result = parser.style(argumentName);
		}
		String("}");
		return result;
	}

	public String identifier() {
		String result = Char(Character::isJavaIdentifierStart, "identifier")
				+ ZeroOrMoreChars(Character::isJavaIdentifierPart, "identifier");
		whiteSpace();
		return result;
	}

	public void whiteSpace() {
		ZeroOrMoreChars(Character::isWhitespace, "white space");
	}

	public Map<String, FormatTypeParser> getFormatParsers() {
		return formatParsers;
	}

}
