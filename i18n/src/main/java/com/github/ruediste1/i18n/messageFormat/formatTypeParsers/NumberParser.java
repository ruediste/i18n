package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import java.util.Locale;
import java.util.function.Function;

import com.github.ruediste.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.i18n.messageFormat.ast.FormatNode;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;
import com.ibm.icu.text.DecimalFormat;
import com.ibm.icu.text.DecimalFormatSymbols;
import com.ibm.icu.text.NumberFormat;

public class NumberParser extends FormatTypeParser {

    public NumberParser(DefaultParsingContext ctx) {
        super(ctx);
    }

    @Override
    public PatternNode style(java.lang.String argumentName) {
        Function<Locale, NumberFormat> formatFactory = Opt(() -> {
            Str(",");
            whiteSpace();
            Function<Locale, NumberFormat> result = this
                    .<Function<Locale, NumberFormat>> FirstOf(() -> {
                Str("integer");
                return l -> NumberFormat.getIntegerInstance(l);
            } , () -> {
                Str("currency");
                return l -> NumberFormat.getCurrencyInstance(l);
            } , () -> {
                Str("percent");
                return l -> NumberFormat.getPercentInstance(l);
            } , () -> {
                String pattern = subFormatPattern();
                return l -> new DecimalFormat(pattern,
                        DecimalFormatSymbols.getInstance(l));
            });
            whiteSpace();
            return result;
        }).orElse(l -> NumberFormat.getInstance(l));
        return new FormatNode(argumentName, formatFactory);
    }

}