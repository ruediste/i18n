package com.github.ruediste1.i18n.messageFormat;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Locale;

import org.junit.Test;

public class MessageFormatTest {

	private static class Dummy {

	}

	@Test
	public void testArgumentPreparationFunction() {
		MessageFormat fmt = new MessageFormat()
				.withArgumentPreparationFunction((arg, locale) -> {
					if (arg instanceof Dummy)
						return "dummy1";
					return arg;
				});

		HashMap<String, Object> args = new HashMap<>();
		args.put("arg", new Dummy());

		assertEquals("hello mister dummy1",
				fmt.format("hello mister {arg}", args, Locale.ENGLISH));
	}
}
