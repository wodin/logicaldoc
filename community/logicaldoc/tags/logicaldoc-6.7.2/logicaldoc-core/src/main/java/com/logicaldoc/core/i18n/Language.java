package com.logicaldoc.core.i18n;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Constructor;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.snowball.SnowballAnalyzer;
import org.apache.lucene.util.Version;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Instances of this class represent a language supported by the LogicalDOC DMS
 * 
 * @author Alessandro Gasparini - Logical Objects
 * @since 3.0.3
 */
public class Language implements Comparable<Language> {

	protected static Logger log = LoggerFactory.getLogger(Language.class);

	private Locale locale;

	private Set<String> stopWords = new HashSet<String>();

	private Analyzer analyzer;

	private String analyzerClass;

	private static final Version LUCENE_VERSION = Version.LUCENE_35;

	public Language(Locale locale) {
		this.locale = locale;
		loadStopwords();
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

			if (is == null) {
				log.warn("No stopwords found for locale " + getLocale().toString());
			} else {
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
			}
			stopWords = swSet;
		} catch (Throwable e) {
			log.warn(e.getMessage(), e);
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
					Constructor constructor = aClass.getConstructor(new Class[] { org.apache.lucene.util.Version.class,
							java.util.Set.class });
					if (constructor != null)
						analyzer = (Analyzer) constructor.newInstance(LUCENE_VERSION, stopWords);
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
						analyzer = (Analyzer) constructor.newInstance(LUCENE_VERSION);
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
			analyzer = new SnowballAnalyzer(LUCENE_VERSION, getLocale().getDisplayName(Locale.ENGLISH), getStopWords());
			log.debug("Using default snowball analyzer");
		}

		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	@Override
	public String toString() {
		return locale.toString();
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public int compareTo(Language o) {
		if ("standard".equals(toString()))
			return -1;
		else if ("standard".equals(o.toString()))
			return 1;
		else
			return toString().compareToIgnoreCase(o.toString());
	}
}