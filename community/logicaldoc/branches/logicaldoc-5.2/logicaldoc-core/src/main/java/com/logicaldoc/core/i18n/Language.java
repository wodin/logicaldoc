package com.logicaldoc.core.i18n;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;

import com.logicaldoc.core.searchengine.Indexer;
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

	private Set<String> stopWords = new HashSet<String>();

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
	 * Populates the field stopWords reading the resource
	 * /stopwords/stopwords_<locale>.txt
	 */
	void loadStopwords() {
		try {
			Set<String> swSet = new HashSet<String>();
			String stopwordsResource = "/stopwords/stopwords_" + getLocale().toString() + ".txt";
			log.debug("Loading stopwords from: " + stopwordsResource);
			InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(stopwordsResource);
			if (is == null)
				is = getClass().getResourceAsStream(stopwordsResource);

			InputStreamReader isr = new InputStreamReader(is, "UTF-8");
			BufferedReader br = new BufferedReader(isr);
			String line = null;
			while ((line = br.readLine()) != null) {
				line = line.trim();
				if (line.indexOf("|") != -1) {
					line = line.substring(0, line.indexOf("|"));
					line = line.trim();
				}
				if (line != null && line.length() > 0 && !swSet.contains(line)) {
					swSet.add(line);
				}
			}

			stopWords = swSet;
		} catch (Throwable e) {
			log.warn(e.getMessage());
		}
	}

	/**
	 * Populates the field charsets reading the resource
	 * /charsets/charset_<locale>.txt
	 */
	private void loadCharsets() {
		try {
			String charsetResource = "/charsets/charset_" + getLocale().toString() + ".txt";
			String buf = ResourceUtil.readAsString(charsetResource);
			charset = buf.toCharArray();
		} catch (Throwable e) {
			log.warn(e.getMessage());
			charset = new char[] {};
		}
	}

	public Set<String> getStopWords() {
		return stopWords;
	}

	public void setStopWords(Set<String> stopWords) {
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

			// Try to instantiate the specified analyzer (Using default
			// constructor)
			Class aClass = null;
			try {
				aClass = Class.forName(analyzerClass);
			} catch (Throwable t) {
				log.error(analyzerClass + " not found");
			}

			// Try to use constructor (Version matchVersion, Set<?> stopwords)
			if (stopWords != null && (!stopWords.isEmpty())) {
				try {
					Constructor constructor = aClass
							.getConstructor(new Class[] {
									org.apache.lucene.util.Version.class,
									java.util.Set.class });
					if (constructor != null)
						analyzer = (Analyzer) constructor.newInstance(
								Version.LUCENE_30, stopWords);
				} catch (Throwable e) {
					log.debug("constructor (Version matchVersion, Set<?> stopwords)  not found");
				}
			}

			// Try to use constructor (Version matchVersion)
			if (analyzer == null) {
				try {
					Constructor constructor = aClass
							.getConstructor(new Class[] { org.apache.lucene.util.Version.class });
					if (constructor != null)
						analyzer = (Analyzer) constructor
								.newInstance(Version.LUCENE_30);
				} catch (Throwable t) {
					log.debug("constructor (Version matchVersion) not found");
				}
			}

			// Try with default constructor
			if (analyzer == null) {
				try {
					analyzer = (Analyzer) aClass.newInstance();
				} catch (Throwable e) {
					log.debug("constructor without arguments not found");
				}
			}
		}

		if (analyzer == null) {
			analyzer = new SnowballAnalyzer(Indexer.LUCENE_VERSION, getLocale()
					.getDisplayName(Locale.ENGLISH), getStopWords());
			log.debug("Using default snowball analyzer");
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

	@Override
	public String toString() {
		return locale.toString();
	}
}