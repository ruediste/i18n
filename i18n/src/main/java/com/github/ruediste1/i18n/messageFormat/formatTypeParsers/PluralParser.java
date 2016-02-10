package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static java.util.stream.Collectors.toSet;

import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.ibm.icu.text.NumberFormat;
import com.ibm.icu.text.PluralFormat;
import com.ibm.icu.text.PluralRules;

/**
 * Format type parser for plural rules. The format closely follows
 * {@link PluralFormat}. See there for details.
 * <p>
 * <b>Samples:</b>
 * 
 * <pre>
 * 
 * format.format("{count, plural, one {There is one Document} more {There are # Documents}}", args, Locale.ENGLISH));
 * =&lt; There is one Document
 * =&lt; There are 5 Documents
 * </pre>
 * 
 * 
 * <p>
 * <b>Grammar:</b>
 * 
 * <pre>
 * style = , (selector '{' subPattern() '}')+
 * selector = explicitValue | keyword
 * explicitValue = '=' number
 * keyword = [^[[:Pattern_Syntax:][:Pattern_White_Space:]]]+
 * subPattern: normal pattern format, # are replaced
 * </pre>
 * 
 *
 */
public class PluralParser extends FormatTypeParserBase {

    public PluralParser(DefaultParsingContext ctx) {
        super(ctx);
    }

    /**
     * <pre> style = "," (selector "{" subPattern "}")+ <pre>
     */
    @Override
    public PluralNode style(String argumentName) {
        PluralNode result = new PluralNode(argumentName);
        String(",");
        whiteSpace();
        OneOrMore(() -> {
            String selector = selector();
            String("{");
            PatternNode node = subPattern(argumentName);
            String("}");
            whiteSpace();
            result.addRule(selector, node);
        });
        return result;
    }

    /**
     * <pre>
     * selector = "="? letterOrDigit+
     * </pre>
     */
    public String selector() {
        String result = Optional(() -> {
            String("=");
            whiteSpace();
            return "=";
        }).orElse("");
        result += OneOrMoreChars(Character::isLetterOrDigit, "argument name");
        whiteSpace();
        return result;
    }

    /**
     * Match a normal full message pattern, with the additional choice of "#",
     * which is equivalent to the argument of the plural choice.
     */
    public PatternNode subPattern(String argumentName) {

        return patternParser.pattern(() -> {
            String("#");
            return new HashNode(argumentName);
        });
    }

    public static class PluralNode extends PatternNode {

        public Map<Double, PatternNode> explicitRules = new HashMap<>();
        public Map<String, PatternNode> keywordRules = new HashMap<>();
        private String argumentName;

        public PluralNode(String argumentName) {
            this.argumentName = argumentName;

        }

        @Override
        public String format(FormattingContext ctx) {
            Object argument = ctx.getArgument(argumentName);
            if (!(argument instanceof Number)) {
                throw new IllegalArgumentException(
                        "'" + argument + "' is not a Number");
            }
            Number numberObject = (Number) argument;
            double number = numberObject.doubleValue();

            // try explicit rules
            {
                PatternNode node = explicitRules.get(number);
                if (node != null) {
                    return node.format(ctx);
                }
            }

            // try keyword rules
            PluralRules pluralRules = getPluralRules(ctx.getLocale());
            String keyword = pluralRules.select(number);
            PatternNode node = keywordRules.get(keyword);
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

        /**
         * Return the {@link PluralRules} to be used for a certain locale.
         * Override to use different rules.
         */
        protected PluralRules getPluralRules(Locale locale) {
            return PluralRules.forLocale(locale);
        }

        @Override
        public Set<String> argumentNames() {
            return Stream
                    .concat(explicitRules.values().stream(),
                            keywordRules.values().stream())
                    .flatMap(node -> node.argumentNames().stream())
                    .collect(toSet());
        }

        public void addRule(String selector, PatternNode node) {
            if (selector.startsWith("=")) {
                explicitRules.put(Double.valueOf(selector.substring(1)), node);
            } else {
                keywordRules.put(selector, node);
            }
        }
    }

    public static class HashNode extends PatternNode {
        private java.lang.String argumentName;

        public HashNode(String argumentName) {
            this.argumentName = argumentName;
        }

        @Override
        public String format(FormattingContext ctx) {
            return NumberFormat.getNumberInstance(ctx.getLocale())
                    .format(ctx.getArgument(argumentName));
        }

        @Override
        public Set<String> argumentNames() {
            return Collections.singleton(argumentName);
        }
    }

}