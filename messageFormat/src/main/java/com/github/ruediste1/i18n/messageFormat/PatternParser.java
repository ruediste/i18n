package com.github.ruediste1.i18n.messageFormat;

import static java.util.stream.Collectors.joining;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import com.github.ruediste1.i18n.messageFormat.ast.ArgumentNode;
import com.github.ruediste1.i18n.messageFormat.ast.LiteralNode;
import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.i18n.messageFormat.ast.SequenceNode;
import com.github.ruediste1.lambdaPegParser.DefaultParser;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public class PatternParser extends DefaultParser {

	private final Map<String, FormatTypeParser> formatParsers = new HashMap<>();

	public PatternParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	// @formatter:off
	
	public Node fullPattern(){
		Node result = pattern();
		EOI();
		return result;
	}
	public Node pattern() {
		return pattern(null, Collections.emptySet());
	}

	public Node pattern(Supplier<Node> additionalChoice,
			Set<Integer> additionalStopChars) {
		return new SequenceNode(ZeroOrMore(() -> FirstOf(
				additionalChoice, 
				() -> placeHolder(),
				() -> literal(additionalStopChars))));
	}

	public LiteralNode literal(Set<Integer> additionalStopChars) {
		return new LiteralNode(OneOrMore(
				() -> FirstOf(
						() -> {
							String("$");
							return AnyChar();
						},
						() -> Char(
								cp -> cp != '{'
										&& !additionalStopChars.contains(cp),
								"literal"))).stream().collect(joining()));
	}

	public Node placeHolder() {
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

		Node result;
		if (type == null) {
			result = new ArgumentNode(argumentName) {
				@Override
				public String format(FormattingContext ctx) {
					return Objects.toString(ctx.arguments.get(argumentName));
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
