package com.github.ruediste1.i18n.messageFormat.ast;

import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

/**
 * A node of an abstract syntax tree (ast) a message patterns is parsed to.
 */
public abstract class PatternNode {
	public abstract String format(FormattingContext ctx);

	public abstract Set<String> argumentNames();
}