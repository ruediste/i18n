package com.github.ruediste1.i18n.messageFormat;

import static java.util.stream.Collectors.toMap;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.BiFunction;

import com.github.ruediste1.i18n.lString.LString;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.DateParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.DateTimeParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.DateTimePatternParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.FormatTypeParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.NumberParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.PluralParser;
import com.github.ruediste1.i18n.messageFormat.formatTypeParsers.TimeParser;
import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;
import com.github.ruediste1.lambdaPegParser.ParserFactory;
import com.github.ruediste1.lambdaPegParser.Tracer;

/**
 * Takes a set of objects, formats them, then inserts the formatted strings into
 * a pattern at the appropriate places. By localizing the pattern strings,
 * complex messages in different languages can easily be created.
 * 
 * <p>
 * <b>Samples</b> <br>
 * 
 * <pre>
 * format.format("The offer has been created on {param, date}.", args, Locale.ENGLISH));
 * =&gt; The record has been created on an 1, 2014.
 * format.format("Die offerte wurde am {param, date} erstellt.", args, Locale.GERMAN));
 * =&gt; Die offerte wurde am 01.01.2014 erstellt.
 * format.format("{count, plural, one {There is one Document} more {There are # Documents}}", args, Locale.ENGLISH));
 * =&gt; There is one Document
 * =&gt; There are 5 Documents
 * </pre>
 * 
 * <p>
 * <b>Pattern Syntax</b> <br>
 * 
 * <pre>
 * pattern      = (placeholder | literalChar)*
 * literalChar  = '$' . | . / '}'
 * placeholder  = '{' argumentName ( ',' formatType style )? '}'
 * argumentName = identifier
 * formatType   = identifier
 * </pre>
 * 
 * <p>
 * The set of supported formatTypes is specified upon instantiation. The default
 * set can be retrieved using the {@link #defaultFormatTypeParsers()} method.
 * This map can be modified freely.
 * 
 * <p>
 * An additional extension point is the argument preparation function. If
 * specified, all argument values are passed through the function before further
 * treatment. This is especially useful to transform some unconventional objects
 * into a form known to the library.
 * 
 * The following table shows the default parsers provided. The argument object
 * is noted as {@code arg}
 * <table border=1 >
 * <caption> Supported formats </caption>
 * <tr>
 * <th id="ft" class="TableHeadingColor">FormatType
 * <th id="fs" class="TableHeadingColor">FormatStyle
 * <th id="sc" class="TableHeadingColor">Formatting used
 * </tr>
 * <tr>
 * <td headers="ft"><i>(none)</i>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc">
 * <code>{@link LString#resolve(Locale)} </code> for LStrings, otherwise
 * <code>{@link String#valueOf(Object) String.valueOf(arg)}</code>
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=5><code>number</code>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc">{@link NumberFormat#getInstance(Locale)
 * NumberFormat.getInstance}{@code (getLocale())}
 * </tr>
 * <tr>
 * <td headers="fs"><code>integer</code>
 * <td headers="sc">{@link NumberFormat#getIntegerInstance(Locale)
 * NumberFormat.getIntegerInstance}{@code (getLocale())}
 * </tr>
 * <tr>
 * <td headers="fs"><code>currency</code>
 * <td headers="sc">{@link NumberFormat#getCurrencyInstance(Locale)
 * NumberFormat.getCurrencyInstance}{@code (getLocale())}
 * </tr>
 * <tr>
 * <td headers="fs"><code>percent</code>
 * <td headers="sc">{@link NumberFormat#getPercentInstance(Locale)
 * NumberFormat.getPercentInstance}{@code (getLocale())}
 * </tr>
 * <tr>
 * <td headers="fs"><i>pattern</i>
 * <td headers="sc">{@code new}
 * {@link DecimalFormat#DecimalFormat(String,DecimalFormatSymbols)
 * DecimalFormat}{@code (pattern,}
 * {@link DecimalFormatSymbols#getInstance(Locale)
 * DecimalFormatSymbols.getInstance}{@code (getLocale()))}
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=9><code>date</code>
 * 
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDate}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>short</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDate}{@code (}{@link FormatStyle#SHORT}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>medium</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDate}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>long</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDate}{@code (}{@link FormatStyle#LONG} {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>full</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDate(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDate}{@code (}{@link FormatStyle#FULL} {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>iso</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_DATE}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoLocal</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_LOCAL_DATE}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoOffset</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_OFFSET_DATE}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoWeek</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_WEEK_DATE}
 * 
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=1><code>dateTimePattern</code>
 * 
 * <td headers="fs"><i>pattern</i>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofPattern(String)
 * DateTimeFormatter.ofPattern(pattern)}
 * 
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=8><code>time</code>
 * 
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedTime(FormatStyle)
 * DateTimeFormatter.ofLocalizedTime}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>short</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedTime}{@code (}{@link FormatStyle#SHORT}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>medium</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedTime}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>long</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedTime}{@code (}{@link FormatStyle#LONG} {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>full</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedTime}{@code (}{@link FormatStyle#FULL} {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>iso</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_TIME}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoLocal</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_LOCAL_TIME}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoOffset</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_OFFSET_TIME}
 * 
 * </tr>
 * <tr>
 * <td headers="ft" rowspan=11><code>dateTime</code>
 * <td headers="fs"><i>(none)</i>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDateTime(FormatStyle)
 * DateTimeFormatter.ofLocalizedDateTime}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>short</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDateTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDateTime}{@code (}{@link FormatStyle#SHORT}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>medium</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDateTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDateTime}{@code (}{@link FormatStyle#MEDIUM}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>long</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDateTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDateTime}{@code (}{@link FormatStyle#LONG}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>full</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ofLocalizedDateTime(java.time.format.FormatStyle)
 * DateTimeFormatter.ofLocalizedDateTime}{@code (}{@link FormatStyle#FULL}
 * {@code )}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>iso</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_DATE_TIME}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoLocal</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_LOCAL_DATE_TIME}
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoOffset</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_OFFSET_DATE_TIME}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoZoned</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_ZONED_DATE_TIME}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>rfc1123</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#RFC_1123_DATE_TIME}
 * 
 * </tr>
 * <tr>
 * <td headers="fs"><code>isoInstant</code>
 * <td headers="sc">
 * {@link DateTimeFormatter#ISO_INSTANT}
 * 
 * </tr>
 * <tr>
 * <td headers="ft"><code>plural</code>
 * <td headers="fs"><i>pattern</i>
 * <td headers="sc">see {@link PluralParser}
 * </tr>
 * </table>
 * 
 */
