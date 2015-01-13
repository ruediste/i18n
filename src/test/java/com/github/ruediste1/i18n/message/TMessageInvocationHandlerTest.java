package com.github.ruediste1.i18n.message;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Proxy;
import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;

import com.github.ruediste1.i18n.PatternString;
import com.github.ruediste1.i18n.TranslatedString;

public class TMessageInvocationHandlerTest {

	public static interface TestMessages {
		PatternString noItemFound();

		TranslatedString noItemFound1();

		TranslatedString tStringWithArgs(int arg);

		int wrongReturnType();

		@TMessage("There are {count} users")
		PatternString userCount(int count);
	}

	TestMessages msgs;

	@Before
	public void setup() {
		TMessageInvocationHandler handler = new TMessageInvocationHandler();
		msgs = (TestMessages) Proxy.newProxyInstance(getClass()
				.getClassLoader(), new Class<?>[] { TestMessages.class },
				handler);
	}

	@Test
	public void testTStringResult() {
		assertEquals(new TranslatedString(null, getClass().getName()
				+ "$TestMessages.noItemFound1", "No Item Found1."),
				msgs.noItemFound1());

	}

	@Test(expected = RuntimeException.class)
	public void testWrongReturnType() {
		msgs.wrongReturnType();
	}

	@Test(expected = RuntimeException.class)
	public void testTStringWithArgs() {
		msgs.tStringWithArgs(4);
	}

	@Test
	public void testMessages() {
		HashMap<String, Object> map = new HashMap<>();
		assertEquals(new PatternString(null, new TranslatedString(null,
				getClass().getName() + "$TestMessages.noItemFound",
				"No Item Found."), map), msgs.noItemFound());

		map.put("count", 4);
		assertEquals(new PatternString(null, new TranslatedString(null,
				getClass().getName() + "$TestMessages.userCount",
				"There are {count} users"), map), msgs.userCount(4));
	}
}
