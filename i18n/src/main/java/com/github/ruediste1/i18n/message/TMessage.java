package com.github.ruediste1.i18n.message;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface TMessage {
	String value();
}
