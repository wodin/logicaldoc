package com.logicaldoc.core.i18n;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import com.logicaldoc.core.text.analyze.Stopwords;
import com.logicaldoc.util.PluginRegistry;
import org.java.plugin.registry.Extension;
import org.java.plugin.registry.Extension.Parameter;

/**
 * A manager for all supported languages. It's internals are initialized frrom
 * the extension point 'Language' of the core plugin.
 * 
 * @author Alessandro Gasparini
 * @version $Id:$
 * @since 3.0.3
 */
public class LanguageManager {

	protected static Log log = LogFactory.getLog(LanguageManager.class);

	private static LanguageManager localeManager;

	private Map<String, Language> languages = new HashMap<String, Language>();

	private Language defaultLanguage;

	private LanguageManager() {
		init();
	}

	private void init() {
		defaultLanguage = new Language(Locale.ENGLISH);

		languages.put("en", new Language(Locale.ENGLISH));
		languages.put("it", new Language(Locale.ITALIAN));
		languages.put("de", new Language(Locale.GERMAN));
		languages.put("es", new Language(new Locale("es")));
		languages.put("fr", new Language(Locale.FRENCH));

		// Acquire the 'Language' extensions of the core plugin and add defined
		// languages
		PluginRegistry registry = PluginRegistry.getInstance();
		if (registry == null)
			return;
		Collection<Extension> extensions = registry.getExtensions("logicaldoc-core", "Language");

		for (Extension ext : extensions) {
			String languageKey = ext.getParameter("languageKey").valueAsString();

			try {
				Locale locale = new Locale(languageKey);
				languages.put(locale.getLanguage(), new Language(locale));
				log.info("Added new Language: " + locale);
			} catch (Throwable e) {
				log.error(e.getMessage());
			}

			Parameter stopWordsParam = ext.getParameter("stopWordsFile");
			if (stopWordsParam != null) {
				try {
					String stopWordsFile = stopWordsParam.valueAsString();
					String[] stopWords = Stopwords.loadStopwords(stopWordsFile);
					Stopwords.addStopwords(languageKey, stopWords);
					log.info("Added Stopwords for the Language: " + languageKey);
				} catch (Throwable e) {
					log.error("Unable to load Stopwords for the Language: " + languageKey);
					log.error(e.getMessage());
				}
			}
		}
	}

	public static LanguageManager getInstance() {
		if (localeManager == null)
			localeManager = new LanguageManager();
		return localeManager;
	}

	public Collection<Language> getLanguages() {
		return languages.values();
	}

	public Language getDefaultLanguage() {
		return defaultLanguage;
	}

	/**
	 * Retrieves the Language for the given language Null is returned if the
	 * corresponding Language could not be found
	 * 
	 * @param languageKey The language to retrieve the language for
	 * @return A Language object
	 */
	public Language getLanguage(String languageKey) {
		return languages.get(languageKey);
	}

	public void addLanguage(String languageKey, Language lang) {
		languages.put(languageKey, lang);
	}

	public void setDefaultLanguage(Language language) {
		this.defaultLanguage = language;
	}

	public List<String> getISO639_2Languages() {
		List<String> languages2 = new ArrayList<String>();
		for (Language lang : getLanguages()) {
			languages2.add(lang.getLanguage());
		}
		return languages2;
	}
}