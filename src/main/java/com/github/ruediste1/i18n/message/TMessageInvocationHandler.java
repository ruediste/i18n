package com.github.ruediste1.i18n.message;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;

import javax.inject.Inject;

import com.github.ruediste1.i18n.PatternString;
import com.github.ruediste1.i18n.PatternStringResolver;
import com.github.ruediste1.i18n.StringUtil;
import com.github.ruediste1.i18n.TranslatedString;
import com.github.ruediste1.i18n.TStringResolver;
import com.google.common.base.CaseFormat;

public class TMessageInvocationHandler implements InvocationHandler {

	@Inject
	PatternStringResolver pStringResolver;

	@Inject
	TStringResolver tStringResovler;

	@Override
	public Object invoke(Object proxy, Method method, Object[] args)
			throws Throwable {

		// calculate fallback
		String fallback;
		TMessage tMessage = method.getAnnotation(TMessage.class);
		if (tMessage != null) {
			fallback = tMessage.value();
		} else {
			fallback = StringUtil
					.insertSpacesIntoCamelCaseString(CaseFormat.LOWER_CAMEL.to(
							CaseFormat.UPPER_CAMEL, method.getName()))
					+ ".";
		}

		// build string
		TranslatedString tString = new TranslatedString(tStringResovler, method
				.getDeclaringClass().getName() + "." + method.getName(),
				fallback);

		// check return type
		if (TranslatedString.class.equals(method.getReturnType())) {
			if (args != null && args.length > 0) {
				throw new RuntimeException(
						"The return type of "
								+ method
								+ " is TString but there are parameters. Change the return type to PString instead");
			}
			return tString;
		}

		if (PatternString.class.equals(method.getReturnType())) {
			// build parameter map
			HashMap<String, Object> parameters = new HashMap<>();
			for (int i = 0; i < method.getParameters().length; i++) {
				parameters.put(method.getParameters()[i].getName(), args[i]);
			}

			return new PatternString(pStringResolver, tString, parameters);
		}

		throw new RuntimeException("The return type of " + method
				+ " must be TString or PString");
	}

}
