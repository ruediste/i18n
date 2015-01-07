package com.github.ruediste1.i18n.messageFormat;

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralRules;

/**
 * Grammar:
 * 
 * <pre>
 * style = , (selector '{' subPattern() '}')+
 * selector = explicitValue | keyword
 * explicitValue = '=' number
 * keyword = [^[[:Pattern_Syntax:][:Pattern_White_Space:]]]+
 * subPattern: normal pattern format, # are replaced
 * </pre>
 *
 */
public class PluralParser extends FormatTypeParserBase {

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

	public String selector() {
		String result = Optional(() -> {
			String("=");
			patternParser.whiteSpace();
			return "=";
		}).orElse("");
		result += OneOrMoreChars(Character::isLetterOrDigit, "argument name");
		patternParser.whiteSpace();
		return result;
	}

	public Node subPattern(String argumentName) {
		Set<Integer> stopChars = new HashSet<>();
		stopChars.add((int) '#');
		stopChars.add((int) '}');

		return patternParser.pattern(() -> {
			String("#");
			return new HashNode(argumentName);
		}, stopChars);
	}

	public static class PluralNode implements Node {

		public Map<Double, Node> explicitRules = new HashMap<>();
		public Map<String, Node> keywordRules = new HashMap<>();
		private String argumentName;

		public PluralNode(String argumentName) {
			this.argumentName = argumentName;

		}

		@Override
		public String format(FormattingContext ctx) {
			Object numberArg = ctx.arguments.get(argumentName);
			if (!(numberArg instanceof Number)) {
				throw new IllegalArgumentException("'" + numberArg
						+ "' is not a Number");
			}
			Number numberObject = (Number) numberArg;
			double number = numberObject.doubleValue();

			// try explicit rules
			{
				Node node = explicitRules.get(number);
				if (node != null) {
					return node.format(ctx);
				}
			}

			// try keyword rules
			PluralRules pluralRules = PluralRules.forLocale(ctx.locale);
			String keyword = pluralRules.select(number);
			Node node = keywordRules.get(keyword);
			if (node == null) {
				node = keywordRules.get(PluralRules.KEYWORD_OTHER);
			}
			if (node == null) {
				throw new RuntimeException("Number " + number
						+ " was mapped to keyword <" + keyword
						+ "> which is not defined, neither is <other>.");
			}
			return node.format(ctx);
		}

		@Override
		public Set<String> argumentNames() {
			return Stream
					.concat(explicitRules.values().stream(),
							keywordRules.values().stream())
					.flatMap(node -> node.argumentNames().stream())
					.collect(toSet());
		}

		public void addRule(String selector, Node node) {
			if (selector.startsWith("=")) {
				explicitRules.put(Double.valueOf(selector.substring(1)), node);
			} else {
				keywordRules.put(selector, node);
			}
		}
	}

	public static class HashNode implements Node {
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

}