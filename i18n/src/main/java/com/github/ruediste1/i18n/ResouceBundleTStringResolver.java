package com.github.ruediste1.i18n;

import java.util.*;

public class ResouceBundleTStringResolver implements TStringResolver {

	private String baseName;
	private ClassLoader loader;

	public void initialize(String baseName, ClassLoader loader) {
		this.baseName = baseName;
		this.loader = loader;

	}

	@Override
	public String resolve(TranslatedString str, Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale,
				loader);

		// return string from bundle if available
		if (bundle.containsKey(str.getResourceKey())) {
			return bundle.getString(str.getResourceKey());
		}

		// check if fallback is available
		if (str.getFallback() == null) {
			throw new MissingResourceException("resource for key <"
					+ str.getResourceKey()
					+ "> not found, and no fallback present", getClass()
					.getName(), str.getResourceKey());
		}

		// return fallback string
		return str.getFallback();

	}
}
