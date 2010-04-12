package com.logicaldoc.core.i18n;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;

import com.logicaldoc.util.io.ResourceUtil;

/**
 * Instances of this class represent a language supported by the LogicalDOC DMS
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0.3
 */
public class Language {

	protected static Log log = LogFactory.getLog(Language.class);

	private Locale locale;

	private String[] stopWords = new String[0];

	private Analyzer analyzer;

	private String analyzerClass;

	private char[] charset;

	public Language(Locale locale) {
		this.locale = locale;
		loadStopwords();
		loadCharsets();
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

	public String getDefaultDisplayLanguage() {
		return locale.getDisplayLanguage(Locale.ENGLISH);
	}

	/**
	 * Retrieves the full text index name for this language
	 */
	public String getIndex() {
		return locale.toString();
	}

	/**
	 * Populates the field stopWords reading the resource /stopwords/stopwords_<locale>.txt
	 */
	void loadStopwords() {
		try {
			List<String> swlist = new ArrayList<String>();
			String stopwordsResource = "/stopwords/stopwords_" + getLocale().toString() + ".txt";
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(stopwordsResource);
			if (is == null)
				is = getClass().getResourceAsStream(stopwordsResource);

			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while((line = br.readLine()) != null) {
				line = line.trim();
				if (line.indexOf("|") != -1) {
					line = line.substring(0, line.indexOf("|"));
					line = line.trim();
				}
				if (line != null && line.length() > 0) {
					swlist.add(line);
				}
			}

			stopWords = (String[]) swlist.toArray(new String[0]);
		} catch (Throwable e) {
			log.warn(e.getMessage());
		}
	}
	
	/**
	 * Populates the field charsets reading the resource /charsets/charset_<locale>.txt
	 */
	private void loadCharsets() {
		try {
			String charsetResource = "/charsets/charset_" + getLocale().toString() + ".txt";
			String buf = ResourceUtil.readAsString(charsetResource);
			charset=buf.toCharArray();
		} catch (Throwable e) {
			log.warn(e.getMessage());
			charset = new char[] {};
		}
	}
	
	public String[] getStopWords() {
		return stopWords;
	}

	public void setStopWords(String[] stopWords) {
		this.stopWords = stopWords;
	}

	public String getAnalyzerClass() {
		return analyzerClass;
	}

	public void setAnalyzerClass(String analyzerClass) {
		this.analyzerClass = analyzerClass;
	}

	@SuppressWarnings("unchecked")
	public Analyzer getAnalyzer() {
		if (analyzer == null && !StringUtils.isEmpty(getAnalyzerClass())) {
			// Try to instantiate the specified analyzer
			Class aClass = null;
			try {
				aClass = getClass().forName(analyzerClass);
			} catch (Throwable t) {
				log.error(analyzerClass + " not found");
			}

			// Try to use constructor init(char[] charset, String[]
			// stopWords)
			try {
				Constructor constructor = aClass.getConstructor(new Class[] { Class.forName("[C"),
						Class.forName("[Ljava.lang.String;") });
				if (constructor != null)
					analyzer = (Analyzer) constructor.newInstance(charset, stopWords);
			} catch (Throwable e) {
				log.debug("constructor (char[] charset, String[] stopWords) not found");
			}

			// Try to use constructor init(String[] stopWords)
			if (analyzer == null)
				try {
					Constructor constructor = aClass.getConstructor(new Class[] { Class.forName("[C") });
					if (constructor != null)
						analyzer = (Analyzer) constructor.newInstance(stopWords);
				} catch (Throwable e) {
					log.debug("constructor (String[] stopWords) not found");
				}

			// Try with default constructor
			try {
				analyzer = (Analyzer) aClass.newInstance();
			} catch (Throwable e) {
				log.debug("constructor without arguments not found");
			}
		}

		if (analyzer == null) {
			analyzer = new SnowballAnalyzer(getLocale().getDisplayName(Locale.ENGLISH), getStopWords());
			log.debug("Using default snawball analyzer");
		}

		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public char[] getCharset() {
		return charset;
	}

	public void setCharset(char[] charset) {
		this.charset = charset;
	}
}