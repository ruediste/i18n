package com.github.ruediste1.i18n.messageFormat.ast;

import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Calendar;
import java.util.Date;

import com.github.ruediste1.i18n.messageFormat.FormattingContext;

public class DateTimeNode extends ArgumentNode {

	private DateTimeFormatter formatter;

	public DateTimeNode(java.lang.String argumentName,
			DateTimeFormatter formatter) {
		super(argumentName);
		this.formatter = formatter;
	}

	@Override
	public java.lang.String format(FormattingContext ctx) {
		Object arg = ctx.arguments.get(argumentName);
		TemporalAccessor temporal;
		if (arg instanceof TemporalAccessor)
			temporal = (TemporalAccessor) arg;
		else if (arg instanceof Date)
			temporal = ((Date) arg).toInstant();
		else if (arg instanceof Calendar)
			temporal = ((Calendar) arg).toInstant();
		else if (arg instanceof com.ibm.icu.util.Calendar)
			temporal = Instant.ofEpochMilli(((com.ibm.icu.util.Calendar) arg)
					.getTimeInMillis());
		else
			throw new RuntimeException("Cannot format given Object as Date: "
					+ arg);
		return formatter.format(temporal);
	}

}