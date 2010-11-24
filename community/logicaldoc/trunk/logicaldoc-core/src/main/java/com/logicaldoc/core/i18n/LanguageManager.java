package com.logicaldoc.core.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.java.plugin.registry.Extension;

import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;
import com.logicaldoc.util.plugin.PluginRegistry;

/**
 * A manager for all supported languages. It's internals are initialized from
 * the extension point 'Language' of the core plugin.
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0.3
 */
public class LanguageManager {

	protected static Log log = LogFactory.getLog(LanguageManager.class);

	private static LanguageManager languageManager;

	private Map<Locale, Language> languages = new HashMap<Locale, Language>();

	private Language defaultLanguage;

	private LanguageManager() {
		init();
	}

	public void init() {
		defaultLanguage = new Language(Locale.ENGLISH);
		languages.clear();

		// Acquire the 'Language' extensions of the core plugin and add defined
		// languages
		PluginRegistry registry = PluginRegistry.getInstance();
		if (registry == null)
			return;

		Collection<Extension> extensions = new ArrayList<Extension>();
		try {
			extensions = registry.getExtensions("logicaldoc-core", "Language");
		} catch (Throwable e) {
			log.error(e.getMessage());
		}

		for (Extension ext : extensions) {
			String language = ext.getParameter("locale").valueAsString();
			String analyzer = ext.getParameter("analyzer").valueAsString();
			log.debug("analyzer = " + analyzer);
			try {
				Locale locale = LocaleUtil.toLocale(language);
				Language lang = new Language(locale);
				if (StringUtils.isNotEmpty(analyzer) && !"-".equals(analyzer))
					lang.setAnalyzerClass(analyzer);
				languages.put(locale, lang);
				log.info("Added new Language: " + language);
			} catch (Throwable e) {
				log.error(e.getMessage());
			}
		}
	}

	public static LanguageManager getInstance() {
		if (languageManager == null)
			languageManager = new LanguageManager();
		return languageManager;
	}

	public Collection<Language> getLanguages() {
		return languages.values();
	}

	public Collection<Language> getActiveLanguages() {
		ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
		if(config==null)
			return getLanguages();
		
		Collection<Language> actives = new ArrayList<Language>();
		for (Language l : getLanguages()) {
			if ("enabled".equals(config.getProperty("lang." + l.getLanguage())))
				actives.add(l);
		}
		return actives;
	}

	public Language getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * Retrieves the Language for the given language Null is returned if the
	 * corresponding Language could not be found
	 * 
	 * @param locale The language to retrieve the language for
	 * @return A Language object
	 */
	public Language getLanguage(Locale locale) {
		return languages.get(locale);
	}

	public void addLanguage(Locale locale, Language lang) {
		languages.put(locale, lang);
	}

	public void setDefaultLanguage(Language language) {
		this.defaultLanguage = language;
	}

	public List<String> getLanguagesAsString() {
		List<String> languages2 = new ArrayList<String>();
		for (Language lang : getActiveLanguages()) {
			languages2.add(lang.getLocale().toString());
		}
		return languages2;
	}
}