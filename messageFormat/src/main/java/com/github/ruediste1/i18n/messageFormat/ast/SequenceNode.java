package com.github.ruediste1.i18n.messageFormat.ast;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public final class SequenceNode extends PatternNode {
	private final Collection<PatternNode> nodes;

	public SequenceNode(Collection<PatternNode> nodes) {
		this.nodes = nodes;
	}

	public static SequenceNode fromObjects(Collection<Object> objects) {
		List<PatternNode> nodes = new ArrayList<PatternNode>();

		StringBuilder sb = new StringBuilder();
		for (Object object : objects) {
			if (object instanceof String) {
				sb.append((String) object);
			} else if (object instanceof LiteralNode) {
				sb.append(((LiteralNode) object).getLiteral());
			} else if (object instanceof PatternNode) {
				String str = sb.toString();
				if (!str.isEmpty()) {
					nodes.add(new LiteralNode(str));
					sb = new StringBuilder();
				}
				nodes.add((PatternNode) object);
			} else
				throw new RuntimeException("Unknown instance: " + object);
		}

		// append trailing string
		String str = sb.toString();
		if (!str.isEmpty()) {
			nodes.add(new LiteralNode(str));
		}
		return new SequenceNode(nodes);
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

	public Collection<PatternNode> getNodes() {
		return nodes;
	}
}