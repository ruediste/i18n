package com.github.ruediste1.i18n.messageFormat;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.PatternFormatter.PluralNode;
import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParser;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.ibm.icu.text.NumberFormat;

/**
 *
 * <blockquote>
 *
 * <pre>
 * pluralStyle = (selector '{' subPattern() '}')+
 * selector = explicitValue | keyword
 * explicitValue = '=' number
 * keyword = [^[[:Pattern_Syntax:][:Pattern_White_Space:]]]+
 * subPattern: normal pattern format, # are replaced
 * </pre>
 *
 * </blockquote>
 */
public class PluralParser extends DefaultParser implements FormatTypeParser {

	public static final class HashNode implements Node {
		private java.lang.String argumentName;

		public HashNode(String argumentName) {
			this.argumentName = argumentName;
		}

		@Override
		public String format(FormattingContext ctx) {
			return NumberFormat.getNumberInstance(ctx.locale).format(
					ctx.arguments.get(argumentName));
		}

		@Override
		public Set<String> argumentNames() {
			return Collections.singleton(argumentName);
		}
	}

	private PatternParser patternParser;

	public PluralParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	@Override
	public PluralNode style(String argumentName) {
		PluralNode result = new PluralNode(argumentName);
		String(",");
		patternParser.whiteSpace();
		OneOrMore(() -> {
			String selector = selector();
			String("{");
			Node node = subPattern(argumentName);
			String("}");
			patternParser.whiteSpace();
			result.addRule(selector, node);
		});
		return result;
	}

	String selector() {
		String result = Optional(() -> {
			String("=");
			patternParser.whiteSpace();
			return "=";
		}).orElse("");
		result += OneOrMoreChars(Character::isLetterOrDigit, "argument name");
		patternParser.whiteSpace();
		return result;
	}

	Node subPattern(String argumentName) {
		Set<Integer> stopChars = new HashSet<>();
		stopChars.add((int) '#');
		stopChars.add((int) '}');

		return patternParser.pattern(() -> {
			String("#");
			return new HashNode(argumentName);
		}, stopChars);
	}

	public void setPatternParser(PatternParser patternParser) {
		this.patternParser = patternParser;
	}

}