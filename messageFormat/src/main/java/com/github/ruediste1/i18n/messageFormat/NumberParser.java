package com.github.ruediste1.i18n.messageFormat;

import java.util.Locale;
import java.util.function.Function;

import com.github.ruediste1.i18n.messageFormat.ast.Node;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberFormat;

public class NumberParser extends FormatTypeParserBase {

	public NumberParser(DefaultParsingContext ctx) {
		super(ctx);
	}

	@Override
	public Node style(java.lang.String argumentName) {
		Function<Locale, NumberFormat> formatFactory = Optional(
				() -> {
					String(",");
					patternParser.whiteSpace();
					Function<Locale, NumberFormat> result = FirstOf(() -> {
						String("integer");
						return l -> NumberFormat.getIntegerInstance(l);
					}, () -> {
						String("currency");
						return l -> NumberFormat.getCurrencyInstance(l);
					}, () -> {
						String("percent");
						return l -> NumberFormat.getPercentInstance(l);
					}, () -> {
						String pattern = subFormatPattern();
						return l -> new DecimalFormat(pattern,
								DecimalFormatSymbols.getInstance(l));
					});
					patternParser.whiteSpace();
					return result;
				}).orElse(l -> NumberFormat.getInstance(l));
		return new FormatNode(argumentName, formatFactory);
	}

}