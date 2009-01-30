package com.logicaldoc.core.searchengine;

import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.core.text.analyzer.Stopwords;

/**
 * This factory instantiates the analyzer for the given language
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 3.0
 */
public class LuceneAnalyzerFactory {

	protected static Log log = LogFactory.getLog(LuceneAnalyzerFactory.class);
	
	private static Map<String, Analyzer> analyzers = new HashMap<String, Analyzer>();

	static {
		// Get languages from LanguageManager
		Collection<Language> languages = LanguageManager.getInstance().getLanguages();
		for (Language language : languages) {
			String[] stopwords = Stopwords.getStopwords(language.getLanguage());
			SnowballAnalyzer analyzer = new SnowballAnalyzer(language.getLocale().getDisplayName(Locale.ENGLISH), stopwords);
			analyzers.put(language.getLanguage(), analyzer);
		}
	}

	private LuceneAnalyzerFactory() {
	}

	public static Analyzer getAnalyzer(String language) {
		if ((language == null) || (!analyzers.containsKey(language)))
			return analyzers.get("en");
		return analyzers.get(language);
	}
}
