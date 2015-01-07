package com.github.ruediste1.i18n.messageFormat.formatTypeParsers;

import java.util.HashMap;
import java.util.Map;

import org.junit.Before;

import com.github.ruediste1.i18n.messageFormat.MessageFormat;

public class FormatTypeParserTestBase {
	protected MessageFormat format;

	@Before
	public void setup() {
		format = new MessageFormat();
	}

	protected Map<String, Object> map(String key, Object value) {
		Map<String, Object> result = new HashMap<>();
		result.put(key, value);
		return result;
	}
}
