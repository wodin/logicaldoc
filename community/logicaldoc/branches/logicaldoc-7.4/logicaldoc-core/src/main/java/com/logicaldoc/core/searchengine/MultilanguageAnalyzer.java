package com.logicaldoc.core.searchengine;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.AnalyzerWrapper;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.miscellaneous.WordDelimiterFilterFactory;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.tartarus.snowball.SnowballProgram;

import com.logicaldoc.core.i18n.Language;
import com.logicaldoc.core.i18n.LanguageManager;
import com.logicaldoc.util.Context;
import com.logicaldoc.util.LocaleUtil;
import com.logicaldoc.util.config.ContextProperties;

/**
 * This analyzer is a wrapper to be used to handle language specific analyzer
 * and to allow searches in subwords tokens, by using the
 * <code>WordDelimiterFilter</code>.
 * 
 * @author Marco Meschieri - Logical Objects
 * @since 7.1
 */
public class MultilanguageAnalyzer extends AnalyzerWrapper {

	public static final ThreadLocal<String> lang = new ThreadLocal<String>() {
		@Override
		protected String initialValue() {
			return "en";
		}
	};

	public MultilanguageAnalyzer() {
		super(GLOBAL_REUSE_STRATEGY);
	}

	private boolean isSubwordsEnabled() {
		try {
			ContextProperties config = (ContextProperties) Context.getInstance().getBean(ContextProperties.class);
			boolean subwords = "true".equals(config.getProperty("index.subwords"));
			return subwords;
		} catch (Throwable t) {
			return false;
		}
	}

	@Override
	protected Analyzer getWrappedAnalyzer(String fieldName) {
		return getLanguage().getAnalyzer();
	}

	private Language getLanguage() {
		LanguageManager man = LanguageManager.getInstance();
		Language language = man.getLanguage(LocaleUtil.toLocale(lang.get()));
		if (language == null)
			language = new Language(Locale.ENGLISH);
		return language;
	}

	@Override
	protected TokenStreamComponents wrapComponents(String fieldName, TokenStreamComponents components) {
		TokenStream ts = components.getTokenStream();

		// Try to add a snowball filter
		SnowballProgram stemmer = getLanguage().getStemmer();
		
		if (stemmer != null)
			ts = new SnowballFilter(components.getTokenStream(), stemmer);

		

		Tokenizer tokenizer = components.getTokenizer();

		if (isSubwordsEnabled()) {
			Map<String, String> configs = new HashMap<String, String>();
			configs.put("preserveOriginal", "0");
			configs.put("splitOnNumerics", "1");
			configs.put("splitOnCaseChange", "1");
			configs.put("catenateWords", "0");
			configs.put("catenateNumbers", "0");
			configs.put("catenateAll", "0");
			configs.put("generateWordParts", "1");
			configs.put("generateNumberParts", "1");
			configs.put("stemEnglishPossessive", "1");
			configs.put("luceneMatchVersion", StandardSearchEngine.VERSION.toString());

			TokenStream wdStream = new WordDelimiterFilterFactory(configs).create(ts);
			return new TokenStreamComponents(tokenizer, wdStream);
		} else
			return new TokenStreamComponents(tokenizer, ts);
	}
}