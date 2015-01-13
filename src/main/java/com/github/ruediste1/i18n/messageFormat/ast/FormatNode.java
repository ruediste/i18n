package com.github.ruediste1.i18n.messageFormat.ast;

import java.text.Format;
import java.util.Locale;
import java.util.function.Function;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public class FormatNode extends ArgumentNode {

	private Function<Locale, ? extends Format> formatFactory;

	public FormatNode(String argumentName,
			Function<Locale, ? extends Format> formatFactory) {
		super(argumentName);
		this.formatFactory = formatFactory;
	}

	@Override
	public String format(FormattingContext ctx) {
		Object arg = ctx.getArgument(argumentName);
		return formatFactory.apply(ctx.getLocale()).format(prepareArg(arg));
	}

	/**
	 * Hook to process the argument before it is passed to the format
	 */
	protected Object prepareArg(Object arg) {
		return arg;
	}

}