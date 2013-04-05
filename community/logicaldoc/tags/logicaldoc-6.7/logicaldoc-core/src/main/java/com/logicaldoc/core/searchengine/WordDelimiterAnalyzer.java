package com.logicaldoc.core.searchengine;

import java.io.IOException;
import java.io.Reader;
import java.util.Locale;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.solr.analysis.SolrAnalyzer;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.util.LocaleUtil;

/**
 * This analyzer is a wrapper to be used to allow searches in subwords tokens,
 * by using the <code>WordDelimiterFilter</code>.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 6.0
 */
public class WordDelimiterAnalyzer extends SolrAnalyzer {

	public static final ThreadLocal<String> lang = new ThreadLocal<String>() {
		@Override
		protected String initialValue() {
			return "en";
		}
	};

	public WordDelimiterAnalyzer() {
	}

	private Analyzer getAnalyzer() {
		LanguageManager man = LanguageManager.getInstance();
		Language language = man.getLanguage(LocaleUtil.toLocale(lang.get()));
		if (language == null)
			language = new Language(Locale.ENGLISH);
		return language.getAnalyzer();
	}

	@Override
	public final TokenStream tokenStream(String fieldName, Reader reader) {
		TokenStream ts = getAnalyzer().tokenStream(fieldName, reader);
		ts = new WordDelimiterFilter(ts, 1, 1, 0, 0, 0, 0, 1, 1, 0, null);
		return ts;
	}

	@Override
	public final TokenStream reusableTokenStream(String fieldName, Reader reader) throws IOException {
		TokenStream ts = getAnalyzer().tokenStream(fieldName, reader);
		ts = new WordDelimiterFilter(ts, 1, 1, 1, 1, 1, 1, 1, 1, 1, null);
		return ts;
	}

	@Override
	public TokenStreamInfo getStream(String fieldName, Reader reader) {
		return null;
	}
}
