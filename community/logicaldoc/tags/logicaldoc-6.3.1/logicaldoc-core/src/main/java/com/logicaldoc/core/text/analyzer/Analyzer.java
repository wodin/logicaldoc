package com.logicaldoc.core.text.analyzer;

import java.lang.reflect.InvocationTargetException;
import java.text.BreakIterator;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.logicaldoc.core.i18n.Language;

/**
 * @author Michael Scholz
 * @author Alessandro Gasparini
 */
public class Analyzer extends WordRanker {

	protected static Log log = LogFactory.getLog(Analyzer.class);

	private Language language;

	/**
	 * Creates a new instance of Analyzer.
	 * 
	 * @param language
	 */
	Analyzer(Language language) {
		this.language = language;
	}

	/**
	 * Creates a new instance of Analyzer.
	 * 
	 * @param language
	 * @param len Minimum length of words which should analyzed.
	 */
	Analyzer(Language language, int len) {
		this.language = language;
		minlen = len;
		stoptable = Analyzer.getAsMap(language.getStopWords());
	}

	/**
	 * This method transforms the array of stop words into a map of stop words.
	 * 
	 * @param stopwords - Map of stop words.
	 */
	final static Map<String, String> getAsMap(Set<String> stopwords) {
		Hashtable<String, String> stoptable = new Hashtable<String, String>();

		for (String stopword : stopwords) {
			stoptable.put(stopword, stopword);
		}

		return stoptable;
	}

	/**
	 * This method analyzes a given text an fills a hitlist.
	 * 
	 * @param text Text which should analyzed.
	 * @throws Exception In case of exception during Stemmer instantation
	 */
	public void analyze(String text) throws Exception {
		BreakIterator boundary = BreakIterator.getWordInstance();
		boundary.setText(text);

		Stemmer stemmer = null;
		try {
			stemmer = new Stemmer(language);
		} catch (Exception e) {
			log.error("Unable to instantiate a Stemmer for language " + language, e);
			throw e;
		}
		AnalyzeResult result = performAnalysis(boundary, new StringBuffer(text), stoptable, minlen, stemmer);
		wordcount = result.getWordCount();
		wordtable = result.getWordTable();
	}

	/**
	 * Analyses a text and builds a table with each unique word stem, number of
	 * stem presence in the text and original word.
	 */
	AnalyzeResult performAnalysis(BreakIterator boundary, StringBuffer source, Map<String, String> stopwords,
			int minlen, Stemmer stemmer) throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {

		int start = boundary.first();
		long wordcount = 0;
		AnalyzeResult result = new AnalyzeResult();
		Hashtable<String, WordEntry> wordtable = new Hashtable<String, WordEntry>(source.length() / 6);

		for (int end = boundary.next(); end != BreakIterator.DONE; start = end, end = boundary.next()) {

			String word = source.substring(start, end).trim();
			char next = ' ';
			try {
				next = source.charAt(end);
			} catch (Exception e) {
			}

			if (word.length() > minlen) {
				String stem = stemmer.stem(word);
				WordEntry entry = new WordEntry();

				if ((word.length() >= minlen) && !stopwords.containsKey(word) && !stopwords.containsKey(stem)) {
					wordcount++;

					if (wordtable.containsKey(stem)) {
						entry = (WordEntry) wordtable.get(stem);
						entry.incValue();

						if ((word.length() < entry.getOriginWord().length()) && (next != (char) 45)) {
							entry.setOriginWord(word);
						}
					} else {
						entry.incValue();
						entry.setOriginWord(word);
					}
					wordtable.put(stem, entry);
				}
			}
		}

		result.setWordCount(wordcount);
		result.setWordTable(wordtable);

		return result;
	}

	public Language getLanguage() {
		return language;
	}
}