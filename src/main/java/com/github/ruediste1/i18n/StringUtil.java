package com.github.ruediste1.i18n;

public class StringUtil {

	public static String insertSpacesIntoCamelCaseString(String str) {
		StringBuilder sb = new StringBuilder(str.length());
		boolean isFirst = true;
		for (int c : str.codePoints().toArray()) {
			if (!isFirst && Character.isUpperCase(c)) {
				sb.append(" ");
			}
			isFirst = false;
			sb.appendCodePoint(c);
		}
		return sb.toString();
	}

}
