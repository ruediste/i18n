/**
 * Representation of localized strings. 
 * <p>
 * The {@link com.github.ruediste1.i18n.lString.LString} is simply
 * a string which can be resolved in any locale. There are two implementations:
 * the {@link com.github.ruediste1.i18n.lString.TranslatedString} simply contains a 
 * resource key which is used to resolve the string. The {@link com.github.ruediste1.i18n.lString.PatternString}
 * contains a TranslatedString which is interpreted as pattern.  
 */
package com.github.ruediste1.i18n.lString;