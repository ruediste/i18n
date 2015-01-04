package com.github.ruediste1.i18n.messageFormat.ast;

import static java.util.stream.Collectors.toSet;

import java.util.Collection;
import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public final class SequenceNode implements Node {
	private Collection<Node> nodes;

	public SequenceNode(Collection<Node> nodes) {
		this.nodes = nodes;
	}

	@Override
	public String format(FormattingContext ctx) {
		StringBuilder sb = new StringBuilder();
		nodes.stream().map(n -> n.format(ctx)).forEach(s -> sb.append(s));
		return sb.toString();
	}

	@Override
	public Set<String> argumentNames() {
		return nodes.stream().flatMap(n -> n.argumentNames().stream())
				.collect(toSet());
	}
}