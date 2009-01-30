package com.logicaldoc.core.i18n;

import java.util.Locale;

/**
 * Instances of this class represent a language supported by the LogicalDOC DMS
 * 
 * @author Alessandro Gasparini
 * @version $Id:$
 * @since 3.0.3
 */
public class Language {

	private Locale locale;

	private String index;

	public Language(Locale locale) {
		this.locale = locale;
		this.index = locale.getDisplayLanguage(Locale.ENGLISH).toLowerCase();
	}

	public Locale getLocale() {
		return locale;
	}

	public String getLanguage() {
		return locale.getLanguage();
	}

	public String getDisplayLanguage() {
		return locale.getDisplayLanguage();
	}

	public String getIndex() {
		return index;
	}

}
