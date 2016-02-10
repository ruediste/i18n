package com.github.ruediste1.i18n.lString;

import java.util.Locale;
import java.util.ResourceBundle;

public interface ResourceBundleResolver {

    ResourceBundle getResourceBundle(Locale locale);

}