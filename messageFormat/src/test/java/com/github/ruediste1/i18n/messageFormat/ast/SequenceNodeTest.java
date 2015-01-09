package com.github.ruediste1.i18n.messageFormat.ast;

import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

public class SequenceNodeTest {

	@Test
	public void testFromObjects() throws Exception {
		DateTimeNode dateTimeNode = new DateTimeNode("foo", null);
		List<PatternNode> nodes = SequenceNode
				.fromObjects(
						Arrays.asList("a", "b", dateTimeNode, new LiteralNode(
								"c"), "d")).getNodes().stream()
				.collect(toList());
		assertEquals(3, nodes.size());
		assertEquals(LiteralNode.class, nodes.get(0).getClass());
		assertEquals("ab", nodes.get(0).format(null));
		assertEquals(dateTimeNode, nodes.get(1));
		assertEquals(LiteralNode.class, nodes.get(2).getClass());
		assertEquals("cd", nodes.get(2).format(null));
	}
}
