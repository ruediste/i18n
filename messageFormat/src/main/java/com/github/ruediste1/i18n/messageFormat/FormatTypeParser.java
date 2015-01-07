package com.github.ruediste1.i18n.messageFormat;

import com.github.ruediste1.i18n.messageFormat.ast.Node;

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
	Node style(String argumentName);

	void setPatternParser(PatternParser patternParser);
}
