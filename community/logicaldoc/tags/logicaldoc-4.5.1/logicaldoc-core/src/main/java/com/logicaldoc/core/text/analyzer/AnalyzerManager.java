package com.logicaldoc.core.text.analyzer;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;

/**
 * Class for analysing texts like extracting tags from a given text. Created on
 * 24.03.2004
 * 
 * @author Michael Scholz
 */
public class AnalyzerManager {

	private Map<Locale, Analyzer> analyzers;

	private void init() {
		analyzers = new HashMap<Locale, Analyzer>();

		// Get languages from LanguageManager
		Collection<Language> languages = LanguageManager.getInstance().getLanguages();
		for (Language language : languages) {
			Analyzer analyzer = new Analyzer(language, 4);
			analyzers.put(language.getLocale(), analyzer);
		}
	}

	public Analyzer getAnalyzer(Locale locale) {
		if (analyzers == null)
			init();
		if ((locale == null) || (!analyzers.containsKey(locale)))
			return analyzers.get(Locale.ENGLISH);
		return analyzers.get(locale);
	}

	public AnalyzerManager() {
	}

	/**
	 * This method extracts a specified number of tags and appends them to a
	 * String
	 * 
	 * @param count Number of tags.
	 * @param text Given text of a document.
	 * @param language Identified language of the text.
	 * @return String of tags like "Information, Retrieval, DMS, CMS"
	 * @throws Exception
	 */
	public String getTermsAsString(int count, String text, Locale locale) throws Exception {
		StringBuffer result = new StringBuffer();
		Analyzer analyzer = getAnalyzer(locale);
		analyzer.analyze(text);

		Collection<Entry> terms = analyzer.getTopWords(count);
		Iterator<Entry> iter = terms.iterator();
		int temp = 0;

		while (iter.hasNext() && (temp < count)) {
			Entry entry = iter.next();

			if (temp > 0) {
				result.append(", ");
			}

			result.append(entry.getOriginWord());
			temp++;
		}

		return result.toString();
	}
}