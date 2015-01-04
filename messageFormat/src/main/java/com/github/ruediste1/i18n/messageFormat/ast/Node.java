package com.github.ruediste1.i18n.messageFormat.ast;

import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public interface Node {
	String format(FormattingContext ctx);

	Set<String> argumentNames();
}