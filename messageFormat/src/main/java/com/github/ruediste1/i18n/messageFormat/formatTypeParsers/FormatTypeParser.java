package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import com.github.ruediste1.i18n.messageFormat.PatternParser;
import com.github.ruediste1.i18n.messageFormat.ast.PatternNode;

/**
 * Interface for parsers parsing a placeholder type
 */
public interface FormatTypeParser {
	/**
	 * The style part of a placeholder, including the initial comma.
	 * 
	 * @param argumentName
	 *            name of the argument
	 */
	PatternNode style(String argumentName);

	void setPatternParser(PatternParser patternParser);
}