public class MessageFormat {

    public static boolean trace;

    private final Map<String, Class<? extends FormatTypeParser>> formatTypeParsers;
    private final BiFunction<Object, ? super Locale, Object> argumentPreparationFunction;

    public MessageFormat() {
        this(defaultFormatTypeParsers(), (a, b) -> a);
    }

    private MessageFormat(
            Map<String, Class<? extends FormatTypeParser>> formatTypeParsers,
            BiFunction<Object, ? super Locale, Object> argumentPreparationFunction) {
        this.formatTypeParsers = new HashMap<>(formatTypeParsers);
        this.argumentPreparationFunction = argumentPreparationFunction;
    }

    /**
     * Create a new instance with the given format type parsers. The format type
     * is the first argument to the format ( {@code argName,&lt;formatType>,...}
     * .
     */
    public MessageFormat withFormatTypeParsers(
            Map<String, Class<? extends FormatTypeParser>> formatTypeParsers) {
        return new MessageFormat(new HashMap<>(formatTypeParsers),
                argumentPreparationFunction);
    }

    /**
     * Create a new instance with the given argument preparation function.
     */
    public MessageFormat withArgumentPreparationFunction(
            BiFunction<Object, ? super Locale, Object> function) {
        return new MessageFormat(formatTypeParsers, function);
    }

    public String format(String pattern, Map<String, Object> arguments,
            Locale locale) {
        DefaultParsingContext ctx = new DefaultParsingContext(pattern);
        if (trace) {
            new Tracer(ctx, System.out);
        }
        PatternParser parser = ParserFactory.create(PatternParser.class, ctx);
        formatTypeParsers
                .entrySet()
                .stream()
                .forEach(
                        e -> {
                            FormatTypeParser tmp = ParserFactory.create(
                                    e.getValue(), FormatTypeParser.class, ctx);
                            tmp.setPatternParser(parser);
                            parser.getFormatParsers().put(e.getKey(), tmp);
                        });
        PatternNode node = parser.fullPattern();
        FormattingContext fCtx = new FormattingContext(locale, arguments
                .entrySet()
                .stream()
                .collect(
                        toMap(e -> e.getKey(),
                                e -> argumentPreparationFunction.apply(
                                        e.getValue(), locale))));
        return node.format(fCtx);
    }

    /**
     * Return a new map containing the default format
     */
    public static Map<String, Class<? extends FormatTypeParser>> defaultFormatTypeParsers() {
        HashMap<String, Class<? extends FormatTypeParser>> result = new HashMap<>();
        result.put("plural", PluralParser.class);
        result.put("number", NumberParser.class);
        result.put("date", DateParser.class);
        result.put("time", TimeParser.class);
        result.put("dateTimePattern", DateTimePatternParser.class);
        result.put("dateTime", DateTimeParser.class);
        return result;
    }
}
