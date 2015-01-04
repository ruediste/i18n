package com.github.ruediste1.i18n.messageFormat.ast;

import java.util.Collections;
import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public class LiteralNode implements Node {

	private String literal;

	public LiteralNode(String literal) {
		this.literal = literal;
	}

	@Override
	public String format(FormattingContext ctx) {
		return literal;
	}

	@Override
	public Set<String> argumentNames() {
		return Collections.emptySet();
	}

}