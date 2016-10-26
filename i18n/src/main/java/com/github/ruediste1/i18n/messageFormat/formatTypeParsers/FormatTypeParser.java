package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import static java.util.stream.Collectors.joining;

import com.github.ruediste.lambdaPegParser.DefaultParser;
import com.github.ruediste.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.i18n.messageFormat.PatternParser;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;

public abstract class FormatTypeParser extends DefaultParser {

    public FormatTypeParser(DefaultParsingContext ctx) {
        super(ctx);
    }

    /**
     * The style part of a placeholder, including the initial comma.
     * 
     * @param argumentName
     *            name of the argument
     */
    abstract public PatternNode style(String argumentName);

    public PatternParser patternParser;

    public void setPatternParser(PatternParser patternParser) {
        this.patternParser = patternParser;

    }

    /**
     * <pre>
     * ( "''" | quoted | ./"}' ) +
     * </pre>
     */
    public String subFormatPattern() {
        return OneOrMore(() -> FirstOf(() -> Str("''"), () -> quotedSubFormatPattern(), () -> NoneOf("}"))).stream()
                .collect(joining());
    }

    /**
     * <pre>
     * "'" ("''" | ./' )* "'"
     * </pre>
     */
    public String quotedSubFormatPattern() {
        return Str("'") + this.<String> ZeroOrMore(() -> FirstOf(() -> Str("''"), () -> NoneOf("'"))).stream()
                .collect(joining()) + Opt(() -> Str("'")).orElse("");
    }

    public void whiteSpace() {
        patternParser.whiteSpace();
    }
}
