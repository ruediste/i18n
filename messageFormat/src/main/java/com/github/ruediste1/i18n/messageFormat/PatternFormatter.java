package com.github.ruediste1.i18n.messageFormat;

import static java.util.stream.Collectors.toSet;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.lambdaPegParser.ParserFactory;
import com.ibm.icu.text.PluralRules;

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

	public static class PluralHandler implements FormatTypeHandler {

		@Override
		public FormatTypeParser createParser(PatternParser parser,
				DefaultParsingContext ctx) {
			PluralParser result = ParserFactory.create(PluralParser.class, ctx);
			result.setPatternParser(parser);
			return result;
		}

	}
}
