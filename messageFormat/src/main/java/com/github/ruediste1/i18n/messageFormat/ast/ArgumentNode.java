package com.github.ruediste1.i18n.messageFormat.ast;

import java.util.Collections;
import java.util.Set;

public abstract class ArgumentNode implements Node {

	protected String argumentName;

	public ArgumentNode(String argumentName) {
		this.argumentName = argumentName;
	}

	@Override
	public Set<String> argumentNames() {
		return Collections.singleton(argumentName);
	}

}