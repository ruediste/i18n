package com.github.ruediste1.i18n.messageFormat;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

public class FormatTypeParserTestBase {
	PatternFormatter format;

	@Before
	public void setup() {
		format = new PatternFormatter();
	}

	Map<String, Object> map(String key, Object value) {
		Map<String, Object> result = new HashMap<>();
		result.put(key, value);
		return result;
	}
}
