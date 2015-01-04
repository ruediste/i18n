package com.github.ruediste1.i18n.messageFormat;

import com.github.ruediste1.lambdaPegParser.DefaultParsingContext;

public interface FormatTypeHandler {

	FormatTypeParser createParser(PatternParser parser, DefaultParsingContext ctx);
}
